package aq.koptev.models.account;

import aq.koptev.models.chat.ChatHistory;

import java.io.Externalizable;

public interface Account extends Externalizable {

    default String getLogin() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    default String getPassword() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    default String getDescription() {
        return null;
    }

    default ChatHistory getChatHistory() throws  UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    default void setChatHistory(String chatHistory) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
