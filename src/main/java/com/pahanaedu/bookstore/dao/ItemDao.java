package com.pahanaedu.bookstore.dao;

import com.pahanaedu.bookstore.model.Item;
import java.util.List;
import java.util.Optional;

public interface ItemDao {
    /**
     * Save a new item
     * @param item the item to save
     * @return the saved item with generated ID
     */
    Item save(Item item);
    
    /**
     * Find an item by ID
     * @param id the item ID
     * @return Optional containing the item if found
     */
    Optional<Item> findById(int id);
    
    /**
     * Find an item by SKU
     * @param sku the item SKU
     * @return Optional containing the item if found
     */
    Optional<Item> findBySku(String sku);
    
    /**
     * Search active items by query (SKU or name)
     * @param query the search query
     * @return list of matching active items
     */
    List<Item> search(String query);
    
    /**
     * Get all active items
     * @return list of all active items
     */
    List<Item> findAllActive();
    
    /**
     * Get all items (including inactive)
     * @return list of all items
     */
    List<Item> findAll();
    
    /**
     * Update an existing item
     * @param item the item to update
     * @return the updated item
     */
    Item update(Item item);
    
    /**
     * Soft delete an item (set active = false)
     * @param id the item ID to deactivate
     */
    void deactivate(int id);
    
    /**
     * Activate an item (set active = true)
     * @param id the item ID to activate
     */
    void activate(int id);
}
