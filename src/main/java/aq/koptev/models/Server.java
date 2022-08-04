package aq.koptev.models;

import aq.koptev.models.account.Account;
import aq.koptev.models.chat.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final int DEFAULT_PORT = 5082;
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private List<Handler> handlers;
    private int port;

    public Server() throws IOException {
        this(DEFAULT_PORT);
    }

    public Server(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        handlers = new ArrayList<>();
    }

    public void launch() throws IOException {
        while (true) {
            System.out.println("Wait connection...");
            clientSocket = serverSocket.accept();
            System.out.println("Connection is success!");
            Handler clientConnection = new Handler(this, clientSocket);
            clientConnection.handle();
        }
    }

    public void processMessage(Message message) throws IOException {
        if(isServerMessage(message)) {
            processSendPublicMessage(message);
        } else if(isPublicMessage(message)) {
            processSendPublicMessage(message);
        } else if(isPrivateMessage(message)) {
            processSendPrivateMessage(message);
        }
    }

    private boolean isServerMessage(Message message) {
        return message.getSender() == null;
    }

    private boolean isPublicMessage(Message message) {
        Account sender = message.getSender();
        Account receiver = message.getReceiver();
        return sender != null && receiver == null;
    }

    private boolean isPrivateMessage(Message message) {
        Account sender = message.getSender();
        Account receiver = message.getReceiver();
        return sender != null && receiver != null;
    }

    private void processSendPublicMessage(Message message) throws IOException {
        for(Handler handler : handlers) {
            handler.sendMessage(message);
        }
    }

    private void processSendPrivateMessage(Message message) throws IOException {
        for(Handler handler : handlers) {
            if(isMessageToSenderAndReceiver(handler, message)) {
                handler.sendMessage(message);
            }
        }
    }

    private boolean isMessageToSenderAndReceiver(Handler handler, Message message) {
        Account sender = message.getSender();
        Account receiver = message.getReceiver();
        return handler.getAccount().getLogin().equals(sender.getLogin()) ||
                handler.getAccount().getLogin().equals(receiver.getLogin());
    }

    public boolean isHandlerConnected(Account account) {
        for(Handler handler : handlers) {
            if(handler.getAccount().getLogin().equals(account.getLogin())) {
                return true;
            }
        }
        return false;
    }

    public synchronized void addHandler(Handler connection) {
        handlers.add(connection);
    }

    public synchronized void removeHandler(Handler connection) {
        handlers.remove(connection);
    }
}
