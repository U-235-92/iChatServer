package aq.koptev.models.chat;

import aq.koptev.models.account.Client;

public class Message {

    private Client sender;
    private Client receiver;
    private String date;
    private String text;

    public Message(Client sender, Client receiver, String date, String text) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.text = text;
    }

    public Client getSender() {
        return sender;
    }

    public Client getReceiver() {
        return receiver;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}
