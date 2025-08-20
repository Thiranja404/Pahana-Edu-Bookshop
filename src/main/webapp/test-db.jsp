<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*" %>
<%@ page import="javax.sql.DataSource" %>
<%@ page import="com.pahanaedu.bookstore.util.DataSourceSingleton" %>
<!DOCTYPE html>
<html>
<head>
    <title>Database Test</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .success { color: green; }
        .error { color: red; }
        .info { color: blue; }
        table { border-collapse: collapse; width: 100%; margin-top: 10px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <h1>Database Connection Test</h1>
    
    <%
    try {
        DataSource dataSource = DataSourceSingleton.getInstance();
        
        out.println("<p class='success'>✅ DataSource instance created successfully</p>");
        
        try (Connection conn = dataSource.getConnection()) {
            out.println("<p class='success'>✅ Database connection established</p>");
            out.println("<p class='info'>Database URL: " + conn.getMetaData().getURL() + "</p>");
            
            // Test customers table
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM customers");
                if (rs.next()) {
                    int customerCount = rs.getInt("count");
                    out.println("<p class='info'>Customer count: " + customerCount + "</p>");
                }
            }
            
            // Test items table
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM items");
                if (rs.next()) {
                    int itemCount = rs.getInt("count");
                    out.println("<p class='info'>Item count: " + itemCount + "</p>");
                }
            }
            
            // Show some customers
            out.println("<h2>Sample Customers:</h2>");
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM customers LIMIT 5");
                out.println("<table>");
                out.println("<tr><th>ID</th><th>Account Number</th><th>Name</th><th>Address</th><th>Phone</th></tr>");
                
                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + rs.getInt("id") + "</td>");
                    out.println("<td>" + rs.getString("account_number") + "</td>");
                    out.println("<td>" + rs.getString("name") + "</td>");
                    out.println("<td>" + rs.getString("address") + "</td>");
                    out.println("<td>" + rs.getString("phone") + "</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            }
            
            // Show some items
            out.println("<h2>Sample Items:</h2>");
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM items LIMIT 5");
                out.println("<table>");
                out.println("<tr><th>ID</th><th>SKU</th><th>Name</th><th>Price</th><th>Stock</th><th>Active</th></tr>");
                
                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + rs.getInt("id") + "</td>");
                    out.println("<td>" + rs.getString("sku") + "</td>");
                    out.println("<td>" + rs.getString("name") + "</td>");
                    out.println("<td>$" + rs.getBigDecimal("price") + "</td>");
                    out.println("<td>" + rs.getInt("stock_quantity") + "</td>");
                    out.println("<td>" + rs.getBoolean("active") + "</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            }
            
        }
        
    } catch (SQLException e) {
        out.println("<p class='error'>❌ Database error: " + e.getMessage() + "</p>");
        e.printStackTrace();
    } catch (Exception e) {
        out.println("<p class='error'>❌ Error: " + e.getMessage() + "</p>");
        e.printStackTrace();
    }
    %>
    
    <p><a href="<%= request.getContextPath() %>">Back to Home</a></p>
</body>
</html>