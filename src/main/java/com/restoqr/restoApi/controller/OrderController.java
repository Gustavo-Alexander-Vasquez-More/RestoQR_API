package com.restoqr.restoApi.controller;

import com.restoqr.restoApi.models.Order;
import com.restoqr.restoApi.models.Product;
import com.restoqr.restoApi.models.User;
import com.restoqr.restoApi.models.ProductOrderDTO;
import com.restoqr.restoApi.repositories.OrderRepository;
import com.restoqr.restoApi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        return orderOpt
                .<ResponseEntity<?>>map(order -> ResponseEntity.ok(order))
                .orElseGet(() -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("mensaje", "La orden no existe.");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @GetMapping("/all")
    public ResponseEntity<?> getOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudieron obtener las órdenes.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countOrders() {
        try {
            long count = orderRepository.count();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo obtener el conteo de órdenes.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestHeader("X-User-Id") String userId, @RequestBody Map<String, Object> payload) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "Usuario no encontrado.");
                return ResponseEntity.status(404).body(response);
            }
            String groupId = user.getGroup_id();
            int numberTable = Integer.parseInt(payload.get("numberTable").toString());
            List<Map<String, Object>> productsPayload = (List<Map<String, Object>>) payload.get("products");
            List<ProductOrderDTO> products = new java.util.ArrayList<>();
            if (productsPayload != null) {
                for (Map<String, Object> prodMap : productsPayload) {
                    ProductOrderDTO dto = new ProductOrderDTO();
                    dto.setId((String) prodMap.get("id"));
                    dto.setNameProduct((String) prodMap.get("nameProduct"));
                    dto.setDescription((String) prodMap.get("description"));
                    dto.setPrice(Double.parseDouble(prodMap.get("price").toString()));
                    dto.setCategory((String) prodMap.get("category"));
                    dto.setAvailable(Boolean.parseBoolean(prodMap.get("available").toString()));
                    if (prodMap.containsKey("stock")) {
                        dto.setStock(Integer.parseInt(prodMap.get("stock").toString()));
                    }
                    if (prodMap.containsKey("isVegetarian")) {
                        dto.setIsVegetarian(Boolean.parseBoolean(prodMap.get("isVegetarian").toString()));
                    }
                    products.add(dto);
                }
            }
            int status = Integer.parseInt(payload.get("status").toString());
            double totalPrice = Double.parseDouble(payload.get("totalPrice").toString());
            Order order = new Order();
            order.setNumberTable(numberTable);
            order.setProducts(products);
            order.setStatus(status);
            order.setTotalPrice(totalPrice);
            order.setGroup_id(groupId); // Asignar el group_id del usuario
            Order savedOrder = orderRepository.save(order);
            return ResponseEntity.status(201).body(savedOrder);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo crear la orden.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable String id) {
        try {
            if (!orderRepository.existsById(id)) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "La orden no existe.");
                return ResponseEntity.status(404).body(response);
            }
            orderRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Orden con id " + id + " eliminada.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo eliminar la orden.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable String id, @RequestBody Order updatedOrder) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "La orden no existe.");
                return ResponseEntity.status(404).body(response);
            }
            order.setNumberTable(updatedOrder.getNumberTable());
            order.setProducts(updatedOrder.getProducts());
            order.setStatus(updatedOrder.getStatus());
            order.setTotalPrice(updatedOrder.getTotalPrice());
            orderRepository.save(order);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Orden actualizada.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo actualizar la orden.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/findByTable/{numberTable}")
    public ResponseEntity<?> getActiveOrdersByTable(@PathVariable int numberTable) {
        try {
            List<Integer> estadosActivos = List.of(1, 2, 3, 4); // pendiente, en_preparacion, listo, entregado
            List<Order> ordenesActivas = orderRepository.findByNumberTableAndStatusIn(numberTable, estadosActivos);
            return ResponseEntity.ok(ordenesActivas);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo obtener las órdenes activas para la mesa.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/findByNumOrden/{numOrden}")
    public ResponseEntity<?> getActiveOrderByNumOrden(@PathVariable int numOrden) {
        try {
            Optional<Order> orderOpt = orderRepository.findByNumOrden(numOrden);
            if (orderOpt.isPresent() && orderOpt.get().getStatus() != 5) { // 5 = cerrado
                return ResponseEntity.ok(orderOpt.get());
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No existe una orden activa con ese número de orden.");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo obtener la orden por número de orden.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/findAllByTable/{numberTable}")
    public ResponseEntity<?> getAllOrdersByTable(@PathVariable int numberTable) {
        try {
            List<Order> ordenes = orderRepository.findByNumberTable(numberTable);
            return ResponseEntity.ok(ordenes);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo obtener las órdenes para la mesa.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
