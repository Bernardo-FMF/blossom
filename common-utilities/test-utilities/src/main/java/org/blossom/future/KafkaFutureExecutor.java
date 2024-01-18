package org.blossom.future;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.blossom.model.ResourceEvent;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KafkaFutureExecutor {
    private final KafkaTemplate<String, ResourceEvent> kafkaTemplate;
    private final List<String> topics;

    private final ScheduledExecutorService threadPoolExecutor;

    public KafkaFutureExecutor(KafkaTemplate<String, ResourceEvent> kafkaTemplate, List<String> topics) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
        this.threadPoolExecutor = new ScheduledThreadPoolExecutor(5);
    }

    public KafkaFutureExecutor(KafkaTemplate<String, ResourceEvent> kafkaTemplate, List<String> topics, ScheduledExecutorService threadPoolExecutor) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void execute(ResourceEvent resourceEvent, int delay, FutureCallback<Void> callback) throws ExecutionException, InterruptedException {
        ListenableFuture<Void> future = Futures.scheduleAsync(() -> {
            topics.forEach(topic -> kafkaTemplate.send(topic, resourceEvent));

            return Futures.scheduleAsync(Futures::immediateVoidFuture, delay, TimeUnit.SECONDS, threadPoolExecutor);
        }, 2, TimeUnit.SECONDS, threadPoolExecutor);

        Futures.addCallback(future, callback, threadPoolExecutor);

        future.get();
    }
}
