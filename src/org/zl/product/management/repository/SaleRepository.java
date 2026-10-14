package org.zl.product.management.repository;

import org.zl.product.management.model.Payment;
import org.zl.product.management.model.Sale;
import org.zl.product.management.model.SaleItem;

import java.util.List;

public interface SaleRepository {
    void saveSale(Sale sale);
    void saveSaleItems(List<SaleItem> items);
    void savePayment (Payment payment);
}
