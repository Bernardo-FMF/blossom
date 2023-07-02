package org.blossom.auth.grpc.streamobserver;

import com.google.protobuf.BoolValue;
import io.grpc.stub.StreamObserver;

public class ImageDeletionStreamObserver implements StreamObserver<BoolValue> {
    private boolean deletionResult;

    @Override
    public void onNext(BoolValue boolValue) {
        deletionResult = boolValue.getValue();
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }

    public boolean getDeletionResult() {
        return deletionResult;
    }
}
