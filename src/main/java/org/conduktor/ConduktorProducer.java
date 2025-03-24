package org.conduktor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.conduktor.domain.Person;
import org.conduktor.domain.RootWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ConduktorProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    private String topicName;

    public void sendMessage(String messageFilePath) {
        try {
            RootWrapper root = objectMapper.readValue(new File(messageFilePath), RootWrapper.class);
            List<Person> people = root.getCtRoot();
            Integer messageCount = 0;

            for (Person person : people) {
                String key = person.getId();
                String message = objectMapper.writeValueAsString(person);

                kafkaTemplate.send(topicName, key, message);
                messageCount++;
                log.info("Sent Message {}: {}", messageCount, message);
            }
        } catch (IOException e) {
            log.error("IOException caught whilst trying to send message:{}", e.getMessage());
            e.printStackTrace();
        }
    }
}
