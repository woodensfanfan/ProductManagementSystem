package org.zl.product.management;

import org.zl.product.management.presentation.ProductConsoleUI;
import org.zl.product.management.repository.FileProductRepository;
import org.zl.product.management.repository.ProductRepository;
import org.zl.product.management.service.ProductService;
import org.zl.product.management.service.ProductServiceImpl;

public class Main {
    public static void main(String[] args) {
        ProductRepository repository = new FileProductRepository("products.txt");
        ProductService productService = new ProductServiceImpl(repository);
        ProductConsoleUI ui = new ProductConsoleUI(productService);

        ui.start();
    }
}