package org.blossom.future;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KafkaFutureExecutor {
    private final KafkaTemplate<String, ResourceEvent> kafkaTemplate;
    private final Map<ResourceType, List<String>> topicMap;

    private final ScheduledExecutorService threadPoolExecutor;

    public KafkaFutureExecutor(KafkaTemplate<String, ResourceEvent> kafkaTemplate, Map<ResourceType, List<String>> topicMap) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicMap = topicMap;
        this.threadPoolExecutor = new ScheduledThreadPoolExecutor(5);
    }

    public KafkaFutureExecutor(KafkaTemplate<String, ResourceEvent> kafkaTemplate, Map<ResourceType, List<String>> topicMap, ScheduledExecutorService threadPoolExecutor) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicMap = topicMap;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void execute(ResourceEvent resourceEvent, int delay, FutureCallback<Void> callback) throws ExecutionException, InterruptedException {
        ListenableFuture<Void> future = Futures.scheduleAsync(() -> {
            List<String> topics = topicMap.get(resourceEvent.getResourceType());
            topics.forEach(topic -> kafkaTemplate.send(topic, resourceEvent));

            return Futures.scheduleAsync(Futures::immediateVoidFuture, delay, TimeUnit.SECONDS, threadPoolExecutor);
        }, 2, TimeUnit.SECONDS, threadPoolExecutor);

        Futures.addCallback(future, callback, threadPoolExecutor);

        future.get();
    }
}
