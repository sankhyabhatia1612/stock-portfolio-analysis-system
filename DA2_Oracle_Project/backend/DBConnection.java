
package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_URL =
            "jdbc:oracle:thin:@localhost:1521/xepdb1";

    private static final String DB_USER = "STOCK_APP";

    
    private static final String DB_PASS = "StockApp123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DB_URL,
                DB_USER,
                DB_PASS
        );
    }
}
