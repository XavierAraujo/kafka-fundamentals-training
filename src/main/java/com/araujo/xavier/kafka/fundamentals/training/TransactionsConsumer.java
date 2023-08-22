package com.araujo.xavier.kafka.fundamentals.training;

import com.araujo.xavier.kafka.fundamentals.training.serialization.JacksonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
public class TransactionsConsumer {

    private final Duration consumerTimeoutSec = Duration.ofSeconds(5);

    private final String topic;

    private final Properties properties;

    TransactionsConsumer(String topic, Properties properties) {
        this.topic = topic;
        this.properties = properties;
    }

    private void start() {
        log.info("Initializing consumer for topic {} and consumer-group {}", topic, properties.get("group.id"));

        new Thread(() -> {
            Deserializer<String> keyDeserializer = new StringDeserializer();
            Deserializer<AccountTransaction> valueDeserializer = new JacksonDeserializer<>(AccountTransaction.class);
            KafkaConsumer<String, AccountTransaction> kafkaConsumer = new KafkaConsumer<>(properties, keyDeserializer, valueDeserializer);
            while (true) {
                kafkaConsumer.subscribe(List.of(topic));
                ConsumerRecords<String, AccountTransaction> records = kafkaConsumer.poll(consumerTimeoutSec);
                for (ConsumerRecord<String, AccountTransaction> record : records) {
                    AccountTransaction accountTransaction = record.value();
                    handleAccountTransaction(accountTransaction);
                }
            }
        }).start();
    }

    private void handleAccountTransaction(AccountTransaction accountTransaction) {
        log.info("handling account transaction {}", accountTransaction);
    }
}
