package aq.koptev.services.disconnect;

import aq.koptev.models.Handler;
import aq.koptev.models.Server;
import aq.koptev.models.account.Account;
import aq.koptev.models.chat.Message;
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
    private Account account;
    private DBConnector connector;

    public DisconnectionService(Server server, Handler handler) {
        this.server = server;
        this.handler = handler;
        this.account = handler.getAccount();
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
            PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            preparedStatement.setObject(1, account.getChatHistory());
            preparedStatement.setString(2, account.getLogin());
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendDisconnectionMessage() {
        String text = String.format("Пользователь %s покинул чат", account.getLogin());
        Message message = new Message(null, null, null, text);
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
