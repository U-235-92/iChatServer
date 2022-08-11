package aq.koptev.services.message;

import aq.koptev.models.connect.Server;
import aq.koptev.models.connect.NetObject;
import aq.koptev.models.obj.Message;
import aq.koptev.util.ParameterNetObject;

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
            Message message = NetObject.getObject(netObject.getData(ParameterNetObject.MESSAGE));
            server.processMessage(message);
        }
    }
}
