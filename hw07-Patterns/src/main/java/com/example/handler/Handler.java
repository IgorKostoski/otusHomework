package com.example.handler;

import com.example.listener.Listener;
import com.example.model.Message;

public interface Handler {
    Message handle(Message msg);

    void addListener(Listener listener);

    void removeListener(Listener listener);
}
