package aq.koptev.i;

import aq.koptev.i.models.Server;

import java.io.IOException;

public class ServerApp {

    public static void main(String[] args) {

        try {
            Server server = new Server();
            server.launch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
