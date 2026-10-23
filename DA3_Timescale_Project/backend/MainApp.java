package backend;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class MainApp {
    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(
            new InetSocketAddress(PORT), 0
        );

        server.createContext("/api/users", ex -> handle(ex, "users"));
        server.createContext("/api/stocks", ex -> handle(ex, "stocks"));
        server.createContext("/api/portfolios", ex -> handle(ex, "portfolios"));
        server.createContext("/api/transactions", ex -> handle(ex, "transactions"));
        server.createContext("/api/watchlists", ex -> handle(ex, "watchlists"));

        server.start();
        System.out.println("Stock Portfolio API running on port " + PORT);
    }

    private static Connection db() throws SQLException {
        return DBConnection.getConnection();
    }

    private static void handle(HttpExchange ex, String resource)
            throws IOException {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        ex.getResponseHeaders().set(
            "Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"
        );

        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 204, "");
            return;
        }

        try {
            String method = ex.getRequestMethod().toUpperCase();
            String result;

            if ("GET".equals(method)) {
                result = get(resource);
            } else if ("POST".equals(method) || "PUT".equals(method)) {
                result = mutate(ex, resource, "PUT".equals(method));
            } else if ("DELETE".equals(method)) {
                result = delete(ex, resource);
            } else {
                send(ex, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            int status = result.startsWith("{\"error\"")
                ? 400
                : ("POST".equals(method) ? 201 : 200);

            send(ex, status, result);

        } catch (Exception e) {
            e.printStackTrace();
            send(ex, 500, jsonError(e.getMessage()));
        }
    }

    private static String get(String resource) throws SQLException {
        String sql;

        switch (resource) {
            case "users":
                sql = "SELECT u.USER_ID, u.FNAME, u.MNAME, u.LNAME, " +
                    "u.DOB, u.EMAIL, u.USER_STATUS, i.RISK_PROFILE " +
                    "FROM APP_USER u LEFT JOIN INVESTOR i " +
                    "ON i.USER_ID=u.USER_ID ORDER BY u.USER_ID";
                break;

            case "stocks":
                sql = "SELECT STOCK_ID, STOCK_SYMBOL, STOCK_NAME, " +
                    "EXCHANGE, SECTOR, CURRENT_PRICE, LAST_UPDATED " +
                    "FROM STOCK ORDER BY STOCK_ID";
                break;

            case "portfolios":
                sql = "SELECT PORTFOLIO_ID, USER_ID, PORTFOLIO_NAME, " +
                    "CASH_BALANCE FROM PORTFOLIO ORDER BY PORTFOLIO_ID";
                break;

            case "transactions":
                sql = "SELECT t.TRANS_ID, p.USER_ID, t.PORTFOLIO_ID, " +
                    "t.STOCK_ID, " +
                    "CASE WHEN b.TRANS_ID IS NOT NULL THEN 'BUY' " +
                    "WHEN s.TRANS_ID IS NOT NULL THEN 'SELL' " +
                    "ELSE 'BUY' END AS TRANS_TYPE, " +
                    "t.QUANTITY, COALESCE(st.CURRENT_PRICE,0) AS PRICE, " +
                    "COALESCE(t.TRANS_FEE,0) AS TRANS_FEE, " +
                    "t.TRANS_DATE FROM TRANSACTIONS t " +
                    "JOIN PORTFOLIO p ON p.PORTFOLIO_ID=t.PORTFOLIO_ID " +
                    "LEFT JOIN STOCK st ON st.STOCK_ID=t.STOCK_ID " +
                    "LEFT JOIN BUY_TRANSACTION b ON b.TRANS_ID=t.TRANS_ID " +
                    "LEFT JOIN SELL_TRANSACTION s ON s.TRANS_ID=t.TRANS_ID " +
                    "ORDER BY t.TRANS_DATE DESC";
                break;

            case "watchlists":
                sql = "SELECT wi.USER_ID, wi.WATCH_ID, ws.STOCK_ID, " +
                    "st.STOCK_SYMBOL, wi.ADDED_AT FROM WATCHLIST_ITEM wi " +
                    "LEFT JOIN WATCHLIST_STOCK ws ON " +
                    "ws.USER_ID=wi.USER_ID AND ws.WATCH_ID=wi.WATCH_ID " +
                    "LEFT JOIN STOCK st ON st.STOCK_ID=ws.STOCK_ID " +
                    "ORDER BY wi.USER_ID, wi.WATCH_ID";
                break;

            default:
                throw new SQLException("Unknown resource");
        }

        try (Connection c = db();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData md = rs.getMetaData();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();

                for (int i = 1; i <= md.getColumnCount(); i++) {
                    Object value = rs.getObject(i);

                    if (value instanceof Timestamp ||
                            value instanceof java.sql.Date) {
                        value = value.toString();
                    }

                    String label = md.getColumnLabel(i);
                    // Standard Uppercase mapping
                    row.put(label.toUpperCase(), value);
                    // Lowercase mapping for frontend JS compatibility
                    row.put(label.toLowerCase(), value);
                }

                rows.add(row);
            }

            return toJson(rows);
        }
    }

    private static String mutate(
            HttpExchange ex, String resource, boolean update)
            throws Exception {
        Map<String, String> query =
            query(ex.getRequestURI().getRawQuery());

        Map<String, String> data = parseObject(
            new String(
                ex.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            )
        );

        try (Connection c = db()) {
            c.setAutoCommit(false);

            try {
                switch (resource) {
                    case "users":
                        saveUser(c, data, query, update);
                        break;

                    case "stocks":
                        saveGeneric(c, "STOCK", "STOCK_ID", data, query,
                            update, new String[] {
                                "STOCK_ID", "STOCK_SYMBOL", "STOCK_NAME",
                                "EXCHANGE", "SECTOR", "CURRENT_PRICE"
                            });
                        break;

                    case "portfolios":
                        saveGeneric(c, "PORTFOLIO", "PORTFOLIO_ID",
                            data, query, update, new String[] {
                                "PORTFOLIO_ID", "USER_ID",
                                "PORTFOLIO_NAME", "CASH_BALANCE"
                            });
                        break;

                    case "transactions":
                        saveTransaction(c, data, query, update);
                        break;

                    case "watchlists":
                        saveWatchlist(c, data, query, update);
                        break;

                    default:
                        throw new SQLException("Unknown resource");
                }

                c.commit();
                return "{\"success\":true}";

            } catch (Exception e) {
                c.rollback();
                return jsonError(e.getMessage());
            }
        }
    }

    private static void saveUser(
            Connection c, Map<String, String> d,
            Map<String, String> q, boolean update)
            throws SQLException {
        String id = first(d, "USER_ID", "userId", "user_id", "id");
        String risk = first(d, "RISK_PROFILE", "riskProfile", "risk_profile", "risk");

        Map<String, String> user = new LinkedHashMap<>(d);
        user.remove("RISK_PROFILE");
        user.remove("riskProfile");
        user.remove("risk_profile");
        user.remove("risk");

        if (update) {
            updateRow(c, "APP_USER", "USER_ID",
                q.getOrDefault("id", id), user,
                new String[] {
                    "FNAME", "MNAME", "LNAME", "DOB", "EMAIL",
                    "PASSWORD_HASH", "USER_STATUS"
                });
        } else {
            insertRow(c, "APP_USER", user,
                new String[] {
                    "USER_ID", "FNAME", "MNAME", "LNAME", "DOB",
                    "EMAIL", "PASSWORD_HASH", "USER_STATUS"
                });
        }

        if (risk != null && !risk.trim().isEmpty()) {
            try (PreparedStatement p = c.prepareStatement(
                    "UPDATE INVESTOR SET RISK_PROFILE=? WHERE USER_ID=?")) {
                p.setString(1, risk);
                p.setString(2, id);

                if (p.executeUpdate() == 0) {
                    try (PreparedStatement ins = c.prepareStatement(
                            "INSERT INTO INVESTOR (USER_ID,RISK_PROFILE) " +
                            "VALUES (?,?)")) {
                        ins.setString(1, id);
                        ins.setString(2, risk);
                        ins.executeUpdate();
                    }
                }
            }
        }
    }

    private static void saveGeneric(
            Connection c, String table, String pk,
            Map<String, String> d, Map<String, String> q,
            boolean update, String[] allowed)
            throws SQLException {
        String primaryKeyVal = first(d, pk, pk.toLowerCase());
        if (update) {
            updateRow(c, table, pk,
                q.getOrDefault("id", primaryKeyVal), d, allowed);
        } else {
            insertRow(c, table, d, allowed);
        }
    }

    private static void saveTransaction(
            Connection c, Map<String, String> d,
            Map<String, String> q, boolean update)
            throws SQLException {
        String id = first(d, "TRANS_ID", "transId", "trans_id", "id");
        String user = first(d, "USER_ID", "userId", "user_id");
        String portfolio = first(d, "PORTFOLIO_ID", "portfolioId", "portfolio_id");
        String stock = first(d, "STOCK_ID", "stockId", "stock_id");
        String type = first(d, "TRANS_TYPE", "type", "trans_type");
        String qty = first(d, "QUANTITY", "qty", "quantity");
        String price = first(d, "PRICE", "price");
        String fee = first(d, "TRANS_FEE", "transFee", "trans_fee");

        if (portfolio == null && user != null) {
            try (PreparedStatement p = c.prepareStatement(
                    "SELECT PORTFOLIO_ID FROM PORTFOLIO " +
                    "WHERE USER_ID=? ORDER BY PORTFOLIO_ID")) {
                p.setString(1, user);

                try (ResultSet rs = p.executeQuery()) {
                    if (rs.next()) {
                        portfolio = rs.getString(1);
                    }
                }
            }
        }

        if (portfolio == null) {
            throw new SQLException(
                "No portfolio exists for this user. Select a user who has a portfolio."
            );
        }

        if (id == null || id.trim().isEmpty() ||
                stock == null || stock.trim().isEmpty() ||
                qty == null || qty.trim().isEmpty()) {
            throw new SQLException(
                "Transaction ID, Stock ID and quantity are required."
            );
        }

        if (fee == null || fee.trim().isEmpty()) {
            fee = "0";
        }

        Map<String, String> row = new LinkedHashMap<>();
        row.put("TRANS_ID", id);
        row.put("PORTFOLIO_ID", portfolio);
        row.put("STOCK_ID", stock);
        row.put("QUANTITY", qty);
        row.put("TRANS_FEE", fee);

        String transDate = first(d, "TRANS_DATE", "transDate", "trans_date");

        if (transDate == null || transDate.trim().isEmpty()) {
            transDate = new java.text.SimpleDateFormat("yyyy-MM-dd")
                .format(new java.util.Date());
        }

        row.put("TRANS_DATE", transDate);

        String targetId = q.getOrDefault("id", id);

        if (update) {
            updateRow(c, "TRANSACTIONS", "TRANS_ID",
                targetId, row,
                new String[] {
                    "PORTFOLIO_ID", "STOCK_ID", "QUANTITY",
                    "TRANS_FEE", "TRANS_DATE"
                });

            deleteWhere(c, "BUY_TRANSACTION", "TRANS_ID", targetId);
            deleteWhere(c, "SELL_TRANSACTION", "TRANS_ID", targetId);
        } else {
            insertRow(c, "TRANSACTIONS", row,
                new String[] {
                    "TRANS_ID", "PORTFOLIO_ID", "STOCK_ID",
                    "QUANTITY", "TRANS_FEE", "TRANS_DATE"
                });
        }

        if ("SELL".equalsIgnoreCase(type)) {
            try (PreparedStatement p = c.prepareStatement(
                "INSERT INTO SELL_TRANSACTION " +
                "(SELL_ID,TRANS_ID,SELL_BROKERAGE) VALUES (?,?,?)")) {
                p.setString(1, nextId(c, "SELL_TRANSACTION",
                    "SELL_ID", "SE"));
                p.setString(2, id);
                p.setBigDecimal(3, new java.math.BigDecimal(fee));
                p.executeUpdate();
            }
        } else {
            try (PreparedStatement p = c.prepareStatement(
                "INSERT INTO BUY_TRANSACTION " +
                "(BUY_ID,TRANS_ID,BUY_BROKERAGE) VALUES (?,?,?)")) {
                p.setString(1, nextId(c, "BUY_TRANSACTION",
                    "BUY_ID", "BY"));
                p.setString(2, id);
                p.setBigDecimal(3, new java.math.BigDecimal(fee));
                p.executeUpdate();
            }
        }
    }

    private static String nextId(
            Connection c, String table, String column, String prefix)
            throws SQLException {
        int max = 0;

        try (Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                 "SELECT " + column + " FROM " + table)) {
            while (rs.next()) {
                String value = rs.getString(1);

                if (value != null) {
                    Matcher m = Pattern.compile("(\\d+)$").matcher(value);

                    if (m.find()) {
                        try {
                            max = Math.max(max, Integer.parseInt(m.group(1)));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }

        return prefix + String.format("%03d", max + 1);
    }

    private static void saveWatchlist(
            Connection c, Map<String, String> d,
            Map<String, String> q, boolean update)
            throws SQLException {
        String user = first(d, "USER_ID", "userId", "user_id");
        String watch = first(d, "WATCH_ID", "watchId", "watch_id", "id");
        String stock = first(d, "STOCK_ID", "stockId", "stock_id");

        if (user == null || watch == null) {
            throw new SQLException("User ID and Watchlist ID are required.");
        }

        if (update) {
            String oldUser = q.getOrDefault("userId", user);
            String oldWatch = q.getOrDefault("watchId", watch);

            deleteWatchlist(c, oldUser, oldWatch);
        }

        try (PreparedStatement check = c.prepareStatement(
                "SELECT COUNT(*) FROM WATCHLIST_ITEM " +
                "WHERE USER_ID=? AND WATCH_ID=?")) {
            check.setString(1, user);
            check.setString(2, watch);

            try (ResultSet rs = check.executeQuery()) {
                rs.next();

                if (rs.getInt(1) == 0) {
                    try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO WATCHLIST_ITEM " +
                        "(USER_ID,WATCH_ID,ADDED_AT) " +
                        "VALUES (?,?,CURRENT_TIMESTAMP)")) {
                        p.setString(1, user);
                        p.setString(2, watch);
                        p.executeUpdate();
                    }
                }
            }
        }

        if (stock != null && !stock.trim().isEmpty()) {
            try (PreparedStatement check = c.prepareStatement(
                    "SELECT COUNT(*) FROM WATCHLIST_STOCK " +
                    "WHERE USER_ID=? AND WATCH_ID=? AND STOCK_ID=?")) {
                check.setString(1, user);
                check.setString(2, watch);
                check.setString(3, stock);

                try (ResultSet rs = check.executeQuery()) {
                    rs.next();

                    if (rs.getInt(1) == 0) {
                        try (PreparedStatement p = c.prepareStatement(
                            "INSERT INTO WATCHLIST_STOCK " +
                            "(USER_ID,WATCH_ID,STOCK_ID,REFERENCE_NO) " +
                            "VALUES (?,?,?,?)")) {
                            p.setString(1, user);
                            p.setString(2, watch);
                            p.setString(3, stock);
                            p.setString(4, nextReference(c, user, watch));
                            p.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    private static String nextReference(
            Connection c, String user, String watch)
            throws SQLException {
        try (PreparedStatement p = c.prepareStatement(
                "SELECT COUNT(*) FROM WATCHLIST_STOCK " +
                "WHERE USER_ID=? AND WATCH_ID=?")) {
            p.setString(1, user);
            p.setString(2, watch);

            try (ResultSet r = p.executeQuery()) {
                r.next();
                return String.valueOf(r.getInt(1) + 1);
            }
        }
    }

    private static String delete(
            HttpExchange ex, String resource) throws Exception {
        Map<String, String> q =
            query(ex.getRequestURI().getRawQuery());
        String id = q.get("id");

        try (Connection c = db()) {
            c.setAutoCommit(false);

            try {
                int count = 0;

                switch (resource) {
                    case "users":
                        deleteWhere(c, "WATCHLIST_STOCK", "USER_ID", id);
                        deleteWhere(c, "WATCHLIST_ITEM", "USER_ID", id);
                        deleteWhere(c, "ADVISOR_WATCHLIST", "USER_ID", id);
                        deleteUserChildren(c, id);
                        count = deleteWhere(c, "APP_USER", "USER_ID", id);
                        break;

                    case "stocks":
                        deleteWhere(c, "WATCHLIST_STOCK", "STOCK_ID", id);
                        deleteWhere(c, "STOCK_PRICE_HISTORY", "STOCK_ID", id);
                        deleteWhere(c, "HOLDING", "STOCK_ID", id);

                        List<String> stockTransactions = new ArrayList<>();

                        try (PreparedStatement p = c.prepareStatement(
                                "SELECT TRANS_ID FROM TRANSACTIONS WHERE STOCK_ID=?")) {
                            p.setString(1, id);

                            try (ResultSet rs = p.executeQuery()) {
                                while (rs.next()) {
                                    stockTransactions.add(rs.getString(1));
                                }
                            }
                        }

                        for (String tid : stockTransactions) {
                            deleteWhere(c, "BUY_TRANSACTION", "TRANS_ID", tid);
                            deleteWhere(c, "SELL_TRANSACTION", "TRANS_ID", tid);
                        }

                        deleteWhere(c, "TRANSACTIONS", "STOCK_ID", id);
                        count = deleteWhere(c, "STOCK", "STOCK_ID", id);
                        break;

                    case "portfolios":
                        deleteTransactionsForPortfolio(c, id);
                        count = deleteWhere(c, "PORTFOLIO", "PORTFOLIO_ID", id);
                        break;

                    case "transactions":
                        deleteWhere(c, "BUY_TRANSACTION", "TRANS_ID", id);
                        deleteWhere(c, "SELL_TRANSACTION", "TRANS_ID", id);
                        count = deleteWhere(c, "TRANSACTIONS", "TRANS_ID", id);
                        break;

                    case "watchlists":
                        String user = q.get("userId");
                        if (user == null) user = q.get("user_id");
                        String watch = q.getOrDefault("watchId", q.getOrDefault("watch_id", id));

                        if (user == null) {
                            throw new SQLException("userId is required.");
                        }

                        deleteWatchlist(c, user, watch);
                        count = 1;
                        break;

                    default:
                        throw new SQLException("Unknown resource");
                }

                if (count == 0) {
                    throw new SQLException("Record not found");
                }

                c.commit();
                return "{\"success\":true}";

            } catch (Exception e) {
                c.rollback();
                return jsonError(e.getMessage());
            }
        }
    }

    private static void deleteUserChildren(
            Connection c, String user) throws SQLException {
        List<String> portfolios = new ArrayList<>();

        try (PreparedStatement p = c.prepareStatement(
                "SELECT PORTFOLIO_ID FROM PORTFOLIO WHERE USER_ID=?")) {
            p.setString(1, user);

            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    portfolios.add(rs.getString(1));
                }
            }
        }

        for (String p : portfolios) {
            deleteTransactionsForPortfolio(c, p);
        }

        deleteWhere(c, "PORTFOLIO", "USER_ID", user);
        deleteWhere(c, "ADMIN", "USER_ID", user);
        deleteWhere(c, "ADVISOR", "USER_ID", user);
        deleteWhere(c, "INVESTOR", "USER_ID", user);
    }

    private static void deleteTransactionsForPortfolio(
            Connection c, String portfolio) throws SQLException {
        List<String> ids = new ArrayList<>();

        try (PreparedStatement p = c.prepareStatement(
                "SELECT TRANS_ID FROM TRANSACTIONS WHERE PORTFOLIO_ID=?")) {
            p.setString(1, portfolio);

            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString(1));
                }
            }
        }

        for (String id : ids) {
            deleteWhere(c, "BUY_TRANSACTION", "TRANS_ID", id);
            deleteWhere(c, "SELL_TRANSACTION", "TRANS_ID", id);
        }

        deleteWhere(c, "TRANSACTIONS", "PORTFOLIO_ID", portfolio);
    }

    private static void deleteWatchlist(
            Connection c, String user, String watch)
            throws SQLException {
        deleteWhere(c, "WATCHLIST_STOCK", "USER_ID", user,
            "WATCH_ID", watch);
        deleteWhere(c, "ADVISOR_WATCHLIST", "USER_ID", user,
            "WATCH_ID", watch);

        try (PreparedStatement p = c.prepareStatement(
                "DELETE FROM WATCHLIST_ITEM WHERE USER_ID=? AND WATCH_ID=?")) {
            p.setString(1, user);
            p.setString(2, watch);

            if (p.executeUpdate() == 0) {
                throw new SQLException("Record not found");
            }
        }
    }

    private static int deleteWhere(
            Connection c, String table, String column, String value)
            throws SQLException {
        if (value == null || value.isEmpty()) {
            return 0;
        }

        try (PreparedStatement p = c.prepareStatement(
                "DELETE FROM " + table + " WHERE " + column + "=?")) {
            p.setString(1, value);
            return p.executeUpdate();
        }
    }

    private static int deleteWhere(
            Connection c, String table, String c1, String v1,
            String c2, String v2) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(
                "DELETE FROM " + table + " WHERE " + c1 + "=? AND " + c2 + "=?")) {
            p.setString(1, v1);
            p.setString(2, v2);
            return p.executeUpdate();
        }
    }

    private static void insertRow(
            Connection c, String table, Map<String, String> data,
            String[] allowed) throws SQLException {
        List<String> columns = new ArrayList<>();

        for (String column : allowed) {
            String val = first(data, column, column.toLowerCase());
            if (val != null) {
                columns.add(column);
            }
        }

        if (columns.isEmpty()) {
            throw new SQLException("No valid fields supplied.");
        }

        String sql = "INSERT INTO " + table + " (" +
            String.join(",", columns) + ") VALUES (" +
            String.join(",", Collections.nCopies(columns.size(), "?")) + ")";

        try (PreparedStatement p = c.prepareStatement(sql)) {
            int i = 1;

            for (String column : columns) {
                String val = first(data, column, column.toLowerCase());
                bind(p, i++, column, val);
            }

            p.executeUpdate();
        }
    }

    private static void updateRow(
            Connection c, String table, String pk, String id,
            Map<String, String> data, String[] allowed)
            throws SQLException {
        if (id == null || id.trim().isEmpty()) {
            throw new SQLException("Record ID is required.");
        }

        List<String> columns = new ArrayList<>();

        for (String column : allowed) {
            String val = first(data, column, column.toLowerCase());
            if (!column.equals(pk) && val != null) {
                columns.add(column);
            }
        }

        if (columns.isEmpty()) {
            throw new SQLException("No fields to update.");
        }

        List<String> sets = new ArrayList<>();
        for (String column : columns) {
            sets.add(column + "=?");
        }

        String sql = "UPDATE " + table + " SET " +
            String.join(",", sets) + " WHERE " + pk + "=?";

        try (PreparedStatement p = c.prepareStatement(sql)) {
            int i = 1;

            for (String column : columns) {
                String val = first(data, column, column.toLowerCase());
                bind(p, i++, column, val);
            }

            p.setString(i, id);

            if (p.executeUpdate() == 0) {
                throw new SQLException("Record not found");
            }
        }
    }

    private static void bind(
            PreparedStatement p, int i, String column, String value)
            throws SQLException {
        if (value == null) {
            p.setNull(i, Types.VARCHAR);
            return;
        }

        switch (column.toUpperCase()) {
            case "CURRENT_PRICE":
            case "CASH_BALANCE":
            case "QUANTITY":
            case "TRANS_FEE":
            case "BUY_BROKERAGE":
            case "SELL_BROKERAGE":
                p.setBigDecimal(i, new java.math.BigDecimal(value));
                break;

            case "DOB":
                p.setDate(i, java.sql.Date.valueOf(value.substring(0, 10)));
                break;

            case "TRANS_DATE":
            case "ADDED_AT":
                try {
                    if (value.length() >= 19) {
                        p.setTimestamp(i, Timestamp.valueOf(
                            value.replace("T", " ").substring(0, 19)));
                    } else if (value.length() >= 10) {
                        p.setTimestamp(i, Timestamp.valueOf(
                            value.substring(0, 10) + " 00:00:00"));
                    } else {
                        throw new SQLException("Invalid date format: " + value);
                    }
                } catch (IllegalArgumentException e) {
                    throw new SQLException("Invalid date format: " + value, e);
                }
                break;

            default:
                p.setString(i, value);
        }
    }

    private static Map<String, String> parseObject(String json) {
        Map<String, String> data = new LinkedHashMap<>();

        Matcher matcher = Pattern.compile(
            "\"([^\"]+)\"\\s*:\\s*(null|\"(?:\\\\.|[^\"])*\"|-?\\d+(?:\\.\\d+)?|true|false)",
            Pattern.CASE_INSENSITIVE
        ).matcher(json);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);

            if ("null".equalsIgnoreCase(value)) {
                data.put(key, null);
            } else if (value.startsWith("\"")) {
                value = value.substring(1, value.length() - 1)
                    .replace("\\\"", "\"")
                    .replace("\\n", "\n")
                    .replace("\\\\", "\\");
                data.put(key, value);
            } else {
                data.put(key, value);
            }
        }

        return data;
    }

    private static String first(
            Map<String, String> data, String... keys) {
        for (String key : keys) {
            if (data.containsKey(key)) {
                return data.get(key);
            }
        }

        return null;
    }

    private static Map<String, String> query(String raw)
            throws UnsupportedEncodingException {
        Map<String, String> result = new HashMap<>();

        if (raw == null) {
            return result;
        }

        for (String pair : raw.split("&")) {
            String[] parts = pair.split("=", 2);

            result.put(
                URLDecoder.decode(parts[0], "UTF-8"),
                parts.length > 1
                    ? URLDecoder.decode(parts[1], "UTF-8") : ""
            );
        }

        return result;
    }

    private static String toJson(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof String ||
                value instanceof Timestamp ||
                value instanceof java.sql.Date) {
            return "\"" + escape(String.valueOf(value)) + "\"";
        }

        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        }

        if (value instanceof Map) {
            StringJoiner joiner = new StringJoiner(",", "{", "}");

            ((Map<?, ?>) value).forEach((k, v) ->
                joiner.add("\"" + escape(String.valueOf(k)) +
                    "\":" + toJson(v)));

            return joiner.toString();
        }

        if (value instanceof Iterable) {
            StringJoiner joiner = new StringJoiner(",", "[", "]");

            for (Object item : (Iterable<?>) value) {
                joiner.add(toJson(item));
            }

            return joiner.toString();
        }

        return "\"" + escape(String.valueOf(value)) + "\"";
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
    }

    private static String jsonError(String message) {
        return "{\"error\":\"" + escape(
            message == null ? "Database operation failed" : message
        ) + "\"}";
    }

    private static void send(
            HttpExchange ex, int status, String body)
            throws IOException {
        ex.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8"
        );

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        if (status == 204) {
            ex.sendResponseHeaders(status, -1);
            return;
        }

        ex.sendResponseHeaders(status, bytes.length);

        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }
}