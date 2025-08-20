package com.pahanaedu.bookstore.controller;

import com.pahanaedu.bookstore.model.Customer;
import com.pahanaedu.bookstore.service.CustomerService;
import com.pahanaedu.bookstore.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

public class CustomerServlet extends HttpServlet {
    
    private CustomerService customerService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.customerService = new CustomerService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        if (requestURI.endsWith("/api/customers")) {
            handleApiSearch(request, response);
        } else if (requestURI.endsWith("/customers/edit")) {
            showEditCustomerPage(request, response);
        } else if (requestURI.endsWith("/customers")) {
            showCustomersPage(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        if (requestURI.endsWith("/customers/edit")) {
            handleUpdateCustomer(request, response);
        } else if (requestURI.endsWith("/customers/delete")) {
            handleDeleteCustomer(request, response);
        } else if (requestURI.endsWith("/customers")) {
            handleCreateCustomer(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void showCustomersPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            List<Customer> customers = customerService.getAllCustomers();
            request.setAttribute("customers", customers);
            request.setAttribute("pageTitle", "Customers");
            
            request.getRequestDispatcher("/WEB-INF/jsp/customers.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error loading customers: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/customers.jsp").forward(request, response);
        }
    }
    
    private void showEditCustomerPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/app/customers");
            return;
        }
        
        try {
            int customerId = Integer.parseInt(idParam);
            Optional<Customer> customerOpt = customerService.findById(customerId);
            
            if (customerOpt.isPresent()) {
                request.setAttribute("customer", customerOpt.get());
                request.setAttribute("pageTitle", "Edit Customer");
                request.getRequestDispatcher("/WEB-INF/jsp/customer_form.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Customer not found");
                response.sendRedirect(request.getContextPath() + "/app/customers");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid customer ID");
            response.sendRedirect(request.getContextPath() + "/app/customers");
        } catch (Exception e) {
            request.setAttribute("error", "Error loading customer: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/app/customers");
        }
    }
    
    private void handleCreateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");
        
        try {
            Customer customer = new Customer();
            customer.setName(name);
            customer.setAddress(address);
            customer.setPhone(phone);
            
            Customer savedCustomer = customerService.createCustomer(customer);
            
            request.setAttribute("success", "Customer created successfully with account number: " + savedCustomer.getAccountNumber());
            
            // Reload customers list
            List<Customer> customers = customerService.getAllCustomers();
            request.setAttribute("customers", customers);
            request.setAttribute("pageTitle", "Customers");
            
            request.getRequestDispatcher("/WEB-INF/jsp/customers.jsp").forward(request, response);
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("name", name);
            request.setAttribute("address", address);
            request.setAttribute("phone", phone);
            
            // Reload customers list
            try {
                List<Customer> customers = customerService.getAllCustomers();
                request.setAttribute("customers", customers);
            } catch (Exception ex) {
                // Ignore error loading customers
            }
            
            request.setAttribute("pageTitle", "Customers");
            request.getRequestDispatcher("/WEB-INF/jsp/customers.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error creating customer: " + e.getMessage());
            
            // Reload customers list
            try {
                List<Customer> customers = customerService.getAllCustomers();
                request.setAttribute("customers", customers);
            } catch (Exception ex) {
                // Ignore error loading customers
            }
            
            request.setAttribute("pageTitle", "Customers");
            request.getRequestDispatcher("/WEB-INF/jsp/customers.jsp").forward(request, response);
        }
    }
    
    private void handleUpdateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");
        
