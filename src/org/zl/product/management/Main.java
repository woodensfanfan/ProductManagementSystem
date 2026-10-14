package org.zl.product.management;

import org.zl.product.management.presentation.ProductConsoleUI;
import org.zl.product.management.repository.FileProductRepository;
import org.zl.product.management.repository.FileSaleRepository;
import org.zl.product.management.repository.ProductRepository;
import org.zl.product.management.repository.SaleRepository;
import org.zl.product.management.service.ProductService;
import org.zl.product.management.service.ProductServiceImpl;
import org.zl.product.management.service.SaleService;
import org.zl.product.management.service.SaleServiceImpl;

public class Main {
    public static void main(String[] args) {
        ProductRepository productRepository = new FileProductRepository("products.txt");
        SaleRepository saleRepository = new FileSaleRepository();

        ProductService productService = new ProductServiceImpl(productRepository);
        SaleService saleService = new SaleServiceImpl(productRepository, saleRepository);

        ProductConsoleUI ui = new ProductConsoleUI(productService, saleService);
        ui.start();
    }
}