import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    // Oracle XE connection URL (Port 1521, SID 'xe')
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "system"; 
    private static final String PASS = "sankhya123"; // Update this if your Oracle password is different

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Testing Direct Connection to Oracle Database...");
        System.out.println("==================================================");
        
        try {
            // Register Oracle JDBC Driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
            
            // Establish connection
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("SUCCESS: Connection established successfully!");
                System.out.println("Database Product Name : " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Database Version      : " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println("Driver Version        : " + conn.getMetaData().getDriverVersion());
                System.out.println("==================================================");
                conn.close();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("\nERROR: Oracle JDBC Driver (ojdbc8.jar) not found!");
            System.err.println("Make sure ojdbc8.jar is included in your classpath execution command.");
        } catch (SQLException e) {
            System.err.println("\nERROR: Failed to connect to Oracle SQL Database!");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message  : " + e.getMessage());
        }
    }
}