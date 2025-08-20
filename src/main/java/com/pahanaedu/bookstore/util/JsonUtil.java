package com.pahanaedu.bookstore.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public class JsonUtil {
    
    private static final ObjectMapper objectMapper;
    
    static {
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Convert object to JSON string
     * @param object the object to convert
     * @return JSON string representation
     * @throws IOException if conversion fails
     */
    public static String toJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
    
    /**
     * Convert JSON string to object
     * @param json the JSON string
     * @param clazz the target class
     * @param <T> the type of the target class
     * @return the deserialized object
     * @throws IOException if conversion fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }
    
    /**
     * Get the ObjectMapper instance for custom configuration
     * @return the ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
