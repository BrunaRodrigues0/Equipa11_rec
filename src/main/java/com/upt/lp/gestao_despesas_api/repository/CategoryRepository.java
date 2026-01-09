package com.upt.lp.gestao_despesas_api.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upt.lp.gestao_despesas_api.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserId(Long userId); 
    // Para validar se já existe uma categoria com este nome no sistema (US9)
    Optional<Category> findByNome(String nome);
}