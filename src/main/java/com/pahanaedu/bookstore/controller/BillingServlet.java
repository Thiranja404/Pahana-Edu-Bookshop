package com.pahanaedu.bookstore.controller;

import com.pahanaedu.bookstore.model.Bill;
import com.pahanaedu.bookstore.service.BillingService;
import com.pahanaedu.bookstore.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BillingServlet extends HttpServlet {
    
    private BillingService billingService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.billingService = new BillingService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        if (requestURI.endsWith("/bill/view")) {
            showBillView(request, response);
        } else if (requestURI.endsWith("/bill/view-simple")) {
            showBillViewSimple(request, response);
        } else if (requestURI.endsWith("/billing")) {
            showBillingPage(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        if (requestURI.endsWith("/api/billing")) {
            handleCreateBill(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void showBillingPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("pageTitle", "Billing");
        request.getRequestDispatcher("/WEB-INF/jsp/billing.jsp").forward(request, response);
    }
    
    private void showBillView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billNo = request.getParameter("billNo");
        if (billNo == null || billNo.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/app/billing");
            return;
        }
        
        try {
            Optional<Bill> billOpt = billingService.findByBillNo(billNo);
            
            if (billOpt.isPresent()) {
                request.setAttribute("bill", billOpt.get());
                request.setAttribute("pageTitle", "Bill " + billNo);
                request.getRequestDispatcher("/WEB-INF/jsp/bill_view.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Bill not found: " + billNo);
                response.sendRedirect(request.getContextPath() + "/app/billing");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error loading bill: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/app/billing");
        }
    }
    
    private void showBillViewSimple(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billNo = request.getParameter("billNo");
        if (billNo == null || billNo.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/app/billing");
            return;
        }
        
        try {
            Optional<Bill> billOpt = billingService.findByBillNo(billNo);
            
            if (billOpt.isPresent()) {
                request.setAttribute("bill", billOpt.get());
                request.setAttribute("pageTitle", "Bill " + billNo);
                request.getRequestDispatcher("/WEB-INF/jsp/bill_view_simple.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Bill not found: " + billNo);
                response.sendRedirect(request.getContextPath() + "/app/billing");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error loading bill: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/app/billing");
        }
    }
    
    @SuppressWarnings("unchecked")
    private void handleCreateBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter();
             BufferedReader reader = request.getReader()) {
            
            // Read JSON request body
            StringBuilder jsonBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
            
            // Parse JSON
            Map<String, Object> requestData = JsonUtil.fromJson(jsonBody.toString(), Map.class);
            
            String customerAccountNumber = (String) requestData.get("customerAccountNumber");
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) requestData.get("items");
            
            if (customerAccountNumber == null || customerAccountNumber.trim().isEmpty() || itemsData == null || itemsData.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Customer account number and items are required\"}");
                return;
            }
            
            // Convert to BillItemRequest objects
            List<BillingService.BillItemRequest> billItems = new ArrayList<>();
            for (Map<String, Object> itemData : itemsData) {
                Integer itemId = (Integer) itemData.get("itemId");
                Integer qty = (Integer) itemData.get("qty");
                
                if (itemId == null || qty == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"Item ID and quantity are required for all items\"}");
                    return;
                }
                
                billItems.add(new BillingService.BillItemRequest(itemId, qty));
            }
            
            // Create bill
            String billNo = billingService.createBill(customerAccountNumber, billItems);
            
            // Return success response
            out.print("{\"success\":true,\"billNo\":\"" + billNo + "\"}");
            
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"" + e.getMessage() + "\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"Error creating bill: " + e.getMessage() + "\"}");
            }
        }
    }
}
