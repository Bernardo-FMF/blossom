package org.blossom.image.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3Service {
    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Config s3Configuration;

    @Autowired
    private S3Bucket s3Buckets;

    public String putObject(String key, byte[] byteContent) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Buckets.getImageBucket())
                .key(key)
                .build();
        s3Client.putObject(objectRequest, RequestBody.fromBytes(byteContent));

        return s3Configuration.buildUrl(s3Buckets.getImageBucket(), key);
    }

    public boolean deleteObject(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Buckets.getImageBucket())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }
}
