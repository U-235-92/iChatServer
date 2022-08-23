package aq.koptev.i.services.connect.registration;

import aq.koptev.i.models.ChatLogger;
import aq.koptev.i.models.NetObject;
import aq.koptev.i.models.Client;
import aq.koptev.i.models.Message;
import aq.koptev.i.services.db.DBConnector;
import aq.koptev.i.services.db.SQLiteConnector;
import aq.koptev.i.util.ParameterNetObject;
import aq.koptev.i.util.TypeNetObject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationService {
    private DBConnector connector;
    private ObjectOutputStream objectOutputStream;

    public RegistrationService(ObjectOutputStream objectOutputStream) {
        connector = new SQLiteConnector();
        this.objectOutputStream = objectOutputStream;
    }
    public void processRegistration(Client client) {
        if(isAccountExist(client)) {
            String text = "Пользователь с указанным логином уже зарегестрирован";
            sendTextMessage(text);
        } else {
            registrarAccount(client);
        }
    }

    private boolean isAccountExist(Client client) {
        String sql = "SELECT login FROM Users WHERE login = ?";
        try(Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
                PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            preparedStatement.setString(1, client.getLogin());
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void registrarAccount(Client client) {
        String sql = "INSERT INTO Users (login, password) VALUES (?, ?)";
        try (Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
             PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            preparedStatement.setString(1, client.getLogin());
            preparedStatement.setString(2, client.getPassword());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            ChatLogger.errorFile(String.format("Ошибка регистрации пользователя с логином %s", client.getLogin()), getClass());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            ChatLogger.errorFile(String.format("Ошибка регистрации пользователя с логином %s", client.getLogin()), getClass());
            e.printStackTrace();
        }
        ChatLogger.infoFile(String.format("Успешная регистрация пользователя с логином %s", client.getLogin()));
        String text = "Регистрация успешно завершена";
        sendTextMessage(text);
    }

    private void sendTextMessage(String text) {
        Message message = new Message(text);
        NetObject netObject = new NetObject(TypeNetObject.ERROR_REGISTRATION);
        netObject.putData(ParameterNetObject.MESSAGE, NetObject.getBytes(message));
        sendNetObject(netObject);
    }

    private void sendNetObject(NetObject netObject) {
        try {
            objectOutputStream.writeObject(netObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
