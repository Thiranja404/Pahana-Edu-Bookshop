package com.pahanaedu.bookstore.dao;

import com.pahanaedu.bookstore.model.Item;
import com.pahanaedu.bookstore.util.DataSourceSingleton;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemDaoImpl implements ItemDao {
    
    private final DataSource dataSource;
    
    public ItemDaoImpl() {
        this.dataSource = DataSourceSingleton.getInstance();
    }
    
    @Override
    public Item save(Item item) {
        String sql = "INSERT INTO items (sku, name, unit_price, active) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, item.getSku());
            stmt.setString(2, item.getName());
            stmt.setBigDecimal(3, item.getUnitPrice());
            stmt.setBoolean(4, item.isActive());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating item failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating item failed, no ID obtained.");
                }
            }
            
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving item: " + item.getSku(), e);
        }
    }
    
    @Override
    public Optional<Item> findById(int id) {
        String sql = "SELECT id, sku, name, unit_price, active, created_at, updated_at FROM items WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding item by ID: " + id, e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Item> findBySku(String sku) {
        String sql = "SELECT id, sku, name, unit_price, active, created_at, updated_at FROM items WHERE sku = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sku);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding item by SKU: " + sku, e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Item> search(String query) {
        List<Item> items = new ArrayList<>();
        // Enhanced search query with better matching and ranking  
        String sql = "SELECT id, sku, name, unit_price, active, created_at, updated_at " +
                    "FROM items " +
                    "WHERE active = true AND (sku LIKE ? OR sku LIKE ? OR name LIKE ? OR name LIKE ?) " +
                    "ORDER BY " +
                    "  CASE " +
                    "    WHEN sku = ? THEN 1 " +
                    "    WHEN sku LIKE ? THEN 2 " +
                    "    WHEN name LIKE ? THEN 3 " +
                    "    WHEN name LIKE ? THEN 4 " +
                    "    ELSE 5 " +
                    "  END, " +
                    "  name " +
                    "LIMIT 15";
        
        System.out.println("=== ITEM DAO SEARCH DEBUG ===");
        System.out.println("Search query: '" + query + "'");
        System.out.println("SQL: " + sql);
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String exactMatch = query;
            String prefixMatch = query + "%";
            String containsMatch = "%" + query + "%";
            
            System.out.println("Exact match: '" + exactMatch + "'");
            System.out.println("Prefix match: '" + prefixMatch + "'");
            System.out.println("Contains match: '" + containsMatch + "'");
            
            // Set parameters for WHERE clause
            stmt.setString(1, prefixMatch);    // SKU prefix match
            stmt.setString(2, containsMatch);  // SKU contains match
            stmt.setString(3, prefixMatch);    // Name prefix match
            stmt.setString(4, containsMatch);  // Name contains match
            
            // Set parameters for ORDER BY clause
            stmt.setString(5, exactMatch);     // Exact SKU match (highest priority)
            stmt.setString(6, prefixMatch);    // SKU prefix match
            stmt.setString(7, prefixMatch);    // Name prefix match
            stmt.setString(8, containsMatch);  // Name contains match
            
            System.out.println("Executing SQL query...");
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    Item item = mapResultSetToItem(rs);
                    items.add(item);
                    count++;
                    System.out.println("Found item " + count + ": " + item.getSku() + " - " + item.getName() + " ($" + item.getUnitPrice() + ", active: " + item.isActive() + ")");
                }
                System.out.println("Total items found: " + count);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in search: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error searching items with query: " + query, e);
        }
        
        System.out.println("Returning " + items.size() + " items");
        System.out.println("=== ITEM DAO SEARCH DEBUG END ===");
        return items;
    }
    
    @Override
    public List<Item> findAllActive() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT id, sku, name, unit_price, active, created_at, updated_at FROM items WHERE active = true ORDER BY name";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all active items", e);
        }
        
        return items;
    }
    
    @Override
    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT id, sku, name, unit_price, active, created_at, updated_at FROM items ORDER BY name";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all items", e);
        }
        
        return items;
    }
    
    @Override
    public Item update(Item item) {
        String sql = "UPDATE items SET sku = ?, name = ?, unit_price = ?, active = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, item.getSku());
            stmt.setString(2, item.getName());
            stmt.setBigDecimal(3, item.getUnitPrice());
            stmt.setBoolean(4, item.isActive());
            stmt.setInt(5, item.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating item failed, no rows affected.");
            }
            
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating item: " + item.getId(), e);
        }
    }
    
    @Override
    public void deactivate(int id) {
        String sql = "UPDATE items SET active = false WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating item: " + id, e);
        }
    }
    
    @Override
    public void activate(int id) {
        String sql = "UPDATE items SET active = true WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error activating item: " + id, e);
        }
    }
    
    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setSku(rs.getString("sku"));
        item.setName(rs.getString("name"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setActive(rs.getBoolean("active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            item.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            item.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return item;
    }
}
