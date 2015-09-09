
package com.hmkcode;
// package uk.ac.rdg.resc.ncwms.config;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;



import com.amazonaws.auth.profile.ProfilesConfigFile;

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

// JA

/*
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
*/

class S3Browser {

    // See, http://stackoverflow.com/questions/5455284/how-can-i-get-only-one-level-of-objects-in-a-s3-bucket

    private final AmazonS3 s3; 
    private final String bucketName;

    public S3Browser( AmazonS3 s3, String bucketName ) { 
        this.s3 = s3; 
        this.bucketName = bucketName;
    }   

    // http://stackoverflow.com/questions/813710/java-1-6-determine-symbolic-links
    public static boolean isSymlink(File file) throws IOException {
        if (file == null)
            throw new NullPointerException("File must not be null");
        File canon;
        if (file.getParent() == null) {
          canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }


    public S3Browser( String credentialsPath, String profileName, String bucketName ) { 

        this.bucketName = bucketName;
        AWSCredentials credentials = null;
        try {
            File file = new File( credentialsPath );

            System.out.println("\n******");
            System.out.println("file '" + file + "'" );

            System.out.println("canonical file '" + file.getCanonicalFile() + "'" );
            System.out.println("absolute file '" + file.getAbsoluteFile() + "'" );

            if(file.exists() ) {  
                System.out.println("JA file exists ");
            } else {
                System.out.println("JA file doesn't exist");
            }

            if(!file.isDirectory()) {  
                System.out.println("JA file not a directory");
            } else {
                System.out.println("JA is a directory");
            }

            if( isSymlink(file)) {
                System.out.println("It's a fucking symbolic link '" );
            } else {
                System.out.println("It's not a fucking symbolic link '" );
            } 
            
/*            if( file.isSymbolicLink()) {
                System.out.println("JA file is a symbollic link");
            } 
 */         

            ProfilesConfigFile config = new ProfilesConfigFile( file );
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

    static private String tidyListingPath( String path )
    {   

        // this shit is horrible. it would be much nicer to handle 
        if( path.equals("/")) {
            return ""; 
        }
        
        while( !path.isEmpty() && path.charAt(0) == '/') {
            path = path.substring(1);
        }   

        // we have to have a '/' on the end to enforce, things being children. 
        if( path.isEmpty() || path.charAt(path.length() -1) != '/') {
            path = path + '/';
        }   
        return path;
    }   

    private ObjectListing getListing( String path )
    {   
        path = tidyListingPath( path);

        return s3.listObjects(new ListObjectsRequest()
            .withBucketName(bucketName)
           .withPrefix(path)
           .withDelimiter("/")
        );  
    }   

    List<String> getDirs( String path )
    {   
        ObjectListing objectListing = getListing( path );

        System.out.println("getting dirs for " + path );
        return objectListing.getCommonPrefixes();
    }   

    // TODO change all paths to keys

    List<String> getFiles( String path )
    {   
        ObjectListing objectListing = getListing( path );

        System.out.println("getting files for " + path );

        List<S3ObjectSummary> summaries = objectListing.getObjectSummaries();
        System.out.println("size " + summaries.size());
        List<String> whoot = new ArrayList<String>();

        for( S3ObjectSummary summary : summaries ) {
            whoot.add( summary.getKey());
        }
        return whoot;
    }


    InputStream getObject( String key ) { 
        // throw or return null....
        // so we have to work with a file ...  


        while( !key.isEmpty() && key.charAt(0) == '/') {
            key = key.substring(1);
        }   

        
        S3Object object = s3.getObject( new GetObjectRequest(bucketName, key));
        InputStream objectData = object.getObjectContent();

        // Process the objectData stream.
        // objectData.close();
        return objectData; 
    }

}

