package org.blossom.image.service;

import com.google.protobuf.BoolValue;
import io.grpc.stub.StreamObserver;
import org.blossom.image.s3.S3Service;
import org.blossom.image.streamobserver.BlockFullUploadStreamObserver;
import org.blossom.image.util.KeyEncoder;
import org.blossom.imagecontract.Block;
import org.blossom.imagecontract.Identifier;
import org.blossom.imagecontract.ImageContractGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageService extends ImageContractGrpc.ImageContractImplBase {
    @Autowired
    private S3Service s3Service;

    @Autowired
    private KeyEncoder keyEncoder;

    @Override
    public StreamObserver<Block> submitImage(StreamObserver<Identifier> responseObserver) {
        return new BlockFullUploadStreamObserver(responseObserver, s3Service, keyEncoder);
    }

    @Override
    public void deleteImage(Identifier identifier, StreamObserver<BoolValue> responseObserver) {
        boolean deletionResult = s3Service.deleteObject(identifier.getUrl());
        responseObserver.onNext(BoolValue.of(deletionResult));
        responseObserver.onCompleted();
    }
}
