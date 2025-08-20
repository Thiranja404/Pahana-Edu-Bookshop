package com.pahanaedu.bookstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Bill {
    private int id;
    private String billNo;
    private String customerId;  // Now stores account number like ACC000005
    private Customer customer;
    private LocalDateTime billDate;
    private BigDecimal total;
    private LocalDateTime createdAt;
    private List<BillItem> billItems;
    
    public Bill() {}
    
    public Bill(String billNo, String customerId, BigDecimal total) {
        this.billNo = billNo;
        this.customerId = customerId;
        this.total = total;
        this.billDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getBillNo() {
        return billNo;
    }
    
    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public LocalDateTime getBillDate() {
        return billDate;
    }
    
    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<BillItem> getBillItems() {
        return billItems;
    }
    
    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }
    
    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", billNo='" + billNo + '\'' +
                ", customerId=" + customerId +
                ", total=" + total +
                ", billDate=" + billDate +
                '}';
    }
}
