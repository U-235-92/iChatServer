package aq.koptev.i.models;

import aq.koptev.i.services.connect.IdentificationService;
import aq.koptev.i.services.disconnect.DisconnectionService;
import aq.koptev.i.services.message.MessageService;
import aq.koptev.i.util.ParameterNetObject;
import aq.koptev.i.util.TypeNetObject;

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
    private Client client;
    private ClientPool clientPool;
    private ChatHistory chatHistory;

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

    public void sendNetObject(NetObject netObject) throws IOException {
        objectOutputStream.writeObject(netObject);
    }

    public boolean isClientConnected(Client client) {
        return server.isHandlerConnected(client);
    }

    public void registerHandler(Client client) {
        server.addHandler(this);
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public ClientPool getClientPool() {
        return clientPool;
    }

    public ChatHistory getChatHistory() {
        return chatHistory;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setClientPool(ClientPool clientPool) {
        this.clientPool = clientPool;
    }

    public void setChatHistory(ChatHistory chatHistory) {
        this.chatHistory = chatHistory;
    }
}
