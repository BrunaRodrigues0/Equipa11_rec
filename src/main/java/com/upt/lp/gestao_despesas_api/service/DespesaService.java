package com.upt.lp.gestao_despesas_api.service;

import com.upt.lp.gestao_despesas_api.model.Despesa;
import com.upt.lp.gestao_despesas_api.repository.DespesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DespesaService {
    @Autowired
    private DespesaRepository despesaRepository;

    public Despesa guardar(Despesa despesa) {
        if (despesa.getValor() <= 0) {
            throw new RuntimeException("O valor da despesa deve ser maior que zero!");
        }
        return despesaRepository.save(despesa);
    }

    public List<Despesa> listarPorUtilizador(Long userId) {
        return despesaRepository.findByUtilizadorId(userId);
    }

    public void eliminar(Long id) {
        despesaRepository.deleteById(id);
    }
}