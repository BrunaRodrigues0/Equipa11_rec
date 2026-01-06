package com.upt.lp.gestao_despesas_api.repository;

import com.upt.lp.gestao_despesas_api.model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
    List<Despesa> findByUtilizadorId(Long utilizadorId); 
}