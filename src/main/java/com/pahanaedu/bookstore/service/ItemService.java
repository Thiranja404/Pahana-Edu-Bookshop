package com.pahanaedu.bookstore.service;

import com.pahanaedu.bookstore.dao.ItemDao;
import com.pahanaedu.bookstore.dao.ItemDaoImpl;
import com.pahanaedu.bookstore.model.Item;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ItemService {
    
    private final ItemDao itemDao;
    
    public ItemService() {
        this.itemDao = new ItemDaoImpl();
    }
    
    /**
     * Create a new item
     * @param item the item to create
     * @return the created item with generated ID
     * @throws IllegalArgumentException if validation fails
     */
    public Item createItem(Item item) {
        validateItem(item);
        
        // Check if SKU already exists
        Optional<Item> existingItem = itemDao.findBySku(item.getSku());
        if (existingItem.isPresent()) {
            throw new IllegalArgumentException("SKU already exists: " + item.getSku());
        }
        
        return itemDao.save(item);
    }
    
    /**
     * Update an existing item
     * @param item the item to update
     * @return the updated item
     * @throws IllegalArgumentException if validation fails or item not found
     */
    public Item updateItem(Item item) {
        if (item.getId() <= 0) {
            throw new IllegalArgumentException("Item ID is required for update");
        }
        
        // Check if item exists
        Optional<Item> existingOpt = itemDao.findById(item.getId());
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Item not found with ID: " + item.getId());
        }
        
        validateItem(item);
        
        // Check if SKU conflicts with another item
        Optional<Item> skuConflict = itemDao.findBySku(item.getSku());
        if (skuConflict.isPresent() && skuConflict.get().getId() != item.getId()) {
            throw new IllegalArgumentException("SKU already exists for another item: " + item.getSku());
        }
        
        return itemDao.update(item);
    }
    
    /**
     * Find an item by ID
     * @param id the item ID
     * @return Optional containing the item if found
     */
    public Optional<Item> findById(int id) {
        return itemDao.findById(id);
    }
    
    /**
     * Find an item by SKU
     * @param sku the item SKU
     * @return Optional containing the item if found
     */
    public Optional<Item> findBySku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            return Optional.empty();
        }
        return itemDao.findBySku(sku.trim());
    }
    
    /**
     * Search items by query (SKU or name)
     * @param query the search query
     * @return list of matching active items
     */
    public List<Item> searchItems(String query) {
        System.out.println("====== ITEM SERVICE DEBUG START ======");
        System.out.println("DEBUG ItemService: searchItems called with query: " + query);
        
        if (query == null || query.trim().isEmpty()) {
            System.out.println("DEBUG ItemService: Empty query, returning empty list");
            System.out.println("====== ITEM SERVICE DEBUG END ======");
            return List.of();
        }

        try {
            System.out.println("DEBUG ItemService: Calling itemDao.search");
            List<Item> result = itemDao.search(query.trim());
            System.out.println("DEBUG ItemService: DAO returned " + (result != null ? result.size() : "null") + " items");
            
            // Return actual database results
            List<Item> finalResult = result != null ? result : List.of();
            System.out.println("DEBUG ItemService: Returning " + finalResult.size() + " items");
            System.out.println("====== ITEM SERVICE DEBUG END ======");
            return finalResult;
        } catch (Exception e) {
            System.err.println("ERROR ItemService: Exception in searchItems: " + e.getMessage());
            e.printStackTrace();
            
            // Return empty list on error
            System.out.println("DEBUG ItemService: Exception occurred, returning empty list");
            System.out.println("====== ITEM SERVICE DEBUG END ======");
            return List.of();
        }
    }
    
    /**
     * Get all active items
     * @return list of all active items
     */
    public List<Item> getAllActiveItems() {
        return itemDao.findAllActive();
    }
    
    /**
     * Get all items (including inactive)
     * @return list of all items
     */
    public List<Item> getAllItems() {
        return itemDao.findAll();
    }
    
    /**
     * Deactivate an item (soft delete)
     * @param id the item ID to deactivate
     * @throws IllegalArgumentException if item not found
     */
    public void deactivateItem(int id) {
        Optional<Item> itemOpt = itemDao.findById(id);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item not found with ID: " + id);
        }
        
        itemDao.deactivate(id);
    }
    
    /**
     * Activate an item
     * @param id the item ID to activate
     * @throws IllegalArgumentException if item not found
     */
    public void activateItem(int id) {
        Optional<Item> itemOpt = itemDao.findById(id);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item not found with ID: " + id);
        }
        
        itemDao.activate(id);
    }
    
    /**
     * Validate item data
     * @param item the item to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        if (item.getSku() == null || item.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("Item SKU is required");
        }
        
        if (item.getSku().trim().length() > 50) {
            throw new IllegalArgumentException("SKU cannot exceed 50 characters");
        }
        
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }
        
        if (item.getName().trim().length() > 200) {
            throw new IllegalArgumentException("Item name cannot exceed 200 characters");
        }
        
        if (item.getUnitPrice() == null) {
            throw new IllegalArgumentException("Unit price is required");
        }
        
        if (item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be greater than zero");
        }
        
        if (item.getUnitPrice().compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException("Unit price cannot exceed 99999999.99");
        }
    }
}
