# Conduktor Technical Assignment

## Table of Contents
* [Overview](#overview)
* [Requirements](#requirements)
* [Technical Requirements](#technical-requirements)
* [Product Requirements](#product-requirements)
* [Implementation](#implementation)
* [Technologies Used](#technologies-used)
* [How to Run Locally](#how-to-run-locally)
* [Running Tests](#running-tests)

## Overview

Given the file (random-people-data.json, attached to the mail) of 500 randomly generated records
representing people, load these records into kafka (one message per record in the file) and create a
simple server application which can serve (read) the records from a Kafka topic via an API.

## Requirements

The records should be loaded into kafka as JSON, such that:

- [x] The key for the Kafka record is the _id field from a record, as a string
- [x] The value for the Kafka record is the whole person record as-is
- [x] Each person is stored in a separate message (i.e. 500 messages, not 1 message with the whole
file!)

### Technical Requirements

- [x] The service should be written on the JVM, using a statically typed JVM language (Java, Kotlin,
Scala etc.)
- [x] The topic used should have 3 partitions, and use log.cleanup.policy=delete (i.e. not
compaction). More info [here](https://www.conduktor.io/kafka/kafka-topic-configuration-log-compaction/).
- [x] You should use the Java client libraries from Kafka, we have a guide [here](https://www.conduktor.io/kafka/java-kafka-programming/).
- [x] You are free to use libraries and frameworks as you see fit, but be prepared to explain your
choices!
- [x] The read of kafka should NOT commit offsets. The java doc for the kafka consumer has
information on this [here](https://kafka.apache.org/31/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html).
- [x] No authentication is required - please feel free to use the default plain protocol to talk to kafka
(this is the default for both the quick-starts provided), and no auth on the API is also fine.

The Kafka client (KafkaConsumer) has two ways of consuming from topics:
1. Via the subscribe method - this creates a consumer group, which is designed for long running
   streaming processes and horizontal scaling. See documentation [here](https://kafka.apache.org/37/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html#subscribe(java.util.regex.Pattern)).
2. Via the assign and seek methods, which are lower level and faster - but have no scaling, and
   you must explicitly assign and position yourself to all topic partitions. See assign documentation [here](https://kafka.apache.org/37/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html#assign(java.util.Collection)) 
   and seek documentation [here](https://kafka.apache.org/37/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html#seek(org.apache.kafka.common.TpicPartition,long)).
  
**We would like you to use method 2 (assign and seek) for this exercise.** All topic partitions must be assigned in one call, but the seeks can be done per-partition. All of this must be done before reading via poll.
The offset for kafka is local to a partition - so a consumer has an independent offset for each of the three partitions in this exercise. When say the API is asked to read from offset 10, for this exercise this means start from offset 10 for each partition - there is no global offset for a topic! The poll will then read what it  wants from any partition, starting at 10 for each.

### Product Requirements

The service must provide two features:
- [x] The ability to run a one-off function (can be a main method somewhere, can be run from an IDE) which loads the 500 records in the random people file into kafka, as per the above.
- [x] A rest like HTTP API which implements a single method for now:
  * GET /topic/[topic_name]/[offset]?count=N 
  * This will return the next N records the consumer returns from the kafka topic topic_name, starting from offset offset for each partition in the topic 
  * Allow sensible defaults for offset and N 
  * The format for the data returned is left to you!

Finally, the service must log what it is doing.

## Implementation

### Technologies Used
* Java
* Kafka
* Spring Boot

### How to Run Locally

To run the service locally first start Kafka using the following command:
```
docker run -p 9092:9092 apache/kafka:3.7.0
```

Then run the service from the Main class. This will ensure the GET /topic/[topic_name]/[offset]?count=N endpoint is available. This can be hit using the following command from your terminal (update the topic_name, offset and count as required):
```
curl -X GET "http://localhost:8080/topic/test-topic/100?count=5"
```

### Running Tests

Unit tests have been added and these can be ran from each test file.