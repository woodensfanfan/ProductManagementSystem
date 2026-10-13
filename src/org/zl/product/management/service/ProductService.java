package org.zl.product.management.service;

import org.zl.product.management.dto.ProductDto;
import org.zl.product.management.exception.DuplicateProductNameException;
import org.zl.product.management.exception.ProductNotFoundException;

public interface ProductService {
    String createProduct(String name, double price, int quantity) throws DuplicateProductNameException;
    void displayAllProducts();
    ProductDto getProductById(String id) throws ProductNotFoundException;
    void updateProduct(ProductDto dto) throws ProductNotFoundException, DuplicateProductNameException;
    void deleteProduct(String id) throws ProductNotFoundException;
}