package org.blossom.future;

import jakarta.annotation.Nonnull;

import java.util.function.Consumer;

public class FutureCallback<T> implements com.google.common.util.concurrent.FutureCallback<T> {
    Runnable successRunnable;
    Consumer<Throwable> failureConsumer;

    public FutureCallback(Runnable successRunnable, Consumer<Throwable> failureConsumer) {
        this.successRunnable = successRunnable;
        this.failureConsumer = failureConsumer;
    }

    @Override
    public void onSuccess(Object o) {
        successRunnable.run();
    }

    @Override
    public void onFailure(@Nonnull Throwable throwable) {
        failureConsumer.accept(throwable);
    }
}
