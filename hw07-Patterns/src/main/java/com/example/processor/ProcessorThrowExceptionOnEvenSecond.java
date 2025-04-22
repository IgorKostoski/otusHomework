package com.example.processor;

import com.example.model.Message;
import java.time.Clock;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorThrowExceptionOnEvenSecond implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorThrowExceptionOnEvenSecond.class);

    private final Clock clock;

    public ProcessorThrowExceptionOnEvenSecond(Clock clock) {
        this.clock = clock;
    }

    public ProcessorThrowExceptionOnEvenSecond() {
        this(Clock.systemDefaultZone());
    }

    @Override
    public Message process(Message message) {
        LocalDateTime now = LocalDateTime.now(clock);
        int second = now.getSecond();
        logger.debug("Current second: {}", second);
        if (second % 2 == 0) {
            throw new EvenSecondException("Exception thrown because second is even: " + second);
        }

        return message;
    }

    public static class EvenSecondException extends RuntimeException {
        public EvenSecondException(String message) {
            super(message);
        }
    }
}
