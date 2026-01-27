package com.upt.lp.gestao_despesas_api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.upt.lp.gestao_despesas_api.model.Category;
import com.upt.lp.gestao_despesas_api.model.Despesa;
import com.upt.lp.gestao_despesas_api.model.User;
import com.upt.lp.gestao_despesas_api.service.CategoryService;
import com.upt.lp.gestao_despesas_api.service.DespesaService;
import com.upt.lp.gestao_despesas_api.service.UserService;

@Component
public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static User utilizadorLogado = null;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Bean
    public CommandLineRunner menuInterativo(final UserService userService,
                                           final CategoryService categoryService,
                                           final DespesaService despesaService) {
        return args -> {
            boolean sair = false;

            System.out.println("\n=== SISTEMA DE GESTAO DE DESPESAS ===");
            System.out.println("Equipa 11 - UPT - 2025");
            System.out.println("User Stories US1-US17");

            while (!sair) {
                limparConsola();
                if (utilizadorLogado == null) {
                    sair = menuSemLogin(userService);
                } else {
                    sair = menuComLogin(userService, categoryService, despesaService);
                }
            }

            System.out.println("\nSessao encerrada. Ate breve!");
            scanner.close();
        };
    }

    // ==================== MENUS PRINCIPAIS ====================

    private static boolean menuSemLogin(UserService userService) {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Login");
        System.out.println("2. Criar conta");
        System.out.println("0. Sair");
        System.out.print("\nEscolha: ");

        int opcao = lerInteiro();

        switch (opcao) {
            case 1: autenticarUtilizador(userService); break;
            case 2: criarConta(userService); break;
            case 0: return true;
            default: 
                System.out.println("Opcao invalida!");
                pausar();
        }
        return false;
    }

    private static boolean menuComLogin(UserService userService, CategoryService categoryService, 
                                       DespesaService despesaService) {
        System.out.println("\n--- SESSAO ATIVA ---");
        System.out.println("Utilizador: " + utilizadorLogado.getName());
        System.out.println("Email: " + utilizadorLogado.getEmail());
        System.out.println("Perfil: " + utilizadorLogado.getRole());
        System.out.println("--------------------");

        if ("ADMIN".equals(utilizadorLogado.getRole())) {
            return menuAdmin(categoryService, despesaService);
        } else {
            return menuUser(despesaService, categoryService);
        }
    }

    private static boolean menuAdmin(CategoryService categoryService, DespesaService despesaService) {
        System.out.println("1. Gerir despesas");
        System.out.println("2. Gerir categorias");
        System.out.println("3. Filtros e analise");
        System.out.println("4. Logout");
        System.out.println("0. Sair");
        System.out.print("\nEscolha: ");

        int opcao = lerInteiro();
        switch (opcao) {
            case 1: menuDespesas(despesaService, categoryService); break;
            case 2: menuCategorias(categoryService, despesaService); break;
            case 3: menuFiltrosAnalise(despesaService, categoryService); break;
            case 4: terminarSessao(); break;
            case 0: return true;
            default: System.out.println("Opcao invalida!"); pausar();
        }
        return false;
    }

    private static boolean menuUser(DespesaService despesaService, CategoryService categoryService) {
        System.out.println("1. Gerir despesas");
        System.out.println("2. Filtros e analise");
        System.out.println("3. Ver categorias");
        System.out.println("4. Logout");
        System.out.println("0. Sair");
        System.out.print("\nEscolha: ");

        int opcao = lerInteiro();
        switch (opcao) {
            case 1: menuDespesas(despesaService, categoryService); break;
            case 2: menuFiltrosAnalise(despesaService, categoryService); break;
            case 3: verTodasCategorias(categoryService); break;
            case 4: terminarSessao(); break;
            case 0: return true;
            default: System.out.println("Opcao invalida!"); pausar();
        }
        return false;
    }

    // ==================== CRIAR CONTA ====================

    private static void criarConta(UserService userService) {
        limparConsola();
        System.out.println("--- CRIAR CONTA ---");
        
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) { System.out.println("Erro: Nome obrigatorio!"); pausar(); return; }

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty() || !email.contains("@")) { System.out.println("Erro: Email invalido!"); pausar(); return; }

        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (password.length() < 3) { System.out.println("Erro: Password muito curta!"); pausar(); return; }

        System.out.print("Perfil (USER/ADMIN): ");
        String role = scanner.nextLine().toUpperCase().trim();
        if (!role.equals("USER") && !role.equals("ADMIN")) role = "USER";

        User user = new User();
        user.setName(nome);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        try {
            userService.registar(user);
            System.out.println("Registo efetuado com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }

    // ==================== AUTENTICAR ====================

    private static void autenticarUtilizador(UserService userService) {
        limparConsola();
        System.out.println("--- LOGIN ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            utilizadorLogado = userService.login(email, password);
            System.out.println("Sucesso: Login efetuado.");
        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }

    // ==================== TERMINAR SESSÃO ====================

    private static void terminarSessao() {
        System.out.println("A encerrar sessao...");
        utilizadorLogado = null;
        System.out.println("Logout concluido.");
        pausar();
    }

    // ==================== GESTÃO DE CATEGORIAS ====================

    private static void menuCategorias(CategoryService categoryService, DespesaService despesaService) {
        limparConsola();
        System.out.println("--- GESTAO DE CATEGORIAS ---");
        System.out.println("1. Criar\n2. Editar\n3. Eliminar\n4. Listar\n0. Voltar");
        System.out.print("\nEscolha: ");

        int opcao = lerInteiro();
        switch (opcao) {
            case 1: criarCategoria(categoryService); break;
            case 2: editarCategoria(categoryService); break;
            case 3: eliminarCategoria(categoryService, despesaService); break;
            case 4: verTodasCategorias(categoryService); break;
        }
    }

    private static void criarCategoria(CategoryService categoryService) {
        System.out.print("Nome da categoria: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) return;

        Category cat = new Category();
        cat.setNome(nome);
        System.out.println(categoryService.guardar(cat, utilizadorLogado.getRole()));
        pausar();
    }

    private static void editarCategoria(CategoryService categoryService) {
        limparConsola();
        System.out.println("--- EDITAR CATEGORIA ---");
        
        // Listar todas as categorias com ID e nome
        List<Category> categorias = categoryService.listarTodas();
        if (categorias.isEmpty()) {
            System.out.println("Sem categorias registadas.");
            pausar();
            return;
        }
        
        System.out.println("\nCategorias disponiveis:");
        System.out.printf("%-5s %-30s%n", "ID", "NOME");
        System.out.println("----------------------------------");
        categorias.forEach(c -> System.out.printf("%-5d %-30s%n", c.getId(), c.getNome()));
        
        System.out.print("\nID da categoria a editar: ");
        Long id = lerLong();
        
        if (id == -1L) {
            System.out.println("ID invalido!");
            pausar();
            return;
        }
        
        System.out.print("Novo nome: ");
        String nome = scanner.nextLine().trim();
        
        if (nome.isEmpty()) {
            System.out.println("Nome nao pode ser vazio!");
            pausar();
            return;
        }

        Category dados = new Category();
        dados.setNome(nome);
        System.out.println(categoryService.editar(id, dados, utilizadorLogado.getRole()));
        pausar();
    }

    private static void eliminarCategoria(CategoryService categoryService, DespesaService despesaService) {
        limparConsola();
        System.out.println("--- ELIMINAR CATEGORIA ---");
        
        // Listar todas as categorias
        List<Category> categorias = categoryService.listarTodas();
        if (categorias.isEmpty()) {
            System.out.println("Sem categorias registadas.");
            pausar();
            return;
        }
        
        System.out.println("\nCategorias disponiveis:");
        System.out.printf("%-5s %-30s%n", "ID", "NOME");
        System.out.println("----------------------------------");
        categorias.forEach(c -> System.out.printf("%-5d %-30s%n", c.getId(), c.getNome()));
        
        System.out.print("\nID da categoria a eliminar: ");
        Long id = lerLong();
        
        if (id == -1L) {
            System.out.println("ID invalido!");
            pausar();
            return;
        }
        
        // Verificar se existem despesas associadas a esta categoria
        List<Despesa> despesasCategoria = despesaService.listarPorUtilizador(utilizadorLogado.getId()).stream()
            .filter(d -> d.getCategoria() != null && d.getCategoria().getId().equals(id))
            .toList();
        
        if (!despesasCategoria.isEmpty()) {
            System.out.println("\nAVISO: Existem " + despesasCategoria.size() + 
                             " despesa(s) associada(s) a esta categoria.");
            System.out.println("Ao eliminar a categoria, estas despesas ficarao sem categoria.");
        }
        
        System.out.print("\nTem certeza que deseja eliminar? (S/N): ");
        String confirmacao = scanner.nextLine().trim();
        
        if (confirmacao.equalsIgnoreCase("S")) {
            try {
                System.out.println(categoryService.eliminar(id, utilizadorLogado.getRole()));
            } catch (Exception e) {
                System.out.println("Erro ao eliminar categoria: " + e.getMessage());
            }
        } else {
            System.out.println("Operacao cancelada.");
        }
        pausar();
    }

    private static void verTodasCategorias(CategoryService categoryService) {
        limparConsola();
        System.out.println("--- LISTAR CATEGORIAS ---");
        
        List<Category> lista = categoryService.listarTodas();
        if (lista.isEmpty()) {
            System.out.println("\nSem categorias registadas.");
        } else {
            System.out.println();
            System.out.printf("%-5s %-30s%n", "ID", "NOME");
            System.out.println("----------------------------------");
            lista.forEach(c -> System.out.printf("%-5d %-30s%n", c.getId(), c.getNome()));
        }
        pausar();
    }

    // ==================== GESTÃO DE DESPESAS ====================

    private static void menuDespesas(DespesaService despesaService, CategoryService categoryService) {
        limparConsola();
        System.out.println("--- GESTAO DE DESPESAS ---");
        System.out.println("1. Registar\n2. Editar\n3. Eliminar\n4. Listar\n5. Detalhes\n0. Voltar");
        System.out.print("\nEscolha: ");

        int opcao = lerInteiro();
        switch (opcao) {
            case 1: registarDespesa(despesaService, categoryService); break;
            case 2: editarDespesa(despesaService, categoryService); break;
            case 3: eliminarDespesa(despesaService); break;
            case 4: verListaDespesas(despesaService); break;
            case 5: verDetalhesDespesa(despesaService); break;
        }
    }

    private static void registarDespesa(DespesaService despesaService, CategoryService categoryService) {
        limparConsola();
        System.out.println("--- REGISTAR DESPESA ---");
        
        List<Category> cats = categoryService.listarTodas();
        if (cats.isEmpty()) { 
            System.out.println("\nSem categorias disponiveis. Crie uma categoria primeiro."); 
            pausar(); 
            return; 
        }

        System.out.println("\nCategorias disponiveis:");
        for (int i = 0; i < cats.size(); i++) {
            System.out.println((i + 1) + ". " + cats.get(i).getNome());
        }
        
        System.out.print("\nEscolha a categoria (numero): ");
        int idx = lerInteiro() - 1;
        
        if (idx < 0 || idx >= cats.size()) {
            System.out.println("Categoria invalida!");
            pausar();
            return;
        }

        System.out.print("Valor (EUR): ");
        double valor = lerDouble();
        
        if (valor <= 0) {
            System.out.println("Valor deve ser positivo!");
            pausar();
            return;
        }
        
        System.out.print("Data (dd/MM/yyyy) ou vazio para hoje: ");
        String dataStr = scanner.nextLine().trim();
        LocalDate data;
        
        try {
            data = dataStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dataStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data invalido!");
            pausar();
            return;
        }

        System.out.print("Metodo Pagamento: ");
        String metodo = scanner.nextLine().trim();

        Despesa d = new Despesa();
        d.setUtilizador(utilizadorLogado);
        d.setCategoria(cats.get(idx));
        d.setValor(valor);
        d.setData(data);
        d.setMetodoPagamento(metodo);

        try {
            despesaService.guardar(d);
            System.out.println("\nDespesa registada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao registar despesa: " + e.getMessage());
        }
        pausar();
    }

    private static void editarDespesa(DespesaService despesaService, CategoryService categoryService) {
        limparConsola();
        System.out.println("--- EDITAR DESPESA ---");
        
        // Mostrar lista de despesas do utilizador
        List<Despesa> despesas = despesaService.listarPorUtilizador(utilizadorLogado.getId());
        if (despesas.isEmpty()) {
            System.out.println("\nSem despesas registadas.");
            pausar();
            return;
        }
        
        System.out.println("\nSuas despesas:");
        System.out.printf("%-5s %-12s %-10s %-15s %-15s%n", "ID", "DATA", "VALOR", "CATEGORIA", "PAGAMENTO");
        System.out.println("----------------------------------------------------------------");
        despesas.forEach(d -> System.out.printf("%-5d %-12s %-10.2f %-15s %-15s%n", 
            d.getId(), 
            d.getData().format(DATE_FORMATTER), 
            d.getValor(), 
            d.getCategoria() != null ? d.getCategoria().getNome() : "N/A",
            d.getMetodoPagamento() != null ? d.getMetodoPagamento() : "N/A"));
        
        System.out.print("\nID da despesa a editar: ");
        Long id = lerLong();
        
        if (id == -1L) {
            System.out.println("ID invalido!");
            pausar();
            return;
        }
        
        // Buscar a despesa
        Despesa despesaExistente;
        try {
            despesaExistente = despesaService.buscarPorId(id);
        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());
            pausar();
            return;
        }
        
        // Verificar se a despesa pertence ao utilizador
        if (!despesaExistente.getUtilizador().getId().equals(utilizadorLogado.getId())) {
            System.out.println("Erro: Nao tem permissao para editar esta despesa!");
            pausar();
            return;
        }
        
        System.out.println("\n--- DADOS ATUAIS ---");
        System.out.println("Valor: " + despesaExistente.getValor() + " EUR");
        System.out.println("Data: " + despesaExistente.getData().format(DATE_FORMATTER));
        System.out.println("Categoria: " + (despesaExistente.getCategoria() != null ? despesaExistente.getCategoria().getNome() : "N/A"));
        System.out.println("Metodo Pagamento: " + (despesaExistente.getMetodoPagamento() != null ? despesaExistente.getMetodoPagamento() : "N/A"));
        
        System.out.println("\n--- NOVOS DADOS (deixe vazio para manter) ---");
        
        // Novo valor
        System.out.print("Novo valor (EUR): ");
        String valorStr = scanner.nextLine().trim();
        Double novoValor = null;
        if (!valorStr.isEmpty()) {
            try {
                novoValor = Double.parseDouble(valorStr);
                if (novoValor <= 0) {
                    System.out.println("Valor deve ser positivo!");
                    pausar();
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido!");
                pausar();
                return;
            }
        }
        
        // Nova data
        System.out.print("Nova data (dd/MM/yyyy): ");
        String dataStr = scanner.nextLine().trim();
        LocalDate novaData = null;
        if (!dataStr.isEmpty()) {
            try {
                novaData = LocalDate.parse(dataStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data invalido!");
                pausar();
                return;
            }
        }
        
        // Nova categoria
        List<Category> cats = categoryService.listarTodas();
        Category novaCategoria = null;
        
        if (!cats.isEmpty()) {
            System.out.println("\nCategorias disponiveis:");
            for (int i = 0; i < cats.size(); i++) {
                System.out.println((i + 1) + ". " + cats.get(i).getNome());
            }
            System.out.print("Nova categoria (numero) ou vazio para manter: ");
            String catStr = scanner.nextLine().trim();
            
            if (!catStr.isEmpty()) {
                try {
                    int idx = Integer.parseInt(catStr) - 1;
                    if (idx >= 0 && idx < cats.size()) {
                        novaCategoria = cats.get(idx);
                    }
                } catch (NumberFormatException e) {
                    // Mantém categoria atual
                }
            }
        }
        
        // Novo método de pagamento
        System.out.print("\nNovo metodo de pagamento: ");
        String novoMetodo = scanner.nextLine().trim();
        
        // Criar objeto com novos dados
        Despesa dadosNovos = new Despesa();
        dadosNovos.setValor(novoValor != null ? novoValor : despesaExistente.getValor());
        dadosNovos.setData(novaData != null ? novaData : despesaExistente.getData());
        dadosNovos.setCategoria(novaCategoria != null ? novaCategoria : despesaExistente.getCategoria());
        dadosNovos.setMetodoPagamento(!novoMetodo.isEmpty() ? novoMetodo : despesaExistente.getMetodoPagamento());
        
        // Editar
        try {
            String resultado = despesaService.editar(id, dadosNovos);
            System.out.println("\n" + resultado);
        } catch (Exception e) {
            System.out.println("Erro ao editar: " + e.getMessage());
        }
        
        pausar();
    }

    private static void eliminarDespesa(DespesaService despesaService) {
        limparConsola();
        System.out.println("--- ELIMINAR DESPESA ---");
        
        // Mostrar lista de despesas do utilizador
        List<Despesa> despesas = despesaService.listarPorUtilizador(utilizadorLogado.getId());
        if (despesas.isEmpty()) {
            System.out.println("\nSem despesas registadas.");
            pausar();
            return;
        }
        
        System.out.println("\nSuas despesas:");
        System.out.printf("%-5s %-12s %-10s %-15s%n", "ID", "DATA", "VALOR", "CATEGORIA");
        System.out.println("--------------------------------------------------");
        despesas.forEach(d -> System.out.printf("%-5d %-12s %-10.2f %-15s%n", 
            d.getId(), d.getData().format(DATE_FORMATTER), d.getValor(), 
            d.getCategoria() != null ? d.getCategoria().getNome() : "N/A"));
        
        System.out.print("\nID da despesa a eliminar: ");
        Long id = lerLong();
        
        if (id == -1L) {
            System.out.println("ID invalido!");
            pausar();
            return;
        }
        
        System.out.print("Tem certeza que deseja eliminar? (S/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("S")) {
            try {
                System.out.println(despesaService.eliminar(id));
            } catch (Exception e) {
                System.out.println("Erro ao eliminar: " + e.getMessage());
            }
        } else {
            System.out.println("Operacao cancelada.");
        }
        pausar();
    }

    private static void verListaDespesas(DespesaService despesaService) {
        limparConsola();
        System.out.println("--- LISTAR DESPESAS ---");
        
        List<Despesa> lista = despesaService.listarPorUtilizador(utilizadorLogado.getId());
        if (lista.isEmpty()) {
            System.out.println("\nSem despesas registadas.");
        } else {
            System.out.println();
            System.out.printf("%-5s %-12s %-10s %-15s %-15s%n", "ID", "DATA", "VALOR", "CATEGORIA", "PAGAMENTO");
            System.out.println("----------------------------------------------------------------");
            lista.forEach(d -> System.out.printf("%-5d %-12s %-10.2f %-15s %-15s%n", 
                d.getId(), 
                d.getData().format(DATE_FORMATTER), 
                d.getValor(), 
                d.getCategoria() != null ? d.getCategoria().getNome() : "N/A",
                d.getMetodoPagamento() != null ? d.getMetodoPagamento() : "N/A"));
        }
        pausar();
    }

    private static void verDetalhesDespesa(DespesaService despesaService) {
        limparConsola();
        System.out.println("--- DETALHES DA DESPESA ---");
        
        // Mostrar lista de despesas
        List<Despesa> despesas = despesaService.listarPorUtilizador(utilizadorLogado.getId());
        if (despesas.isEmpty()) {
            System.out.println("\nSem despesas registadas.");
            pausar();
            return;
        }
        
        System.out.println("\nSuas despesas:");
        System.out.printf("%-5s %-12s %-10s %-15s%n", "ID", "DATA", "VALOR", "CATEGORIA");
        System.out.println("--------------------------------------------------");
        despesas.forEach(d -> System.out.printf("%-5d %-12s %-10.2f %-15s%n", 
            d.getId(), 
            d.getData().format(DATE_FORMATTER), 
            d.getValor(), 
            d.getCategoria() != null ? d.getCategoria().getNome() : "N/A"));
        
        System.out.print("\nID da despesa para ver detalhes: ");
        Long id = lerLong();
        
        if (id == -1L) {
            System.out.println("ID invalido!");
            pausar();
            return;
        }
        
        // Buscar a despesa
        Despesa despesa;
        try {
            despesa = despesaService.buscarPorId(id);
        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());
            pausar();
            return;
        }
        
        // Verificar se a despesa pertence ao utilizador (ou se é admin)
        if (!despesa.getUtilizador().getId().equals(utilizadorLogado.getId()) && 
            !"ADMIN".equals(utilizadorLogado.getRole())) {
            System.out.println("Erro: Nao tem permissao para ver esta despesa!");
            pausar();
            return;
        }
        
        // Mostrar detalhes completos
        limparConsola();
        System.out.println("=== DETALHES COMPLETOS DA DESPESA ===");
        System.out.println();
        System.out.println("ID: " + despesa.getId());
        System.out.println("Valor: " + String.format("%.2f EUR", despesa.getValor()));
        System.out.println("Data: " + despesa.getData().format(DATE_FORMATTER));
        System.out.println("Categoria: " + (despesa.getCategoria() != null ? despesa.getCategoria().getNome() : "N/A"));
        System.out.println("Metodo Pagamento: " + (despesa.getMetodoPagamento() != null ? despesa.getMetodoPagamento() : "N/A"));
        System.out.println("Descricao: " + (despesa.getDescricao() != null ? despesa.getDescricao() : "N/A"));
        System.out.println("Utilizador: " + despesa.getUtilizador().getName() + " (" + despesa.getUtilizador().getEmail() + ")");
        System.out.println();
        System.out.println("=====================================");
        
        pausar();
    }

    // ==================== FILTROS E ANÁLISE ====================

    private static void menuFiltrosAnalise(DespesaService despesaService, CategoryService categoryService) {
        limparConsola();
        System.out.println("--- FILTROS E ANALISE ---");
        System.out.println("1. Filtrar despesas");
        System.out.println("2. Total por Categoria");
        System.out.println("0. Voltar");
        System.out.print("\nEscolha: ");

        int opcao = lerInteiro();
        if (opcao == 1) filtrarDespesas(despesaService, categoryService);
        else if (opcao == 2) verTotalPorCategoria(despesaService, categoryService);
    }

    private static void filtrarDespesas(DespesaService despesaService, CategoryService categoryService) {
        limparConsola();
        System.out.println("--- FILTRAR DESPESAS ---");
        System.out.println("\nInsira os criterios de filtro (deixe vazio para ignorar):\n");
        
        // Filtro por categoria
        List<Category> categorias = categoryService.listarTodas();
        Long categoriaId = null;
        
        if (!categorias.isEmpty()) {
            System.out.println("Categorias disponiveis:");
            for (int i = 0; i < categorias.size(); i++) {
                System.out.println((i + 1) + ". " + categorias.get(i).getNome());
            }
            System.out.print("\nEscolha a categoria (numero) ou deixe vazio: ");
            String catInput = scanner.nextLine().trim();
            
            if (!catInput.isEmpty()) {
                try {
                    int idx = Integer.parseInt(catInput) - 1;
                    if (idx >= 0 && idx < categorias.size()) {
                        categoriaId = categorias.get(idx).getId();
                    }
                } catch (NumberFormatException e) {
                    // Ignora entrada inválida
                }
            }
        }
        
        // Filtro por data inicial
        LocalDate dataInicio = null;
        System.out.print("\nData inicial (dd/MM/yyyy) ou vazio: ");
        String dataInicioStr = scanner.nextLine().trim();
        
        if (!dataInicioStr.isEmpty()) {
            try {
                dataInicio = LocalDate.parse(dataInicioStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data invalido! Ignorando filtro de data inicial.");
            }
        }
        
        // Filtro por valor máximo
        Double valorMaximo = null;
        System.out.print("\nValor maximo (EUR) ou vazio: ");
        String valorMaxStr = scanner.nextLine().trim();
        
        if (!valorMaxStr.isEmpty()) {
            try {
                valorMaximo = Double.parseDouble(valorMaxStr);
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido! Ignorando filtro de valor maximo.");
            }
        }
        
        // Filtro por método de pagamento
        System.out.print("\nMetodo de pagamento ou vazio: ");
        String metodoPagamento = scanner.nextLine().trim();
        if (metodoPagamento.isEmpty()) metodoPagamento = null;
        
        // Aplicar filtros
        List<Despesa> todasDespesas = despesaService.listarPorUtilizador(utilizadorLogado.getId());
        List<Despesa> despesasFiltradas = new ArrayList<>();
        
        for (Despesa d : todasDespesas) {
            boolean passa = true;
            
            // Filtrar por categoria
            if (categoriaId != null && (d.getCategoria() == null || 
                !d.getCategoria().getId().equals(categoriaId))) {
                passa = false;
            }
            
            // Filtrar por data inicial
            if (dataInicio != null && d.getData().isBefore(dataInicio)) {
                passa = false;
            }
            
            // Filtrar por valor máximo
            if (valorMaximo != null && d.getValor() > valorMaximo) {
                passa = false;
            }
            
            // Filtrar por método de pagamento
            if (metodoPagamento != null && !metodoPagamento.equalsIgnoreCase(d.getMetodoPagamento())) {
                passa = false;
            }
            
            if (passa) {
                despesasFiltradas.add(d);
            }
        }
        
        // Mostrar resultados
        System.out.println("\n--- RESULTADOS DA FILTRAGEM ---");
        if (despesasFiltradas.isEmpty()) {
            System.out.println("\nNenhuma despesa encontrada com os criterios especificados.");
        } else {
            System.out.println("\nForam encontradas " + despesasFiltradas.size() + " despesa(s):\n");
            System.out.printf("%-5s %-12s %-10s %-15s %-15s%n", "ID", "DATA", "VALOR", "CATEGORIA", "PAGAMENTO");
            System.out.println("----------------------------------------------------------------");
            
            double total = 0.0;
            for (Despesa d : despesasFiltradas) {
                System.out.printf("%-5d %-12s %-10.2f %-15s %-15s%n", 
                    d.getId(), 
                    d.getData().format(DATE_FORMATTER), 
                    d.getValor(), 
                    d.getCategoria() != null ? d.getCategoria().getNome() : "N/A",
                    d.getMetodoPagamento() != null ? d.getMetodoPagamento() : "N/A");
                total += d.getValor();
            }
            
            System.out.println("----------------------------------------------------------------");
            System.out.printf("TOTAL: %.2f EUR%n", total);
        }
        
        pausar();
    }

    private static void verTotalPorCategoria(DespesaService despesaService, CategoryService categoryService) {
        // Carregar os dados primeiro
        List<Category> categorias = categoryService.listarTodas();
        
        if (categorias.isEmpty()) {
            limparConsola();
            System.out.println("--- TOTAL GASTO POR CATEGORIA ---\n");
            System.out.println("Sem categorias registadas.");
            pausar();
            return;
        }
        
        // Calcular todos os totais primeiro (isso vai gerar os logs do Hibernate)
        double totalGeral = 0.0;
        boolean temDespesas = false;
        StringBuilder resultado = new StringBuilder();
        
        for (Category cat : categorias) {
            Double total = despesaService.calcularTotalPorCategoria(utilizadorLogado.getId(), cat.getId());
            
            if (total != null && total > 0) {
                resultado.append(String.format("%-30s %15.2f%n", cat.getNome(), total));
                totalGeral += total;
                temDespesas = true;
            }
        }
        
        // Agora limpar a consola e mostrar apenas o resultado final
        limparConsola();
        System.out.println("--- TOTAL GASTO POR CATEGORIA ---\n");
        System.out.printf("%-30s %15s%n", "CATEGORIA", "TOTAL (EUR)");
        System.out.println("------------------------------------------------");
        
        if (!temDespesas) {
            System.out.println("\nSem despesas registadas.");
        } else {
            System.out.print(resultado.toString());
            System.out.println("------------------------------------------------");
            System.out.printf("%-30s %15.2f%n", "TOTAL GERAL", totalGeral);
        }
        
        pausar();
    }

    // ==================== UTILITÁRIOS ====================

    private static void limparConsola() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void pausar() {
        System.out.print("\nPrima ENTER para continuar...");
        scanner.nextLine();
    }

    private static int lerInteiro() {
        try { 
            String input = scanner.nextLine().trim();
            return input.isEmpty() ? -1 : Integer.parseInt(input);
        } catch (Exception e) { 
            return -1; 
        }
    }

    private static long lerLong() {
        try { 
            String input = scanner.nextLine().trim();
            return input.isEmpty() ? -1L : Long.parseLong(input);
        } catch (Exception e) { 
            return -1L; 
        }
    }

    private static double lerDouble() {
        try { 
            String input = scanner.nextLine().trim();
            return input.isEmpty() ? 0.0 : Double.parseDouble(input);
        } catch (Exception e) { 
            return 0.0; 
        }
    }
}