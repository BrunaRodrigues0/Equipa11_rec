package com.upt.lp.gestao_despesas_api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.upt.lp.gestao_despesas_api.model.Despesa;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
    
    // US7: Lista ordenada por data decrescente
    List<Despesa> findByUtilizadorIdOrderByDataDesc(Long utilizadorId);

    // US14: Filtro combinado - usar @Query porque o método derivado fica muito complexo
    @Query("SELECT d FROM Despesa d WHERE d.utilizador.id = :userId " +
           "AND d.data BETWEEN :dataInicio AND :dataFim " +
           "AND d.valor BETWEEN :valorMin AND :valorMax " +
           "AND d.categoria.id IN :categoryIds")
    List<Despesa> filtrarDespesas(
        @Param("userId") Long userId,
        @Param("dataInicio") LocalDate dataInicio, 
        @Param("dataFim") LocalDate dataFim,
        @Param("valorMin") Double valorMin, 
        @Param("valorMax") Double valorMax,
        @Param("categoryIds") List<Long> categoryIds
    );

    // US11: Para verificar se a categoria tem despesas antes de eliminar
    List<Despesa> findByCategoriaId(Long categoriaId);
}