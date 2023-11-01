package org.blossom.post.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.blossom.post.grpc.client.ImageContractGrpcClientFacade;
import org.blossom.post.grpc.streamobserver.IdentifierStreamObserver;
import org.blossom.imagecontract.Block;
import org.blossom.imagecontract.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class GrpcClientImageService {
    public static final int BUFFER_SIZE = 1024;

    @Autowired
    private ImageContractGrpcClientFacade grpcClient;

    public String[] uploadImages(MultipartFile[] mediaFiles) throws IOException, InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(mediaFiles.length);
        Queue<IdentifierStreamObserver> mediaStreams = new ConcurrentLinkedQueue<>();

        for (MultipartFile file: mediaFiles) {
            String fileName = file.getOriginalFilename();
            String contentType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            byte[] buffer = new byte[BUFFER_SIZE];

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

            mediaStreams.add(identifierStreamObserver);
        }


        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);

        return await ? mediaStreams.stream().map(IdentifierStreamObserver::getUrl).toArray(String[]::new) : null;
    }

    public void deleteImages(String[] mediaUrls) {
        for (String media: mediaUrls) {
            grpcClient.getBlockingStub().deleteImage(Identifier.newBuilder().setUrl(media).build());
        }
    }
}
