package org.zl.product.management.presentation;

import org.zl.product.management.dto.ProductDto;
import org.zl.product.management.exception.DuplicateProductNameException;
import org.zl.product.management.exception.ProductNotFoundException;
import org.zl.product.management.service.ProductService;

import java.util.Scanner;

public class ProductConsoleUI {

    private final ProductService productService;
    private final Scanner scanner;

    public ProductConsoleUI(ProductService productService) {
        this.productService = productService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int choice = -1;
        do {
            showMenu();
            System.out.print("Choose an option: ");
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1 -> createProduct();
                    case 2 -> listProducts();
                    case 3 -> findProduct();
                    case 4 -> updateProduct();
                    case 5 -> deleteProduct();
                    case 0 -> System.out.println("Good bye !");
                    default -> System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
            System.out.println();
        } while (choice != 0);
    }

    private void showMenu() {
        System.out.println("=========================================");
        System.out.println("        PRODUCT MANAGEMENT SYSTEM        ");
        System.out.println("=========================================");
        System.out.println("1. Create Product");
        System.out.println("2. List Products");
        System.out.println("3. Find Product");
        System.out.println("4. Update Product");
        System.out.println("5. Delete Product");
        System.out.println("0. Exit");
    }

    private void createProduct() {
        System.out.println("\n--- Create Product ---");
        String name = readNonEmptyString("Name: ");
        double price = readPositiveDouble("Price: ");
        int quantity = readPositiveInt("Quantity: ");

        try {
            String generatedId = productService.createProduct(name, price, quantity);
            System.out.println("Product registered successfully ! ID attributed : " + generatedId);
        } catch (DuplicateProductNameException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    private void updateProduct() {
        System.out.print("Enter product ID to update: ");
        String id = scanner.nextLine().trim();
        try {
            productService.getProductById(id);

            String name = readNonEmptyString("New Name: ");
            double price = readPositiveDouble("New Price: ");
            int quantity = readPositiveInt("New Quantity: ");

            productService.updateProduct(new ProductDto(id, name, price, quantity, null, null));
            System.out.println("Product updated successfully !");
        } catch (ProductNotFoundException | DuplicateProductNameException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Error : The name cannot be empty.");
        }
    }

    private double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double val = Double.parseDouble(scanner.nextLine().trim());
                if (val > 0) {
                    return val;
                }
                System.out.println("Error: The value must be strictly greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                if (val > 0) {
                    return val;
                }
                System.out.println("Error: The quantity must be strictly greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    private static final java.time.format.DateTimeFormatter FORMATTER = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private void listProducts() {
        System.out.println("\n==================================== ALL PRODUCTS ====================================");
        System.out.printf("%-10s %-16s %-10s %-10s %-20s %-20s\n", "ID", "NAME", "PRICE", "QUANTITY", "CREATED AT", "UPDATED AT");
        System.out.println("--------------------------------------------------------------------------------------");
        productService.displayAllProducts();
        System.out.println("======================================================================================");
    }

    private void findProduct() {
        System.out.print("Enter product ID: ");
        String id = scanner.nextLine().trim();
        try {
            ProductDto p = productService.getProductById(id);
            System.out.printf("%-10s %-16s %-10s %-10s %-20s %-20s\n", "ID", "NAME", "PRICE", "QUANTITY", "CREATED AT", "UPDATED AT");
            System.out.println("--------------------------------------------------------------------------------------");
            System.out.printf("%-10s %-16s %-10.2f %-10d %-20s %-20s\n",
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getQuantity(),
                    p.getCreatedAt().format(FORMATTER),
                    p.getUpdatedAt().format(FORMATTER));
            System.out.println("--------------------------------------------------------------------------------------");
        } catch (ProductNotFoundException e) {
            System.out.println("Product with ID " + id + " not found.");
        }
    }

    private void deleteProduct() {
        System.out.print("Enter product ID: ");
        String id = scanner.nextLine().trim();
        try {
            productService.getProductById(id);
            System.out.print("Confirmation before deletion (Y/N): ");
            String confirm = scanner.nextLine().trim();
            if (confirm.equalsIgnoreCase("N") || confirm.equalsIgnoreCase("Y")) {
                productService.deleteProduct(id);
                System.out.println("Product successfully removed!");
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (ProductNotFoundException e) {
            System.out.println("Product with ID " + id + " not found.");
        }
    }
}