package org.conduktor;

import lombok.SneakyThrows;
import org.conduktor.config.TestsLogging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ConduktorControllerTest extends TestsLogging {

    public static final String TEST_TOPIC = "test-topic";
    private MockMvc mockMvc;

    @Mock
    private ConduktorConsumer consumer;

    @InjectMocks
    private ConduktorController testObj;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(testObj).build();
    }

    @Test
    @SneakyThrows
    public void getMessages_givenTopicNameOffsetAndCount_willReturnMessagesAndHttpStatusOk() {
        // Given
        List<String> mockMessages = List.of("Message 1", "Message 2", "Message 3");
        ResponseEntity<List<String>> expected = new ResponseEntity<>(mockMessages, HttpStatusCode.valueOf(200));
        given(consumer.listen(TEST_TOPIC, 100, 10)).willReturn(mockMessages);

        // When
        ResponseEntity<List<String>> actual =  testObj.getMessages(TEST_TOPIC, 100, 10);

        // Then
        assertThat(actual).isEqualTo(expected);
        mockMvc.perform(get("/topic/test-topic/100?count=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("Message 1"))
                .andExpect(jsonPath("$[1]").value("Message 2"))
                .andExpect(jsonPath("$[2]").value("Message 3"));
    }

    @Test
    @SneakyThrows
    public void getMessages_givenTopicNameOffsetAndCount_willPrintLogs() {
        // Given
        List<String> mockMessages = List.of("Message 1", "Message 2", "Message 3");
        given(consumer.listen(TEST_TOPIC, 100, 10)).willReturn(mockMessages);

        // When
        testObj.getMessages(TEST_TOPIC, 100, 10);

        // Then
        assertThat(messageHasBeenLogged(capture(),
                "Message requested from topic: test-topic starting from offset: 100 with count: 10")).isTrue();
        assertThat(messageHasBeenLogged(capture(),
                "Messages fetched and returned: [Message 1, Message 2, Message 3]")).isTrue();
    }
}