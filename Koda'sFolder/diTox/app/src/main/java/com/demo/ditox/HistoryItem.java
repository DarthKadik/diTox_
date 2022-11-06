package com.demo.ditox;

import java.util.List;

public class HistoryItem {
    String peerId;
    List<Message> messages;
    public HistoryItem(String peerId, List<Message> messages) {
        this.peerId = peerId;
        this.messages = messages;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "HistoryItem{" +
                "peerId='" + peerId + '\'' +
                ", messages=" + messages +
                '}';
    }
}
