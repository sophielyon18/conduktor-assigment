package org.conduktor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class ConduktorController {

    @Autowired
    private ConduktorConsumer consumer;

    @GetMapping("/topic/{topicName}/{offset}")
    public ResponseEntity<List<String>> getMessages(
            @PathVariable String topicName,
            @PathVariable long offset,
            @RequestParam(defaultValue = "10") int count) {
        log.info("Message requested from topic: {} starting from offset: {} with count: {}", topicName, offset, count);
        List<String> messages = consumer.listen(topicName, offset, count);
        log.info("Messages fetched and returned: {}", messages);
        return ResponseEntity.ok(messages);
    }
}
