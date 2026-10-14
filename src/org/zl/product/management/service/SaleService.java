package org.zl.product.management.service;

import org.zl.product.management.model.SaleItem;

import java.util.List;

public interface SaleService {
    String addToCart(String productId, int quantity);
    List<SaleItem> getCart();
    double getCartTotal();
    boolean isCartEmpty();
    String processCheckout(double paidAmount);
}