package com.example.processor;

import com.example.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;

public class ProcessorThrowExceptionOnEvenSecond implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorThrowExceptionOnEvenSecond.class);

    private final Clock clock;

    public ProcessorThrowExceptionOnEvenSecond(Clock clock) {
        if (clock == null) {
            throw new NullPointerException("Clock cannot be null");
        }
        this.clock = clock;
    }

    public ProcessorThrowExceptionOnEvenSecond() {
        this(Clock.systemDefaultZone());
    }

    @Override
    public Message process(Message message) {

        if (message == null) {
            logger.warn("Received null message to process");

            return null;
        }

        LocalDateTime now = LocalDateTime.now(clock);
        int second = now.getSecond();
        logger.debug("Processing message. Current second provided by the clock: {}", second);

        if (second % 2 == 0) {
            String exceptionMessage = ("Exception thrown because second is even: " + second);
            logger.warn(exceptionMessage);
            throw new EvenSecondException(exceptionMessage);
        }
        logger.debug("Processing successful (second was odd).");
        return message;
    }

    public static class EvenSecondException extends RuntimeException {
        public EvenSecondException(String message) {
            super(message);
        }
    }
}
