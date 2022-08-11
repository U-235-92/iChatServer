package aq.koptev.i.models;

import java.io.Serializable;

public class Message implements Serializable {
    private String sender;
    private String receiver;
    private String date;
    private String text;

    public Message(String sender, String receiver, String date, String text) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.text = text;
    }

    public Message(String sender, String date, String text) {
        this(sender, null, date, text);
    }

    public Message(String text) {
        this(null, null, null, text);
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        if(text != null) {
            if(sender == null && receiver == null) {
                return text;
            } else if (sender != null && date != null) {
                return String.format("%s [%s] %s", sender, date, text);
            } else {
                return text;
            }
        } else {
            return "";
        }
    }
}
