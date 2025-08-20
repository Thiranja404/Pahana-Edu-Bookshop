package com.pahanaedu.bookstore.service;

import com.pahanaedu.bookstore.dao.UserDao;
import com.pahanaedu.bookstore.dao.UserDaoImpl;
import com.pahanaedu.bookstore.model.User;
import com.pahanaedu.bookstore.util.PasswordUtil;

import java.util.Optional;

public class AuthService {
    
    private final UserDao userDao;
    
    public AuthService() {
        this.userDao = new UserDaoImpl();
    }
    
    /**
     * Authenticate a user with username and password
     * @param username the username
     * @param password the plain text password
     * @return Optional containing the user if authentication succeeds
     */
    public Optional<User> login(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return Optional.empty();
        }
        
        Optional<User> userOpt = userDao.findByUsername(username.trim());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Register a new user
     * @param username the username
     * @param password the plain text password
     * @param role the user role
     * @return the created user
     * @throws IllegalArgumentException if username already exists or invalid input
     */
    public User register(String username, String password, String role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        if (role == null || role.trim().isEmpty()) {
            role = "USER";
        }
        
        // Check if username already exists
        if (userDao.findByUsername(username.trim()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Hash the password
        String passwordHash = PasswordUtil.hashPassword(password);
        
        // Create and save user
        User user = new User(username.trim(), passwordHash, role.toUpperCase());
        return userDao.save(user);
    }
    
    /**
     * Change user password
     * @param userId the user ID
     * @param currentPassword the current password
     * @param newPassword the new password
     * @return true if password was changed successfully
     */
    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userDao.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Verify current password
            if (PasswordUtil.verifyPassword(currentPassword, user.getPasswordHash())) {
                // Hash new password and update
                String newPasswordHash = PasswordUtil.hashPassword(newPassword);
                user.setPasswordHash(newPasswordHash);
                userDao.update(user);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if a user has a specific role
     * @param user the user to check
     * @param role the role to check for
     * @return true if user has the role
     */
    public boolean hasRole(User user, String role) {
        return user != null && role != null && 
               role.equalsIgnoreCase(user.getRole());
    }
    
    /**
     * Check if a user is an admin
     * @param user the user to check
     * @return true if user is an admin
     */
    public boolean isAdmin(User user) {
        return hasRole(user, "ADMIN");
    }
}
