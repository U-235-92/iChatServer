package aq.koptev.models.account;

import aq.koptev.models.chat.ChatHistory;

public interface Account {

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

    default void setChatHistory(ChatHistory chatHistory) throws  UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