        try {
            int customerId = Integer.parseInt(idParam);
            
            Customer customer = new Customer();
            customer.setId(customerId);
            customer.setName(name);
            customer.setAddress(address);
            customer.setPhone(phone);
            
            customerService.updateCustomer(customer);
            
            request.setAttribute("success", "Customer updated successfully");
            response.sendRedirect(request.getContextPath() + "/app/customers");
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid customer ID");
            response.sendRedirect(request.getContextPath() + "/app/customers");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("customer", createCustomerFromParams(idParam, name, address, phone));
            request.setAttribute("pageTitle", "Edit Customer");
            request.getRequestDispatcher("/WEB-INF/jsp/customer_form.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error updating customer: " + e.getMessage());
            request.setAttribute("customer", createCustomerFromParams(idParam, name, address, phone));
            request.setAttribute("pageTitle", "Edit Customer");
            request.getRequestDispatcher("/WEB-INF/jsp/customer_form.jsp").forward(request, response);
        }
    }
    
    private void handleApiSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String query = request.getParameter("query");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        
        try (PrintWriter out = response.getWriter()) {
            // Debug: Add logging
            System.out.println("====== CUSTOMER API DEBUG START ======");
            System.out.println("DEBUG: handleApiSearch called with query: " + query);
            System.out.println("DEBUG: Request URI: " + request.getRequestURI());
            System.out.println("DEBUG: Request method: " + request.getMethod());
            
            if (query == null || query.trim().isEmpty()) {
                System.out.println("DEBUG: Empty query, returning empty array");
                out.print("[]");
                out.flush();
                System.out.println("DEBUG: Response sent - empty array for empty query");
                System.out.println("====== CUSTOMER API DEBUG END ======");
                return;
            }
            
            System.out.println("DEBUG: About to call customerService.searchCustomers");
            List<Customer> customers = customerService.searchCustomers(query);
            System.out.println("DEBUG: customerService.searchCustomers returned: " + (customers != null ? customers.size() : "null") + " customers");
            
            // Handle null response
            if (customers == null) {
                System.out.println("DEBUG: Customer service returned null, returning empty array");
                out.print("[]");
                out.flush();
                System.out.println("DEBUG: Response sent - empty array for null result");
                System.out.println("====== CUSTOMER API DEBUG END ======");
                return;
            }
            
            System.out.println("DEBUG: Converting to JSON using JsonUtil");
            
            String json;
            try {
                json = JsonUtil.toJson(customers);
                System.out.println("DEBUG: JSON conversion result - length: " + (json != null ? json.length() : "null"));
                System.out.println("DEBUG: JSON content: " + json);
            } catch (Exception jsonException) {
                System.err.println("ERROR: JSON conversion failed: " + jsonException.getMessage());
                jsonException.printStackTrace();
                json = "[]"; // Fallback to empty array
            }
            
            // Ensure we have valid JSON
            if (json == null || json.trim().isEmpty()) {
                System.out.println("DEBUG: Empty JSON result, returning empty array");
                json = "[]";
            }
            
            System.out.println("DEBUG: Sending JSON response: " + json);
            out.print(json);
            out.flush();
            System.out.println("DEBUG: Response flushed successfully");
            System.out.println("====== CUSTOMER API DEBUG END ======");
            
        } catch (Exception e) {
            System.err.println("====== CUSTOMER API ERROR START ======");
            System.err.println("ERROR in handleApiSearch: " + e.getMessage());
            e.printStackTrace();
            System.err.println("====== CUSTOMER API ERROR END ======");
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                String errorJson = "{\"error\":\"Error searching customers: " + e.getMessage().replace("\"", "\\\"") + "\"}";
                System.out.println("DEBUG: Sending error response: " + errorJson);
                out.print(errorJson);
                out.flush();
            }
        }
    }
    
    private void handleDeleteCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/app/customers");
            return;
        }
        
        try {
            int customerId = Integer.parseInt(idParam);
            
            // Check if customer exists
            Optional<Customer> existingCustomer = customerService.findById(customerId);
            if (existingCustomer.isEmpty()) {
                request.getSession().setAttribute("error", "Customer not found.");
                response.sendRedirect(request.getContextPath() + "/app/customers");
                return;
            }
            
            // Delete the customer
            customerService.deleteCustomer(customerId);
            request.getSession().setAttribute("success", "Customer deleted successfully.");
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Invalid customer ID.");
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error deleting customer: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/app/customers");
    }
    
    private Customer createCustomerFromParams(String idParam, String name, String address, String phone) {
        Customer customer = new Customer();
        try {
            customer.setId(Integer.parseInt(idParam));
        } catch (NumberFormatException e) {
            // Ignore
        }
        customer.setName(name);
        customer.setAddress(address);
        customer.setPhone(phone);
        return customer;
    }
}
