/*
 * This file is part of Metamail
 *
 * Copyright (C) 2012 Igalia, S.L.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.igalia.metamail.jobs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

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

public class MessagesByTimePeriod {

    private static final String mailsTable = "enron";

    private static final String MAIL_OUT = "mail/out/msgByTimePeriod/";

    public static class MessagesByTimePeriodMapper extends
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
            MimeMessage msg;

            try {
                msg = new MimeMessage(s, input);

                Date date = msg.getSentDate();
                if (date == null) {
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                // Divide date in parts (day-month-year)
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DATE);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                // Create key for each part
                String yearKey = String.format("%d", year);
                String monthKey = String.format("%d-%d", month, year);
                String dayKey = String.format("%d-%d-%d", day, month, year);

                // Count +1 for each key
                context.write(new Text("YEAR:" + yearKey), one);
                context.write(new Text("MONTH:" + monthKey), one);
                context.write(new Text("DATE:" + dayKey), one);
                context.write(new Text("DAY_OF_WEEK:" + dayOfWeek), one);
                context.write(new Text("HOUR:" + hour), one);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    public static class MessagesByTimePeriodReducer extends
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

    public static Boolean execute() throws Exception {
        return Boolean.valueOf(JobRunner.run(setupJob()));
    }

    private static Job setupJob() throws IOException, InterruptedException,
            ClassNotFoundException {
        Configuration config = HBaseConfiguration.create();
        Job job = new Job(config, "MessagesByTimePeriod");
        job.setJarByClass(MessagesByTimePeriod.class);

        Scan scan = new Scan();
        scan.setCaching(500);
        scan.setCacheBlocks(false); // don't set to true for MR jobs

        // Mapper
        TableMapReduceUtil.initTableMapperJob(
                mailsTable, // input HBase table name
                scan, // Scan instance to control CF and attribute selection
                MessagesByTimePeriod.MessagesByTimePeriodMapper.class, Text.class,
                IntWritable.class, job);

        // Reducer
        job.setReducerClass(MessagesByTimePeriod.MessagesByTimePeriodReducer.class);
        job.setNumReduceTasks(1);

        FileOutputFormat.setOutputPath(job, new Path(MessagesByTimePeriod.MAIL_OUT));

        return job;
    }

}
