package com.upt.lp.gestao_despesas_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upt.lp.gestao_despesas_api.model.Category;
import com.upt.lp.gestao_despesas_api.repository.CategoryRepository;
import com.upt.lp.gestao_despesas_api.repository.DespesaRepository;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DespesaRepository despesaRepository;

    // US9: Criar categoria (Apenas Administrador)
    public String guardar(Category category, String userRole) {
        // Validação de perfil
        if (!"ADMIN".equals(userRole)) {
            return "Erro: Apenas o administrador pode criar categorias.";
        }

        // US9: O nome da categoria deve ser único no sistema
        if (categoryRepository.findByNome(category.getNome()).isPresent()) {
            return "Erro: Já existe uma categoria com este nome.";
        }

        categoryRepository.save(category);
        return "Categoria criada com sucesso!"; 
    }

    // US10: Editar categoria (Apenas Administrador)
    public String editar(Long id, Category novosDados, String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return "Erro: Apenas o administrador pode editar categorias.";
        }

        Category existente = categoryRepository.findById(id).orElse(null);
        if (existente == null) return "Erro: Categoria não encontrada.";

        // Verificar se o novo nome já existe noutra categoria
        Optional<Category> duplicada = categoryRepository.findByNome(novosDados.getNome());
        if (duplicada.isPresent() && !duplicada.get().getId().equals(id)) {
            return "Erro: Já existe outra categoria com este nome.";
        }

        existente.setNome(novosDados.getNome());
        categoryRepository.save(existente);
        return "Categoria editada com sucesso!"; 
    }

    public List<Category> listarTodas() {
        return categoryRepository.findAll();
    }

    // US11: Eliminar categoria
    public String eliminar(Long id, String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return "Erro: Apenas o administrador pode eliminar categorias.";
        }

        // US11: O sistema impede a eliminação se existirem despesas associadas
        if (!despesaRepository.findByCategoriaId(id).isEmpty()) {
            return "Erro: Não é possível eliminar. Existem despesas associadas a esta categoria.";
        }

        categoryRepository.deleteById(id);
        return "Categoria eliminada com sucesso!";
    }
}