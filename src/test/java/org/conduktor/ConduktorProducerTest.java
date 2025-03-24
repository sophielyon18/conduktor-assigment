package org.conduktor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.conduktor.config.TestsLogging;
import org.conduktor.domain.Address;
import org.conduktor.domain.Person;
import org.conduktor.domain.RootWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class ConduktorProducerTest extends TestsLogging {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private RecordMetadata recordMetadata;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RootWrapper rootWrapper;

    @InjectMocks
    private ConduktorProducer testObj;

    private static final String TOPIC_NAME = "test-topic";
    private static final String MESSAGE_FILE_PATH = "src/test/resources/random-people-test-data.json";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        testObj = new ConduktorProducer(kafkaTemplate, objectMapper, TOPIC_NAME);
    }

    @Test
    @SneakyThrows
    void sendMessage_givenMessageFilePath_willSendDataFromFile() {
        // Given
        Person personOne = Person.builder()
                .name("Bob")
                .id("1234")
                .dob("12/12/12")
                .url("url.com")
                .score(12.0)
                .email("bob@email.com")
                .pets(List.of("cat"))
                .address(Address.builder()
                        .town("Chester")
                        .postcode("SW12 34G")
                        .street("Bobs street")
                        .build())
                .description("Description of Bob")
                .salary(100)
                .build();

        Person personTwo = Person.builder()
                .name("Bill")
                .id("5678")
                .dob("10/10/10")
                .url("url.com")
                .score(10.0)
                .email("bill@email.com")
                .pets(List.of("cat", "dog"))
                .address(Address.builder()
                        .town("Manchester")
                        .postcode("SW10 55F")
                        .street("Bills street")
                        .build())
                .description("Description of Bill")
                .salary(100)
                .build();

        String personOneAsString = "{\"_id\":\"1234\",\"name\":\"Bob\",\"dob\":\"12/12/12\",\"address\":{\"street\":\"Bobs street\",\"town\":\"Chester\",\"postode\":\"SW12 34G\"},\"telephone\":null,\"pets\":[\"cat\"],\"score\":12.0,\"email\":\"bob@email.com\",\"url\":\"url.com\",\"description\":\"Description of Bob\",\"verified\":false,\"salary\":100}";
        String personTwoAsString = "{\"_id\":\"5678\",\"name\":\"Bill\",\"dob\":\"10/10/10\",\"address\":{\"street\":\"Bills street\",\"town\":\"Manchester\",\"postode\":\"SW10 55F\"},\"telephone\":null,\"pets\":[\"cat\",\"dog\"],\"score\":10.0,\"email\":\"bill@email.com\",\"url\":\"url.com\",\"description\":\"Description of Bill\",\"verified\":false,\"salary\":100}";

        rootWrapper.setCtRoot(List.of(personOne, personTwo));

        CompletableFuture<SendResult<String, String>> futureOne = CompletableFuture.completedFuture(new SendResult<>(
                new ProducerRecord<>(TOPIC_NAME, personOne.getId(), personOneAsString),
                recordMetadata
        ));
        CompletableFuture<SendResult<String, String>> futureTwo = CompletableFuture.completedFuture(new SendResult<>(
                new ProducerRecord<>(TOPIC_NAME, personTwo.getId(), personTwoAsString),
                recordMetadata
        ));

        given(kafkaTemplate.send(TOPIC_NAME, personOne.getId(), personOneAsString)).willReturn(futureOne);
        given(kafkaTemplate.send(TOPIC_NAME, personTwo.getId(), personTwoAsString)).willReturn(futureTwo);

        // When
        testObj.sendMessage(MESSAGE_FILE_PATH);

        // Then
        then(kafkaTemplate).should().send(TOPIC_NAME, personOne.getId(), personOneAsString);
        then(kafkaTemplate).should().send(TOPIC_NAME, personTwo.getId(), personTwoAsString);
    }

    @Test
    @SneakyThrows
    void sendMessage_givenNoFileIsFound_ioExceptionIsCaughtAndMessageLogged() {
        // Given When
        testObj.sendMessage("");

        // Then
        assertThat(messageHasBeenLogged(capture(),
                "IOException caught whilst trying to send message: (No such file or directory)")).isTrue();
    }
}
