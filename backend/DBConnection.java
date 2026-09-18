package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Reads credentials from System Environment Variables (DB_URL, DB_USER, DB_PASS).
    // Falls back to safe default placeholders if environment variables are not set.
    private static final String URL = System.getenv("DB_URL") != null 
            ? System.getenv("DB_URL") 
            : "jdbc:oracle:thin:@localhost:1521:xe";

    private static final String USER = System.getenv("DB_USER") != null 
            ? System.getenv("DB_USER") 
            : "c##portfolio_user";

    private static final String PASSWORD = System.getenv("DB_PASS") != null 
            ? System.getenv("DB_PASS") 
            : "YOUR_DATABASE_PASSWORD";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found in classpath.", e);
        }
    }
}