package com.restoqr.restoApi.controller;

import com.restoqr.restoApi.models.Table;
import com.restoqr.restoApi.models.User;
import com.restoqr.restoApi.repositories.TableRepository;
import com.restoqr.restoApi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tables")
public class TableController {
    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/find/{number}")
    public ResponseEntity<?> getTableByNumber(@PathVariable int number) {
        Optional<Table> tableOpt = tableRepository.findByNumber(number);
        return tableOpt
                .<ResponseEntity<?>>map(table -> ResponseEntity.ok(table))
                .orElseGet(() -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("mensaje", "La mesa no existe.");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @GetMapping("/all")
    public ResponseEntity<?> getTables() {
        try {
            List<Table> tables = tableRepository.findAll();
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudieron obtener las mesas.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countTables() {
        try {
            long count = tableRepository.count();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo obtener el conteo de mesas.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTable(@RequestHeader("X-User-Id") String userId, @RequestBody Map<String, Object> payload) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "Usuario no encontrado.");
                return ResponseEntity.status(404).body(response);
            }
            String groupId = user.getGroup_id();
            int number = Integer.parseInt(payload.get("number").toString());
            int capacity = Integer.parseInt(payload.get("capacity").toString());
            boolean available = payload.get("available") != null ? Boolean.parseBoolean(payload.get("available").toString()) : true;
            Table table = new Table(number, capacity);
            table.setAvailable(available);
            table.setGroup_id(groupId); // Asignar el group_id del usuario
            Table savedTable = tableRepository.save(table);
            return ResponseEntity.status(201).body(savedTable);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo crear la mesa.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTable(@PathVariable String id) {
        try {
            if (!tableRepository.existsById(id)) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "La mesa no existe.");
                return ResponseEntity.status(404).body(response);
            }
            tableRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Mesa con id " + id + " eliminada.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo eliminar la mesa.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/update/{id}/{number}/{capacity}/{available}")
    public ResponseEntity<?> updateTable(
            @PathVariable String id,
            @PathVariable int number,
            @PathVariable int capacity,
            @PathVariable boolean available) {
        try {
            Table table = tableRepository.findById(id).orElse(null);
            if (table == null) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "La mesa no existe.");
                return ResponseEntity.status(404).body(response);
            }
            table.setNumber(number);
            table.setCapacity(capacity);
            table.setAvailable(available);
            tableRepository.save(table);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Mesa actualizada.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo actualizar la mesa.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
