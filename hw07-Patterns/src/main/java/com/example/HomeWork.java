package com.example;

import com.example.handler.ComplexProcessor;
import com.example.listener.ListenerPrinterConsole;
import com.example.listener.homework.HistoryListener;
import com.example.model.Message;
import com.example.model.ObjectForMessage;
import com.example.processor.Processor;
import com.example.processor.ProcessorSwapFields11And12;
import com.example.processor.ProcessorThrowExceptionOnEvenSecond;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeWork {
    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    /*
    Реализовать to do:
      1. Добавить поля field11 - field13 (для field13 используйте класс ObjectForMessage)
      2. Сделать процессор, который поменяет местами значения field11 и field12
      3. Сделать процессор, который будет выбрасывать исключение в четную секунду (сделайте тест с гарантированным результатом)
            Секунда должна определяьться во время выполнения.
            Тест - важная часть задания
            Обязательно посмотрите пример к паттерну Мементо!
      4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
         Уже есть заготовка - класс HistoryListener, надо сделать его реализацию
         Для него уже есть тест, убедитесь, что тест проходит
    */

    public static void main(String[] args) {
        /*
          по аналогии с Demo.class
          из элеменов "to do" создать new ComplexProcessor и обработать сообщение
        */

        var processors = List.<Processor>of(
                new ProcessorSwapFields11And12(), new ProcessorThrowExceptionOnEvenSecond(Clock.systemDefaultZone()));

        var complexProcessor =
                new ComplexProcessor(processors, ex -> logger.error("Processor Error: {}", ex.getMessage()));

        var listenerPrinter = new ListenerPrinterConsole();
        var historyListener = new HistoryListener();

        complexProcessor.addListener(listenerPrinter);
        complexProcessor.addListener(historyListener);

        var field13Object = new ObjectForMessage();
        field13Object.setData(new ArrayList<>(List.of("data1", "data2")));

        var message = new Message.Builder(1L)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field4("field4")
                .field5("field5")
                .field6("field6")
                .field7("field7")
                .field8("field8")
                .field9("field9")
                .field10("field10")
                .field11("value11")
                .field12("value12")
                .field13(field13Object)
                .build();

        logger.info("Initial message: {}", message);

        Message result = null;

        try {
            result = complexProcessor.handle(message);
            logger.info("Result message: {}", result);
        } catch (Exception e) {
            logger.error("Exception during handle: {}", e.getMessage());
        }

        if (result != null) {
            final long resultId = result.getId();

            var messageFromHistory = historyListener.findMessageById(resultId);
            messageFromHistory.ifPresentOrElse(
                    msg -> logger.info("Message found in history: {}", msg),
                    () -> logger.warn("Message NOT found in history for id: {}", resultId));

            if (message.getField13() != null) {
                message.getField13().setData(List.of("newData!"));
                logger.info("Original Message after modification : {}", message);

                var messageFromHistoryAgain = historyListener.findMessageById(result.getId());
                messageFromHistoryAgain.ifPresent(
                        msg -> logger.info("Message in history after original modification: {}", msg));
            }
        } else {
            var messageFromHistory = historyListener.findMessageById(message.getId());
            messageFromHistory.ifPresentOrElse(
                    msg -> logger.info("Original message found in history after exception: {}", msg),
                    () -> logger.warn(
                            "Original message NOT found in history for Id: {} after exception", message.getId()));
        }

        complexProcessor.removeListener(listenerPrinter);
        complexProcessor.removeListener(historyListener);

        logger.info("Demo finished.");
    }
}
