package com.pahanaedu.bookstore.controller;

import com.pahanaedu.bookstore.model.Bill;
import com.pahanaedu.bookstore.model.Customer;
import com.pahanaedu.bookstore.service.BillingService;
import com.pahanaedu.bookstore.service.CustomerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AccountServlet extends HttpServlet {
    
    private CustomerService customerService;
    private BillingService billingService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.customerService = new CustomerService();
        this.billingService = new BillingService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        showAccountPage(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        handleAccountSearch(request, response);
    }
    
    private void showAccountPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("pageTitle", "Account Details");
        request.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(request, response);
    }
    
    private void handleAccountSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String searchType = request.getParameter("searchType");
        String searchValue = request.getParameter("searchValue");
        
        if (searchValue == null || searchValue.trim().isEmpty()) {
            request.setAttribute("error", "Search value is required");
            request.setAttribute("pageTitle", "Account Details");
            request.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(request, response);
            return;
        }
        
        try {
            Customer customer = null;
            
            if ("accountNumber".equals(searchType)) {
                Optional<Customer> customerOpt = customerService.findByAccountNumber(searchValue.trim());
                if (customerOpt.isPresent()) {
                    customer = customerOpt.get();
                }
            } else if ("name".equals(searchType)) {
                List<Customer> customers = customerService.searchCustomers(searchValue.trim());
                if (!customers.isEmpty()) {
                    // Take the first match
                    customer = customers.get(0);
                    
                    // If multiple matches, show info about that
                    if (customers.size() > 1) {
                        request.setAttribute("info", "Multiple customers found with that name. Showing first match: " + customer.getName());
                    }
                }
            }
            
            if (customer != null) {
                // Get customer's bills using account number
                List<Bill> bills = billingService.findBillsByCustomerId(customer.getAccountNumber());
                
                request.setAttribute("customer", customer);
                request.setAttribute("bills", bills);
                request.setAttribute("searchType", searchType);
                request.setAttribute("searchValue", searchValue);
            } else {
                request.setAttribute("error", "No customer found with the given " + 
                    ("accountNumber".equals(searchType) ? "account number" : "name"));
                request.setAttribute("searchType", searchType);
                request.setAttribute("searchValue", searchValue);
            }
            
        } catch (Exception e) {
            request.setAttribute("error", "Error searching for customer: " + e.getMessage());
            request.setAttribute("searchType", searchType);
            request.setAttribute("searchValue", searchValue);
        }
        
        request.setAttribute("pageTitle", "Account Details");
        request.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(request, response);
    }
}
