

package com.hmkcode;


import java.util.List;
import java.util.ArrayList;
import java.io.IOException;


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


class S3Browser {

    private final AmazonS3 s3; 
    private final String bucketName; 

    public S3Browser( AmazonS3 s3, String bucketName ) {
        this.s3 = s3; 
        this.bucketName = bucketName;
    } 

    public S3Browser( String credentialsPath, String profileName, String bucketName ) {
 
        this.bucketName = bucketName;
        AWSCredentials credentials = null;
        try {
 
            ProfilesConfigFile config = new ProfilesConfigFile( credentialsPath );
            ProfileCredentialsProvider provider = new ProfileCredentialsProvider(config, profileName); 
            credentials = provider.getCredentials();

        } catch (Exception e) {

            System.out.println("JA couldn't load credentials " + e.getMessage() );

            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

        System.out.println("authenticating");
        this.s3 = new AmazonS3Client(credentials);
        System.out.println("done authenticating");
    }

    List<String> getDirs( String path )   // change name to getDirs() or get getChildDirs() or getVirtualDirs() etc.
    {
        while( path.charAt(0) == '/') {
            path = path.substring(1);
        }

        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
            .withBucketName(bucketName)
           .withPrefix(path)
           .withDelimiter("/")
        );

        System.out.println("getting common prefixes for " + path );

        // VERY IMPORTANT 
        // we're not seeing the files here. where other examples seem to show this...
        return objectListing.getCommonPrefixes();
    }


    List<String> getFiles( String path )   // change name to getDirs() or get getChildDirs() or getVirtualDirs() etc.
    {
        while( path.charAt(0) == '/') {
            path = path.substring(1);
        }

        if( path.isEmpty() || path.charAt(path.length() -1) != '/') {
            path = path + '/';
        }


        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
            .withBucketName(bucketName)
           .withPrefix(path)
           .withDelimiter("/")
        );

        System.out.println("getting common prefixes for " + path );

        List<S3ObjectSummary> summaries = objectListing.getObjectSummaries();
        System.out.println("size " + summaries.size());
        List<String> whoot = new ArrayList<String>();

        for( S3ObjectSummary summary : summaries ) {
            whoot.add( summary.getKey());
        }
        return whoot;
    }

}


public class S3Sample {

    static void recurse( S3Browser browser, String path )
    {
        for (String key : browser.getDirs(path)) {
            System.out.println(" - " + key );
            recurse( browser, key );

            for( String file : browser.getFiles( key )) {
                System.out.println(" * " + file );
            } 

        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println("main()");

        S3Browser browser = new S3Browser( "/home/meteo/.aws/credentials" , "default", "imos-test-data-1" ) ;
        // S3Browser browser = new S3Browser( s3, bucketName );

        recurse( browser, "/home" );
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

