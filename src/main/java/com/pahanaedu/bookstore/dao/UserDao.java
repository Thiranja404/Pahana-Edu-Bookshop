package com.pahanaedu.bookstore.dao;

import com.pahanaedu.bookstore.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    /**
     * Find a user by username
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Save a new user
     * @param user the user to save
     * @return the saved user with generated ID
     */
    User save(User user);
    
    /**
     * Find a user by ID
     * @param id the user ID
     * @return Optional containing the user if found
     */
    Optional<User> findById(int id);
    
    /**
     * Get all users
     * @return list of all users
     */
    List<User> findAll();
    
    /**
     * Update an existing user
     * @param user the user to update
     * @return the updated user
     */
    User update(User user);
    
    /**
     * Delete a user by ID
     * @param id the user ID to delete
     */
    void deleteById(int id);
}
