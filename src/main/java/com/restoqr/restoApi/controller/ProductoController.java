package com.restoqr.restoApi.controller;

import com.restoqr.restoApi.models.Product;
import com.restoqr.restoApi.models.Drink;
import com.restoqr.restoApi.models.Dish;
import com.restoqr.restoApi.models.SubCategory;
import com.restoqr.restoApi.repositories.ProductRepository;
import com.restoqr.restoApi.repositories.SubCategoryRepository;
import com.restoqr.restoApi.repositories.UserRepository;
import com.restoqr.restoApi.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductoController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestHeader("X-User-Id") String userId, @RequestBody Map<String, Object> payload) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "Usuario no encontrado.");
                return ResponseEntity.status(404).body(response);
            }
            String groupId = user.getGroup_id();
            String category = (String) payload.get("category");
            String photoUrl = (String) payload.get("photoUrl");
            List<Map<String, Object>> subCategoryList = (List<Map<String, Object>>) payload.get("subCategory");
            List<String> subCategoryIds = new java.util.ArrayList<>();
            if (subCategoryList != null) {
                for (Map<String, Object> subCatMap : subCategoryList) {
                    String id = (String) subCatMap.get("id");
                    if (id != null && !id.isEmpty()) {
                        subCategoryIds.add(id);
                    } else {
                        // si no viene id, creamos la subcategoría y usamos su id
                        SubCategory sub = new SubCategory();
                        sub.setNombre((String) subCatMap.get("nombre"));
                        sub.setPhotoUrl((String) subCatMap.get("photoUrl"));
                        SubCategory saved = subCategoryRepository.save(sub);
                        subCategoryIds.add(saved.getId());
                    }
                }
            }
            Product product;
            if ("drink".equalsIgnoreCase(category)) {
                product = new Drink(
                        null,
                        (String) payload.get("nameProduct"),
                        photoUrl,
                        (String) payload.get("description"),
                        Double.parseDouble(payload.get("price").toString()),
                        category,
                        subCategoryIds,
                        Boolean.parseBoolean(payload.get("available").toString()),
                        Integer.parseInt(payload.get("stock").toString())
                );
            } else if ("dish".equalsIgnoreCase(category)) {
                product = new Dish(
                        null,
                        (String) payload.get("nameProduct"),
                        photoUrl,
                        (String) payload.get("description"),
                        Double.parseDouble(payload.get("price").toString()),
                        category,
                        subCategoryIds,
                        Boolean.parseBoolean(payload.get("available").toString()),
                        Boolean.parseBoolean(payload.get("isVegetarian").toString())
                );
            } else {
                product = new Product(
                        null,
                        (String) payload.get("nameProduct"),
                        photoUrl,
                        (String) payload.get("description"),
                        Double.parseDouble(payload.get("price").toString()),
                        category,
                        subCategoryIds,
                        Boolean.parseBoolean(payload.get("available").toString())
                ) {};
            }
            product.setGroup_id(groupId); // Asignar el group_id del usuario
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
            String photoUrl = (String) payload.get("photoUrl");
            List<Map<String, Object>> subCategoryList = (List<Map<String, Object>>) payload.get("subCategory");
            List<String> subCategoryIds = new java.util.ArrayList<>();
            if (subCategoryList != null) {
                for (Map<String, Object> subCatMap : subCategoryList) {
                    String sid = (String) subCatMap.get("id");
                    if (sid != null && !sid.isEmpty()) {
                        subCategoryIds.add(sid);
                    } else {
                        SubCategory sub = new SubCategory();
                        sub.setNombre((String) subCatMap.get("nombre"));
                        sub.setPhotoUrl((String) subCatMap.get("photoUrl"));
                        SubCategory saved = subCategoryRepository.save(sub);
                        subCategoryIds.add(saved.getId());
                    }
                }
            }
            product.setNameProduct((String) payload.get("nameProduct"));
            product.setDescription((String) payload.get("description"));
            product.setPrice(Double.parseDouble(payload.get("price").toString()));
            product.setCategory(category);
            product.setAvailable(Boolean.parseBoolean(payload.get("available").toString()));
            product.setPhotoUrl(photoUrl);
            product.setSubCategory(subCategoryIds);
            if ("drink".equalsIgnoreCase(category) && product instanceof Drink) {
                ((Drink) product).setStock(Integer.parseInt(payload.get("stock").toString()));
            } else if ("dish".equalsIgnoreCase(category) && product instanceof Dish) {
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

    @GetMapping("/by-subcategory/{subId}")
    public ResponseEntity<?> getProductsBySubcategory(@PathVariable String subId, @RequestParam(name = "populate", defaultValue = "false") boolean populate) {
        try {
            List<Product> products = productRepository.findBySubCategoryContaining(subId);
            if (!populate) {
                return ResponseEntity.ok(products);
            }
            List<Map<String, Object>> populated = products.stream().map(p -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", p.getId());
                m.put("nameProduct", p.getNameProduct());
                m.put("description", p.getDescription());
                m.put("price", p.getPrice());
                m.put("category", p.getCategory());
                m.put("available", p.isAvailable());
                m.put("photoUrl", p.getPhotoUrl());
                // resolver subcategorias
                List<SubCategory> subs = subCategoryRepository.findAllById(p.getSubCategory());
                m.put("subCategories", subs);
                if (p instanceof Drink) {
                    m.put("stock", ((Drink) p).getStock());
                } else if (p instanceof Dish) {
                    m.put("isVegetarian", ((Dish) p).isVegetarian());
                }
                return m;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(populated);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo obtener productos por subcategoría.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
