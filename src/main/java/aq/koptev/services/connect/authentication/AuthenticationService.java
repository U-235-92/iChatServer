package aq.koptev.services.connect.authentication;

import aq.koptev.models.connect.Handler;
import aq.koptev.models.connect.Server;
import aq.koptev.models.network.NetObject;
import aq.koptev.models.obj.Client;
import aq.koptev.models.obj.Message;
import aq.koptev.models.obj.Meta;
import aq.koptev.services.db.DBConnector;
import aq.koptev.services.db.SQLiteConnector;
import aq.koptev.util.ParameterNetObject;
import aq.koptev.util.TypeNetObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationService {

    private DBConnector connector;
    private ObjectOutputStream objectOutputStream;
    private Handler handler;
    private Server server;

    public AuthenticationService(Server server, Handler handler, ObjectOutputStream objectOutputStream) {
        this.server = server;
        this.handler = handler;
        this.objectOutputStream = objectOutputStream;
        connector = new SQLiteConnector();
    }

    public boolean processAuthentication(Client client) {
        boolean isSuccessAuthentication = false;
        if(isExistAccount(client)) {
            if(isAuthorizeAccount(handler, client)) {
                String text = String.format("Пользователь с логином %s уже авторизован", client.getLogin());
                sendErrorMessage(text);
            } else {
                if(isCorrectPassword(client)) {
                    handler.registerHandler();
                    Meta meta = getMetaByClient(client);
                    handler.setMeta(meta);
                    sendMeta(meta);
                    isSuccessAuthentication = true;
                } else {
                    String text = "Введен неверный пароль";
                    sendErrorMessage(text);
                }
            }
        } else {
            String text = String.format("Пользователя с логином %s не существует", client.getLogin());
            sendErrorMessage(text);
        }
        return isSuccessAuthentication;
    }

    private boolean isExistAccount(Client client) {
        String sql = "SELECT login FROM Users WHERE login = ?";
        try(Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
            PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            preparedStatement.setString(1, client.getLogin());
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAuthorizeAccount(Handler handler, Client client) {
        return handler.isClientConnected(client);
    }

    private boolean isCorrectPassword(Client client) {
        String sql = "SELECT password FROM Users WHERE login = ?";
        try(Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
            PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            preparedStatement.setString(1, client.getLogin());
            ResultSet rs = preparedStatement.executeQuery();
            String password = rs.getString(1);
            if(client.getPassword().equals(password)) {
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sendErrorMessage(String text) {
        Message message = new Message(text);
        NetObject netObject = new NetObject(TypeNetObject.MESSAGE);
        netObject.putData(ParameterNetObject.MESSAGE, NetObject.getBytes(message));
        sendNetObject(netObject);
    }

    private Meta getMetaByClient(Client client) {
        Meta meta = new Meta();
        List<Message> messages = getMessages(client);
        List<Client> clients = server.getConnectedClients();
        meta.setClient(client);
        meta.addClients(clients);
        meta.addMessages(messages);
        return meta;
    }

    private List<Message> getMessages(Client client) {
        List<Message> messages = null;
        String sql = "SELECT chatHistory FROM Chats WHERE userId = (SELECT userID FROM Users WHERE login = ?))";
        try(Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
            PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            preparedStatement.setString(1, client.getLogin());
            ResultSet rs = preparedStatement.executeQuery();
            byte[] buf = rs.getBytes(1);
            if(buf != null) {
                try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buf))) {
                    messages = (List<Message>) objectInputStream.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return messages;
    }

    private void sendMeta(Meta meta) {
        String text = String.format("Пользователь %s подключился к чату", meta.getClient().getLogin());
        Message message = new Message(text);
        NetObject netObject = new NetObject(TypeNetObject.SUCCESS_AUTHENTICATION);
        netObject.putData(ParameterNetObject.META, NetObject.getBytes(meta));
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
