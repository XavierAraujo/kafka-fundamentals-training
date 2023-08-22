package com.araujo.xavier.kafka.fundamentals.training;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
@Profile("consumer")
public class TransactionsConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionsConsumerApplication.class, args);
		while(true) {} // Hack to stop the application from terminating since we have no spring web-server to keep it running
	}

	@Bean(initMethod = "start")
	public TransactionsConsumer transactionsConsumer(@Value("${kafka.topics.transactions}") String topic) throws IOException {
		Properties properties = new Properties();
		properties.load(TransactionsConsumerApplication.class.getClassLoader().getResourceAsStream("consumer.properties"));
		return new TransactionsConsumer(topic, properties);
	}

}
