package com.pahanaedu.bookstore.dao;

import com.pahanaedu.bookstore.model.Bill;
import com.pahanaedu.bookstore.model.BillItem;
import com.pahanaedu.bookstore.model.Customer;
import com.pahanaedu.bookstore.model.Item;
import com.pahanaedu.bookstore.util.DataSourceSingleton;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BillDaoImpl implements BillDao {
    
    private final DataSource dataSource;
    
    public BillDaoImpl() {
        this.dataSource = DataSourceSingleton.getInstance();
    }
    
    @Override
    public Bill save(Bill bill) {
        String sql = "INSERT INTO bills (bill_no, customer_id, bill_date, total) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, bill.getBillNo());
            stmt.setString(2, bill.getCustomerId());  // Now stores account number
            stmt.setTimestamp(3, Timestamp.valueOf(bill.getBillDate()));
            stmt.setBigDecimal(4, bill.getTotal());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating bill failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bill.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating bill failed, no ID obtained.");
                }
            }
            
            return bill;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving bill: " + bill.getBillNo(), e);
        }
    }
    
    @Override
    public Optional<Bill> findById(int id) {
        String sql = "SELECT b.id, b.bill_no, b.customer_id, b.bill_date, b.total, b.created_at, " +
                    "c.account_number, c.name as customer_name, c.address, c.phone " +
                    "FROM bills b " +
                    "LEFT JOIN customers c ON b.customer_id = c.id " +
                    "WHERE b.id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Bill bill = mapResultSetToBill(rs);
                    bill.setBillItems(findBillItemsByBillId(id));
                    return Optional.of(bill);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bill by ID: " + id, e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Bill> findByBillNo(String billNo) {
        String sql = "SELECT b.id, b.bill_no, b.customer_id, b.bill_date, b.total, b.created_at, " +
                    "c.account_number, c.name as customer_name, c.address, c.phone " +
                    "FROM bills b " +
                    "LEFT JOIN customers c ON b.customer_id = c.account_number " +
                    "WHERE b.bill_no = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, billNo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Bill bill = mapResultSetToBill(rs);
                    bill.setBillItems(findBillItemsByBillId(bill.getId()));
                    return Optional.of(bill);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bill by bill number: " + billNo, e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Bill> findByCustomerId(String customerAccountNumber) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT b.id, b.bill_no, b.customer_id, b.bill_date, b.total, b.created_at, " +
                    "c.account_number, c.name as customer_name, c.address, c.phone " +
                    "FROM bills b " +
                    "LEFT JOIN customers c ON b.customer_id = c.account_number " +
                    "WHERE b.customer_id = ? " +
                    "ORDER BY b.bill_date DESC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerAccountNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapResultSetToBill(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bills by customer account number: " + customerAccountNumber, e);
        }
        
        return bills;
    }
    
    @Override
    public List<Bill> findAll() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT b.id, b.bill_no, b.customer_id, b.bill_date, b.total, b.created_at, " +
                    "c.account_number, c.name as customer_name, c.address, c.phone " +
                    "FROM bills b " +
                    "LEFT JOIN customers c ON b.customer_id = c.id " +
                    "ORDER BY b.bill_date DESC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all bills", e);
        }
        
        return bills;
    }
    
    @Override
    public Bill update(Bill bill) {
        String sql = "UPDATE bills SET bill_no = ?, customer_id = ?, bill_date = ?, total = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bill.getBillNo());
            stmt.setString(2, bill.getCustomerId());  // Now stores account number
            stmt.setTimestamp(3, Timestamp.valueOf(bill.getBillDate()));
            stmt.setBigDecimal(4, bill.getTotal());
            stmt.setInt(5, bill.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating bill failed, no rows affected.");
            }
            
            return bill;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating bill: " + bill.getId(), e);
        }
    }
    
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM bills WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting bill: " + id, e);
        }
    }
    
    @Override
    public BillItem saveBillItem(BillItem billItem) {
        String sql = "INSERT INTO bill_items (bill_id, item_id, qty, unit_price, line_total) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, billItem.getBillId());
            stmt.setInt(2, billItem.getItemId());
            stmt.setInt(3, billItem.getQty());
            stmt.setBigDecimal(4, billItem.getUnitPrice());
            stmt.setBigDecimal(5, billItem.getLineTotal());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating bill item failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    billItem.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating bill item failed, no ID obtained.");
                }
            }
            
            return billItem;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving bill item for bill: " + billItem.getBillId(), e);
        }
    }
    
    @Override
    public List<BillItem> findBillItemsByBillId(int billId) {
        List<BillItem> billItems = new ArrayList<>();
        String sql = "SELECT bi.id, bi.bill_id, bi.item_id, bi.qty, bi.unit_price, bi.line_total, " +
                    "i.sku, i.name as item_name " +
                    "FROM bill_items bi " +
                    "LEFT JOIN items i ON bi.item_id = i.id " +
                    "WHERE bi.bill_id = ? " +
                    "ORDER BY bi.id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, billId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    billItems.add(mapResultSetToBillItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bill items by bill ID: " + billId, e);
        }
        
        return billItems;
    }
    
    @Override
    public void deleteBillItemsByBillId(int billId) {
        String sql = "DELETE FROM bill_items WHERE bill_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, billId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting bill items for bill: " + billId, e);
        }
    }
    
    @Override
    public String generateBillNumber() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(bill_no, 6) AS UNSIGNED)), 0) + 1 as next_number " +
                    "FROM bills WHERE bill_no LIKE 'BILL-%'";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int nextNumber = rs.getInt("next_number");
                return String.format("BILL-%06d", nextNumber);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating bill number", e);
        }
        
        return "BILL-000001"; // Fallback
    }
    
    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getInt("id"));
        bill.setBillNo(rs.getString("bill_no"));
        bill.setCustomerId(rs.getString("customer_id"));  // Now reads account number
        bill.setTotal(rs.getBigDecimal("total"));
        
        Timestamp billDate = rs.getTimestamp("bill_date");
        if (billDate != null) {
            bill.setBillDate(billDate.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            bill.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        // Map customer if available
        String customerName = rs.getString("customer_name");
        if (customerName != null) {
            Customer customer = new Customer();
            // Note: we can get the numeric customer ID from the joined table if needed
            // But for now, we'll use the account number as the primary identifier
            customer.setAccountNumber(rs.getString("account_number"));
            customer.setName(customerName);
            customer.setAddress(rs.getString("address"));
            customer.setPhone(rs.getString("phone"));
            bill.setCustomer(customer);
        }
        
        return bill;
    }
    
    private BillItem mapResultSetToBillItem(ResultSet rs) throws SQLException {
        BillItem billItem = new BillItem();
        billItem.setId(rs.getInt("id"));
        billItem.setBillId(rs.getInt("bill_id"));
        billItem.setItemId(rs.getInt("item_id"));
        billItem.setQty(rs.getInt("qty"));
        billItem.setUnitPrice(rs.getBigDecimal("unit_price"));
        billItem.setLineTotal(rs.getBigDecimal("line_total"));
        
        // Map item if available
        String itemName = rs.getString("item_name");
        if (itemName != null) {
            Item item = new Item();
            item.setId(billItem.getItemId());
            item.setSku(rs.getString("sku"));
            item.setName(itemName);
            item.setUnitPrice(billItem.getUnitPrice());
            billItem.setItem(item);
        }
        
        return billItem;
    }
}
