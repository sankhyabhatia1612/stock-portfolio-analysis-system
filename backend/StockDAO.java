import java.sql.*;

public class StockDAO {

    public void getAllStocks() {
        String query = "SELECT stock_id, stock_symbol, stock_name, exchange, sector, current_price FROM stock";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n------------------------------------------------------------------------------------");
            System.out.printf("%-10s | %-12s | %-30s | %-10s | %-10s | %-10s%n", 
                              "ID", "SYMBOL", "NAME", "EXCHANGE", "SECTOR", "PRICE (INR)");
            System.out.println("------------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10s | %-12s | %-30s | %-10s | %-10s | %-10.2f%n",
                    rs.getString("stock_id"),
                    rs.getString("stock_symbol"),
                    rs.getString("stock_name"),
                    rs.getString("exchange"),
                    rs.getString("sector"),
                    rs.getDouble("current_price")
                );
            }
            System.out.println("------------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }

    public void getUserPortfolioSummary(String userId) {
        String query = "SELECT p.portfolio_name, p.cash_balance, " +
                       "NVL(SUM(h.total_quantity * s.current_price), 0) AS total_holding_value " +
                       "FROM portfolio p " +
                       "LEFT JOIN holding h ON p.portfolio_id = h.portfolio_id " +
                       "LEFT JOIN stock s ON h.stock_id = s.stock_id " +
                       "WHERE p.user_id = ? " +
                       "GROUP BY p.portfolio_name, p.cash_balance";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- PORTFOLIO SUMMARY FOR USER: " + userId + " ---");
            while (rs.next()) {
                double cash = rs.getDouble("cash_balance");
                double holdings = rs.getDouble("total_holding_value");
                double totalAsset = cash + holdings;

                System.out.println("Portfolio: " + rs.getString("portfolio_name"));
                System.out.printf("Cash Balance  : INR %.2f%n", cash);
                System.out.printf("Holdings Value: INR %.2f%n", holdings);
                System.out.printf("Total Assets  : INR %.2f%n%n", totalAsset);
            }

        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }
}