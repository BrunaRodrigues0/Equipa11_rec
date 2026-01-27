package com.upt.lp.gestao_despesas_api.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upt.lp.gestao_despesas_api.model.Despesa;
import com.upt.lp.gestao_despesas_api.repository.DespesaRepository;

@Service
public class DespesaService {

    @Autowired
    private DespesaRepository despesaRepository;

    // US4: Registar despesa
    public Despesa guardar(Despesa despesa) {
    if (despesa == null) {
        throw new RuntimeException("Dados inválidos.");
    }
    if (despesa.getValor() == null || despesa.getValor() <= 0) {
        throw new RuntimeException("O valor deve ser positivo.");
    }
    if (despesa.getData() == null || despesa.getData().isAfter(LocalDate.now())) {
        throw new RuntimeException("A data não pode ser futura.");
    }
    return despesaRepository.save(despesa);
}

    public List<Despesa> listarPorUtilizador(Long userId) {
        return despesaRepository.findByUtilizadorIdOrderByDataDesc(userId);
    }

    // US14: Filtros combinados 
    public List<Despesa> filtrarDespesas(Long userId, LocalDate inicio, LocalDate fim, 
                                         Double min, Double max, List<Long> categorias) {
        return despesaRepository.filtrarDespesas(userId, inicio, fim, min, max, categorias);
    }

    // US6: Eliminar despesa
    public String eliminar(Long id) {
        if (!despesaRepository.existsById(id)) {
            return "Erro: Despesa não encontrada.";
        }
        despesaRepository.deleteById(id);
        return "Despesa eliminada com sucesso!";
    }

    // US17: Total por categoria
    public Double calcularTotalPorCategoria(Long userId, Long categoryId) {
        List<Despesa> despesas = despesaRepository.findByUtilizadorIdOrderByDataDesc(userId);
        return despesas.stream()
                .filter(d -> d.getCategoria() != null)
                .filter(d -> d.getCategoria().getId() != null)
                .filter(d -> d.getCategoria().getId().equals(categoryId))
                .mapToDouble(Despesa::getValor)
                .sum();
    }

    // US5: Editar despesa
    public String editar(Long id, Despesa novaDespesa) {
        Despesa existente = despesaRepository.findById(id).orElse(null);
        if (existente == null) return "Erro: Despesa não encontrada.";

        if (novaDespesa.getValor() == null || novaDespesa.getValor() <= 0) {
            return "Erro: O valor deve ser positivo.";
        }
        if (novaDespesa.getData() == null || novaDespesa.getData().isAfter(LocalDate.now())) {
            return "Erro: A data não pode ser futura.";
        }

        existente.setValor(novaDespesa.getValor()); 
        existente.setData(novaDespesa.getData()); 
        existente.setCategoria(novaDespesa.getCategoria());
        existente.setMetodoPagamento(novaDespesa.getMetodoPagamento()); 

        despesaRepository.save(existente);
        return "Despesa editada com sucesso!"; 
    }
    // US8:
    public Despesa buscarPorId(Long id) {
        return despesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));
    }
    public List<Despesa> listarTodas() {
    return despesaRepository.findAll();
}
   
}