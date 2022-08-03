package aq.koptev.models.account;

import aq.koptev.models.chat.ChatHistory;

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
    public void setChatHistory(ChatHistory chatHistory) {
        this.chatHistory = chatHistory;
    }
}
