package sqlite;

import java.sql.*;

public class SqliteExecution
{
    public static ResultSet execute(final Connection connection, final String sql) throws SQLException {
        final Statement statement = connection.createStatement();
        statement.setQueryTimeout(20);
        final ResultSet rs = statement.executeQuery(sql);
        return rs;
    }
}

