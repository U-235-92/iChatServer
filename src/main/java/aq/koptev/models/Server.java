package aq.koptev.models;

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
            clientConnection.processConnection();
        }
    }

    public synchronized void addHandler(Handler connection) {
        handlers.add(connection);
    }

    public synchronized void removeHandler(Handler connection) {
        handlers.remove(connection);
    }
}
