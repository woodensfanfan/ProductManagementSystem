package org.zl.product.management.repository;

import org.zl.product.management.model.Payment;
import org.zl.product.management.model.Sale;
import org.zl.product.management.model.SaleItem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileSaleRepository implements SaleRepository {
    private static final String SEPARATOR = ";";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

    private final String salesFilePath = "sales.txt";
    private final String saleItemsFilePath = "sale_items.txt";
    private final String paymentsFilePath = "payments.txt";

    public FileSaleRepository() {
        initFile(salesFilePath);
        initFile(saleItemsFilePath);
        initFile(paymentsFilePath);
    }

    private void initFile(String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error creation " + path + " : " + e.getMessage());
        }
    }

    @Override
    public void saveSale(Sale sale) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(salesFilePath, true))) {
            writer.write(sale.getTransactionId() + SEPARATOR +
                    sale.getDate().format(FORMATTER) + SEPARATOR +
                    sale.getTotalAmount());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing sales.txt : " + e.getMessage());
        }
    }

    @Override
    public void saveSaleItems(List<SaleItem> items) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saleItemsFilePath, true))) {
            for (SaleItem item : items) {
                writer.write(item.getSaleId() + SEPARATOR +
                        item.getProductId() + SEPARATOR +
                        item.getQuantity() + SEPARATOR +
                        item.getUnitPrice() + SEPARATOR +
                        item.getTotal());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing sale_items.txt : " + e.getMessage());
        }
    }

    @Override
    public void savePayment(Payment payment) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(paymentsFilePath, true))) {
            writer.write(payment.getSaleId() + SEPARATOR +
                    payment.getPaymentAmount() + SEPARATOR +
                    payment.getDate().format(FORMATTER));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing payments.txt : " + e.getMessage());
        }
    }
}