package org.blossom.image.service;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.blossom.image.AbstractContextBeans;
import org.blossom.imagecontract.Block;
import org.blossom.imagecontract.Identifier;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ImageServiceTest extends AbstractContextBeans {
    private static final List<Identifier> result = new ArrayList<>();

    @Order(1)
    @Test
    void uploadImage_successful() throws InterruptedException, IOException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamObserver<Identifier> responseObserver = buildIdentifierStreamObserverForTesting(result, countDownLatch);
        StreamObserver<Block> blockStreamObserver = mockGrpcClient.getNonBlockingStub().submitImage(responseObserver);

        ArgumentCaptor<RequestBody> requestBodyArgumentCaptor = ArgumentCaptor.forClass(RequestBody.class);

        Block block = Block
                .newBuilder()
                .setContent(ByteString.copyFrom("TestByteString", Charset.defaultCharset()))
                .setContentType("ContentType")
                .setBlobName("FileName")
                .build();

        blockStreamObserver.onNext(block);
        blockStreamObserver.onCompleted();

        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);

        Mockito.verify(s3Client).putObject(Mockito.any(PutObjectRequest.class), requestBodyArgumentCaptor.capture());

        try (InputStream inputStream = requestBodyArgumentCaptor.getValue().contentStreamProvider().newStream()) {
            byte[] bytes = inputStream.readAllBytes();

            Assertions.assertArrayEquals(block.getContent().toByteArray(), bytes);
            Assertions.assertEquals(1, result.size());
            Assertions.assertNotNull(result.get(0).getUrl());
        }
    }

    @Order(2)
    @Test
    void deleteImage_successful() throws InterruptedException, IOException {
        Identifier identifier = Identifier.newBuilder().setUrl(result.get(0).getUrl()).build();

        ArgumentCaptor<DeleteObjectRequest> requestBodyArgumentCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);

        BoolValue boolValue = mockGrpcClient.getBlockingStub().deleteImage(identifier);

        Mockito.verify(s3Client).deleteObject(requestBodyArgumentCaptor.capture());

        Assertions.assertTrue(boolValue.getValue());

        Assertions.assertEquals(result.get(0).getUrl(), requestBodyArgumentCaptor.getValue().key());
    }

    private StreamObserver<Identifier> buildIdentifierStreamObserverForTesting(final Collection<Identifier> identifierResponses, final CountDownLatch countDownLatch) {
        return new StreamObserver<>() {
            @Override
            public void onNext(Identifier identifierResponse) {
                identifierResponses.add(identifierResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                Assertions.fail("There was an error in the Upload StreamObserver used as response", throwable);
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        };
    }
}