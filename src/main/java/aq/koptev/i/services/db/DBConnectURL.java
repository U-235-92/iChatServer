package aq.koptev.i.services.db;

public enum DBConnectURL {

    CHAT_DB("jdbc:sqlite:src/main/resources/db/chat-db.db");

    private String url;

    DBConnectURL(String url) {
        this.url = url;
    }

    public String getURL() {
        return url;
    }
}
