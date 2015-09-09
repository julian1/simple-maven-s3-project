

package com.hmkcode;


import java.util.List;
import java.util.ArrayList;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
// import java.lang.Exception; 

import java.io.IOException;


import com.hmkcode.S3Browser;


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



public class S3Sample {

    static void recurse( S3Browser browser, String path ) throws IOException
    {

        // rename dir to dir,, 
        for (String dir : browser.getDirs(path)) {
            System.out.println(" - " + dir );
            recurse( browser, dir );

            for( String file : browser.getFiles( dir )) {
                System.out.println(" opening " + file );


                S3ToFileAdaptor s3ToFileAdaptor = new S3ToFileAdaptor(browser);

                String f = s3ToFileAdaptor.getObject( file ); 

            }

        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println("main()");

        // S3Browser browser = new S3Browser( "/home/meteo/.aws/credentials" , "default", "imos-test-data-1" ) ;
        S3Browser browser = new S3Browser( "./aws_credentials" , "default", "imos-data" ) ;


        recurse( browser, "/IMOS/ACORN/gridded_1h-avg-current-map_QC/ROT/2014/01" );
    }
}



/*
        // should write a recursive version ... that drills down...
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(bucketName)
               .withPrefix("home/meteo/")   //
               .withDelimiter("/")
            )
        ;
*/


/*

        System.out.println("getting listing");

        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(bucketName)
               .withPrefix("home/meteo/")   //
               .withDelimiter("/")
            )
        ;
        System.out.println("done getting listing");


        // boolean
        System.out.println("found " + !objectListing.getObjectSummaries().isEmpty() );

        System.out.println("count " + objectListing.getObjectSummaries().size());

        System.out.println("Listing objects");
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey() + "  " +
                               "(size = " + objectSummary.getSize() + ")");
        }

        System.out.println("\ndone\n");
*/

