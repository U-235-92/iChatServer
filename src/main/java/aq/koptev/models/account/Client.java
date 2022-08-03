package aq.koptev.models.account;

import aq.koptev.models.chat.ChatHistory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Client implements Account {

    private String login;
    private String password;
    private ChatHistory chatHistory;

    public Client(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public ChatHistory getChatHistory() {
        return chatHistory;
    }

    @Override
    public void setChatHistory(String chatHistory) {
        this.chatHistory = chatHistory;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}
