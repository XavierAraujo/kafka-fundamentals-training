package com.araujo.xavier.kafka.fundamentals.training;

public record AccountTransaction(String accountId, String transactionId, long timestamp, long value) {
}
