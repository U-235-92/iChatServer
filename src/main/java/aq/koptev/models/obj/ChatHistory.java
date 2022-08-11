package aq.koptev.models.obj;

import java.util.ArrayList;
import java.util.List;

public class ChatHistory {

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
