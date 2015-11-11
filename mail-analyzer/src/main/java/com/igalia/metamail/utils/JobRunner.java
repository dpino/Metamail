package com.igalia.metamail.utils;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Job;

/**
 *
 * @author Diego Pino García <dpino@igalia.com>
 *
 */
public class JobRunner {

    public static boolean run(Job job) throws IOException,
            InterruptedException, ClassNotFoundException {
        try {
            return job.waitForCompletion(true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
