package com.upt.lp.gestao_despesas_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upt.lp.gestao_despesas_api.model.Category;
import com.upt.lp.gestao_despesas_api.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // US9: Criar categoria 
    @PostMapping
    public String criar(@RequestBody Category category, @RequestParam String role) {
        return categoryService.guardar(category, role);
    }

    // US10: Editar categoria
    @PutMapping("/{id}")
    public String editar(@PathVariable Long id, @RequestBody Category category, @RequestParam String role) {
        return categoryService.editar(id, category, role);
    }

    // US12: Listar todas as categorias
    @GetMapping
    public List<Category> listarTodas() {
        return categoryService.listarTodas();
    }

    // US11: Eliminar categoria
    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id, @RequestParam String role) {
        return categoryService.eliminar(id, role);
    }
}
