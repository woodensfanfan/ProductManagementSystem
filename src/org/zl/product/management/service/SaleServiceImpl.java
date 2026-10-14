package org.zl.product.management.service;

import org.zl.product.management.model.Payment;
import org.zl.product.management.model.Product;
import org.zl.product.management.model.Sale;
import org.zl.product.management.model.SaleItem;
import org.zl.product.management.repository.ProductRepository;
import org.zl.product.management.repository.SaleRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SaleServiceImpl implements SaleService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final List<SaleItem> cart = new ArrayList<>();
    private final Random random = new Random();

    public SaleServiceImpl(ProductRepository productRepository, SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
    }

    @Override
    public String addToCart(String productId, int quantity) {
        Product product = productRepository.findById(productId);
        if (product == null) {
            return "Error : Can't find this ID : " + productId;
        }
        if (product.getQuantity() < quantity) {
            return "Error : Insufficient Stock (Available : " + product.getQuantity() + ").";
        }

        double total = product.getPrice() * quantity;
        cart.add(new SaleItem("", product.getId(), quantity, product.getPrice(), total));
        return "Product '" + product.getName() + "' added to cart.";
    }

    @Override
    public List<SaleItem> getCart() {
        return cart;
    }

    @Override
    public double getCartTotal() {
        double total = 0.0;
        for (SaleItem item : cart) {
            total += item.getTotal();
        }
        return total;
    }

    @Override
    public boolean isCartEmpty() {
        return cart.isEmpty();
    }

    @Override
    public String processCheckout(double paidAmount) {
        double total = getCartTotal();
        if (paidAmount < total) {
            return "Error : Insufficient Amount. The total amount due is " + String.format("%.2f", total);
        }


        String txId = "Tx" + (1000 + random.nextInt(9000));
        LocalDateTime now = LocalDateTime.now();

        Sale sale = new Sale(txId, now, total);
        saleRepository.saveSale(sale);

        for (SaleItem item : cart) {
            item.setSaleId(txId);

            Product p = productRepository.findById(item.getProductId());
            if (p != null) {
                p.setQuantity(p.getQuantity() - item.getQuantity());
                productRepository.update(p);
            }
        }
        saleRepository.saveSaleItems(cart);


        Payment payment = new Payment(txId, paidAmount, now);
        saleRepository.savePayment(payment);

        cart.clear();

        double change = paidAmount - total;
        return "Sale successfully recorded! Reference: " + txId +
                (change > 0 ? " (Change returned: " + String.format("%.2f", change) + ")" : "");
    }
}