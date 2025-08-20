package com.pahanaedu.bookstore.dao;

import com.pahanaedu.bookstore.model.Customer;
import com.pahanaedu.bookstore.util.DataSourceSingleton;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDaoImpl implements CustomerDao {
    
    private final DataSource dataSource;
    
    public CustomerDaoImpl() {
        this.dataSource = DataSourceSingleton.getInstance();
    }
    
    @Override
    public Customer save(Customer customer) {
        String sql = "INSERT INTO customers (name, address, phone) VALUES (?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPhone());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getInt(1));
                    // Generate account number
                    String accountNumber = "ACC" + String.format("%06d", customer.getId());
                    customer.setAccountNumber(accountNumber);
                    
                    // Update with account number
                    updateAccountNumber(customer.getId(), accountNumber);
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
            
            return customer;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving customer: " + customer.getName(), e);
        }
    }
    
    private void updateAccountNumber(int customerId, String accountNumber) throws SQLException {
        String sql = "UPDATE customers SET account_number = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            stmt.setInt(2, customerId);
            stmt.executeUpdate();
        }
    }
    
    @Override
    public Optional<Customer> findById(int id) {
        String sql = "SELECT id, account_number, name, address, phone, created_at, updated_at FROM customers WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding customer by ID: " + id, e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Customer> findByAccountNumber(String accountNumber) {
        String sql = "SELECT id, account_number, name, address, phone, created_at, updated_at FROM customers WHERE account_number = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding customer by account number: " + accountNumber, e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Customer> search(String query) {
        List<Customer> customers = new ArrayList<>();
        // Enhanced search query with better matching and ranking
        String sql = "SELECT id, account_number, name, address, phone, created_at, updated_at " +
                    "FROM customers " +
                    "WHERE account_number LIKE ? OR account_number LIKE ? OR name LIKE ? OR name LIKE ? " +
                    "ORDER BY " +
                    "  CASE " +
                    "    WHEN account_number = ? THEN 1 " +
                    "    WHEN account_number LIKE ? THEN 2 " +
                    "    WHEN name LIKE ? THEN 3 " +
                    "    WHEN name LIKE ? THEN 4 " +
                    "    ELSE 5 " +
                    "  END, " +
                    "  name " +
                    "LIMIT 15";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String exactMatch = query;
            String prefixMatch = query + "%";
            String containsMatch = "%" + query + "%";
            
            // Set parameters for WHERE clause
            stmt.setString(1, prefixMatch);    // Account number prefix match
            stmt.setString(2, containsMatch);  // Account number contains match
            stmt.setString(3, prefixMatch);    // Name prefix match  
            stmt.setString(4, containsMatch);  // Name contains match
            
            // Set parameters for ORDER BY clause
            stmt.setString(5, exactMatch);     // Exact account number match (highest priority)
            stmt.setString(6, prefixMatch);    // Account number prefix match
            stmt.setString(7, prefixMatch);    // Name prefix match
            stmt.setString(8, containsMatch);  // Name contains match
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching customers with query: " + query, e);
        }
        
        return customers;
    }
    
    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, account_number, name, address, phone, created_at, updated_at FROM customers ORDER BY name";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all customers", e);
        }
        
        return customers;
    }
    
    @Override
    public Customer update(Customer customer) {
        String sql = "UPDATE customers SET name = ?, address = ?, phone = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPhone());
            stmt.setInt(4, customer.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating customer failed, no rows affected.");
            }
            
            return customer;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating customer: " + customer.getId(), e);
        }
    }
    
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting customer: " + id, e);
        }
    }
    
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setAccountNumber(rs.getString("account_number"));
        customer.setName(rs.getString("name"));
        customer.setAddress(rs.getString("address"));
        customer.setPhone(rs.getString("phone"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            customer.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            customer.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return customer;
    }
}
