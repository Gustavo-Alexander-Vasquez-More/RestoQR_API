package com.restoqr.restoApi.controller;

import com.restoqr.restoApi.dto.UserDTO;
import com.restoqr.restoApi.models.User;
import com.restoqr.restoApi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String SUPERADMIN_GROUP_ID = "68d6e7beca5d0dead320d4ca";

    private boolean isAdmin(User user) {
        return user != null && user.getRole() == 1;
    }

    private boolean isSuperAdmin(User user) {
        return user != null && SUPERADMIN_GROUP_ID.equals(user.getGroup_id());
    }

    // Obtener todos los usuarios
    @GetMapping("/group/{group_id}/user/{user_id}")
    public ResponseEntity<?> getUsers(@PathVariable String group_id, @PathVariable String user_id) {
        User requester = userRepository.findById(user_id).orElse(null);
        if (requester == null) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario no encontrado.");
            return ResponseEntity.status(404).body(response);
        }
        if (!isAdmin(requester)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. Solo administradores pueden ver usuarios.");
            return ResponseEntity.status(403).body(response);
        }
        if (!isSuperAdmin(requester) && !requester.getGroup_id().equals(group_id)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. Solo administradores del grupo pueden ver usuarios de su grupo.");
            return ResponseEntity.status(403).body(response);
        }
        try {
            List<User> users;
            if (isSuperAdmin(requester)) {
                users = userRepository.findAll();
            } else {
                users = userRepository.findByGroupId(group_id);
            }
            List<UserDTO> userDTOs = users.stream()
                    .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getRole()))
                    .toList();
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudieron obtener los usuarios.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // Buscar usuario por username
    @GetMapping("/find/{username}/group/{group_id}/user/{user_id}")
    public ResponseEntity<?> getUser(@PathVariable String username, @PathVariable String group_id, @PathVariable String user_id) {
        User requester = userRepository.findById(user_id).orElse(null);
        if (requester == null) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario no encontrado.");
            return ResponseEntity.status(404).body(response);
        }
        if (!isAdmin(requester)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. Solo administradores pueden buscar usuarios.");
            return ResponseEntity.status(403).body(response);
        }
        if (!isSuperAdmin(requester) && !requester.getGroup_id().equals(group_id)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. Solo administradores del grupo pueden buscar usuarios de su grupo.");
            return ResponseEntity.status(403).body(response);
        }
        return userRepository.findByUsername(username)
                .map(user -> {
                    if (isSuperAdmin(requester) || requester.getGroup_id().equals(user.getGroup_id())) {
                        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getRole());
                        return ResponseEntity.ok(userDTO);
                    } else {
                        Map<String, String> response = new HashMap<>();
                        response.put("mensaje", "No tienes acceso a este usuario.");
                        return ResponseEntity.status(403).body(response);
                    }
                })
                .orElseGet(() -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("mensaje", "El usuario no existe.");
                    return ResponseEntity.status(404).body(response);
                });
    }

    // Contar usuarios
    @GetMapping("/count/group/{group_id}/user/{user_id}")
    public ResponseEntity<?> countUsers(@PathVariable String group_id, @PathVariable String user_id) {
        User requester = userRepository.findById(user_id).orElse(null);
        if (requester == null) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario no encontrado.");
            return ResponseEntity.status(404).body(response);
        }
        if (!isAdmin(requester)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. Solo administradores pueden contar usuarios.");
            return ResponseEntity.status(403).body(response);
        }
        if (!isSuperAdmin(requester) && !requester.getGroup_id().equals(group_id)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. Solo administradores del grupo pueden contar usuarios de su grupo.");
            return ResponseEntity.status(403).body(response);
        }
        try {
            long count;
            if (isSuperAdmin(requester)) {
                count = userRepository.count();
            } else {
                count = userRepository.countByGroupId(group_id);
            }
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
    @GetMapping("/exists/{id}/group/{group_id}/user/{user_id}")
    public ResponseEntity<?> userExists(@PathVariable String id, @PathVariable String group_id, @PathVariable String user_id) {
        User requester = userRepository.findById(user_id).orElse(null);
        if (requester == null) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario no encontrado.");
            return ResponseEntity.status(404).body(response);
        }
        if (!isAdmin(requester)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. Solo administradores pueden verificar usuarios.");
            return ResponseEntity.status(403).body(response);
        }
        if (!isSuperAdmin(requester) && !requester.getGroup_id().equals(group_id)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. Solo administradores del grupo pueden verificar usuarios de su grupo.");
            return ResponseEntity.status(403).body(response);
        }
        User user = userRepository.findById(id).orElse(null);
        boolean exists = user != null && (isSuperAdmin(requester) || requester.getGroup_id().equals(user.getGroup_id()));
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    // Crear usuario
    @PostMapping("/create/user/{user_id}")
    public ResponseEntity<?> createUser(@PathVariable String user_id, @RequestBody Map<String, Object> payload) {
        User creator = userRepository.findById(user_id).orElse(null);
        if (creator == null) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario creador no encontrado.");
            return ResponseEntity.status(404).body(response);
        }
        if (!isAdmin(creator)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. Solo administradores pueden crear usuarios en su grupo.");
            return ResponseEntity.status(403).body(response);
        }
        String groupId = creator.getGroup_id();
        try {
            String username = (String) payload.get("username");
            String password = (String) payload.get("password");
            Object roleObj = payload.get("role");
            int roleInt = 3; // default USER
            if (roleObj instanceof Number) {
                roleInt = ((Number) roleObj).intValue();
            } else if (roleObj instanceof String) {
                try { roleInt = Integer.parseInt((String) roleObj); } catch (NumberFormatException ex) { roleInt = 3; }
            }
            // Validación: username único
            if (username == null || username.trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "El nombre de usuario es obligatorio.");
                return ResponseEntity.status(400).body(response);
            }
            Optional<User> existsOpt = userRepository.findByUsername(username);
            if (existsOpt.isPresent()) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "El nombre de usuario ya existe.");
                return ResponseEntity.status(409).body(response);
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(roleInt);
            user.setGroup_id(groupId); // Asignar el group_id del usuario creador
            User savedUser = userRepository.save(user);
            return ResponseEntity.status(201).body(savedUser);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo crear el usuario.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // Eliminar usuario por id
    @DeleteMapping("/delete/{id}/group/{group_id}/user/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id, @PathVariable String group_id, @PathVariable String user_id) {
        User requester = userRepository.findById(user_id).orElse(null);
        if (requester == null) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario no encontrado.");
            return ResponseEntity.status(404).body(response);
        }
        if (!isAdmin(requester)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. Solo administradores del grupo pueden eliminar usuarios de su grupo.");
            return ResponseEntity.status(403).body(response);
        }
        if (!isSuperAdmin(requester) && !requester.getGroup_id().equals(group_id)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. El administrador no pertenece al grupo indicado.");
            return ResponseEntity.status(403).body(response);
        }
        User user = userRepository.findById(id).orElse(null);
        if (user == null || (!isSuperAdmin(requester) && !requester.getGroup_id().equals(user.getGroup_id()))) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No tienes acceso para eliminar este usuario.");
            return ResponseEntity.status(403).body(response);
        }
        try {
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
    @PutMapping("/update/{id}/{username}/{password}/{role}/group/{group_id}/user/{user_id}")
    public ResponseEntity<?> updateUser(
            @PathVariable String id,
            @PathVariable String username,
            @PathVariable String password,
            @PathVariable int role,
            @PathVariable String group_id,
            @PathVariable String user_id) {
        User requester = userRepository.findById(user_id).orElse(null);
        if (requester == null) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario no encontrado.");
            return ResponseEntity.status(404).body(response);
        }
        if (!isAdmin(requester)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. Solo administradores del grupo pueden actualizar usuarios de su grupo.");
            return ResponseEntity.status(403).body(response);
        }
        if (!isSuperAdmin(requester) && !requester.getGroup_id().equals(group_id)) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Acceso denegado. El administrador no pertenece al grupo indicado.");
            return ResponseEntity.status(403).body(response);
        }
        User user = userRepository.findById(id).orElse(null);
        if (user == null || (!isSuperAdmin(requester) && !requester.getGroup_id().equals(user.getGroup_id()))) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No tienes acceso para actualizar este usuario.");
            return ResponseEntity.status(403).body(response);
        }
        try {
            // Validación: username único (no permita cambiar a un username ya usado por otro)
            if (username == null || username.trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "El nombre de usuario es obligatorio.");
                return ResponseEntity.status(400).body(response);
            }
            Optional<User> byName = userRepository.findByUsername(username);
            if (byName.isPresent() && !byName.get().getId().equals(id)) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "El nombre de usuario ya existe.");
                return ResponseEntity.status(409).body(response);
            }

            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
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

    // --- Endpoints específicos para superadministradores ---

    // Listar todos los usuarios (solo superadmin)
    @GetMapping("/super/all/{super_user_id}")
    public ResponseEntity<?> getAllUsersSuper(@PathVariable String super_user_id) {
        User requester = userRepository.findById(super_user_id).orElse(null);
        if (requester == null) {
            Map<String, String> r = new HashMap<>(); r.put("mensaje", "Usuario no encontrado.");
            return ResponseEntity.status(404).body(r);
        }
        if (!isSuperAdmin(requester)) {
            Map<String, String> r = new HashMap<>(); r.put("mensaje", "Acceso denegado. Solo superadministradores.");
            return ResponseEntity.status(403).body(r);
        }
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream().map(u -> new UserDTO(u.getId(), u.getUsername(), u.getRole())).toList();
        return ResponseEntity.ok(userDTOs);
    }

    // Crear usuario y asignarle un group_id arbitrario (solo superadmin)
    @PostMapping("/super/create/{super_user_id}/{target_group_id}")
    public ResponseEntity<?> createUserSuper(@PathVariable String super_user_id, @PathVariable String target_group_id, @RequestBody Map<String,Object> payload) {
        User requester = userRepository.findById(super_user_id).orElse(null);
        if (requester == null) {
            Map<String, String> r = new HashMap<>(); r.put("mensaje", "Usuario no encontrado.");
            return ResponseEntity.status(404).body(r);
        }
        if (!isSuperAdmin(requester)) {
            Map<String, String> r = new HashMap<>(); r.put("mensaje", "Acceso denegado. Solo superadministradores.");
            return ResponseEntity.status(403).body(r);
        }
        try {
            String username = (String) payload.get("username");
            String password = (String) payload.get("password");
            Object roleObj = payload.get("role");
            int roleInt = 3;
            if (roleObj instanceof Number) roleInt = ((Number)roleObj).intValue();
            else if (roleObj instanceof String) {
                try { roleInt = Integer.parseInt((String) roleObj);} catch(Exception ex){ roleInt = 3; }
            }

            // Validación: username único
            if (username == null || username.trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "El nombre de usuario es obligatorio.");
                return ResponseEntity.status(400).body(response);
            }
            Optional<User> existsOpt = userRepository.findByUsername(username);
            if (existsOpt.isPresent()) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "El nombre de usuario ya existe.");
                return ResponseEntity.status(409).body(response);
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(roleInt);
            user.setGroup_id(target_group_id);
            User saved = userRepository.save(user);
            UserDTO dto = new UserDTO(saved.getId(), saved.getUsername(), saved.getRole());
            return ResponseEntity.status(201).body(dto);
        } catch (Exception e) {
            Map<String,String> r = new HashMap<>(); r.put("mensaje","No se pudo crear el usuario.");
            return ResponseEntity.status(500).body(r);
        }
    }

    // Buscar usuario por id (solo superadmin)
    @GetMapping("/super/find/{id}/{super_user_id}")
    public ResponseEntity<?> getUserByIdSuper(@PathVariable String id, @PathVariable String super_user_id) {
        User requester = userRepository.findById(super_user_id).orElse(null);
        if (requester == null) { Map<String,String> r = new HashMap<>(); r.put("mensaje","Usuario no encontrado."); return ResponseEntity.status(404).body(r); }
        if (!isSuperAdmin(requester)) { Map<String,String> r = new HashMap<>(); r.put("mensaje","Acceso denegado. Solo superadministradores."); return ResponseEntity.status(403).body(r); }
        User user = userRepository.findById(id).orElse(null);
        if (user == null) { Map<String,String> r = new HashMap<>(); r.put("mensaje","Usuario no existe."); return ResponseEntity.status(404).body(r); }
        UserDTO dto = new UserDTO(user.getId(), user.getUsername(), user.getRole());
        return ResponseEntity.ok(dto);
    }

    // Eliminar cualquier usuario (solo superadmin)
    @DeleteMapping("/super/delete/{id}/{super_user_id}")
    public ResponseEntity<?> deleteUserSuper(@PathVariable String id, @PathVariable String super_user_id) {
        User requester = userRepository.findById(super_user_id).orElse(null);
        if (requester == null) { Map<String,String> r = new HashMap<>(); r.put("mensaje","Usuario no encontrado."); return ResponseEntity.status(404).body(r); }
        if (!isSuperAdmin(requester)) { Map<String,String> r = new HashMap<>(); r.put("mensaje","Acceso denegado. Solo superadministradores."); return ResponseEntity.status(403).body(r); }
        if (!userRepository.existsById(id)) { Map<String,String> r = new HashMap<>(); r.put("mensaje","Usuario no existe."); return ResponseEntity.status(404).body(r); }
        userRepository.deleteById(id);
        Map<String,String> r = new HashMap<>(); r.put("mensaje","Usuario con id " + id + " eliminado por superadmin.");
        return ResponseEntity.ok(r);
    }

    // Actualizar cualquier usuario (solo superadmin). Permite cambiar group_id.
    @PutMapping("/super/update/{id}/{username}/{password}/{role}/{new_group_id}/{super_user_id}")
    public ResponseEntity<?> updateUserSuper(
            @PathVariable String id,
            @PathVariable String username,
            @PathVariable String password,
            @PathVariable int role,
            @PathVariable String new_group_id,
            @PathVariable String super_user_id) {
        User requester = userRepository.findById(super_user_id).orElse(null);
        if (requester == null) { Map<String,String> r = new HashMap<>(); r.put("mensaje","Usuario no encontrado."); return ResponseEntity.status(404).body(r); }
        if (!isSuperAdmin(requester)) { Map<String,String> r = new HashMap<>(); r.put("mensaje","Acceso denegado. Solo superadministradores."); return ResponseEntity.status(403).body(r); }
        User user = userRepository.findById(id).orElse(null);
        if (user == null) { Map<String,String> r = new HashMap<>(); r.put("mensaje","Usuario no existe."); return ResponseEntity.status(404).body(r); }
        try {
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setGroup_id(new_group_id);
            userRepository.save(user);
            Map<String,String> r = new HashMap<>(); r.put("mensaje","Usuario actualizado por superadmin.");
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            Map<String,String> r = new HashMap<>(); r.put("mensaje","No se pudo actualizar el usuario.");
            return ResponseEntity.status(500).body(r);
        }
    }

    // --- fin endpoints superadmin ---
}
