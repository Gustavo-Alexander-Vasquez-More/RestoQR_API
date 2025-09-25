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
    private UserRepository userRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private final Random rnd = new Random();

    @PostMapping("/seed")
    public ResponseEntity<?> seedData(
            @RequestParam(defaultValue = "5") int products,
            @RequestParam(defaultValue = "5") int tables,
            @RequestParam(defaultValue = "3") int users,
            @RequestParam(defaultValue = "5") int orders
    ) {
        Map<String, Object> result = new HashMap<>();

        // Create users
        List<User> createdUsers = new ArrayList<>();
        String[] roles = {"ADMIN", "USER", "MANAGER"};
        for (int i = 0; i < users; i++) {
            String username = "user" + (rnd.nextInt(9000) + 1000);
            User.Role role = User.Role.valueOf(roles[rnd.nextInt(roles.length)]);
            User u = new User();
            u.setUsername(username);
            u.setPassword("password"); // cambiar si quieres hash
            u.setRole(role);
            createdUsers.add(userRepository.save(u));
        }
        result.put("users_created", createdUsers.size());

        // Create tables
        List<Table> createdTables = new ArrayList<>();
        int startNumber = 1;
        // find max existing number
        try {
            List<Table> allTables = tableRepository.findAll();
            OptionalInt max = allTables.stream().mapToInt(Table::getNumber).max();
            if (max.isPresent()) startNumber = max.getAsInt() + 1;
        } catch (Exception ignored) {}

        for (int i = 0; i < tables; i++) {
            Table t = new Table(startNumber + i, 4 + rnd.nextInt(3));
            createdTables.add(tableRepository.save(t));
        }
        result.put("tables_created", createdTables.size());

        // Create products (mix drinks and dishes)
        List<Product> createdProducts = new ArrayList<>();
        String[] drinkNames = {"Coca Cola", "Agua Mineral", "Cerveza Rubia", "Jugo de Naranja"};
        String[] dishNames = {"Milanesa Napolitana", "Bruschetta", "Tiramisú", "Ensalada César"};

        for (int i = 0; i < products; i++) {
            boolean makeDrink = rnd.nextBoolean();
            if (makeDrink) {
                String name = drinkNames[rnd.nextInt(drinkNames.length)] + " " + (rnd.nextInt(100)+1);
                Drink d = new Drink(name, "Bebida refrescante", 10 + rnd.nextInt(40), true, 10 + rnd.nextInt(40), rnd.nextBoolean());
                createdProducts.add(productRepository.save(d));
            } else {
                String name = dishNames[rnd.nextInt(dishNames.length)] + " " + (rnd.nextInt(100)+1);
                int sub = 1 + rnd.nextInt(3); // 1 entrada,2 principal,3 postre
                Dish dish = new Dish(name, "Delicioso plato", 30 + rnd.nextInt(120), true, sub, rnd.nextBoolean());
                createdProducts.add(productRepository.save(dish));
            }
        }
        result.put("products_created", createdProducts.size());

        // Create orders
        List<Order> createdOrders = new ArrayList<>();
        // compute starting numOrden
        int nextNumOrden = orderRepository.findAll().stream().mapToInt(Order::getNumOrden).max().orElse(0) + 1;

        List<Table> availableTables = createdTables.isEmpty() ? tableRepository.findAll() : createdTables;
        List<Product> allProducts = productRepository.findAll();

        for (int i = 0; i < orders; i++) {
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
                    pd.setIsAlcoholic(dk.isAlcoholic());
                } else if (p instanceof Dish) {
                    Dish ds = (Dish) p;
                    pd.setSubcategory(ds.getSubcategory());
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

