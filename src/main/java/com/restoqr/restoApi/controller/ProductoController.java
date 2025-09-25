package com.restoqr.restoApi.controller;

import com.restoqr.restoApi.models.Product;
import com.restoqr.restoApi.models.Drink;
import com.restoqr.restoApi.models.Dish;
import com.restoqr.restoApi.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductoController {
    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> payload) {
        try {
            String category = (String) payload.get("category");
            Product product;
            if ("drink".equalsIgnoreCase(category)) {
                product = new Drink(
                        (String) payload.get("nameProduct"),
                        (String) payload.get("description"),
                        Double.parseDouble(payload.get("price").toString()),
                        Boolean.parseBoolean(payload.get("available").toString()),
                        Integer.parseInt(payload.get("stock").toString()),
                        Boolean.parseBoolean(payload.get("isAlcoholic").toString())
                );
            } else if ("dish".equalsIgnoreCase(category)) {
                product = new Dish(
                        (String) payload.get("nameProduct"),
                        (String) payload.get("description"),
                        Double.parseDouble(payload.get("price").toString()),
                        Boolean.parseBoolean(payload.get("available").toString()),
                        Integer.parseInt(payload.get("subcategory").toString()),
                        Boolean.parseBoolean(payload.get("isVegetarian").toString())
                );
            } else {
                product = new Product(
                        (String) payload.get("nameProduct"),
                        (String) payload.get("description"),
                        Double.parseDouble(payload.get("price").toString()),
                        category,
                        Boolean.parseBoolean(payload.get("available").toString())
                ) {};
            }
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.status(201).body(savedProduct);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo crear el producto.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getProducts() {
        try {
            List<Product> products = productRepository.findAll();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudieron obtener los productos.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countProducts() {
        try {
            long count = productRepository.count();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo obtener el conteo de productos.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        try {
            if (!productRepository.existsById(id)) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "El producto no existe.");
                return ResponseEntity.status(404).body(response);
            }
            productRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Producto con id " + id + " eliminado.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo eliminar el producto.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        try {
            Product product = productRepository.findById(id).orElse(null);
            if (product == null) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "El producto no existe.");
                return ResponseEntity.status(404).body(response);
            }
            String category = (String) payload.get("category");
            product.setNameProduct((String) payload.get("nameProduct"));
            product.setDescription((String) payload.get("description"));
            product.setPrice(Double.parseDouble(payload.get("price").toString()));
            product.setCategory(category);
            product.setAvailable(Boolean.parseBoolean(payload.get("available").toString()));
            if ("drink".equalsIgnoreCase(category) && product instanceof Drink) {
                ((Drink) product).setStock(Integer.parseInt(payload.get("stock").toString()));
                ((Drink) product).setIsAlcoholic(Boolean.parseBoolean(payload.get("isAlcoholic").toString()));
            } else if ("dish".equalsIgnoreCase(category) && product instanceof Dish) {
                ((Dish) product).setSubcategory(Integer.parseInt(payload.get("subcategory").toString()));
                ((Dish) product).setIsVegetarian(Boolean.parseBoolean(payload.get("isVegetarian").toString()));
            }
            productRepository.save(product);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Producto actualizado.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo actualizar el producto.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/search/{nameProduct}")
    public ResponseEntity<?> searchProducts(@PathVariable String nameProduct) {
        try {
            List<Product> products = productRepository.findByNameProductContainingIgnoreCase(nameProduct);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo realizar la búsqueda.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
