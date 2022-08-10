package aq.koptev.models.obj;

import java.util.ArrayList;
import java.util.List;

public class Meta {
    private Meta meta;
    private Client client;
    private List<Message> messages;
    private List<Client> clients;

    public Meta() {
        messages = new ArrayList<>();
        clients = new ArrayList<>();
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void addClients(List<Client> clients) {
        this.clients.removeAll(this.clients);
        this.clients.addAll(clients);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessages(List<Message> messages) {
        messages.addAll(messages);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}
