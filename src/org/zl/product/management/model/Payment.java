package org.zl.product.management.model;

import java.time.LocalDateTime;

public class Payment {
    private String saleId;
    private double paymentAmount;
    private LocalDateTime date;

    public Payment() {}

    public Payment(String saleId, double paymentAmount, LocalDateTime date) {
        this.saleId = saleId;
        this.paymentAmount = paymentAmount;
        this.date = date;
    }

    public String getSaleId() { return saleId; }
    public void setSaleId(String saleId) { this.saleId = saleId; }

    public double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(double paymentAmount) { this.paymentAmount = paymentAmount; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}