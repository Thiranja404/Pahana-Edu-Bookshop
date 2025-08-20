package com.pahanaedu.bookstore.controller;

import com.pahanaedu.bookstore.util.DataSourceSingleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuickSearchServlet extends HttpServlet {
    
    private DataSource dataSource;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.dataSource = DataSourceSingleton.getInstance();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String type = request.getParameter("t");
        String query = request.getParameter("q");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        
        try (PrintWriter out = response.getWriter()) {
            if (query == null || query.trim().isEmpty()) {
                out.print("[]");
                return;
            }
            
            String result = "";
            if ("c".equals(type)) {
                result = searchCustomers(query.trim());
            } else if ("i".equals(type)) {
                result = searchItems(query.trim());
            } else if ("b".equals(type)) {
                result = searchBills(query.trim());
            } else if ("bill".equals(type)) {
                result = getBillDetails(query.trim());
            } else {
                out.print("[]");
                return;
            }
            
            out.print(result);
            out.flush();
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("[]");
            }
        }
    }
    
    private String searchCustomers(String query) {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        
        String sql = "SELECT id, account_number, name, address, phone FROM customers " +
                    "WHERE account_number LIKE ? OR name LIKE ? " +
                    "ORDER BY CASE " +
                    "  WHEN account_number LIKE ? THEN 1 " +
                    "  WHEN name LIKE ? THEN 2 " +
                    "  ELSE 3 END " +
                    "LIMIT 10";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String prefix = query + "%";
            String contains = "%" + query + "%";
            
            stmt.setString(1, prefix);
            stmt.setString(2, contains);
            stmt.setString(3, prefix);
            stmt.setString(4, prefix);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;
                    
                    json.append("{");
                    json.append("\"id\":").append(rs.getInt("id")).append(",");
                    json.append("\"accountNumber\":\"").append(escapeJson(rs.getString("account_number"))).append("\",");
                    json.append("\"name\":\"").append(escapeJson(rs.getString("name"))).append("\",");
                    json.append("\"address\":\"").append(escapeJson(rs.getString("address"))).append("\",");
                    json.append("\"phone\":\"").append(escapeJson(rs.getString("phone"))).append("\"");
                    json.append("}");
                }
            }
        } catch (SQLException e) {
            // Silent fail
        }
        
        json.append("]");
        return json.toString();
    }
    
    private String searchItems(String query) {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        
        // Simple direct query based on what we see in the database
        String sql = "SELECT id, sku, name, unit_price, active FROM items " +
                    "WHERE active = 1 AND (sku LIKE ? OR name LIKE ?) " +
                    "ORDER BY sku " +
                    "LIMIT 10";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;
                    
                    json.append("{");
                    json.append("\"id\":").append(rs.getInt("id")).append(",");
                    json.append("\"sku\":\"").append(escapeJson(rs.getString("sku"))).append("\",");
                    json.append("\"name\":\"").append(escapeJson(rs.getString("name"))).append("\",");
                    json.append("\"price\":").append(rs.getBigDecimal("unit_price")).append(",");
                    json.append("\"stockQuantity\":10,"); // Default stock value
                    json.append("\"active\":").append(rs.getBoolean("active"));
                    json.append("}");
                }
            }
        } catch (SQLException e) {
            // Add basic logging to see what's wrong
            System.err.println("Item search error for query '" + query + "': " + e.getMessage());
            e.printStackTrace();
        }
        
        json.append("]");
        return json.toString();
    }
    
    private String searchBills(String customerAccount) {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        
        String sql = "SELECT b.id, b.bill_no, b.bill_date, b.total, b.created_at, " +
                    "       c.name as customer_name, c.account_number " +
                    "FROM bills b " +
                    "JOIN customers c ON b.customer_id = c.account_number " +
                    "WHERE c.account_number = ? " +
                    "ORDER BY b.bill_date DESC, b.created_at DESC " +
                    "LIMIT 50";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerAccount);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;
                    
                    json.append("{");
                    json.append("\"id\":").append(rs.getInt("id")).append(",");
                    json.append("\"billNo\":\"").append(escapeJson(rs.getString("bill_no"))).append("\",");
                    json.append("\"billDate\":\"").append(rs.getDate("bill_date")).append("\",");
                    json.append("\"total\":").append(rs.getBigDecimal("total")).append(",");
                    json.append("\"createdAt\":\"").append(rs.getTimestamp("created_at")).append("\",");
                    json.append("\"customerName\":\"").append(escapeJson(rs.getString("customer_name"))).append("\",");
                    json.append("\"accountNumber\":\"").append(escapeJson(rs.getString("account_number"))).append("\"");
                    json.append("}");
                }
            }
        } catch (SQLException e) {
            // Silent fail
        }
        
        json.append("]");
        return json.toString();
    }
    
    private String getBillDetails(String billNo) {
        StringBuilder json = new StringBuilder("{");
        
        System.out.println("DEBUG: Getting bill details for billNo: " + billNo);
        
        // Step 1: Get bill basic info
        String billSql = "SELECT id, bill_no, customer_id, bill_date, total FROM bills WHERE bill_no = ?";
        
        try (Connection conn = dataSource.getConnection()) {
            
            // Get bill info first
            try (PreparedStatement stmt = conn.prepareStatement(billSql)) {
                stmt.setString(1, billNo);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int billId = rs.getInt("id");
                        String customerId = rs.getString("customer_id");
                        
                        System.out.println("DEBUG: Found bill with ID: " + billId + ", Customer ID: " + customerId);
                        
                        json.append("\"billNo\":\"").append(escapeJson(rs.getString("bill_no"))).append("\",");
                        json.append("\"billDate\":\"").append(rs.getDate("bill_date")).append("\",");
                        json.append("\"total\":").append(rs.getBigDecimal("total")).append(",");
                        
                        // Step 2: Get customer info
                        String customerSql = "SELECT name, account_number, phone, address FROM customers WHERE account_number = ?";
                        try (PreparedStatement custStmt = conn.prepareStatement(customerSql)) {
                            custStmt.setString(1, customerId);
                            
                            try (ResultSet custRs = custStmt.executeQuery()) {
                                if (custRs.next()) {
                                    System.out.println("DEBUG: Found customer: " + custRs.getString("name"));
                                    json.append("\"customerName\":\"").append(escapeJson(custRs.getString("name"))).append("\",");
                                    json.append("\"customerAccount\":\"").append(escapeJson(custRs.getString("account_number"))).append("\",");
                                    json.append("\"customerPhone\":\"").append(escapeJson(custRs.getString("phone") != null ? custRs.getString("phone") : "")).append("\",");
                                    json.append("\"customerAddress\":\"").append(escapeJson(custRs.getString("address") != null ? custRs.getString("address") : "")).append("\",");
                                } else {
                                    System.out.println("DEBUG: Customer not found for ID: " + customerId);
                                    json.append("\"customerName\":\"Customer Not Found\",");
                                    json.append("\"customerAccount\":\"").append(escapeJson(customerId)).append("\",");
                                    json.append("\"customerPhone\":\"\",");
                                    json.append("\"customerAddress\":\"\",");
                                }
                            }
                        }
                        
                        // Step 3: Get bill items
                        String itemsSql = "SELECT bi.qty, bi.unit_price, bi.line_total, " +
                                         "       i.sku, i.name as item_name " +
                                         "FROM bill_items bi " +
                                         "JOIN items i ON bi.item_id = i.id " +
                                         "WHERE bi.bill_id = ? " +
                                         "ORDER BY bi.id";
                        
                        json.append("\"items\":[");
                        try (PreparedStatement itemStmt = conn.prepareStatement(itemsSql)) {
                            itemStmt.setInt(1, billId);
                            
                            try (ResultSet itemRs = itemStmt.executeQuery()) {
                                boolean firstItem = true;
                                int itemCount = 0;
                                while (itemRs.next()) {
                                    if (!firstItem) json.append(",");
                                    firstItem = false;
                                    itemCount++;
                                    
                                    json.append("{");
                                    json.append("\"sku\":\"").append(escapeJson(itemRs.getString("sku"))).append("\",");
                                    json.append("\"name\":\"").append(escapeJson(itemRs.getString("item_name"))).append("\",");
                                    json.append("\"qty\":").append(itemRs.getInt("qty")).append(",");
                                    json.append("\"unitPrice\":").append(itemRs.getBigDecimal("unit_price")).append(",");
                                    json.append("\"lineTotal\":").append(itemRs.getBigDecimal("line_total"));
                                    json.append("}");
                                }
                                System.out.println("DEBUG: Found " + itemCount + " items for bill");
                            }
                        }
                        json.append("]");
                        
                    } else {
                        System.out.println("DEBUG: Bill not found in database for billNo: " + billNo);
                        json.append("\"error\":\"Bill not found in database\"");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DEBUG: SQL Error in getBillDetails for bill '" + billNo + "': " + e.getMessage());
            e.printStackTrace();
            json.append("\"error\":\"Database error: ").append(escapeJson(e.getMessage())).append("\"");
        }
        
        json.append("}");
        String result = json.toString();
        System.out.println("DEBUG: Final JSON response: " + result);
        return result;
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
