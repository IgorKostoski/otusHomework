package com.example.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.example.model.Message;
import java.time.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProcessorThrowExceptionOnEvenSecondTest {

    @Test
    @DisplayName("Shpuld throw exception when second is even")
    void processEvenSecondTest() {

        Instant fixedInstant = LocalDateTime.of(2023, 1, 1, 10, 30, 10).toInstant(ZoneOffset.UTC);
        Clock fixedCLock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

        Processor processor = new ProcessorThrowExceptionOnEvenSecond(fixedCLock);
        Message message = new Message.Builder(1L).build();

        assertThatExceptionOfType(ProcessorThrowExceptionOnEvenSecond.EvenSecondException.class)
                .isThrownBy(() -> processor.process(message))
                .withMessageContaining("second is even: 10");
    }

    @Test
    @DisplayName("Shpuld NOT throw exception when second is odd")
    void processOddSecondTest() {
        Instant fixedInstant = LocalDateTime.of(2023, 1, 1, 10, 30, 11).toInstant(ZoneOffset.UTC);
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

        Processor processor = new ProcessorThrowExceptionOnEvenSecond(fixedClock);
        Message message = new Message.Builder(1L).build();

        Message result = processor.process(message);

        assertThat(result).isEqualTo(message);
    }
}
