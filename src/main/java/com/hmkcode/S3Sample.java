/*
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.hmkcode;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;
import java.util.List;



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

class S3Browser {

    private final AmazonS3 s3; 
    private final String bucketName; 

    public S3Browser( AmazonS3 s3, String bucketName ) {
        this.s3 = s3; 
        this.bucketName = bucketName;
    } 

    List<String> get( String path )
    {
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
            .withBucketName(bucketName)
           // .withPrefix("home/meteo/")   // 
           .withPrefix(path)
           .withDelimiter("/")
        )
        ;
        System.out.println("getting common prefixes");

        return objectListing.getCommonPrefixes();
    }


}


public class S3Sample {

    static void recurse( S3Browser browser, String path )
    {
        for (String key : browser.get(path)) {
            System.out.println(" - " + key );
            recurse( browser, key );
        }
    }


    public static void main(String[] args) throws IOException {

        System.out.println("main()");

        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

        // looking up a single entry... 

        System.out.println("connecting");

        AmazonS3 s3 = new AmazonS3Client(credentials);
        System.out.println("done connecting");
        // System.out.flush();


        String bucketName = "imos-test-data-1" ;

        S3Browser browser = new S3Browser( s3, bucketName );

        recurse( browser, "home" );

/*
        // should write a recursive version ... that drills down... 
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(bucketName)
               .withPrefix("home/meteo/")   // 
               .withDelimiter("/")
            )
        ;
*/



    }
}

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

