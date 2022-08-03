package aq.koptev.services.connect.authentication;

import aq.koptev.models.Handler;
import aq.koptev.models.account.Account;
import aq.koptev.models.account.Client;
import aq.koptev.models.account.NullClient;
import aq.koptev.services.db.DBConnector;
import aq.koptev.services.db.SQLiteConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationService {

    private DBConnector connector;

    public AuthenticationService() {
        connector = new SQLiteConnector();
    }

    public Account processAuthentication(Handler handler, Account account) {
        if(isExistAccount(account)) {
            if(isAuthorizeAccount(handler, account)) {
                String description = String.format("Пользователь с логином %s уже авторизован", account.getLogin());
                return new NullClient(description);
            } else {
                if(isCorrectPassword(account)) {
                    return getClientAccount(account);
                } else {
                    String description = "Введен неверный пароль";
                    return new NullClient(description);
                }
            }
        } else {
            String description = String.format("Пользователя с логином %s не существует", account.getLogin());
            return new NullClient(description);
        }
    }

    private boolean isExistAccount(Account account) {
        String sql = "SELECT login FROM Users WHERE login = ?";
        try(Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
            PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            preparedStatement.setString(1, account.getLogin());
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAuthorizeAccount(Handler handler, Account account) {
        return handler.isClientConnected(account);
    }

    private boolean isCorrectPassword(Account account) {
        String sql = "SELECT password FROM Users WHERE login = ?";
        try(Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
            PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            preparedStatement.setString(1, account.getLogin());
            ResultSet rs = preparedStatement.executeQuery();
            String password = rs.getString(1);
            if(account.getPassword().equals(password)) {
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Account getClientAccount(Account account) {
        String sql = "SELECT Users.login, Users.password, Chats.chatHistory FROM Users INNER JOIN Chats ON Users.userID = Chats.userId WHERE login = ?";
        Account client = null;
        try(Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
            PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            preparedStatement.setString(1, account.getLogin());
            ResultSet rs = preparedStatement.executeQuery();
            String login = rs.getString(1);
            String password = rs.getString(2);
            String chatHistory = rs.getString(3);
            client = new Client(login, password);
            client.setChatHistory(chatHistory);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return client;
    }
}
