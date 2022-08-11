package aq.koptev.i.models;

import java.io.Serializable;

public class Client implements Serializable {

    private String login;
    private String password;

    public Client(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
