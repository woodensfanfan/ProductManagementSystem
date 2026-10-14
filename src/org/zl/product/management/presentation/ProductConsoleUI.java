package org.zl.product.management.presentation;

import org.zl.product.management.dto.ProductDto;
import org.zl.product.management.exception.DuplicateProductNameException;
import org.zl.product.management.exception.ProductNotFoundException;
import org.zl.product.management.model.SaleItem;
import org.zl.product.management.service.ProductService;
import org.zl.product.management.service.SaleService;

import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ProductConsoleUI {

    private final ProductService productService;
    private final SaleService saleService;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

    public ProductConsoleUI(ProductService productService, SaleService saleService) {
        this.productService = productService;
        this.saleService = saleService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        String mainChoice = "";
        do {
            System.out.println("=========================================");
            System.out.println("             MAIN MENU                   ");
            System.out.println("=========================================");
            System.out.println("A - Product Management");
            System.out.println("B - Sales");
            System.out.println("0 - Exit");
            System.out.print("Choose option: ");
            mainChoice = scanner.nextLine().trim().toUpperCase();

            switch (mainChoice) {
                case "A" -> productMenu();
                case "B" -> salesMenu();
                case "0" -> System.out.println("Good Bye !");
                default -> System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
        } while (!mainChoice.equals("0"));
    }

    // ==========================================
    //       MENU A: PRODUCT MANAGEMENT
    // ==========================================
    private void productMenu() {
        int choice = -1;
        do {
            System.out.println("\n=========================================");
            System.out.println("        PRODUCT MANAGEMENT SYSTEM        ");
            System.out.println("=========================================");
            System.out.println("1. Create Product");
            System.out.println("2. List Products");
            System.out.println("3. Find Product");
            System.out.println("4. Update Product");
            System.out.println("5. Delete Product");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose option: ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1 -> createProduct();
                    case 2 -> listProducts();
                    case 3 -> findProduct();
                    case 4 -> updateProduct();
                    case 5 -> deleteProduct();
                    case 0 -> {}
                    default -> System.out.println("Invalid Option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        } while (choice != 0);
    }

    private void createProduct() {
        System.out.println("\n--- Create Product ---");
        String name = readNonEmptyString("Name: ");
        double price = readPositiveDouble("Price: ");
        int quantity = readPositiveInt("Quantity: ");

        try {
            String generatedId = productService.createProduct(name, price, quantity);
            System.out.println("Product successfully registered! Assigned ID: " + generatedId);
        } catch (DuplicateProductNameException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

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
            System.out.println("------------------------------------------------------");
            System.out.println("ID        : " + p.getId());
            System.out.println("Name       : " + p.getName());
            System.out.println("Price     : " + p.getPrice());
            System.out.println("Quantity  : " + p.getQuantity());
            if (p.getCreatedAt() != null) {
                System.out.println("Created on   : " + p.getCreatedAt().format(DATE_FORMAT));
            }
            if (p.getUpdatedAt() != null) {
                System.out.println("Modified on   : " + p.getUpdatedAt().format(DATE_FORMAT));
            }
            System.out.println("------------------------------------------------------");
        } catch (ProductNotFoundException e) {
            System.out.println("Product with ID " + id + " not found.");
        }
    }

    private void updateProduct() {
        System.out.print("Enter product ID to update: ");
        String id = scanner.nextLine().trim();
        try {
            ProductDto existing = productService.getProductById(id);

            String name = readNonEmptyString("New Name: ");
            double price = readPositiveDouble("New Price: ");
            int quantity = readPositiveInt("New Quantity: ");

            ProductDto dto = new ProductDto(id, name, price, quantity, existing.getCreatedAt(), null);
            productService.updateProduct(dto);
            System.out.println("Product updated successfully !");
        } catch (ProductNotFoundException | DuplicateProductNameException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    private void deleteProduct() {
        System.out.print("Enter product ID to delete: ");
        String id = scanner.nextLine().trim();
        try {
            productService.getProductById(id);
            System.out.print("Are you sure you want to delete this product? (Y/N):");
            String confirm = scanner.nextLine().trim();
            if (confirm.equalsIgnoreCase("N") || confirm.equalsIgnoreCase("Y")) {
                productService.deleteProduct(id);
                System.out.println("Product successfully deleted !");
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (ProductNotFoundException e) {
            System.out.println("Product with ID " + id + " not found.");
        }
    }

    // ==========================================
    //             MENU B : SALES
    // ==========================================
    private void salesMenu() {
        int choice = -1;
        do {
            System.out.println("\n--- SALES ---");
            System.out.println("1. Add Product to Cart");
            System.out.println("2. View Cart");
            System.out.println("3. Checkout & Payment");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose option: ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1 -> handleAddToCart();
                    case 2 -> displayCart();
                    case 3 -> handleCheckout();
                    case 0 -> {}
                    default -> System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        } while (choice != 0);
    }

    private void handleAddToCart() {
        while (true) {
            System.out.println("\n--- AVAILABLE PRODUCTS ---");
            listProducts();

            System.out.print("Enter Product ID to add: ");
            String id = scanner.nextLine().trim();

            int qty = readPositiveInt("Quantity: ");

            String result = saleService.addToCart(id, qty);
            System.out.println(result);

            displayCart();

            System.out.print("\nDo you want to add another product? (Y/N): ");
            String response = scanner.nextLine().trim();

            if (!response.equalsIgnoreCase("N") && !response.equalsIgnoreCase("Y")) {
                break;
            }
        }
    }

    private void displayCart() {
        if (saleService.isCartEmpty()) {
            System.out.println("The cart is currently empty.");
            return;
        }
        System.out.println("\n======================== CART ========================");
        System.out.printf("%-12s %-6s %-10s %-10s\n", "ID PROD", "QTY", "PR/UNT", "TOTAL");
        System.out.println("------------------------------------------------------");
        for (SaleItem item : saleService.getCart()) {
            System.out.printf("%-12s %-6d %-10.2f %-10.2f\n",
                    item.getProductId(), item.getQuantity(), item.getUnitPrice(), item.getTotal());
        }
        System.out.println("------------------------------------------------------");
        System.out.printf("TOTAL AMOUNT: %.2f\n", saleService.getCartTotal());
        System.out.println("======================================================");
    }

    private void handleCheckout() {
        if (saleService.isCartEmpty()) {
            System.out.println("The cart is empty.");
            return;
        }
        displayCart();
        double payment = readPositiveDouble("Enter Payment Amount: ");
        System.out.println(saleService.processCheckout(payment));
    }

    // ==========================================
    //        USER INPUT VALIDATION
    // ==========================================
    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Error: This field cannot be empty.");
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
                System.out.println("Error: The value must be strictly greater than 0..");
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
                System.out.println("Error: The value must be strictly greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }
}