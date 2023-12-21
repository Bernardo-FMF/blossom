package org.blossom.image.s3;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Log4j2
public class S3Config {
    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.mock}")
    private boolean mock;

    @Bean
    public S3Client s3Client() {
        if (mock) {
            log.info("Building file system S3 facade");
            return new S3Facade();
        }

        log.info("Building S3 client");
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .build();
    }

    public String buildUrl(String bucketName, String key) {
        return "https://" + bucketName + ".s3." + awsRegion + ".amazonaws.com/" + key;
    }
}
