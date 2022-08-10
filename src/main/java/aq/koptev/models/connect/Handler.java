package aq.koptev.models.connect;

import aq.koptev.models.network.NetObject;
import aq.koptev.models.obj.Client;
import aq.koptev.models.obj.Message;
import aq.koptev.models.obj.Meta;
import aq.koptev.services.connect.IdentificationService;
import aq.koptev.services.disconnect.DisconnectionService;
import aq.koptev.services.message.MessageService;
import aq.koptev.util.ParameterNetObject;
import aq.koptev.util.TypeNetObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Handler {
    private IdentificationService identificationService;
    private DisconnectionService disconnectionService;
    private MessageService messageService;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Socket clientSocket;
    private Server server;
    private Meta meta;

    public Handler(Server server, Socket clientSocket) throws IOException {
        this.server = server;
        this.clientSocket = clientSocket;
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        identificationService = new IdentificationService(server, this, objectInputStream, objectOutputStream);
        disconnectionService = new DisconnectionService(server, this);
        messageService = new MessageService(server, objectInputStream);
    }

    public void handle() {
        Thread connectionThread = new Thread(() -> {
            try {
                identificationService.processIdentification();
                messageService.processMessage();
            } catch (IOException | ClassNotFoundException e) {
                disconnectionService.processDisconnection();
                e.printStackTrace();
            }
        });
        connectionThread.start();
    }

    public void closeConnection() throws IOException {
        clientSocket.close();
        server.removeHandler(this);
    }

    public void sendMessage(Message message) throws IOException {
        NetObject netObject = new NetObject(TypeNetObject.MESSAGE);
        netObject.putData(ParameterNetObject.MESSAGE, NetObject.getBytes(message));
        objectOutputStream.writeObject(netObject);
    }

    public boolean isClientConnected(Client client) {
        return server.isHandlerConnected(client);
    }

    public void registerHandler() {
        server.addHandler(this);
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
