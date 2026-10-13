package org.zl.product.management.repository;

import org.zl.product.management.model.Product;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileProductRepository implements ProductRepository {
    private final String filePath;
    private static final String SEPARATOR = ";";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public FileProductRepository(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Erreur création fichier : " + e.getMessage());
        }
    }

    @Override
    public void save(Product product) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(product.getId() + SEPARATOR +
                    product.getName() + SEPARATOR +
                    product.getPrice() + SEPARATOR +
                    product.getQuantity() + SEPARATOR +
                    product.getCreatedAt().format(FORMATTER) + SEPARATOR +
                    product.getUpdatedAt().format(FORMATTER));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erreur écriture : " + e.getMessage());
        }
    }

    @Override
    public void printAll() {
        File file = new File(filePath);
        if (!file.exists()) return;

        List<Product> products = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(SEPARATOR);
                    if (parts.length >= 6) {
                        String id = parts[0];
                        String name = parts[1];
                        double price = Double.parseDouble(parts[2]);
                        int quantity = Integer.parseInt(parts[3]);
                        LocalDateTime created = LocalDateTime.parse(parts[4], FORMATTER);
                        LocalDateTime updated = LocalDateTime.parse(parts[5], FORMATTER);
                        products.add(new Product(id, name, price, quantity, created, updated));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lecture : " + e.getMessage());
        }

        products.sort((p1, p2) -> p2.getUpdatedAt().compareTo(p1.getUpdatedAt()));

        for (Product p : products) {
            System.out.printf("%-10s %-16s %-10.2f %-10d %-20s %-20s\n",
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getQuantity(),
                    p.getCreatedAt().format(FORMATTER),
                    p.getUpdatedAt().format(FORMATTER));
        }
    }

    @Override
    public Product findById(String id) {
        File file = new File(filePath);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(SEPARATOR);
                    if (parts.length >= 6 && parts[0].equalsIgnoreCase(id)) {
                        return new Product(
                                parts[0],
                                parts[1],
                                Double.parseDouble(parts[2]),
                                Integer.parseInt(parts[3]),
                                LocalDateTime.parse(parts[4], FORMATTER),
                                LocalDateTime.parse(parts[5], FORMATTER)
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lecture : " + e.getMessage());
        }
        return null;
    }

    @Override
    public Product findByName(String name) {
        File file = new File(filePath);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(SEPARATOR);
                    if (parts.length >= 6 && parts[1].equalsIgnoreCase(name.trim())) {
                        return new Product(
                                parts[0],
                                parts[1],
                                Double.parseDouble(parts[2]),
                                Integer.parseInt(parts[3]),
                                LocalDateTime.parse(parts[4], FORMATTER),
                                LocalDateTime.parse(parts[5], FORMATTER)
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lecture: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean update(Product product) {
        File original = new File(filePath);
        File temp = new File("temp_" + filePath);
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(original));
             BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(SEPARATOR);
                if (parts.length >= 6 && parts[0].equalsIgnoreCase(product.getId())) {
                    // On garde la date de création originale, mais on écrit la nouvelle date de modification
                    writer.write(product.getId() + SEPARATOR +
                            product.getName() + SEPARATOR +
                            product.getPrice() + SEPARATOR +
                            product.getQuantity() + SEPARATOR +
                            product.getCreatedAt().format(FORMATTER) + SEPARATOR +
                            product.getUpdatedAt().format(FORMATTER));
                    updated = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur modification : " + e.getMessage());
            return false;
        }

        if (original.delete()) {
            temp.renameTo(original);
        }
        return updated;
    }

    @Override
    public boolean deleteById(String id) {
        File original = new File(filePath);
        File temp = new File("temp_" + filePath);
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(original));
             BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(SEPARATOR);
                if (parts.length >= 4 && parts[0].equalsIgnoreCase(id)) {
                    deleted = true;
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur suppression : " + e.getMessage());
            return false;
        }

        if (original.delete()) {
            temp.renameTo(original);
        }
        return deleted;
    }
}