package com.igalia.metamail.jobs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.Session;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.igalia.metamail.utils.JobRunner;
import com.igalia.metamail.utils.MailRecord;

/**
 * 
 * Thread length for each group of threads
 * 
 * @author Diego Pino García <dpino@igalia.com>
 * 
 */
public class MessagesByThreadLength {

	private static final String jobName = "MessagesByThreadLength";
	
	private static final String mailsTable = "enron";

	private static final String MAIL_OUT = "mail/out/msgThreadLength/";

	public static class MessagesByThreadLengthMapper extends
			TableMapper<Text, IntWritable> {
		
		private IntWritable one = new IntWritable(1);

		public void map(ImmutableBytesWritable row, Result value,
				Context context) throws InterruptedException, IOException {

			byte[] body = value.getValue(Bytes.toBytes("body"),
					Bytes.toBytes(""));

			if (body == null) {
				return;
			}
			
			InputStream input = new ByteArrayInputStream(body);
			Session s = Session.getDefaultInstance(new Properties());
			MailRecord mail;
			try {
				mail = MailRecord.create(s, input);

				String subject = originalSubject(mail.getSubject());
				if (!subject.isEmpty()) {
					Text keySubject = new Text(subject);
					context.write(keySubject, one);
					String sender = mail.getFrom();
					if (!sender.isEmpty()) {
						Text keySubjectSender = new Text(String.format("%s:%s",
								subject, sender));
						context.write(keySubjectSender, one);
					}

				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			
		}
		
		/**
		 * Removed all the 'Re:' labels at the beginning of a subject
		 * 
		 * @param subject
		 * @return
		 */
	    private static String originalSubject(String subject) {
	        String result = subject != null ? subject : "";
	        
	        Pattern pattern = Pattern.compile("(\\[.*?\\]\\s*|Re:\\s*)*(.*)");
	        java.util.regex.Matcher matcher = pattern.matcher(subject);
	        while (matcher.find()) {
	            result = matcher.group(2);
	            break;
	        }
	        return (result != null) ? result.trim() : "";
	    }
		
	}

	public static class MessagesByThreadLengthReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int i = 0;
			for (IntWritable val : values) {
				i += val.get();
			}
			context.write(key, new IntWritable(i));
		}
	}
	
		
	public static void main(String args[]) throws Exception {
		if (JobRunner.run(setupJob())) {
			System.out.println("Job completed!");
		}		
	}
	
	private static Job setupJob() throws IOException {
		Configuration config = HBaseConfiguration.create();
		Job job = new Job(config, jobName);
		job.setJarByClass(MessagesByThreadLength.class);

		Scan scan = new Scan();
		scan.setCaching(500);
		scan.setCacheBlocks(false); // don't set to true for MR jobs

		// Mapper
		TableMapReduceUtil.initTableMapperJob(
				mailsTable, // input HBase table name
				scan, // Scan instance to control CF and attribute selection
				MessagesByThreadLengthMapper.class, 
				Text.class, IntWritable.class, 
				job);

		// Reducer
		job.setCombinerClass(MessagesByThreadLengthReducer.class);
		job.setReducerClass(MessagesByThreadLengthReducer.class);
		job.setNumReduceTasks(1);

		FileOutputFormat.setOutputPath(job, new Path(
				MessagesByThreadLength.MAIL_OUT));

		return job;
	}
	
}
