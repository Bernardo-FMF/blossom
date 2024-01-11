package org.blossom.post.grpc.service;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import org.blossom.imagecontract.Block;
import org.blossom.imagecontract.Identifier;
import org.blossom.post.exception.FileDeleteException;
import org.blossom.post.exception.FileUploadException;
import org.blossom.post.grpc.client.ImageContractGrpcClientFacade;
import org.blossom.post.grpc.streamobserver.IdentifierStreamObserver;
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
@Log4j2
public class GrpcClientImageService {
    public static final int BUFFER_SIZE = 1024;

    @Autowired
    private ImageContractGrpcClientFacade grpcClient;

    public String[] uploadImages(MultipartFile[] mediaFiles) throws IOException, InterruptedException, FileUploadException {
        final CountDownLatch countDownLatch = new CountDownLatch(mediaFiles.length);
        Queue<IdentifierStreamObserver> mediaStreams = new ConcurrentLinkedQueue<>();

        for (MultipartFile file: mediaFiles) {
            String fileName = file.getOriginalFilename();
            if (fileName == null || file.isEmpty()) {
                log.info("Blob is not valid, has no name ({}) or content ({})", fileName == null, file.isEmpty());
                throw new FileUploadException("Blob has no name or content");
            }

            String contentType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            byte[] buffer = new byte[BUFFER_SIZE];

            IdentifierStreamObserver identifierStreamObserver = new IdentifierStreamObserver(countDownLatch);

            StreamObserver<Block> blockStreamObserver = grpcClient.getNonBlockingStub().submitImage(identifierStreamObserver);

            try (InputStream input = file.getInputStream()) {
                boolean firstBlock = true;
                while (input.read(buffer) >= 0) {
                    firstBlock = false;
                    Block block = Block
                            .newBuilder()
                            .setContent(ByteString.copyFrom(buffer))
                            .setContentType(contentType)
                            .setBlobName(fileName)
                            .build();
                    blockStreamObserver.onNext(block);
                }
                if (firstBlock) {
                    blockStreamObserver.onError(new FileUploadException("Blob has no content"));
                } else {
                    blockStreamObserver.onCompleted();
                }
            } catch (StatusRuntimeException ex) {
                log.error("Error on the grpc server, terminating upload of blob {}", fileName, ex);
                blockStreamObserver.onError(ex);
            } catch (Exception ex) {
                log.error("Error on the grpc client, terminating upload of blob {}", fileName, ex);
                blockStreamObserver.onError(ex);
            }

            mediaStreams.add(identifierStreamObserver);
        }


        boolean await = countDownLatch.await(30, TimeUnit.SECONDS);

        if (await) {
            return mediaStreams.stream().map(IdentifierStreamObserver::getUrl).toArray(String[]::new);
        }

        throw new FileUploadException("Blob upload failed");
    }

    public void deleteImages(String[] mediaUrls) throws FileDeleteException {
            for (String media: mediaUrls) {
                try {
                    BoolValue deleteResult = grpcClient.getBlockingStub().deleteImage(Identifier.newBuilder().setUrl(media).build());
                    if (deleteResult.getValue()) {
                        log.info("Deleted blob {} with success", media);
                    } else {
                        log.error("Error occurred while deleting blob {}", media);
                    }
                } catch (StatusRuntimeException ex) {
                    log.error("Error on the grpc server, terminating upload of blob {}", media, ex);
                    throw new FileDeleteException("Blob delete failed");
                }
            }
    }
}
