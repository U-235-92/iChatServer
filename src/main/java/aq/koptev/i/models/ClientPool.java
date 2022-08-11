package aq.koptev.i.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientPool implements Serializable {

    private List<Client> clients;

    public ClientPool() {
        clients = new ArrayList<>();
    }

    public void add(Client client) {
        clients.add(client);
    }

    public void addAll(List<Client> clients) {
        clients.addAll(clients);
    }

    public void update(ClientPool clientPool) {
        this.clients.removeAll(this.clients);
        this.clients.addAll(clientPool.getClients());
    }

    public void update(List<Client> clients) {
        this.clients.removeAll(this.clients);
        this.clients.addAll(clients);
    }

    public List<Client> getClients() {
        return clients;
    }
}
