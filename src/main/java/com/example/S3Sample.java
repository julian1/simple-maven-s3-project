
package com.example;

import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.S3Browser;
import com.example.S3ToFileAdaptor;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;


class SimpleThreadPool {
  // TODO do we even need this...

   ExecutorService executor ;

    public SimpleThreadPool() {
        executor = Executors.newFixedThreadPool(15);
    }

    public void post( Runnable worker )
    {
        executor.execute(worker);
    }

    public void waitForCompletion()
    {
        executor.shutdown();
        while (!executor.isTerminated()) {
            // Thread.sleep(1000ms) etc ...
        }
    }
}


class WorkerThread implements Runnable {

    private S3ToFileAdaptor s3ToFileAdaptor;
    private String file;

    public WorkerThread( S3ToFileAdaptor s3ToFileAdaptor, String s){
        this.s3ToFileAdaptor = s3ToFileAdaptor;
        this.file=s;
    }

    @Override
    public void run() {
        // System.out.println(Thread.currentThread().getName()+" Start. Command = "+file);
        // processCommand();
        try {

            String f = s3ToFileAdaptor.getObjectFilename( file );
            s3ToFileAdaptor.closeFile( f );

/*
            extract as in-memory buffer
            byte [] buf = s3ToFileAdaptor.getObjectBytes( file );
*/

        } catch( IOException e )
        {
            e.printStackTrace();
        }
        // System.out.println(Thread.currentThread().getName()+" End.");
    }

    @Override
    public String toString(){
        return this.file;
    }
}



public class S3Sample {

    // TODO this function should be in the pool,

    static void recurse(
        SimpleThreadPool pool,
        S3Browser browser,
        S3ToFileAdaptor s3ToFileAdaptor,
        String path )
        throws IOException
    {
        for (String dir : browser.getDirs(path)) {
            System.out.println(" - " + dir );
            recurse( pool, browser, s3ToFileAdaptor, dir );
        }

        for( String file : browser.getFiles( path)) {
            System.out.println(" opening " + file );
    //        pool.post( new WorkerThread( s3ToFileAdaptor, file ) );
        }
    }

    public static void main(String[] args) throws IOException {

        SimpleThreadPool pool = new  SimpleThreadPool();

        // AmazonS3 s3 = new S3Authenticate().doit("./aws_credentials" , "default");
        AmazonS3 s3 = new AmazonS3Client();

        S3Browser browser = new S3Browser( s3, "imos-data" );//"./aws_credentials" , "default", "imos-data" ) ;

//        S3ToFileAdaptor s3ToFileAdaptor = new S3ToFileAdaptor( browser, "/tmp/ncwms" );
//        recurse( pool, browser, s3ToFileAdaptor, "" );
//        recurse( pool, browser, s3ToFileAdaptor, "/IMOS/ACORN/gridded_1h-avg-current-map_QC/ROT/2014/01" );
//        recurse( pool, browser, s3ToFileAdaptor, "/IMOS/SRS/sst/ghrsst/L3U-S/n19/2015" );

        recurse( pool, browser, null /*s3ToFileAdaptor*/, "/IMOS" );


        // recurse( pool, browser, "/" );

        System.out.println("waiting for completion" );
        pool.waitForCompletion();

        System.out.println("finished" );

    }
}


