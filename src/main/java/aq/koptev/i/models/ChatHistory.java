package aq.koptev.i.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatHistory implements Serializable {

    private List<Message> messages;

    public ChatHistory() {
        messages = new ArrayList<>();
    }

    public void add(Message message) {
        messages.add(message);
    }

    public void addAll(List<Message> messages) {
        messages.addAll(messages);
    }

    public List<Message> getMessages() {
        return messages;
    }
}
