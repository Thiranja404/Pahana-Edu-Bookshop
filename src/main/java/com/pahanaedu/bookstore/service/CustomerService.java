package com.pahanaedu.bookstore.service;

import com.pahanaedu.bookstore.dao.CustomerDao;
import com.pahanaedu.bookstore.dao.CustomerDaoImpl;
import com.pahanaedu.bookstore.model.Customer;

import java.util.List;
import java.util.Optional;

public class CustomerService {
    
    private final CustomerDao customerDao;
    
    public CustomerService() {
        this.customerDao = new CustomerDaoImpl();
    }
    
    /**
     * Create a new customer
     * @param customer the customer to create
     * @return the created customer with generated ID and account number
     * @throws IllegalArgumentException if validation fails
     */
    public Customer createCustomer(Customer customer) {
        validateCustomer(customer);
        return customerDao.save(customer);
    }
    
    /**
     * Update an existing customer
     * @param customer the customer to update
     * @return the updated customer
     * @throws IllegalArgumentException if validation fails or customer not found
     */
    public Customer updateCustomer(Customer customer) {
        if (customer.getId() <= 0) {
            throw new IllegalArgumentException("Customer ID is required for update");
        }
        
        // Check if customer exists
        Optional<Customer> existingOpt = customerDao.findById(customer.getId());
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found with ID: " + customer.getId());
        }
        
        validateCustomer(customer);
        
        // Preserve account number from existing customer
        Customer existing = existingOpt.get();
        customer.setAccountNumber(existing.getAccountNumber());
        
        return customerDao.update(customer);
    }
    
    /**
     * Find a customer by ID
     * @param id the customer ID
     * @return Optional containing the customer if found
     */
    public Optional<Customer> findById(int id) {
        return customerDao.findById(id);
    }
    
    /**
     * Find a customer by account number
     * @param accountNumber the account number
     * @return Optional containing the customer if found
     */
    public Optional<Customer> findByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        return customerDao.findByAccountNumber(accountNumber.trim());
    }
    
    /**
     * Search customers by query (account number or name)
     * @param query the search query
     * @return list of matching customers
     */
    public List<Customer> searchCustomers(String query) {
        System.out.println("DEBUG CustomerService: searchCustomers called with query: " + query);
        
        if (query == null || query.trim().isEmpty()) {
            System.out.println("DEBUG CustomerService: Empty query, returning empty list");
            return List.of();
        }
        
        try {
            System.out.println("DEBUG CustomerService: Calling customerDao.search");
            List<Customer> result = customerDao.search(query.trim());
            System.out.println("DEBUG CustomerService: DAO returned " + (result != null ? result.size() : "null") + " customers");
            
            // Return actual database results
            return result != null ? result : List.of();
        } catch (Exception e) {
            System.err.println("ERROR CustomerService: Exception in searchCustomers: " + e.getMessage());
            e.printStackTrace();
            
            // Return empty list on error instead of sample data
            return List.of();
        }
    }
    
    /**
     * Get all customers
     * @return list of all customers
     */
    public List<Customer> getAllCustomers() {
        return customerDao.findAll();
    }
    
    /**
     * Delete a customer by ID
     * @param id the customer ID to delete
     * @throws IllegalArgumentException if customer not found
     */
    public void deleteCustomer(int id) {
        Optional<Customer> customerOpt = customerDao.findById(id);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found with ID: " + id);
        }
        
        customerDao.deleteById(id);
    }
    
    /**
     * Validate customer data
     * @param customer the customer to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        
        if (customer.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Customer name cannot exceed 100 characters");
        }
        
        if (customer.getAddress() != null && customer.getAddress().length() > 500) {
            throw new IllegalArgumentException("Address cannot exceed 500 characters");
        }
        
        if (customer.getPhone() != null && !customer.getPhone().trim().isEmpty()) {
            String phone = customer.getPhone().trim();
            if (phone.length() > 20) {
                throw new IllegalArgumentException("Phone number cannot exceed 20 characters");
            }
            // Basic phone validation
            if (!phone.matches("^[+\\-\\s\\d()]+$")) {
                throw new IllegalArgumentException("Phone number contains invalid characters");
            }
        }
    }
}
