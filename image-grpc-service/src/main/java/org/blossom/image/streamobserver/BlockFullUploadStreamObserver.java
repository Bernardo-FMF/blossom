package org.blossom.image.streamobserver;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.blossom.image.s3.S3Service;
import org.blossom.image.util.KeyEncoder;
import org.blossom.imagecontract.Block;
import org.blossom.imagecontract.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
        if (originalName == null) {
            this.originalName = block.getBlobName();
            this.contentType = block.getContentType();
        }
        ByteString blockContent = block.getContent();
        try {
            blockContent.writeTo(contentStream);
        } catch (IOException e) {
            // Handle the exception if needed
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        String url = s3Service.putObject(
                keyEncoder.generateKey(KeyEncoder.Encoder.BASE64, KeyEncoder.Appender.TIMESTAMP, this.originalName) + "." + this.contentType,
                contentStream.toByteArray());

        responseStreamObserver.onNext(Identifier.newBuilder().setUrl(url).build());
        responseStreamObserver.onCompleted();
    }
}
