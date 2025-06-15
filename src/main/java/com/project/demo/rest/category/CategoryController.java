package com.project.demo.rest.category;


import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // Listar categorías (solo usuarios autenticados)
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Crear categoría (solo SUPER_ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Category createCategory(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @RequestBody Category incoming
    ) {
        return categoryRepository.findById(id)
                .map(existing -> {
                    // Sólo actualizamos description (o name si viene)
                    if (incoming.getName() != null) {
                        existing.setName(incoming.getName());
                    }
                    if (incoming.getDescription() != null) {
                        existing.setDescription(incoming.getDescription());
                    }
                    Category updated = categoryRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Borrar categoría (solo SUPER_ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría con ID " + id + " no encontrada.");
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok("Categoría con ID " + id + " eliminada exitosamente.");
    }
}