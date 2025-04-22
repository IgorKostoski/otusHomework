package com.example.homework;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.example.listener.homework.HistoryListener;
import com.example.model.Message;
import com.example.model.ObjectForMessage;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class HistoryListenerTest {

    @Test
    void listenerTest() {

        var historyListener = new HistoryListener();

        var id = 100L;
        var data = "33";
        var field13 = new ObjectForMessage();
        var field13Data = new ArrayList<String>();
        field13Data.add(data);
        field13.setData(field13Data);

        var message = new Message.Builder(id)
                .field13(field13)
                .field10("field10")
                .field11("field11")
                .field12("field12")
                .field13(field13)
                .build();

        historyListener.onUpdated(message);

        message.getField13().setData(new ArrayList<>());
        field13Data.clear();

        var messageFromHistory = historyListener.findMessageById(id);
        assertThat(messageFromHistory).isPresent();

        assertThat(messageFromHistory.get().getField13()).isNotNull();
        assertThat(messageFromHistory.get().getField13().getData()).containsExactly(data);
    }
}
