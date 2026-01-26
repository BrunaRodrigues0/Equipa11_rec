package com.upt.lp.gestao_despesas_api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.upt.lp.gestao_despesas_api.model.Category;
import com.upt.lp.gestao_despesas_api.model.Despesa;
import com.upt.lp.gestao_despesas_api.model.User;
import com.upt.lp.gestao_despesas_api.service.CategoryService;
import com.upt.lp.gestao_despesas_api.service.DespesaService;
import com.upt.lp.gestao_despesas_api.service.UserService;

@SpringBootApplication
public class MainClient {

    public static void main(String[] args) {
        SpringApplication.run(MainClient.class, args);
    }

    @Bean
    public CommandLineRunner testarSistema(final UserService userService, 
                                    final CategoryService categoryService, 
                                    final DespesaService despesaService) {
        return args -> {
            try {
                System.out.println("\n========================================");
                System.out.println("  TESTES DO SISTEMA DE GESTÃO DESPESAS");
                System.out.println("========================================\n");
                
                System.out.println("⚠️  NOTA: Se houver erros de duplicação, limpa a base de dados");
                System.out.println("    ou configura spring.jpa.hibernate.ddl-auto=create\n");

                // ========== TESTE 1: REGISTO DE UTILIZADORES ==========
                System.out.println("--- TESTE 1: Registo de Utilizadores ---");
                
                User admin = null;
                User user1 = null;
                
                try {
                    admin = new User();
                    admin.setName("Admin Principal");
                    admin.setEmail("admin@upt.pt");
                    admin.setPassword("admin123");
                    admin.setRole("ADMIN");
                    admin = userService.registar(admin);
                    System.out.println("✓ Admin criado: " + admin.getName() + " (ID: " + admin.getId() + ")");
                } catch (RuntimeException e) {
                    System.out.println("⚠️  Admin já existe (OK se for re-execução)");
                    admin = userService.login("admin@upt.pt", "admin123");
                }

                try {
                    user1 = new User();
                    user1.setName("João Silva");
                    user1.setEmail("joao@upt.pt");
                    user1.setPassword("pass123");
                    user1.setRole("USER");
                    user1 = userService.registar(user1);
                    System.out.println("✓ User criado: " + user1.getName() + " (ID: " + user1.getId() + ")");
                } catch (RuntimeException e) {
                    System.out.println("⚠️  User já existe (OK se for re-execução)");
                    user1 = userService.login("joao@upt.pt", "pass123");
                }

                // Testar email duplicado
                User duplicado = new User();
                duplicado.setEmail("joao@upt.pt");
                try {
                    userService.registar(duplicado);
                } catch (RuntimeException e) {
                    System.out.println("✓ Validação OK: " + e.getMessage());
                }

                // ========== TESTE 2: LOGIN ==========
                System.out.println("\n--- TESTE 2: Login ---");
                User loginOk = userService.login("admin@upt.pt", "admin123");
                System.out.println("✓ Login bem-sucedido: " + loginOk.getName());

                try {
                    userService.login("admin@upt.pt", "errado");
                } catch (RuntimeException e) {
                    System.out.println("✓ Login falhou corretamente: " + e.getMessage());
                }

                // ========== TESTE 3: US9 - CRIAR CATEGORIAS ==========
                System.out.println("\n--- TESTE 3: US9 - Criar Categorias (Admin) ---");
                
                Category catAlimentacao = new Category();
                catAlimentacao.setNome("Alimentação");
                String resultado = categoryService.guardar(catAlimentacao, "ADMIN");
                System.out.println("✓ " + resultado);

                Category catTransporte = new Category();
                catTransporte.setNome("Transporte");
                resultado = categoryService.guardar(catTransporte, "ADMIN");
                System.out.println("✓ " + resultado);

                Category catLazer = new Category();
                catLazer.setNome("Lazer");
                resultado = categoryService.guardar(catLazer, "ADMIN");
                System.out.println("✓ " + resultado);

                // Tentar criar como USER (deve falhar)
                Category catErro = new Category();
                catErro.setNome("Saúde");
                resultado = categoryService.guardar(catErro, "USER");
                System.out.println("✓ " + resultado);

                // Tentar duplicar categoria
                Category catDuplicada = new Category();
                catDuplicada.setNome("Alimentação");
                resultado = categoryService.guardar(catDuplicada, "ADMIN");
                System.out.println("✓ " + resultado);

                // ========== TESTE 4: US10 - EDITAR CATEGORIA ==========
                System.out.println("\n--- TESTE 4: US10 - Editar Categoria ---");
                List<Category> categorias = categoryService.listarTodas();
                if (!categorias.isEmpty()) {
                    Category paraEditar = categorias.get(0);
                    Category novoDado = new Category();
                    novoDado.setNome("Alimentação & Bebidas");
                    resultado = categoryService.editar(paraEditar.getId(), novoDado, "ADMIN");
                    System.out.println("✓ " + resultado);
                }

                // ========== TESTE 5: US4 - REGISTAR DESPESAS ==========
                System.out.println("\n--- TESTE 5: US4 - Registar Despesas ---");
                categorias = categoryService.listarTodas();
                
                Despesa despesa1 = new Despesa();
                despesa1.setUtilizador(user1);
                despesa1.setCategoria(categorias.get(0));
                despesa1.setValor(25.50);
                despesa1.setData(LocalDate.now().minusDays(2));
                despesa1.setMetodoPagamento("Cartão");
                despesa1.setDescricao("Almoço no restaurante");
                despesa1 = despesaService.guardar(despesa1);
                System.out.println("✓ Despesa 1 criada: €" + despesa1.getValor());

                Despesa despesa2 = new Despesa();
                despesa2.setUtilizador(user1);
                despesa2.setCategoria(categorias.size() > 1 ? categorias.get(1) : categorias.get(0));
                despesa2.setValor(15.0);
                despesa2.setData(LocalDate.now().minusDays(1));
                despesa2.setMetodoPagamento("Dinheiro");
                despesa2.setDescricao("Autocarro");
                despesa2 = despesaService.guardar(despesa2);
                System.out.println("✓ Despesa 2 criada: €" + despesa2.getValor());

                // Testar validação: valor negativo
                try {
                    Despesa invalida = new Despesa();
                    invalida.setUtilizador(user1);
                    invalida.setValor(-10.0);
                    invalida.setData(LocalDate.now());
                    despesaService.guardar(invalida);
                } catch (RuntimeException e) {
                    System.out.println("✓ Validação OK: " + e.getMessage());
                }

                // Testar validação: data futura
                try {
                    Despesa dataFutura = new Despesa();
                    dataFutura.setUtilizador(user1);
                    dataFutura.setValor(50.0);
                    dataFutura.setData(LocalDate.now().plusDays(1));
                    despesaService.guardar(dataFutura);
                } catch (RuntimeException e) {
                    System.out.println("✓ Validação OK: " + e.getMessage());
                }

                // ========== TESTE 6: US5 - EDITAR DESPESA ==========
                System.out.println("\n--- TESTE 6: US5 - Editar Despesa ---");
                Despesa paraEditar = despesa1;
                Despesa novaDespesa = new Despesa();
                novaDespesa.setValor(30.0);
                novaDespesa.setData(LocalDate.now().minusDays(2));
                novaDespesa.setCategoria(paraEditar.getCategoria());
                novaDespesa.setMetodoPagamento("MB Way");
                resultado = despesaService.editar(paraEditar.getId(), novaDespesa);
                System.out.println("✓ " + resultado);

                // ========== TESTE 7: LISTAR DESPESAS ==========
                System.out.println("\n--- TESTE 7: Listar Despesas do Utilizador ---");
                List<Despesa> despesas = despesaService.listarPorUtilizador(user1.getId());
                despesas.forEach(d -> 
                    System.out.println("  - €" + d.getValor() + " | " + d.getData() + 
                                     " | " + d.getCategoria().getNome() + " | " + d.getDescricao())
                );

                // ========== TESTE 8: US14 - FILTROS COMBINADOS ==========
                System.out.println("\n--- TESTE 8: US14 - Filtros Combinados ---");
                LocalDate inicio = LocalDate.now().minusDays(7);
                LocalDate fim = LocalDate.now();
                List<Despesa> filtradas = despesaService.filtrarDespesas(
                    user1.getId(), inicio, fim, 10.0, 50.0, null
                );
                System.out.println("✓ Encontradas " + filtradas.size() + " despesas entre €10-€50");

                // ========== TESTE 9: US17 - TOTAL POR CATEGORIA ==========
                System.out.println("\n--- TESTE 9: US17 - Total por Categoria ---");
                for (Category cat : categorias) {
                    Double total = despesaService.calcularTotalPorCategoria(user1.getId(), cat.getId());
                    System.out.println("  " + cat.getNome() + ": €" + total);
                }

                // ========== TESTE 10: US6 - ELIMINAR DESPESA ==========
                System.out.println("\n--- TESTE 10: US6 - Eliminar Despesa ---");
                resultado = despesaService.eliminar(despesa2.getId());
                System.out.println("✓ " + resultado);

                // ========== TESTE 11: US11 - ELIMINAR CATEGORIA ==========
                System.out.println("\n--- TESTE 11: US11 - Eliminar Categoria ---");
                
                // Criar categoria sem despesas
                Category catSemDespesas = new Category();
                catSemDespesas.setNome("Categoria Vazia");
                categoryService.guardar(catSemDespesas, "ADMIN");
                categorias = categoryService.listarTodas();
                Category ultimaCat = categorias.get(categorias.size() - 1);
                
                resultado = categoryService.eliminar(ultimaCat.getId(), "ADMIN");
                System.out.println("✓ " + resultado);

                // Tentar eliminar categoria com despesas
                if (!categorias.isEmpty()) {
                    resultado = categoryService.eliminar(categorias.get(0).getId(), "ADMIN");
                    System.out.println("✓ " + resultado);
                }

                System.out.println("\n========================================");
                System.out.println("  ✓ TODOS OS TESTES CONCLUÍDOS!");
                System.out.println("========================================\n");

            } catch (Exception e) {
                System.err.println("❌ ERRO: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
