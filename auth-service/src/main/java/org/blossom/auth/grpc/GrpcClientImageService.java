package org.blossom.auth.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import org.blossom.auth.grpc.streamobserver.IdentifierStreamObserver;
import org.blossom.facade.ImageContractGrpcClientFacade;
import org.blossom.imagecontract.Block;
import org.blossom.imagecontract.Identifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class GrpcClientImageService {
    public static final int BUFFER_SIZE = 1024;

    @Lazy
    private final ImageContractGrpcClientFacade grpcClient;

    public String uploadImage(MultipartFile file) throws IOException, InterruptedException {
        String fileName = file.getOriginalFilename();
        String contentType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        byte[] buffer = new byte[BUFFER_SIZE];

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        IdentifierStreamObserver identifierStreamObserver = new IdentifierStreamObserver(countDownLatch);

        StreamObserver<Block> blockStreamObserver = grpcClient.getNonBlockingStub().submitImage(identifierStreamObserver);

        try (InputStream input = file.getInputStream()) {
            while (input.read(buffer) >= 0) {
                try {
                    Block block = Block
                            .newBuilder()
                            .setContent(ByteString.copyFrom(buffer))
                            .setContentType(contentType)
                            .setBlobName(fileName)
                            .build();
                    blockStreamObserver.onNext(block);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            blockStreamObserver.onCompleted();
        }

        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);

        return await ? identifierStreamObserver.getUrl() : null;
    }

    public void deleteImage(String imageUrl) {
        grpcClient.getBlockingStub().deleteImage(Identifier.newBuilder().setUrl(imageUrl).build());
    }
}
