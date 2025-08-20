package com.pahanaedu.bookstore.controller;

import com.pahanaedu.bookstore.model.Item;
import com.pahanaedu.bookstore.service.ItemService;
import com.pahanaedu.bookstore.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ItemServlet extends HttpServlet {
    
    private ItemService itemService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.itemService = new ItemService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        if (requestURI.endsWith("/api/items")) {
            handleApiSearch(request, response);
        } else if (requestURI.endsWith("/items/edit")) {
            showEditItemPage(request, response);
        } else if (requestURI.endsWith("/items")) {
            showItemsPage(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        if (requestURI.endsWith("/items/edit")) {
            handleUpdateItem(request, response);
        } else if (requestURI.endsWith("/items")) {
            String action = request.getParameter("action");
            if ("delete".equals(action)) {
                handleDeactivateItem(request, response);
            } else if ("activate".equals(action)) {
                handleActivateItem(request, response);
            } else {
                handleCreateItem(request, response);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void showItemsPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            List<Item> items = itemService.getAllItems();
            request.setAttribute("items", items);
            request.setAttribute("pageTitle", "Items");
            
            request.getRequestDispatcher("/WEB-INF/jsp/items.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error loading items: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/items.jsp").forward(request, response);
        }
    }
    
    private void showEditItemPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/app/items");
            return;
        }
        
        try {
            int itemId = Integer.parseInt(idParam);
            Optional<Item> itemOpt = itemService.findById(itemId);
            
            if (itemOpt.isPresent()) {
                request.setAttribute("item", itemOpt.get());
                request.setAttribute("pageTitle", "Edit Item");
                request.getRequestDispatcher("/WEB-INF/jsp/item_form.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Item not found");
                response.sendRedirect(request.getContextPath() + "/app/items");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid item ID");
            response.sendRedirect(request.getContextPath() + "/app/items");
        } catch (Exception e) {
            request.setAttribute("error", "Error loading item: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/app/items");
        }
    }
    
    private void handleCreateItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String sku = request.getParameter("sku");
        String name = request.getParameter("name");
        String unitPriceStr = request.getParameter("unitPrice");
        String activeStr = request.getParameter("active");
        
        try {
            BigDecimal unitPrice = new BigDecimal(unitPriceStr);
            boolean active = "on".equals(activeStr) || "true".equals(activeStr);
            
            Item item = new Item();
            item.setSku(sku);
            item.setName(name);
            item.setUnitPrice(unitPrice);
            item.setActive(active);
            
            itemService.createItem(item);
            
            request.setAttribute("success", "Item created successfully");
            
            // Reload items list
            List<Item> items = itemService.getAllItems();
            request.setAttribute("items", items);
            request.setAttribute("pageTitle", "Items");
            
            request.getRequestDispatcher("/WEB-INF/jsp/items.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid unit price format");
            request.setAttribute("sku", sku);
            request.setAttribute("name", name);
            request.setAttribute("unitPrice", unitPriceStr);
            request.setAttribute("active", activeStr);
            
            // Reload items list
            try {
                List<Item> items = itemService.getAllItems();
                request.setAttribute("items", items);
            } catch (Exception ex) {
                // Ignore error loading items
            }
            
            request.setAttribute("pageTitle", "Items");
            request.getRequestDispatcher("/WEB-INF/jsp/items.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("sku", sku);
            request.setAttribute("name", name);
            request.setAttribute("unitPrice", unitPriceStr);
            request.setAttribute("active", activeStr);
            
            // Reload items list
            try {
                List<Item> items = itemService.getAllItems();
                request.setAttribute("items", items);
            } catch (Exception ex) {
                // Ignore error loading items
            }
            
            request.setAttribute("pageTitle", "Items");
            request.getRequestDispatcher("/WEB-INF/jsp/items.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error creating item: " + e.getMessage());
            
            // Reload items list
            try {
                List<Item> items = itemService.getAllItems();
                request.setAttribute("items", items);
            } catch (Exception ex) {
                // Ignore error loading items
            }
            
            request.setAttribute("pageTitle", "Items");
            request.getRequestDispatcher("/WEB-INF/jsp/items.jsp").forward(request, response);
        }
    }
    
    private void handleUpdateItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        String sku = request.getParameter("sku");
        String name = request.getParameter("name");
        String unitPriceStr = request.getParameter("unitPrice");
        String activeStr = request.getParameter("active");
        
        try {
            int itemId = Integer.parseInt(idParam);
            BigDecimal unitPrice = new BigDecimal(unitPriceStr);
            boolean active = "on".equals(activeStr) || "true".equals(activeStr);
            
            Item item = new Item();
            item.setId(itemId);
            item.setSku(sku);
            item.setName(name);
            item.setUnitPrice(unitPrice);
            item.setActive(active);
            
            itemService.updateItem(item);
            
            request.setAttribute("success", "Item updated successfully");
            response.sendRedirect(request.getContextPath() + "/app/items");
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid item ID or unit price format");
            request.setAttribute("item", createItemFromParams(idParam, sku, name, unitPriceStr, activeStr));
            request.setAttribute("pageTitle", "Edit Item");
            request.getRequestDispatcher("/WEB-INF/jsp/item_form.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("item", createItemFromParams(idParam, sku, name, unitPriceStr, activeStr));
            request.setAttribute("pageTitle", "Edit Item");
            request.getRequestDispatcher("/WEB-INF/jsp/item_form.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error updating item: " + e.getMessage());
            request.setAttribute("item", createItemFromParams(idParam, sku, name, unitPriceStr, activeStr));
            request.setAttribute("pageTitle", "Edit Item");
            request.getRequestDispatcher("/WEB-INF/jsp/item_form.jsp").forward(request, response);
        }
    }
    
    private void handleDeactivateItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        try {
            int itemId = Integer.parseInt(idParam);
            itemService.deactivateItem(itemId);
            
            request.setAttribute("success", "Item deactivated successfully");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid item ID");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            request.setAttribute("error", "Error deactivating item: " + e.getMessage());
        }
        
        // Reload items page
        try {
            List<Item> items = itemService.getAllItems();
            request.setAttribute("items", items);
            request.setAttribute("pageTitle", "Items");
            request.getRequestDispatcher("/WEB-INF/jsp/items.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error loading items: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/items.jsp").forward(request, response);
        }
    }
    
    private void handleActivateItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        try {
            int itemId = Integer.parseInt(idParam);
            itemService.activateItem(itemId);
            
            request.setAttribute("success", "Item marked as in stock successfully");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid item ID");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            request.setAttribute("error", "Error activating item: " + e.getMessage());
        }
        
        // Reload items page
        try {
            List<Item> items = itemService.getAllItems();
            request.setAttribute("items", items);
            request.setAttribute("pageTitle", "Items");
            request.getRequestDispatcher("/WEB-INF/jsp/items.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error loading items: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/items.jsp").forward(request, response);
        }
    }
    
    private void handleApiSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String query = request.getParameter("query");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try (PrintWriter out = response.getWriter()) {
            // Debug logging
            System.out.println("=== ITEM API DEBUG START ===");
            System.out.println("DEBUG: Item API search called with query: " + query);
            System.out.println("DEBUG: Request URI: " + request.getRequestURI());
            
            if (query == null || query.trim().isEmpty()) {
                System.out.println("DEBUG: Empty query, returning empty array");
                out.print("[]");
                out.flush();
                return;
            }
            
            System.out.println("DEBUG: Calling itemService.searchItems with query: '" + query + "'");
            List<Item> items = itemService.searchItems(query);
            System.out.println("DEBUG: ItemService returned " + items.size() + " items");
            
            if (items.isEmpty()) {
                System.out.println("DEBUG: No items found, returning empty array");
                out.print("[]");
                out.flush();
                return;
            }
            
            // Print each item for debugging
            for (Item item : items) {
                System.out.println("DEBUG: Item found - ID:" + item.getId() + ", SKU:" + item.getSku() + ", Name:" + item.getName() + ", Active:" + item.isActive());
            }
            
            System.out.println("DEBUG: Converting items to JSON");
            String json = JsonUtil.toJson(items);
            System.out.println("DEBUG: JSON length: " + json.length());
            System.out.println("DEBUG: JSON preview: " + (json.length() > 100 ? json.substring(0, 100) + "..." : json));
            
            out.print(json);
            out.flush();
            System.out.println("DEBUG: Response sent successfully");
            System.out.println("=== ITEM API DEBUG END ===");
            
        } catch (Exception e) {
            System.err.println("=== ITEM API ERROR ===");
            System.err.println("ERROR in Item API search: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== ITEM API ERROR END ===");
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"Error searching items: " + e.getMessage().replace("\"", "'") + "\"}");
                out.flush();
            }
        }
    }
    
    private Item createItemFromParams(String idParam, String sku, String name, String unitPriceStr, String activeStr) {
        Item item = new Item();
        try {
            item.setId(Integer.parseInt(idParam));
        } catch (NumberFormatException e) {
            // Ignore
        }
        item.setSku(sku);
        item.setName(name);
        try {
            item.setUnitPrice(new BigDecimal(unitPriceStr));
        } catch (NumberFormatException e) {
            item.setUnitPrice(BigDecimal.ZERO);
        }
        item.setActive("on".equals(activeStr) || "true".equals(activeStr));
        return item;
    }
}
