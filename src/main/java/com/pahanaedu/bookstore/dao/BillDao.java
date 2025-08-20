package com.pahanaedu.bookstore.dao;

import com.pahanaedu.bookstore.model.Bill;
import com.pahanaedu.bookstore.model.BillItem;
import java.util.List;
import java.util.Optional;

public interface BillDao {
    /**
     * Save a new bill
     * @param bill the bill to save
     * @return the saved bill with generated ID
     */
    Bill save(Bill bill);
    
    /**
     * Find a bill by ID
     * @param id the bill ID
     * @return Optional containing the bill if found
     */
    Optional<Bill> findById(int id);
    
    /**
     * Find a bill by bill number
     * @param billNo the bill number
     * @return Optional containing the bill if found
     */
    Optional<Bill> findByBillNo(String billNo);
    
    /**
     * Find bills by customer account number
     * @param customerAccountNumber the customer account number (e.g., ACC000005)
     * @return list of bills for the customer
     */
    List<Bill> findByCustomerId(String customerAccountNumber);
    
    /**
     * Get all bills
     * @return list of all bills
     */
    List<Bill> findAll();
    
    /**
     * Update an existing bill
     * @param bill the bill to update
     * @return the updated bill
     */
    Bill update(Bill bill);
    
    /**
     * Delete a bill by ID
     * @param id the bill ID to delete
     */
    void deleteById(int id);
    
    /**
     * Save a bill item
     * @param billItem the bill item to save
     * @return the saved bill item with generated ID
     */
    BillItem saveBillItem(BillItem billItem);
    
    /**
     * Find bill items by bill ID
     * @param billId the bill ID
     * @return list of bill items
     */
    List<BillItem> findBillItemsByBillId(int billId);
    
    /**
     * Delete bill items by bill ID
     * @param billId the bill ID
     */
    void deleteBillItemsByBillId(int billId);
    
    /**
     * Generate next bill number
     * @return the next bill number
     */
    String generateBillNumber();
}
