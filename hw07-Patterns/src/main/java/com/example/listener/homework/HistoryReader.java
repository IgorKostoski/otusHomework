package com.example.listener.homework;

import com.example.model.Message;
import java.util.Optional;

public interface HistoryReader {

    Optional<Message> findMessageById(long id);
}
