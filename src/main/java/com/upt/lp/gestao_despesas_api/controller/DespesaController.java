package com.upt.lp.gestao_despesas_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upt.lp.gestao_despesas_api.model.Despesa;
import com.upt.lp.gestao_despesas_api.service.DespesaService;

@RestController
@RequestMapping("/api/despesas")
public class DespesaController {
    @Autowired
    private DespesaService despesaService;

    @PostMapping
    public Despesa guardar(@RequestBody Despesa despesa) {
        return despesaService.guardar(despesa);
    }

    @GetMapping("/user/{userId}")
    public List<Despesa> listar(@PathVariable Long userId) {
        return despesaService.listarPorUtilizador(userId);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        despesaService.eliminar(id); 
    }
}