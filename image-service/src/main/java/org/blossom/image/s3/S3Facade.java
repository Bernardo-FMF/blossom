package org.blossom.image.s3;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;

public class S3Facade implements S3Client {
    @Value("${aws.s3.path}")
    private String path;

    private static final String KEY_PREFIX = "amazonaws.com/";

    @Override
    public String serviceName() {
        return "S3FacadeClient";
    }

    @Override
    public void close() {
        // Do nothing
    }

    @Override
    public PutObjectResponse putObject(PutObjectRequest putObjectRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException {
        InputStream inputStream = requestBody.contentStreamProvider().newStream();

        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            FileUtils.writeByteArrayToFile(new File(buildObjectFullPath(putObjectRequest.bucket(), putObjectRequest.key())), bytes);
            return PutObjectResponse.builder().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DeleteObjectResponse deleteObject(DeleteObjectRequest deleteObjectRequest) throws AwsServiceException, SdkClientException, S3Exception {
        File fileToDelete = new File(buildObjectFullPath(deleteObjectRequest.bucket(), extractKey(deleteObjectRequest.key())));

        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                return DeleteObjectResponse.builder().build();
            } else {
                throw new RuntimeException("Failed to delete file: " + fileToDelete.getAbsolutePath());
            }
        } else {
            throw new RuntimeException("File not found: " + fileToDelete.getAbsolutePath());
        }
    }

    private String extractKey(String key) {
        return key.substring(key.indexOf(KEY_PREFIX) + KEY_PREFIX.length());
    }

    @Override
    public ResponseInputStream<GetObjectResponse> getObject(
            GetObjectRequest getObjectRequest)
            throws  AwsServiceException, SdkClientException {

        try {
            FileInputStream fileInputStream = new FileInputStream(
                    buildObjectFullPath(
                            getObjectRequest.bucket(),
                            getObjectRequest.key())
            );
            return new ResponseInputStream<>(
                    GetObjectResponse.builder().build(),
                    fileInputStream
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildObjectFullPath(String bucketName, String key) {
        return path + "/" + bucketName + "/" + key;
    }
}
