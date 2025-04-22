package com.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjectForMessage {
    private List<String> data;

    public ObjectForMessage() {
        this.data = new ArrayList<>();
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "ObjectForMessage{" + "data" + data + '}';
    }
}
