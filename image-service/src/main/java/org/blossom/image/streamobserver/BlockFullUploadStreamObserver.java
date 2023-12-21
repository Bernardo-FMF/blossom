package org.blossom.image.streamobserver;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import org.blossom.image.s3.S3Service;
import org.blossom.image.util.KeyEncoder;
import org.blossom.imagecontract.Block;
import org.blossom.imagecontract.Identifier;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Log4j2
public class BlockFullUploadStreamObserver implements StreamObserver<Block>, BlockStreamObserver {
    private final StreamObserver<Identifier> responseStreamObserver;
    private final S3Service s3Service;
    private final KeyEncoder keyEncoder;

    private final ByteArrayOutputStream contentStream;
    private String originalName;
    private String contentType;

    public BlockFullUploadStreamObserver(StreamObserver<Identifier> responseStreamObserver, S3Service s3Service, KeyEncoder keyEncoder) {
        this.responseStreamObserver = responseStreamObserver;
        this.s3Service = s3Service;
        this.keyEncoder = keyEncoder;
        this.contentStream = new ByteArrayOutputStream();
    }

    @Override
    public void onNext(Block block) {
        log.info("Processing byte block for blob {}", block.getBlobName());
        if (originalName == null) {
            log.info("Processing first block byte block for blob {}", block.getBlobName());
            this.originalName = block.getBlobName();
            this.contentType = block.getContentType();
        }
        ByteString blockContent = block.getContent();
        try {
            log.info("Writing content stream for blob {}", block.getBlobName());
            blockContent.writeTo(contentStream);
            log.info("Finished writing content stream for blob {}", block.getBlobName());
        } catch (IOException ex) {
            log.error("Error writing content stream for blob {}", block.getBlobName(), ex);
            responseStreamObserver.onError(ex);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.info("Error uploading blob {}", originalName);
        try {
            contentStream.close();
        } catch (IOException ex) {
            log.error("Error closing content stream for blob {}", originalName, ex);
        }
    }

    @Override
    public void onCompleted() {
        try {
            String url = s3Service.putObject(
                    keyEncoder.generateKey(KeyEncoder.Encoder.BASE64, KeyEncoder.Appender.TIMESTAMP, this.originalName) + "." + this.contentType,
                    contentStream.toByteArray());

            responseStreamObserver.onNext(Identifier.newBuilder().setUrl(url).build());
            responseStreamObserver.onCompleted();
        } catch (AwsServiceException | SdkClientException ex) {
            log.error("Error uploading blob {}", originalName, ex);
            responseStreamObserver.onError(ex);
        }
    }
}
