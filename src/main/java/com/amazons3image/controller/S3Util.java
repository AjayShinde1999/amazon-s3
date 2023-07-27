package com.amazons3image.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;


public class S3Util {
    private static final String BUCKET = "3kt-bucket-1";

    public static void uploadFile(String fileName, InputStream inputStream)
            throws S3Exception, AwsServiceException, SdkClientException, IOException {
        S3Client client = S3Client.builder().build();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key(fileName)
                .contentType("image/jpeg")
                .acl("public-read")
                .build();

        client.putObject(request,
                RequestBody.fromInputStream(inputStream, inputStream.available()));

        S3Waiter waiter = client.waiter();
        HeadObjectRequest waitRequest = HeadObjectRequest.builder()
                .bucket(BUCKET)
                .key(fileName)
                .build();

        WaiterResponse<HeadObjectResponse> waitResponse = waiter.waitUntilObjectExists(waitRequest);

        waitResponse.matched().response().ifPresent(x -> {
        });
    }


    public static InputStream downloadFile(String fileName) throws S3Exception, AwsServiceException, SdkClientException, IOException {
        S3Client client = S3Client.builder()
                .region(software.amazon.awssdk.regions.Region.EU_NORTH_1) // Replace with the appropriate region for your S3 bucket
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(fileName)
                .build();

        ResponseInputStream<?> response = client.getObject(request);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = response.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        response.close();
        client.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

}
