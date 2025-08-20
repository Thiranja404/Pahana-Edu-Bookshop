package com.pahanaedu.bookstore.service;

import com.pahanaedu.bookstore.dao.BillDao;
import com.pahanaedu.bookstore.dao.BillDaoImpl;
import com.pahanaedu.bookstore.dao.CustomerDao;
import com.pahanaedu.bookstore.dao.CustomerDaoImpl;
import com.pahanaedu.bookstore.dao.ItemDao;
import com.pahanaedu.bookstore.dao.ItemDaoImpl;
import com.pahanaedu.bookstore.model.Bill;
import com.pahanaedu.bookstore.model.BillItem;
import com.pahanaedu.bookstore.model.Customer;
import com.pahanaedu.bookstore.model.Item;
import com.pahanaedu.bookstore.util.DataSourceSingleton;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BillingService {
    
    private final BillDao billDao;
    private final CustomerDao customerDao;
    private final ItemDao itemDao;
    private final DataSource dataSource;
    
    public BillingService() {
        this.billDao = new BillDaoImpl();
        this.customerDao = new CustomerDaoImpl();
        this.itemDao = new ItemDaoImpl();
        this.dataSource = DataSourceSingleton.getInstance();
    }
    
    /**
     * Create a new bill with items in a single transaction
     * @param customerAccountNumber the customer account number (e.g., ACC000005)
     * @param billItems list of bill items (itemId and qty)
     * @return the bill number of the created bill
     * @throws IllegalArgumentException if validation fails
     */
    public String createBill(String customerAccountNumber, List<BillItemRequest> billItems) {
        validateCreateBillRequest(customerAccountNumber, billItems);
        
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            
            // Verify customer exists
            Optional<Customer> customerOpt = customerDao.findByAccountNumber(customerAccountNumber);
            if (customerOpt.isEmpty()) {
                throw new IllegalArgumentException("Customer not found with account number: " + customerAccountNumber);
            }
            
            // Calculate total and create bill items
            List<BillItem> validatedBillItems = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;
            
            for (BillItemRequest request : billItems) {
                Optional<Item> itemOpt = itemDao.findById(request.getItemId());
                if (itemOpt.isEmpty()) {
                    throw new IllegalArgumentException("Item not found with ID: " + request.getItemId());
                }
                
                Item item = itemOpt.get();
                if (!item.isActive()) {
                    throw new IllegalArgumentException("Item is not active: " + item.getName());
                }
                
                BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(request.getQty()));
                total = total.add(lineTotal);
                
                BillItem billItem = new BillItem();
                billItem.setItemId(item.getId());
                billItem.setQty(request.getQty());
                billItem.setUnitPrice(item.getUnitPrice());
                billItem.setLineTotal(lineTotal);
                validatedBillItems.add(billItem);
            }
            
            // Generate bill number
            String billNo = billDao.generateBillNumber();
            
            // Create bill
            Bill bill = new Bill();
            bill.setBillNo(billNo);
            bill.setCustomerId(customerAccountNumber);  // Store account number instead of numeric ID
            bill.setBillDate(LocalDateTime.now());
            bill.setTotal(total);
            
            bill = billDao.save(bill);
            
            // Save bill items
            for (BillItem billItem : validatedBillItems) {
                billItem.setBillId(bill.getId());
                billDao.saveBillItem(billItem);
            }
            
            conn.commit();
            return billNo;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Error rolling back transaction", rollbackEx);
                }
            }
            throw new RuntimeException("Error creating bill", e);
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Error rolling back transaction", rollbackEx);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log error but don't throw
                }
            }
        }
    }
    
    /**
     * Find a bill by ID
     * @param id the bill ID
     * @return Optional containing the bill if found
     */
    public Optional<Bill> findById(int id) {
        return billDao.findById(id);
    }
    
    /**
     * Find a bill by bill number
     * @param billNo the bill number
     * @return Optional containing the bill if found
     */
    public Optional<Bill> findByBillNo(String billNo) {
        if (billNo == null || billNo.trim().isEmpty()) {
            return Optional.empty();
        }
        return billDao.findByBillNo(billNo.trim());
    }
    
    /**
     * Find bills by customer account number
     * @param customerAccountNumber the customer account number
     * @return list of bills for the customer
     */
    public List<Bill> findBillsByCustomerId(String customerAccountNumber) {
        return billDao.findByCustomerId(customerAccountNumber);
    }
    
    /**
     * Get all bills
     * @return list of all bills
     */
    public List<Bill> getAllBills() {
        return billDao.findAll();
    }
    
    /**
     * Delete a bill by ID
     * @param id the bill ID to delete
     * @throws IllegalArgumentException if bill not found
     */
    public void deleteBill(int id) {
        Optional<Bill> billOpt = billDao.findById(id);
        if (billOpt.isEmpty()) {
            throw new IllegalArgumentException("Bill not found with ID: " + id);
        }
        
        billDao.deleteById(id);
    }
    
    /**
     * Validate create bill request
     * @param customerId the customer ID
     * @param billItems the bill items
     * @throws IllegalArgumentException if validation fails
     */
    private void validateCreateBillRequest(String customerAccountNumber, List<BillItemRequest> billItems) {
        if (customerAccountNumber == null || customerAccountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Valid customer account number is required");
        }
        
        if (billItems == null || billItems.isEmpty()) {
            throw new IllegalArgumentException("At least one bill item is required");
        }
        
        for (BillItemRequest item : billItems) {
            if (item.getItemId() <= 0) {
                throw new IllegalArgumentException("Valid item ID is required");
            }
            
            if (item.getQty() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }
            
            if (item.getQty() > 9999) {
                throw new IllegalArgumentException("Quantity cannot exceed 9999");
            }
        }
    }
    
    /**
     * Inner class to represent a bill item request
     */
    public static class BillItemRequest {
        private int itemId;
        private int qty;
        
        public BillItemRequest() {}
        
        public BillItemRequest(int itemId, int qty) {
            this.itemId = itemId;
            this.qty = qty;
        }
        
        public int getItemId() {
            return itemId;
        }
        
        public void setItemId(int itemId) {
            this.itemId = itemId;
        }
        
        public int getQty() {
            return qty;
        }
        
        public void setQty(int qty) {
            this.qty = qty;
        }
    }
}
