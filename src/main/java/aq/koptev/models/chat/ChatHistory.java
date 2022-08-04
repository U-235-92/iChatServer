package aq.koptev.models.chat;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class ChatHistory implements Externalizable {

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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(messages.size());
        for(Message message : messages) {
            out.writeObject(message);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int size = (int) in.readInt();
        for (int i = 0; i < size; i++) {
            Message message = (Message) in.readObject();
            messages.add(message);
        }
    }
}
