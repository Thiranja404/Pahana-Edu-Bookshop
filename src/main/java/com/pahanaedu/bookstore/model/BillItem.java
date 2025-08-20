package com.pahanaedu.bookstore.model;

import java.math.BigDecimal;

public class BillItem {
    private int id;
    private int billId;
    private int itemId;
    private Item item;
    private int qty;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    
    public BillItem() {}
    
    public BillItem(int billId, int itemId, int qty, BigDecimal unitPrice) {
        this.billId = billId;
        this.itemId = itemId;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getBillId() {
        return billId;
    }
    
    public void setBillId(int billId) {
        this.billId = billId;
    }
    
    public int getItemId() {
        return itemId;
    }
    
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    public Item getItem() {
        return item;
    }
    
    public void setItem(Item item) {
        this.item = item;
    }
    
    public int getQty() {
        return qty;
    }
    
    public void setQty(int qty) {
        this.qty = qty;
        if (this.unitPrice != null) {
            this.lineTotal = this.unitPrice.multiply(BigDecimal.valueOf(qty));
        }
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        if (this.qty > 0) {
            this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(this.qty));
        }
    }
    
    public BigDecimal getLineTotal() {
        return lineTotal;
    }
    
    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
    
    @Override
    public String toString() {
        return "BillItem{" +
                "id=" + id +
                ", billId=" + billId +
                ", itemId=" + itemId +
                ", qty=" + qty +
                ", unitPrice=" + unitPrice +
                ", lineTotal=" + lineTotal +
                '}';
    }
}
