package org.conduktor.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TestsLogging {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("ROOT");
    protected Appender appender = (Appender) Mockito.mock(Appender.class);
    protected ArgumentCaptor<LoggingEvent> argumentCaptor = ArgumentCaptor.forClass(LoggingEvent.class);

    public TestsLogging() {
    }

    @BeforeEach
    public void before() {
        LOGGER.addAppender(this.appender);
        LOGGER.setLevel(Level.INFO);
    }

    @AfterEach
    public void after() {
        LOGGER.detachAppender(this.appender);
    }

    protected List<LoggingEvent> capture() {
        ((Appender) BDDMockito.then(this.appender).should(Mockito.atLeastOnce())).doAppend(this.argumentCaptor.capture());
        return this.argumentCaptor.getAllValues();
    }

    protected boolean messageHasBeenLogged(List<LoggingEvent> loggingEvents, String message) {
        return loggingEvents.stream().anyMatch((loggingEvent) -> {
            return loggingEvent.getFormattedMessage().contains(message);
        });
    }

    protected boolean exceptionHasBeenLogged(List<LoggingEvent> loggingEvents, Class<? extends Exception> exception) {
        return loggingEvents.stream().anyMatch((loggingEvent) -> {
            return loggingEvent.getThrowableProxy() != null ? loggingEvent.getThrowableProxy().getClassName().equals(exception.getName()) : false;
        });
    }
}
