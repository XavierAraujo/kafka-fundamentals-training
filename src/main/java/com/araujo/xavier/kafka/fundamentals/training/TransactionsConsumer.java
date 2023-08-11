package com.araujo.xavier.kafka.fundamentals.training;

import lombok.extern.slf4j.Slf4j;

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
        // TODO: Implement a basic Kafka consumer
    }
}
