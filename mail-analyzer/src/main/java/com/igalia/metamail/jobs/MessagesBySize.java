package com.igalia.metamail.jobs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
 * Sorts emails by size (size, msgid:sender)
 *
 * Ideally, the job should get the top N messages. I was reading about it and it
 * seems that doing operations like top N or unique functions in a single
 * mapreduce job is hard. Usually it's better to code two independent jobs, one
 * that does some calculation and later apply another job over the results to
 * get the top N entries.
 *
 * At the moment I leave like this, the job orders the emails by size. Later the
 * file can be split by the latest elements, with a linux tool like tail, to get
 * the top N largest emails.
 *
 * @author Diego Pino García <dpino@igalia.com>
 *
 */
public class MessagesBySize {

    private static final String jobName = "MessagesBySize";

    private static final String mailsTable = "enron";

    private static final String MAIL_OUT = "mail/out/msgBySize/";

    public static class MessagesBySizeMapper extends
            TableMapper<IntWritable, IntWritable> {

        private static final IntWritable one = new IntWritable(1);

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

                String from = mail.getFrom();
                if (!from.isEmpty()) {
                    int size = mail.getSize();
                    if (size > 0) {
                        size = (int) size / (1024*20);
                    }
                    context.write(new IntWritable(size*20), one);
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        }

    }

    public static class MessagesBySizeReducer extends
            Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

        public void reduce(IntWritable key, Iterable<IntWritable> values,
                Context context) throws IOException, InterruptedException {

            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }

    public static void main(String args[]) throws Exception {
        if (JobRunner.run(setupJob())) {
            System.out.println("Job completed!");
        }
    }

    public static Boolean execute() throws Exception {
        return Boolean.valueOf(JobRunner.run(setupJob()));
    }

    private static Job setupJob() throws IOException {
        Configuration config = HBaseConfiguration.create();
        Job job = new Job(config, MessagesBySize.jobName);
        job.setJarByClass(MessagesBySize.class);

        Scan scan = new Scan();
        scan.setCaching(500);
        scan.setCacheBlocks(false); // don't set to true for MR jobs

        // Mapper
        TableMapReduceUtil.initTableMapperJob(
                mailsTable, // input HBase table name
                scan, // Scan instance to control CF and attribute selection
                MessagesBySize.MessagesBySizeMapper.class,
                IntWritable.class, IntWritable.class,
                job);

        job.setCombinerClass(MessagesBySize.MessagesBySizeReducer.class);
        job.setReducerClass(MessagesBySize.MessagesBySizeReducer.class);

        FileOutputFormat.setOutputPath(job, new Path(MessagesBySize.MAIL_OUT));

        return job;
    }

}
