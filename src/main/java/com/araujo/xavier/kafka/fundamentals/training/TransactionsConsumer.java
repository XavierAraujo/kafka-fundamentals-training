package com.araujo.xavier.kafka.fundamentals.training;

import com.araujo.xavier.kafka.fundamentals.training.serialization.JacksonDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class TransactionsConsumer {

    private final String topic;

    private final Properties properties;

    TransactionsConsumer(String topic, Properties properties) {
        this.topic = topic;
        this.properties = properties;
    }

    private void start() {
        log.info("Initializing consumer for topic {} and consumer-group {}", topic, properties.get("group.id"));

        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getProperty("bootstrap.servers"));
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, properties.get("group.id"));

        ReceiverOptions<String, AccountTransaction> receiverOptions =
                ReceiverOptions.<String, AccountTransaction>create(consumerProps)
                        .withKeyDeserializer(new StringDeserializer())
                        .withValueDeserializer(new JacksonDeserializer<>(AccountTransaction.class))
                        .maxDeferredCommits(100) // Allows for unordered commits! The framework will handle this out of the box.
                        .subscription(Collections.singleton(topic));

        KafkaReceiver.create(receiverOptions)
                .receive()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(r -> {
                    boolean successfulHandling = handleAccountTransaction(r.value());
                    if (successfulHandling) {
                        r.receiverOffset().commit().block(); // allows to control the way we commit offsets
                    }
                })
                .retryWhen(Retry.backoff(3, Duration.of(10L, ChronoUnit.SECONDS)))
                .subscribe();
    }

    private boolean handleAccountTransaction(AccountTransaction accountTransaction) {
        log.info("handling account transaction {}", accountTransaction);
        return true;
    }
}
