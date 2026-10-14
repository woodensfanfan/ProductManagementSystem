package org.zl.product.management.model;

import java.time.LocalDateTime;

public class Sale {
    private String transactionId; // TxID (ex: Tx1000)
    private LocalDateTime date;   // date
    private double totalAmount;   // Total Amount

    public Sale() {}

    public Sale(String transactionId, LocalDateTime date, double totalAmount) {
        this.transactionId = transactionId;
        this.date = date;
        this.totalAmount = totalAmount;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}