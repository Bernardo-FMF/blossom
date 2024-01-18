package org.blossom.future;

import org.jetbrains.annotations.NotNull;

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
    public void onFailure(@NotNull Throwable throwable) {
        failureConsumer.accept(throwable);
    }
}
