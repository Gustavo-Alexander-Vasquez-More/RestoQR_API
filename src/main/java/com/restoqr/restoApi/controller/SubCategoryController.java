package com.restoqr.restoApi.controller;

import com.restoqr.restoApi.models.SubCategory;
import com.restoqr.restoApi.repositories.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subcategories")
public class SubCategoryController {
    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createSubCategory(@RequestBody SubCategory subCategory) {
        try {
            SubCategory saved = subCategoryRepository.save(subCategory);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo crear la subcategoría.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSubCategories() {
        try {
            List<SubCategory> subcategories = subCategoryRepository.findAll();
            return ResponseEntity.ok(subcategories);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudieron obtener las subcategorías.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSubCategory(@PathVariable String id, @RequestBody SubCategory subCategory) {
        try {
            SubCategory existing = subCategoryRepository.findById(id).orElse(null);
            if (existing == null) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "La subcategoría no existe.");
                return ResponseEntity.status(404).body(response);
            }
            existing.setNombre(subCategory.getNombre());
            existing.setPhotoUrl(subCategory.getPhotoUrl());
            subCategoryRepository.save(existing);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Subcategoría actualizada.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo actualizar la subcategoría.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSubCategory(@PathVariable String id) {
        try {
            if (!subCategoryRepository.existsById(id)) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "La subcategoría no existe.");
                return ResponseEntity.status(404).body(response);
            }
            subCategoryRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Subcategoría con id " + id + " eliminada.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo eliminar la subcategoría.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
