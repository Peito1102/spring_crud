package com.vasquez.curso.springboot.app.springboot_crud.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.vasquez.curso.springboot.app.springboot_crud.ProductValidation;
import com.vasquez.curso.springboot.app.springboot_crud.entities.Product;
import com.vasquez.curso.springboot.app.springboot_crud.services.ProductService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService service;

    //@Autowired
    //private ProductValidation validation;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public List<Product> list() {
        return service.findAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Product> view(@PathVariable Long id) {
        Optional<Product> producOptional = service.findById(id);
        if (producOptional.isPresent()) {
            return ResponseEntity.ok(producOptional.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody Product product, BindingResult result) {
        //validation.validate(product, result);
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@Valid @RequestBody Product product, BindingResult result, @PathVariable Long id) {
        //validation.validate(product, result);
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        Optional<Product> productOptional = service.update(id, product);
        if (productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(productOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> delete(@PathVariable Long id) {
        Optional<Product> producOptional = service.delete(id);
        if (producOptional.isPresent()) {
            return ResponseEntity.ok(producOptional.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(e -> {
            errors.put(e.getField(), "El campo " + e.getField() + " " + e.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
