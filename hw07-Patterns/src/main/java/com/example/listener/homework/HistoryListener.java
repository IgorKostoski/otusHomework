package com.example.listener.homework;

import com.example.listener.Listener;
import com.example.model.Message;
import com.example.model.ObjectForMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// todo: 4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
// DONE: Implemented HistoryListener with deep copy
public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Message> history = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {
        // Create a deep copy to prevent external modifications from affecting history
        Message messageCopy = deepCopyMessage(msg);
        history.put(messageCopy.getId(), messageCopy);
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(history.get(id));
    }

    // Helper method for deep copying the Message object
    private Message deepCopyMessage(Message originalMsg) {
        if (originalMsg == null) {
            return null;
        }

        // Deep copy field13 if it exists
        ObjectForMessage field13Copy = null;
        ObjectForMessage originalField13 = originalMsg.getField13();
        if (originalField13 != null) {
            field13Copy = new ObjectForMessage();
            // Ensure the list inside ObjectForMessage is also copied
            if (originalField13.getData() != null) {
                field13Copy.setData(new ArrayList<>(originalField13.getData()));
            } else {
                field13Copy.setData(new ArrayList<>()); // Or null, depending on desired behavior
            }
        }

        // Use the builder to create a copy, replacing field13 with its deep copy
        return originalMsg.toBuilder()
                .field13(field13Copy) // Set the copied field13
                .build();
    }
}
