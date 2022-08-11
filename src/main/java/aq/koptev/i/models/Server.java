package aq.koptev.i.models;

import aq.koptev.i.util.ParameterNetObject;
import aq.koptev.i.util.TypeNetObject;

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

    public void processSendNetObject(NetObject netObject) throws IOException {
        switch (netObject.getType()) {
            case MESSAGE:
                Message message = NetObject.getObject(netObject.getData(ParameterNetObject.MESSAGE));
                processMessage(message);
                break;
            case DISCONNECTION_NOTIFY:
                processDisconnectionNotify(netObject);
                break;
            default:
                for(Handler handler : handlers) {
                    handler.sendNetObject(netObject);
                }
                break;
        }
    }

    private void processMessage(Message message) throws IOException {
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
        String sender = message.getSender();
        String receiver = message.getReceiver();
        return sender != null && receiver == null;
    }

    private boolean isPrivateMessage(Message message) {
        String sender = message.getSender();
        String receiver = message.getReceiver();
        return sender != null && receiver != null;
    }

    private void processSendPublicMessage(Message message) throws IOException {
        for(Handler handler : handlers) {
            sendMessage(handler, message);
        }
    }

    private void processSendPrivateMessage(Message message) throws IOException {
        for(Handler handler : handlers) {
            if(isMessageToSenderAndReceiver(handler, message)) {
                sendMessage(handler, message);
            }
        }
    }

    private boolean isMessageToSenderAndReceiver(Handler handler, Message message) {
        String sender = message.getSender();
        String receiver = message.getReceiver();
        return handler.getClient().getLogin().equals(sender) ||
                handler.getClient().getLogin().equals(receiver);
    }

    private void sendMessage(Handler handler, Message message) throws IOException {
        NetObject netObject = new NetObject(TypeNetObject.MESSAGE);
        netObject.putData(ParameterNetObject.MESSAGE, NetObject.getBytes(message));
        handler.sendNetObject(netObject);
        handler.getChatHistory().add(message);
    }

    private void processDisconnectionNotify(NetObject netObject) throws IOException {
        for(Handler handler : handlers) {
            Message message = NetObject.getObject(netObject.getData(ParameterNetObject.MESSAGE));
            handler.getChatHistory().add(message);
            handler.sendNetObject(netObject);
        }
    }

    public boolean isHandlerConnected(Client client) {
        for(Handler handler : handlers) {
            if(handler.getClient().getLogin().equals(client.getLogin())) {
                return true;
            }
        }
        return false;
    }

    public List<Client> getConnectedClients() {
        List<Client> clients = new ArrayList<>();
        for(Handler handler : handlers) {
            clients.add(handler.getClient());
        }
        return clients;
    }

    public List<Client> getConnectedClientsWithoutClient(Client client) {
        List<Client> clients = new ArrayList<>();
        for(Handler handler : handlers) {
            if(client.getLogin().equals(handler.getClient().getLogin())) {
                continue;
            }
            clients.add(handler.getClient());
        }
        return clients;
    }

    public synchronized void addHandler(Handler connection) {
        handlers.add(connection);
    }

    public synchronized void removeHandler(Handler connection) {
        handlers.remove(connection);
    }
}
