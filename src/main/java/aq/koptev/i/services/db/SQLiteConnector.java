package aq.koptev.i.services.db;

import java.sql.*;

public class SQLiteConnector implements DBConnector {

    public static String DEFAULT_DB_URL = "jdbc:sqlite:src/main/resources/db/chat-db.db";

    private Connection connection;
    private PreparedStatement preparedStatement;

    @Override
    public Connection getConnection(String url) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(url);
        return connection;
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection connection, String sql) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        return preparedStatement;
    }

    @Override
    public void closeConnection() throws SQLException {
        connection.close();
    }
}
