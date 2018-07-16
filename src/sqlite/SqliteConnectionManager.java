package sqlite;


import utils.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteConnectionManager {
    private static Connection connection;

    public static Connection getConnection() {
        if (SqliteConnectionManager.connection == null) {
            SqliteConnectionManager.connection = createConnection();
        }
        return SqliteConnectionManager.connection;
    }

    public static Connection createConnection() {
        Connection connection1 = null;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection1 = DriverManager.getConnection(Constants.JDBC_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("sqlite connected!");

        return connection1;
    }

    static {
        SqliteConnectionManager.connection = null;
    }
}
