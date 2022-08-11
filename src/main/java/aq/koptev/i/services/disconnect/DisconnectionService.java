package aq.koptev.i.services.disconnect;

import aq.koptev.i.models.*;
import aq.koptev.i.services.db.DBConnector;
import aq.koptev.i.services.db.SQLiteConnector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DisconnectionService {

    private Server server;
    private Handler handler;
    private DBConnector connector;

    public DisconnectionService(Server server, Handler handler) {
        this.server = server;
        this.handler = handler;
        connector = new SQLiteConnector();
    }

    public void processDisconnection() {
        if(isDbChatHistoryEmpty()) {
            insertChatHistory();
        } else {
            updateChatHistory();
        }
        sendDisconnectionMessage();
        closeConnection();
    }

    private boolean isDbChatHistoryEmpty() {
        String sql = "SELECT chatId FROM Chats WHERE userId = (SELECT userId FROM Users WHERE login = ?)";
        try(Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
            PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            Client client = handler.getClient();
            preparedStatement.setString(1, client.getLogin());
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void insertChatHistory() {
        String sql = "INSERT INTO Chats (userID, chatHistory) VALUES ((SELECT userId FROM Users WHERE login = ?), ?)";
        try(Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
                PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            Client client = handler.getClient();
            ChatHistory chatHistory = handler.getChatHistory();
            objectOutputStream.writeObject(chatHistory);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            preparedStatement.setString(1, client.getLogin());
            preparedStatement.setBytes(2, bytes);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateChatHistory() {
        String sql = "UPDATE Chats SET chatHistory = ? WHERE userId = (SELECT userId FROM Users WHERE login = ?)";
        try(Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
                PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            Client client = handler.getClient();
            ChatHistory chatHistory = handler.getChatHistory();
            objectOutputStream.writeObject(chatHistory);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            preparedStatement.setBytes(1, bytes);
            preparedStatement.setString(2, client.getLogin());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendDisconnectionMessage() {
        String login = handler.getClient().getLogin();
        String text = String.format("Пользователь %s покинул чат", login);
        Message message = new Message(text);
        try {
            server.processMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            handler.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
