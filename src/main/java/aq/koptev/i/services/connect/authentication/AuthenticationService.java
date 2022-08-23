package aq.koptev.i.services.connect.authentication;

import aq.koptev.i.models.*;
import aq.koptev.i.services.db.DBConnector;
import aq.koptev.i.services.db.SQLiteConnector;
import aq.koptev.i.util.ParameterNetObject;
import aq.koptev.i.util.TypeNetObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                ChatLogger.infoFile(String.format("Попытка повторной авторизации пользователя с логином %s", client.getLogin()));
                String text = String.format("Пользователь с логином %s уже авторизован", client.getLogin());
                sendErrorMessage(text);
            } else {
                if(isCorrectPassword(client)) {
                    handler.registerHandler(client);
                    ChatHistory chatHistory = getChatHistory(client);
                    ClientPool clientPool = getClientPool();
                    handler.setChatHistory(chatHistory);
                    handler.setClientPool(clientPool);
                    sendSuccessAuthenticationData(client, chatHistory, clientPool);
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
            String password = rs.getString(1).trim();
            if(client.getPassword().equals(password)) {
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ChatHistory getChatHistory(Client client) {
        ChatHistory chatHistory = new ChatHistory();
        String sql = "SELECT chatHistory FROM Chats WHERE userId = (SELECT userID FROM Users WHERE login = ?)";
        try(Connection connection = connector.getConnection(SQLiteConnector.DEFAULT_DB_URL);
            PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            preparedStatement.setString(1, client.getLogin());
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                byte[] buf = rs.getBytes(1);
                if(buf != null) {
                    try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buf))) {
                        chatHistory = (ChatHistory) objectInputStream.readObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return chatHistory;
    }

    private ClientPool getClientPool() {
        ClientPool clientPool = new ClientPool();
        List<Client> clients = server.getConnectedClients();
        clientPool.addAll(clients);
        return clientPool;
    }

    private void sendSuccessAuthenticationData(Client client, ChatHistory chatHistory, ClientPool clientPool) {
        NetObject netObject = new NetObject(TypeNetObject.SUCCESS_AUTHENTICATION);
        netObject.putData(ParameterNetObject.CHAT_HISTORY, NetObject.getBytes(chatHistory));
        netObject.putData(ParameterNetObject.CLIENT, NetObject.getBytes(client));
        netObject.putData(ParameterNetObject.CLIENT_POOL, NetObject.getBytes(clientPool));
        Message message = new Message(String.format("Пользователь %s подключился к чату!", client.getLogin()));
        netObject.putData(ParameterNetObject.MESSAGE, NetObject.getBytes(message));
        try {
            server.processSendNetObject(netObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendErrorMessage(String text) {
        Message message = new Message(text);
        NetObject netObject = new NetObject(TypeNetObject.ERROR_AUTHENTICATION);
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
