package com.upt.lp.gestao_despesas_api.repository;

import com.upt.lp.gestao_despesas_api.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserId(Long userId); 
}