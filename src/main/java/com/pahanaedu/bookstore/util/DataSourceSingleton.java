package com.pahanaedu.bookstore.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceSingleton {
    private static volatile DataSource instance;
    private static final Object lock = new Object();
    
    private DataSourceSingleton() {}
    
    public static DataSource getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = createDataSource();
                }
            }
        }
        return instance;
    }
    
    private static DataSource createDataSource() {
    HikariConfig config = new HikariConfig();
    String url = "jdbc:mysql://localhost:3306/pahana_edu?useSSL=false&serverTimezone=UTC";
    String user = "root";
    String pass = "admin";
    config.setJdbcUrl(url);
    config.setUsername(user);
    config.setPassword(pass);
    config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
       
        config.setMaximumPoolSize(5);    // Reduced from 20 to 5
        config.setMinimumIdle(1);        // Reduced from 5 to 1
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(300000);   // Reduced from 600000 to 300000 (5 minutes)
        config.setMaxLifetime(900000);   // Reduced from 1800000 to 900000 (15 minutes)
        config.setLeakDetectionThreshold(60000);
        
        
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        
        return new HikariDataSource(config);
    }
    
    private static String getenvOrDefault(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
    }
    
    public static void close() {
        if (instance instanceof HikariDataSource) {
            ((HikariDataSource) instance).close();
        }
    }
}
