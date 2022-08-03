package aq.koptev.models;

import aq.koptev.models.account.Account;
import aq.koptev.models.chat.Message;
import aq.koptev.services.connect.IdentificationService;
import aq.koptev.services.disconnect.DisconnectionService;
import aq.koptev.services.message.MessageService;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Handler {

    private IdentificationService identificationService;
    private MessageService messageService;
    private DisconnectionService disconnectionService;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Socket clientSocket;
    private Server server;

    private Account account;

    public Handler(Server server, Socket clientSocket) throws IOException {
        this.server = server;
        this.clientSocket = clientSocket;
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        identificationService = new IdentificationService(this, objectInputStream, objectOutputStream);
        messageService = new MessageService(this, objectInputStream);
        disconnectionService = new DisconnectionService(this, objectInputStream, objectOutputStream);
    }

    public void processConnection() {
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

    public void sendMessage(Message message) throws IOException {
        objectOutputStream.writeObject(message);
    }

    public boolean isClientConnected(Account account) {
        return server.isHandlerConnected(account);
    }

    public void registerClientConnection() {
        server.addHandler(this);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
