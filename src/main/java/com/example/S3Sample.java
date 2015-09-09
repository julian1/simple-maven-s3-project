

package com.example;


import java.util.List;
import java.util.ArrayList;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
// import java.lang.Exception; 
import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.example.S3Browser;



 
class SimpleThreadPool {

   ExecutorService executor ;

    public SimpleThreadPool() {
        executor = Executors.newFixedThreadPool(10);
    }

    public void post( Runnable worker )
    {
        executor.execute(worker);
    }

    public void waitForCompletion()
    {
        executor.shutdown(); 
        while (!executor.isTerminated()) {
        }
    }
}


class S3ToFileAdaptor
{
    // S3 to file adaptor... -- look at the ncdf generator for example
    S3Browser browser;

    S3ToFileAdaptor( S3Browser browser ) {
        this.browser = browser; 
    }

    private static void copyStream(InputStream input, OutputStream output)
        throws IOException
    {
        // avoid dependency on org.apache.commons.io.IOUtils 
        byte[] buffer = new byte[16384]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1)
        {
            output.write(buffer, 0, bytesRead);
        }
    }

    String getObject( String key ) throws IOException  {
        // returns the filename

        System.out.println( "&&&&&&&&&&&\n JA S3ToFile getObject() " + key  ); 
  
        InputStream is = null; 
        OutputStream os = null; 
        String filename = "/tmp/ncwms/" + key.replace( "/", "-" ); // kiss 
        try { 
            is = browser.getObject( key );
            // should delete file first? or try without 
            os = new FileOutputStream( filename ); 
            copyStream( is, os); 
        }
        catch( IOException e ) {
            System.out.println( "&&&&&&&&&&&\n JA exception " + e.getMessage() ); 
            throw e;
        } finally { 
            is.close();
            os.close();
        }

        System.out.println( "&&&&&&&&&&&\n JA S3ToFile returning  " + filename ); 
        // should return a File structure
        return filename;
    }
}



class WorkerThread implements Runnable {
     
    private S3Browser browser;
    private String file;
     
    public WorkerThread(S3Browser browser, String s){
        this.browser = browser;
        this.file=s;
    }
 
    @Override
    public void run() {
        // System.out.println(Thread.currentThread().getName()+" Start. Command = "+file);
        // processCommand();
        try {
            S3ToFileAdaptor s3ToFileAdaptor = new S3ToFileAdaptor(browser);
            String f = s3ToFileAdaptor.getObject( file ); 
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

    static void recurse( SimpleThreadPool pool, S3Browser browser, String path ) throws IOException
    {
        for (String dir : browser.getDirs(path)) {
            System.out.println(" - " + dir );
            recurse( pool, browser, dir );

            for( String file : browser.getFiles( dir )) {
                System.out.println(" opening " + file );

                pool.post( new WorkerThread( browser, file ) );
/* 
                // ok, so we are going to have to just dispatch out ....
                S3ToFileAdaptor s3ToFileAdaptor = new S3ToFileAdaptor(browser);
                String f = s3ToFileAdaptor.getObject( file ); 
*/
            }
        }
    }

    public static void main(String[] args) throws IOException {

        SimpleThreadPool pool = new  SimpleThreadPool();

        // System.out.println("main()");

        S3Browser browser = new S3Browser( "./aws_credentials" , "default", "imos-data" ) ;

//        recurse( pool, browser, "/IMOS/ACORN/gridded_1h-avg-current-map_QC/ROT/2014/01" );

        recurse( pool, browser, "/" );



        System.out.println("waiting for completion" ); 
        pool.waitForCompletion();

        System.out.println("finished" ); 
 
    }
}


