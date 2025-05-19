package com.araujo.xavier.kafka.fundamentals.training;

import com.araujo.xavier.kafka.fundamentals.training.serialization.JacksonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.Timestamp;
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

        new Thread(() -> {
            try (Serializer<String> keySerializer = new StringSerializer();
                 Serializer<AccountTransaction> valueSerializer = new JacksonSerializer<>();
                 KafkaProducer<String, AccountTransaction> kafkaProducer = new KafkaProducer<>(properties, keySerializer, valueSerializer)) {
                while (true) {
                    AccountTransaction accountTransaction = generateRandomTransactionForAccount();
                    ProducerRecord<String, AccountTransaction> record = transactionToProducerRecord(topic, accountTransaction);
                    log.info("producing account transaction {}", accountTransaction);
                    kafkaProducer.send(record);

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
            }
        }).start();

    }

    private ProducerRecord<String, AccountTransaction> transactionToProducerRecord(String topic, AccountTransaction accountTransaction) {
        return new ProducerRecord<>(topic, accountTransaction.accountId(), accountTransaction);
    }

    private AccountTransaction generateRandomTransactionForAccount() {
        return new AccountTransaction(
                String.valueOf(ThreadLocalRandom.current().nextLong(0, 10)),
                UUID.randomUUID().toString(),
                new Timestamp(System.currentTimeMillis()).getTime(),
                ThreadLocalRandom.current().nextLong(-100, 100)
        );
    }
}
