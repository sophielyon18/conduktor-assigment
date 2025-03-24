package org.conduktor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.conduktor.config.KafkaConsumerConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class ConduktorConsumer {

    private KafkaConsumerConfig consumerConfig;
    private final long POLL_INTERVAL = 100;

    public List<String> listen(String topicName, long offset, int count) {
        List<String> messages = new ArrayList<>();
        Consumer<String, String> consumer = consumerConfig.configureConsumer();

        List<TopicPartition> partitions = consumer.partitionsFor(topicName).stream()
                .map(info -> new TopicPartition(topicName, info.partition()))
                .collect(Collectors.toList());

        consumer.assign(partitions);

        for (TopicPartition partition : partitions) {
            consumer.seek(partition, offset);
        }

        int fetchedMessages = 0;
        while (fetchedMessages < count) {
            ConsumerRecords<String, String> records = consumer.poll(POLL_INTERVAL);

            for (ConsumerRecord<String, String> record : records) {
                if (fetchedMessages < count) {
                    log.info("Message fetched by consumer: {}", record.value());
                    messages.add(record.value());
                    fetchedMessages++;
                }
            }
        }

        consumer.close();
        return messages;
    }
}
