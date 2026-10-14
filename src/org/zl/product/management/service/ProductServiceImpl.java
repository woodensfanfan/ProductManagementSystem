package org.zl.product.management.service;

import org.zl.product.management.dto.ProductDto;
import org.zl.product.management.exception.DuplicateProductNameException;
import org.zl.product.management.exception.ProductNotFoundException;
import org.zl.product.management.model.Product;
import org.zl.product.management.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.Random;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final Random random = new Random();

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    private String generateRandomId() {
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }

    @Override
    public String createProduct(String name, double price, int quantity) throws DuplicateProductNameException {
        if (repository.findByName(name) != null) {
            throw new DuplicateProductNameException("Product with the name '" + name + "' already exist.");
        }

        String generatedId = generateRandomId();
        while (repository.findById(generatedId) != null) {
            generatedId = generateRandomId();
        }

        LocalDateTime now = LocalDateTime.now();
        Product product = new Product(generatedId, name, price, quantity, now, now);
        repository.save(product);
        return generatedId;
    }

    @Override
    public void displayAllProducts() {
        repository.printAll();
    }

    @Override
    public ProductDto getProductById(String id) throws ProductNotFoundException {
        Product product = repository.findById(id);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    @Override
    public void updateProduct(ProductDto dto) throws ProductNotFoundException, DuplicateProductNameException {
        Product existing = repository.findById(dto.getId());
        if (existing == null) {
            throw new ProductNotFoundException("Product with ID " + dto.getId() + " not found.");
        }

        Product productWithSameName = repository.findByName(dto.getName());
        if (productWithSameName != null && !productWithSameName.getId().equalsIgnoreCase(dto.getId())) {
            throw new DuplicateProductNameException("A Product with the name '" + dto.getName() + "' already exist.");
        }

        Product product = new Product(
                dto.getId(),
                dto.getName(),
                dto.getPrice(),
                dto.getQuantity(),
                existing.getCreatedAt(),
                LocalDateTime.now()
        );

        boolean success = repository.update(product);
        if (!success) {
            throw new ProductNotFoundException("Product with ID " + dto.getId() + " not found.");
        }
    }

    @Override
    public void deleteProduct(String id) throws ProductNotFoundException {
        boolean success = repository.deleteById(id);
        if (!success) {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
    }
}