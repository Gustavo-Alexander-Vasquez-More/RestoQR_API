package com.restoqr.restoApi.controller;

import com.restoqr.restoApi.models.*;
import com.restoqr.restoApi.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class TestDataController {

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    private final Random rnd = new Random();

    @PostMapping("/seed")
    public ResponseEntity<?> seedData() {
        Map<String, Object> result = new HashMap<>();

        // Crear subcategorías fijas
        String[] subNames = {"Sopas", "Bebidas Alcohólicas", "Bebidas gasificadas", "Postres", "Panadería", "Pescados", "Pollo", "Carne", "Guisos", "Pastas"};
        List<SubCategory> subCategories = new ArrayList<>();
        for (String name : subNames) {
            SubCategory sub = new SubCategory();
            sub.setNombre(name);
            sub.setPhotoUrl("https://ejemplo.com/subcat/" + name.replace(" ", "_") + ".jpg");
            subCategories.add(subCategoryRepository.save(sub));
        }
        result.put("subcategories_created", subCategories.size());

        // Crear 8 mesas
        List<Table> createdTables = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Table t = new Table(i, 4 + rnd.nextInt(3));
            createdTables.add(tableRepository.save(t));
        }
        result.put("tables_created", createdTables.size());

        // Crear productos asignando subcategorías
        List<Product> createdProducts = new ArrayList<>();
        String[] drinkNames = {"Coca Cola", "Agua Mineral", "Cerveza Rubia", "Jugo de Naranja"};
        String[] dishNames = {"Milanesa Napolitana", "Bruschetta", "Tiramisú", "Ensalada César", "Sopa de Pollo", "Guiso de Carne", "Pastas Alfredo", "Pan de Ajo"};
        for (int i = 0; i < 20; i++) {
            boolean makeDrink = rnd.nextBoolean();
            List<String> prodSubcats = new ArrayList<>();
            if (makeDrink) {
                String name = drinkNames[rnd.nextInt(drinkNames.length)] + " " + (rnd.nextInt(100)+1);
                String photoUrl = "https://ejemplo.com/drink" + (rnd.nextInt(100)+1) + ".jpg";
                // Asignar subcategoría aleatoria de bebidas
                prodSubcats.add(subCategories.get(rnd.nextInt(3)).getId()); // 0-2: Bebidas
                Drink d = new Drink(
                    name,
                    photoUrl,
                    "Bebida refrescante",
                    10 + rnd.nextInt(40),
                    "drink",
                    prodSubcats,
                    true,
                    10 + rnd.nextInt(40)
                );
                createdProducts.add(productRepository.save(d));
            } else {
                String name = dishNames[rnd.nextInt(dishNames.length)] + " " + (rnd.nextInt(100)+1);
                String photoUrl = "https://ejemplo.com/dish" + (rnd.nextInt(100)+1) + ".jpg";
                // Asignar subcategoría aleatoria de platos
                prodSubcats.add(subCategories.get(3 + rnd.nextInt(subCategories.size()-3)).getId()); // 3+: Platos
                Dish dish = new Dish(
                    name,
                    photoUrl,
                    "Delicioso plato",
                    30 + rnd.nextInt(120),
                    "dish",
                    prodSubcats,
                    true,
                    rnd.nextBoolean()
                );
                createdProducts.add(productRepository.save(dish));
            }
        }
        result.put("products_created", createdProducts.size());

        // Crear 5 órdenes en mesas disponibles
        List<Order> createdOrders = new ArrayList<>();
        // compute starting numOrden
        int nextNumOrden = orderRepository.findAll().stream().mapToInt(Order::getNumOrden).max().orElse(0) + 1;

        List<Table> availableTables = createdTables.isEmpty() ? tableRepository.findAll() : createdTables;
        List<Product> allProducts = productRepository.findAll();

        for (int i = 0; i < 5; i++) {
            Order o = new Order();
            o.setNumOrden(nextNumOrden++);
            // pick a table
            Table t = availableTables.get(rnd.nextInt(availableTables.size()));
            o.setNumberTable(t.getNumber());
            // pick 1-4 products
            int count = 1 + rnd.nextInt(Math.max(1, Math.min(4, allProducts.size())));
            List<Product> picks = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                picks.add(allProducts.get(rnd.nextInt(allProducts.size())));
            }
            // convert to ProductOrderDTO
            List<ProductOrderDTO> dtoList = picks.stream().map(p -> {
                ProductOrderDTO pd = new ProductOrderDTO();
                pd.setId(p.getId());
                pd.setNameProduct(p.getNameProduct());
                pd.setDescription(p.getDescription());
                pd.setPrice(p.getPrice());
                pd.setCategory(p.getCategory());
                pd.setAvailable(p.isAvailable());
                if (p instanceof Drink) {
                    Drink dk = (Drink) p;
                    pd.setStock(dk.getStock());
                } else if (p instanceof Dish) {
                    Dish ds = (Dish) p;
                    pd.setIsVegetarian(ds.isVegetarian());
                }
                return pd;
            }).collect(Collectors.toList());
            o.setProducts(dtoList);
            o.setStatus(1); // pendiente
            double total = dtoList.stream().mapToDouble(ProductOrderDTO::getPrice).sum();
            o.setTotalPrice(total);
            createdOrders.add(orderRepository.save(o));
        }
        result.put("orders_created", createdOrders.size());

        return ResponseEntity.ok(result);
    }
}
