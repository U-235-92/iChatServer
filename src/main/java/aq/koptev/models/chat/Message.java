package aq.koptev.models.chat;

import aq.koptev.models.account.Account;
import aq.koptev.models.account.Client;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Message implements Externalizable {

    private Account sender;
    private Account receiver;
    private String date;
    private String text;

    public Message() {}

    public Message(Client sender, Client receiver, String date, String text) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.text = text;
    }

    public Account getSender() {
        return sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(sender);
        out.writeObject(receiver);
        out.writeObject(date);
        out.writeObject(text);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        sender = (Account) in.readObject();
        receiver = (Account) in.readObject();
        date = (String) in.readObject();
        text = (String) in.readObject();
    }
}
