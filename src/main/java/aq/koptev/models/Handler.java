package aq.koptev.models;

import aq.koptev.models.account.Account;
import aq.koptev.models.chat.Message;
import aq.koptev.services.connect.IdentificationService;
import aq.koptev.services.disconnect.DisconnectionService;
import aq.koptev.services.message.MessageService;

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

    private Account account;

    public Handler(Server server, Socket clientSocket) throws IOException {
        this.server = server;
        this.clientSocket = clientSocket;
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        identificationService = new IdentificationService(this, objectInputStream, objectOutputStream);
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
        account.getChatHistory().addMessage(message);
        objectOutputStream.writeObject(message);
    }

    public boolean isClientConnected(Account account) {
        return server.isHandlerConnected(account);
    }

    public void registerHandler() {
        server.addHandler(this);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
