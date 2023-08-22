package com.araujo.xavier.kafka.fundamentals.training;

import com.araujo.xavier.kafka.fundamentals.training.serialization.JacksonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class TransactionsProducer {

    private final String topic;

    private final Properties properties;

    TransactionsProducer(String topic, Properties properties) {
        this.topic = topic;
        this.properties = properties;
    }

    private void start() {
        log.info("Initializing producer for topic {}", topic);

        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getProperty("bootstrap.servers"));

        SenderOptions<String, AccountTransaction> senderOptions = SenderOptions
                .<String, AccountTransaction>create(producerProps)
                .withKeySerializer(new StringSerializer())
                .withValueSerializer(new JacksonSerializer<>())
                .maxInFlight(1024); // Controls back-pressure

		KafkaSender.create(senderOptions)
				.createOutbound()
                .send(Flux.interval(Duration.ofSeconds(1))
                        .map(ignored -> generateRandomTransaction())
                        .map(transaction -> new ProducerRecord<>(topic, transaction.accountId(), transaction)))
                .then()
				.doOnError(Throwable::printStackTrace)
				.subscribe();
    }

    private AccountTransaction generateRandomTransaction() {
        return new AccountTransaction(
                String.valueOf(ThreadLocalRandom.current().nextLong(0, 10)),
                UUID.randomUUID().toString(),
                Timestamp.from(Instant.now()).getTime(),
                ThreadLocalRandom.current().nextLong(-100, 100)
        );
    }
}
