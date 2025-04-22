package com.example.listener.homework;

import com.example.listener.Listener;
import com.example.model.Message;
import com.example.model.ObjectForMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HistoryListener implements Listener, HistoryReader {
    private static final Logger logger = LoggerFactory.getLogger(HistoryListener.class);

    private final Map<Long, Message> history = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {

        Message messageCopy = deepCopyMessage(msg);
        if (messageCopy != null) {
            history.put(messageCopy.getId(), messageCopy);
        } else {
            logger.warn("Attemted to update history with a nulll message copy");
        }
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(history.get(id));
    }

    private Message deepCopyMessage(Message originalMsg) {
        if (originalMsg == null) {
            return null;
        }

        ObjectForMessage field13Copy = null;
        ObjectForMessage originalField13 = originalMsg.getField13();
        if (originalField13 != null) {
            field13Copy = new ObjectForMessage();

            if (originalField13.getData() != null) {
                field13Copy.setData(new ArrayList<>(originalField13.getData()));
            } else {
                field13Copy.setData(new ArrayList<>());
            }
        }

        return originalMsg.toBuilder().field13(field13Copy).build();
    }
}
