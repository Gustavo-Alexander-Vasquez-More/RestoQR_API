package com.restoqr.restoApi.controller;

import com.restoqr.restoApi.dto.UserDTO;
import com.restoqr.restoApi.models.User;
import com.restoqr.restoApi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/find/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .<ResponseEntity<?>>map(user -> {
                    UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getRole().toString());
                    return ResponseEntity.ok(userDTO);
                })
                .orElseGet(() -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("mensaje", "El usuario no existe.");
                    return ResponseEntity.status(404).body(response);
                });
    }

    // Obtener todos los usuarios
    @GetMapping("/")
    public ResponseEntity<?> getUsers() {
        try {
            List<User> users = userRepository.findAll();
            List<UserDTO> userDTOs = users.stream()
                    .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getRole().toString()))
                    .toList();
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudieron obtener los usuarios.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // Contar usuarios
    @GetMapping("/count")
    public ResponseEntity<?> countUsers() {
        try {
            long count = userRepository.count();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo obtener el conteo de usuarios.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // Verificar si existe usuario por id
    @GetMapping("/exists/{id}")
    public ResponseEntity<?> userExists(@PathVariable String id) {
        try {
            boolean exists = userRepository.existsById(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo verificar la existencia del usuario.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // Crear usuario
    @PostMapping("/create/{username}/{password}/{role}")
    public ResponseEntity<?> createUser(@PathVariable String username, @PathVariable String password, @PathVariable String role) {
        try {
            if (userRepository.findByUsername(username).isPresent()) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "El nombre de usuario ya existe.");
                return ResponseEntity.status(409).body(response);
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(User.Role.valueOf(role.toUpperCase()));
            User savedUser = userRepository.save(user);
            UserDTO userDTO = new UserDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getRole().toString());
            return ResponseEntity.status(201).body(userDTO);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Rol inválido.");
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo crear el usuario.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // Login (verifica hash y guarda usuario en sesión)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String jwt = Jwts.builder()
                        .setSubject(user.getUsername())
                        .claim("role", user.getRole().toString())
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 día
                        .signWith(SignatureAlgorithm.HS256, "secreto123") // Usa una clave segura
                        .compact();
                Map<String, String> response = new HashMap<>();
                response.put("token", jwt);
                return ResponseEntity.ok(response);
            }
        }
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Usuario o contraseña incorrectos.");
        return ResponseEntity.status(401).body(response);
    }

    // Eliminar usuario por id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            if (!userRepository.existsById(id)) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "El usuario no existe.");
                return ResponseEntity.status(404).body(response);
            }
            userRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario con id " + id + " eliminado.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo eliminar el usuario.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // Actualizar usuario por id
    @PutMapping("/update/{id}/{username}/{password}/{role}")
    public ResponseEntity<?> updateUser(
            @PathVariable String id,
            @PathVariable String username,
            @PathVariable String password,
            @PathVariable String role) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "El usuario no existe.");
                return ResponseEntity.status(404).body(response);
            }
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(User.Role.valueOf(role.toUpperCase()));
            userRepository.save(user);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario actualizado.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Rol inválido.");
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo actualizar el usuario.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
