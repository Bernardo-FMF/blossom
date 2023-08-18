package org.blossom.grpc.streamobserver;

import io.grpc.stub.StreamObserver;
import org.blossom.imagecontract.Identifier;

import java.util.concurrent.CountDownLatch;

public class IdentifierStreamObserver implements StreamObserver<Identifier> {
    private final CountDownLatch countDownLatch;

    private String url;

    public IdentifierStreamObserver(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void onNext(Identifier submissionResponse) {
        if (url == null) {
            this.url = submissionResponse.getUrl();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        countDownLatch.countDown();
    }

    @Override
    public void onCompleted() {
        countDownLatch.countDown();
    }

    public String getUrl() {
        return this.url;
    }
}