package com.project.demo.rest.product;

import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {


    @Autowired
    private ProductRepository productRepository;

    // Listar productos (solo usuarios autenticados)
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Crear producto (solo SUPER_ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    // Actualizar producto (solo SUPER_ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product incoming
    ) {
        return productRepository.findById(id)
                .map(existing -> {
                    // Actualizamos sólo si vienen valores distintos de null
                    if (incoming.getName() != null) {
                        existing.setName(incoming.getName());
                    }
                    if (incoming.getDescription() != null) {
                        existing.setDescription(incoming.getDescription());
                    }
                    // Como price es primitivo, lo actualizamos siempre que quieras permitir precio 0:
                    existing.setPrice(incoming.getPrice());
                    // Lo mismo para stockQuantity:
                    existing.setStockQuantity(incoming.getStockQuantity());
                    if (incoming.getCategory() != null) {
                        existing.setCategory(incoming.getCategory());
                    }
                    Product updated = productRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Borrar producto (solo SUPER_ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Producto con ID " + id + " no encontrado.");
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok("Producto con ID " + id + " eliminado exitosamente.");

    }
}