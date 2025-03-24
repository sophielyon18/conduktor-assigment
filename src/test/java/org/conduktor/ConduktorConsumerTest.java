package org.conduktor;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.conduktor.config.KafkaConsumerConfig;
import org.conduktor.config.TestsLogging;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConduktorConsumerTest extends TestsLogging {

    private static final String TOPIC_NAME = "test-topic";
    private static final long OFFSET = 10;
    private static final int COUNT = 2;

    @Mock
    private Consumer<String, String> consumer;

    @Mock
    private KafkaConsumerConfig consumerConfig;

    @InjectMocks
    private ConduktorConsumer testObj;

    @Test
    void listen_givenTopicNameOffsetAndCount_willReturnListOfMessages() {
        // Given
        String personOneAsString = "{\"_id\":\"1234\",\"name\":\"Bob\",\"dob\":\"12/12/12\",\"address\":{\"street\":\"Bobs street\",\"town\":\"Chester\",\"postode\":\"SW12 34G\"},\"telephone\":null,\"pets\":[\"cat\"],\"score\":12.0,\"email\":\"bob@email.com\",\"url\":\"url.com\",\"description\":\"Description of Bob\",\"verified\":false,\"salary\":100}";
        String personTwoAsString = "{\"_id\":\"5678\",\"name\":\"Bill\",\"dob\":\"10/10/10\",\"address\":{\"street\":\"Bills street\",\"town\":\"Manchester\",\"postode\":\"SW10 55F\"},\"telephone\":null,\"pets\":[\"cat\",\"dog\"],\"score\":10.0,\"email\":\"bill@email.com\",\"url\":\"url.com\",\"description\":\"Description of Bill\",\"verified\":false,\"salary\":100}";
        List<String> expectedMessages = List.of(personOneAsString, personTwoAsString);

        TopicPartition topicPartition = new TopicPartition(TOPIC_NAME, 3);
        PartitionInfo partitionInfo = new PartitionInfo(TOPIC_NAME, 0, null, null, null);


        ConsumerRecord<String, String> record1 = new ConsumerRecord<>(TOPIC_NAME, 0, OFFSET, "1234", personOneAsString);
        ConsumerRecord<String, String> record2 = new ConsumerRecord<>(TOPIC_NAME, 0, OFFSET, "5678", personTwoAsString);
        ConsumerRecords<String, String> consumerRecords = new ConsumerRecords<>(
                Map.of(topicPartition, Arrays.asList(record1, record2))
        );

        given(consumerConfig.configureConsumer()).willReturn(consumer);
        given(consumer.partitionsFor(TOPIC_NAME)).willReturn(List.of(partitionInfo));
        given(consumer.poll(100)).willReturn(consumerRecords);

        // When
        List<String> actualMessages = testObj.listen(TOPIC_NAME, OFFSET, COUNT);

        // Then
        assertThat(actualMessages).isEqualTo(expectedMessages);
        assertThat(messageHasBeenLogged(capture(), String.format("Message fetched by consumer: %s", personOneAsString))).isTrue();
        assertThat(messageHasBeenLogged(capture(), String.format("Message fetched by consumer: %s", personTwoAsString))).isTrue();
    }
}