#!/bin/bash

kafka-topics.sh --bootstrap-server 127.0.0.1:9092 --create --topic transactions --partitions 3 --replication-factor 1
kafka-topics.sh --bootstrap-server 127.0.0.1:9092 --create --topic blippers --partitions 10 --replication-factor 1
kafka-topics.sh --bootstrap-server 127.0.0.1:9092 --create --topic loans --partitions 5 --replication-factor 1

kafka-configs.sh  --bootstrap-server 127.0.0.1:9092 --alter --topic transactions --add-config retention.ms=1209600000
