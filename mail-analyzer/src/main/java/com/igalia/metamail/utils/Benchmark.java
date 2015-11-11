package com.igalia.metamail.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class Benchmark {

    private static final Map<String, String> jobs = new TreeMap<String, String>();

    private static final int NTIMES = 20;

    static {
        jobs.put("msgBySize", "com.igalia.metamail.jobs.MessagesBySize");
        jobs.put("msgThreadLength", "com.igalia.metamail.jobs.MessagesByThreadLength");
        jobs.put("msgByTimePeriod", "com.igalia.metamail.jobs.MessagesByTimePeriod");
        jobs.put("msgReceived", "com.igalia.metamail.jobs.MessagesReceived");
        jobs.put("msgSentByPerson", "com.igalia.metamail.jobs.MessagesSentByPerson");
    }

    public static void main(String args[]) throws Exception {
        BenchmarkHelper benchmarkHelper = new BenchmarkHelper();

        benchmarkHelper.start();
        for (String jobDir: jobs.keySet()) {
            String jobName = jobs.get(jobDir);
            benchmarkHelper.executeJob(jobDir, jobName, NTIMES);
        }
        benchmarkHelper.stop();
    }

    static class BenchmarkHelper {

        private static BufferedWriter log;

        public void start() {
            FileWriter fstream;
            try {
                fstream = new FileWriter("benchmark.out");
                log = new BufferedWriter(fstream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void stop() throws IOException {
            log.close();
        }

        public void executeJob(String dirName, String jobName, int times) throws Exception {
            log(String.format("-- %s", dirName));
            for (int i = 0; i < times; i++) {
                double seconds = executeJob(dirName, jobName);
                log(String.format("%s,%.2f", jobName,
                        seconds));
            }
            System.out.println(String.format("### Benchmark for %s finished", jobName));
        }

        public void log(String str) throws IOException {
            if (log != null) {
                log.append(str + "\n");
            }
        }

        private double executeJob(String dirName, String jobName) throws Exception {
            long start = System.currentTimeMillis();

            deleteDir(pathTo(dirName));
            Class<?> klass = Class.forName(jobName);
            Method m = klass.getDeclaredMethod("execute");
            Boolean result = (Boolean) m.invoke(null, null);
            if (result) {
                long end = System.currentTimeMillis();
                return (end - start) / 1000;
            }
            return 0;
        }

        private String pathTo(String dirName) {
            return String.format("mail/out/%s/", dirName);
        }

        private boolean deleteDir(String dirName) {
            return deleteDir(new File(dirName));
        }

        // http://www.exampledepot.com/egs/java.io/DeleteDir.html
        private boolean deleteDir(File dir) {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i=0; i<children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            // The directory is now empty so delete it
            return dir.delete();
        }

    }

}
