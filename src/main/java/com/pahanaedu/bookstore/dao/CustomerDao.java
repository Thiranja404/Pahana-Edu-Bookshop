package com.pahanaedu.bookstore.dao;

import com.pahanaedu.bookstore.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    /**
     * Save a new customer
     * @param customer the customer to save
     * @return the saved customer with generated ID and account number
     */
    Customer save(Customer customer);
    
    /**
     * Find a customer by ID
     * @param id the customer ID
     * @return Optional containing the customer if found
     */
    Optional<Customer> findById(int id);
    
    /**
     * Find a customer by account number
     * @param accountNumber the account number
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByAccountNumber(String accountNumber);
    
    /**
     * Search customers by query (account number or name)
     * @param query the search query
     * @return list of matching customers
     */
    List<Customer> search(String query);
    
    /**
     * Get all customers
     * @return list of all customers
     */
    List<Customer> findAll();
    
    /**
     * Update an existing customer
     * @param customer the customer to update
     * @return the updated customer
     */
    Customer update(Customer customer);
    
    /**
     * Delete a customer by ID
     * @param id the customer ID to delete
     */
    void deleteById(int id);
}
