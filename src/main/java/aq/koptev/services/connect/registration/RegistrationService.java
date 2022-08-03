package aq.koptev.services.connect.registration;

import aq.koptev.models.account.Account;
import aq.koptev.models.account.NullClient;
import aq.koptev.services.db.DBConnector;
import aq.koptev.services.db.SQLiteConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationService {

    private DBConnector connector;

    public RegistrationService() {
        connector = new SQLiteConnector();
    }
    public Account processRegistration(Account account) {
        if(isAccountExist(account)) {
            String description = "Пользователь с указанным логином уже зарегестрирован";
            return new NullClient(description);
        } else {
            return registrarAccount(account);
        }
    }

    private boolean isAccountExist(Account account) {
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

    private Account registrarAccount(Account account) {
        String sql = "INSERT INTO Users (login, password) VALUES (?, ?)";
        try (Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
             PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            preparedStatement.setString(1, account.getLogin());
            preparedStatement.setString(2, account.getPassword());
            connection.setAutoCommit(false);
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        String description = "Регистрация успешно завершена";
        return new NullClient(description);
    }
}
