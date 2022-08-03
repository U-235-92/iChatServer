package aq.koptev.models.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatHistory {

    private List<Message> messages;

    public ChatHistory() {
        messages = new ArrayList<>();
    }

    public void addMessages(List<Message> messages)  {
        messages.addAll(messages);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}
