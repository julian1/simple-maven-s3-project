

package com.hmkcode;


import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import java.io.InputStream;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import com.amazonaws.auth.profile.ProfilesConfigFile;

import com.hmkcode.S3Browser;



public class S3Sample {

    static void recurse( S3Browser browser, String path ) throws IOException
    {

        // rename dir to dir,, 
        for (String dir : browser.getDirs(path)) {
            System.out.println(" - " + dir );
            recurse( browser, dir );

            for( String file : browser.getFiles( dir )) {
                System.out.println(" opening " + file );

                InputStream is = browser.getObject( "/" + file ); 

                is.close();
 
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

