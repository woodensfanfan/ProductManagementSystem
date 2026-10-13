package org.zl.product.management.repository;

import org.zl.product.management.model.Product;

public interface ProductRepository {
    void save(Product product);
    void printAll();
    Product findById(String id);
    Product findByName(String name);
    boolean update(Product product);
    boolean deleteById(String id);
}