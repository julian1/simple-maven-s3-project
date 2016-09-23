
package com.example;

import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.S3Browser;
import com.example.S3ToFileAdaptor;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;


// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

import org.apache.commons.cli.*;



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



/*
      // String to be scanned to find the pattern.
      String line = "This order was placed for QT3000! OK?";
      String pattern = "(.*)(\\d+)(.*)";

      // Create a Pattern object
      Pattern r = Pattern.compile(pattern);

      // Now create matcher object.
      Matcher m = r.matcher(line);
      if (m.find( )) {

*/




public class S3Sample {

    // TODO this function should be in the pool,

    // Pattern isFilePattern; 
    // use a context for all this?  or use this class as a context,

    // static string buf = "";
    static int count = 0;

    static void recurse(
        SimpleThreadPool pool,
        S3Browser browser,
        S3ToFileAdaptor s3ToFileAdaptor,
        String path )
        throws IOException
    {
        // we only use recursion on things that are not complete file

        // although we could set up the recursion to work in parallel,


        for (String dir : browser.getDirs(path)) {
            // System.out.println(" - " + dir );
            recurse( pool, browser, s3ToFileAdaptor, dir );
        }

        // get objects at the current level
        for( String file : browser.getFiles( path)) {

            // buf += ".";
            // System.out.print(  buf + '\r' );
            ++count;
            System.out.print( " count: " + String.format("%d", count)  + '\r' );

            // System.out.println(" got " + file );
            // System.out.println( file );
            // pool.post( new WorkerThread( s3ToFileAdaptor, file ) );
        }
    }



    public static void getConnection(Options options) { 


    }


    public static void main(String[] args) throws IOException {


        Options options = new Options();

        options.addOption("u", "username", true, "Database user.");
        options.addOption("p", "password", true, "Database password.");
        options.addOption("d", "db", true, "Database connection string.");
        options.addOption("D", "driver", true, "Database driver class.");



        SimpleThreadPool pool = new  SimpleThreadPool();

        // AmazonS3 s3 = new S3Authenticate().doit("./aws_credentials" , "default");
        AmazonS3 s3 = new AmazonS3Client();

        // S3Browser browser = new S3Browser( s3, "imos-data/IMOS/SRS/" );
        S3Browser browser = new S3Browser( s3, "imos-data" );

        recurse( pool, browser, null /*s3ToFileAdaptor*/, "/IMOS/SRS" ); 

        // recurse( pool, browser, "/" );

        System.out.println("waiting for completion" );
        pool.waitForCompletion();

        System.out.println("finished" );

    }
}


// imos-data/IMOS/SRS/
// S3ToFileAdaptor s3ToFileAdaptor = new S3ToFileAdaptor( browser, "/tmp/ncwms" );
// recurse( pool, browser, s3ToFileAdaptor, "" );
// recurse( pool, browser, s3ToFileAdaptor, "/IMOS/ACORN/gridded_1h-avg-current-map_QC/ROT/2014/01" );
// recurse( pool, browser, s3ToFileAdaptor, "/IMOS/SRS/sst/ghrsst/L3U-S/n19/2015" ); 

