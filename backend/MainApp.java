import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.Scanner;

public class MainApp {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Define API Routes for Core Tables
        server.createContext("/api/users", new GenericTableHandler("APP_USER"));
        server.createContext("/api/stocks", new GenericTableHandler("STOCK"));
        server.createContext("/api/portfolios", new GenericTableHandler("PORTFOLIO"));
        server.createContext("/api/transactions", new GenericTableHandler("TRANSACTIONS"));
        server.createContext("/api/watchlists", new GenericTableHandler("WATCHLIST"));
        
        // Dynamic SQL Simulator Endpoint
        server.createContext("/api/query", new SqlQueryHandler());

        server.setExecutor(null);
        System.out.println("==================================================");
        System.out.println(" Stock Portfolio Oracle API Server Running! ");
        System.out.println(" Listening on: http://localhost:" + PORT);
        System.out.println("==================================================");
        server.start();
    }

    // Generic JSON Table Fetcher
    static class GenericTableHandler implements HttpHandler {
        private final String tableName;

        public GenericTableHandler(String tableName) {
            this.tableName = tableName;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Enable CORS for Frontend Communication
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String jsonResponse = fetchTableAsJson(tableName);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes());
                os.close();
            }
        }
    }

    // Custom SQL Execution Handler
    static class SqlQueryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String query = scanner.hasNext() ? scanner.next() : "";
                
                String jsonResponse = executeRawQuery(query);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes());
                os.close();
            }
        }
    }

    // Helper: Convert Oracle SQL ResultSet to JSON
    private static String fetchTableAsJson(String tableName) {
        StringBuilder json = new StringBuilder("[");
        String sql = "SELECT * FROM " + tableName;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            boolean firstRow = true;

            while (rs.next()) {
                if (!firstRow) json.append(",");
                json.append("{");
                for (int i = 1; i <= columnCount; i++) {
                    String colName = metaData.getColumnName(i);
                    String val = rs.getString(i);
                    json.append("\"").append(colName).append("\":\"").append(val != null ? val : "").append("\"");
                    if (i < columnCount) json.append(",");
                }
                json.append("}");
                firstRow = false;
            }
        } catch (SQLException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
        json.append("]");
        return json.toString();
    }

    // Helper: Execute Arbitrary SELECT SQL
    private static String executeRawQuery(String sql) {
        if (!sql.trim().toUpperCase().startsWith("SELECT")) {
            return "{\"error\":\"Only SELECT queries are allowed.\"}";
        }
        StringBuilder json = new StringBuilder("[");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            boolean firstRow = true;

            while (rs.next()) {
                if (!firstRow) json.append(",");
                json.append("{");
                for (int i = 1; i <= columnCount; i++) {
                    String colName = metaData.getColumnName(i);
                    String val = rs.getString(i);
                    json.append("\"").append(colName).append("\":\"").append(val != null ? val : "").append("\"");
                    if (i < columnCount) json.append(",");
                }
                json.append("}");
                firstRow = false;
            }
        } catch (SQLException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
        json.append("]");
        return json.toString();
    }
}