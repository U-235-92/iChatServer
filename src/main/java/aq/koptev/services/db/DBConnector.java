package aq.koptev.services.db;

import java.sql.*;

public interface DBConnector {
    Connection getConnection(String url) throws SQLException, ClassNotFoundException;
    PreparedStatement getPreparedStatement(Connection connection, String sql) throws SQLException;
    void closeConnection() throws SQLException;
}
