package com.araujo.xavier.kafka.fundamentals.training;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
@Profile("producer")
public class TransactionsProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionsProducerApplication.class, args);
		while(true) {} // Hack to stop the application from terminating since we have no spring web-server to keep it running
	}

	@Bean(initMethod = "start")
	public TransactionsProducer transactionsProducer(@Value("${kafka.topics.transactions}") String topic) throws IOException {
		Properties properties = new Properties();
		properties.load(TransactionsProducerApplication.class.getClassLoader().getResourceAsStream("producer.properties"));
		return new TransactionsProducer(topic, properties);
	}

}
