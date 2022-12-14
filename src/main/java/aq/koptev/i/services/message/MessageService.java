package aq.koptev.i.services.message;

import aq.koptev.i.models.Server;
import aq.koptev.i.models.NetObject;

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
            NetObject netObject = (NetObject) objectInputStream.readObject();
            server.processSendNetObject(netObject);
        }
    }
}
