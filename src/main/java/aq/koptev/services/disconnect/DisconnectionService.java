package aq.koptev.services.disconnect;

import aq.koptev.models.connect.Handler;
import aq.koptev.models.connect.Server;
import aq.koptev.models.obj.Client;
import aq.koptev.models.obj.Message;
import aq.koptev.services.db.DBConnector;
import aq.koptev.services.db.SQLiteConnector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        writeChatHistory();
        sendDisconnectionMessage();
        closeConnection();
    }

    private void writeChatHistory() {
        String sql = "UPDATE Chats SET chatHistory = ? WHERE userId = (SELECT userId FROM Users WHERE login = ?)";
        try(Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
                PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            Client client = handler.getClient();
            objectOutputStream.writeObject(handler.getChatHistory().getMessages());
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
