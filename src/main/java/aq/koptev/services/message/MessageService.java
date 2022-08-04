package aq.koptev.services.message;

import aq.koptev.models.Server;
import aq.koptev.models.chat.Message;

import java.io.IOException;
import java.io.ObjectInputStream;

public class MessageService {

    private Server server;
    private ObjectInputStream objectInputStream;

    public MessageService(Server server, ObjectInputStream objectInputStream) {
        this.server = server;
        this.objectInputStream = objectInputStream;
    }

    public void processMessage() throws IOException, ClassNotFoundException {
        while (true) {
            Message message = (Message) objectInputStream.readObject();
            server.processMessage(message);
        }
    }
}
