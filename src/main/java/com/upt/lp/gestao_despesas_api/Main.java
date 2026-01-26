package com.upt.lp.gestao_despesas_api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
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

            while (!sair) {
                limparConsola();
                mostrarCabecalho();

                if (utilizadorLogado == null) {
                    sair = menuSemLogin(userService);
                } else {
                    sair = menuComLogin(userService, categoryService, despesaService);
                }
            }

            System.out.println("\n👋 Até breve!");
            scanner.close();
        };
    }

    // ==================== MENUS ====================

    private static boolean menuSemLogin(UserService userService) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║          MENU PRINCIPAL                ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("1. 🔐 Login");
        System.out.println("2. 📝 Registar novo utilizador");
        System.out.println("0. 🚪 Sair");
        System.out.print("\nEscolha uma opção: ");

        int opcao = lerInteiro();

        switch (opcao) {
            case 1:
                fazerLogin(userService);
                break;
            case 2:
                registarUtilizador(userService);
                break;
            case 0:
                return true;
            default:
                System.out.println("❌ Opção inválida!");
                pausar();
        }

        return false;
    }

    private static boolean menuComLogin(UserService userService, CategoryService categoryService, 
                                       DespesaService despesaService) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  👤 " + utilizadorLogado.getName());
        System.out.println("║  📧 " + utilizadorLogado.getEmail());
        System.out.println("║  🎭 " + utilizadorLogado.getRole());
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();

        if ("ADMIN".equals(utilizadorLogado.getRole())) {
            return menuAdmin(categoryService, despesaService);
        } else {
            return menuUser(despesaService, categoryService);
        }
    }

    private static boolean menuAdmin(CategoryService categoryService, DespesaService despesaService) {
        System.out.println("📋 MENU ADMINISTRADOR");
        System.out.println("─────────────────────");
        System.out.println("1. 📁 Gerir Categorias");
        System.out.println("2. 💰 Gerir Minhas Despesas");
        System.out.println("3. 🔓 Logout");
        System.out.println("0. 🚪 Sair");
        System.out.print("\nEscolha uma opção: ");

        int opcao = lerInteiro();

        switch (opcao) {
            case 1:
                menuCategorias(categoryService);
                break;
            case 2:
                menuDespesas(despesaService, categoryService);
                break;
            case 3:
                utilizadorLogado = null;
                System.out.println("✓ Logout efetuado com sucesso!");
                pausar();
                break;
            case 0:
                return true;
            default:
                System.out.println("❌ Opção inválida!");
                pausar();
        }

        return false;
    }

    private static boolean menuUser(DespesaService despesaService, CategoryService categoryService) {
        System.out.println("📋 MENU UTILIZADOR");
        System.out.println("──────────────────");
        System.out.println("1. 💰 Gerir Despesas");
        System.out.println("2. 📊 Consultar Estatísticas");
        System.out.println("3. 🔓 Logout");
        System.out.println("0. 🚪 Sair");
        System.out.print("\nEscolha uma opção: ");

        int opcao = lerInteiro();

        switch (opcao) {
            case 1:
                menuDespesas(despesaService, categoryService);
                break;
            case 2:
                menuEstatisticas(despesaService, categoryService);
                break;
            case 3:
                utilizadorLogado = null;
                System.out.println("✓ Logout efetuado com sucesso!");
                pausar();
                break;
            case 0:
                return true;
            default:
                System.out.println("❌ Opção inválida!");
                pausar();
        }

        return false;
    }

    // ==================== CATEGORIAS ====================

    private static void menuCategorias(CategoryService categoryService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║       GESTÃO DE CATEGORIAS             ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("1. ➕ Criar nova categoria");
        System.out.println("2. 📋 Listar todas as categorias");
        System.out.println("3. ✏️  Editar categoria");
        System.out.println("4. 🗑️  Eliminar categoria");
        System.out.println("0. ⬅️  Voltar");
        System.out.print("\nEscolha uma opção: ");

        int opcao = lerInteiro();

        switch (opcao) {
            case 1:
                criarCategoria(categoryService);
                break;
            case 2:
                listarCategorias(categoryService);
                break;
            case 3:
                editarCategoria(categoryService);
                break;
            case 4:
                eliminarCategoria(categoryService);
                break;
            case 0:
                break;
            default:
                System.out.println("❌ Opção inválida!");
                pausar();
        }
    }

    private static void criarCategoria(CategoryService categoryService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║         CRIAR CATEGORIA                ║");
        System.out.println("╚════════════════════════════════════════╝");

        System.out.print("Nome da categoria: ");
        String nome = scanner.nextLine();

        Category categoria = new Category();
        categoria.setNome(nome);

        String resultado = categoryService.guardar(categoria, utilizadorLogado.getRole());
        System.out.println("\n" + resultado);
        pausar();
    }

    private static void listarCategorias(CategoryService categoryService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║       LISTA DE CATEGORIAS              ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        List<Category> categorias = categoryService.listarTodas();

        if (categorias.isEmpty()) {
            System.out.println("⚠️  Não existem categorias registadas.");
        } else {
            System.out.printf("%-5s %-30s%n", "ID", "NOME");
            System.out.println("─────────────────────────────────────");
            for (Category cat : categorias) {
                System.out.printf("%-5d %-30s%n", cat.getId(), cat.getNome());
            }
        }

        pausar();
    }

    private static void editarCategoria(CategoryService categoryService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║        EDITAR CATEGORIA                ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        listarCategorias(categoryService);

        System.out.print("\nID da categoria a editar: ");
        Long id = lerLong();

        System.out.print("Novo nome: ");
        String novoNome = scanner.nextLine();

        Category novaDados = new Category();
        novaDados.setNome(novoNome);

        String resultado = categoryService.editar(id, novaDados, utilizadorLogado.getRole());
        System.out.println("\n" + resultado);
        pausar();
    }

    private static void eliminarCategoria(CategoryService categoryService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║       ELIMINAR CATEGORIA               ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        listarCategorias(categoryService);

        System.out.print("\nID da categoria a eliminar: ");
        Long id = lerLong();

        System.out.print("⚠️  Tem a certeza? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            String resultado = categoryService.eliminar(id, utilizadorLogado.getRole());
            System.out.println("\n" + resultado);
        } else {
            System.out.println("❌ Operação cancelada.");
        }

        pausar();
    }

    // ==================== DESPESAS ====================

    private static void menuDespesas(DespesaService despesaService, CategoryService categoryService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║        GESTÃO DE DESPESAS              ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("1. ➕ Registar nova despesa");
        System.out.println("2. 📋 Listar minhas despesas");
        System.out.println("3. 🔍 Filtrar despesas");
        System.out.println("4. ✏️  Editar despesa");
        System.out.println("5. 🗑️  Eliminar despesa");
        System.out.println("0. ⬅️  Voltar");
        System.out.print("\nEscolha uma opção: ");

        int opcao = lerInteiro();

        switch (opcao) {
            case 1:
                registarDespesa(despesaService, categoryService);
                break;
            case 2:
                listarDespesas(despesaService);
                break;
            case 3:
                filtrarDespesas(despesaService, categoryService);
                break;
            case 4:
                editarDespesa(despesaService, categoryService);
                break;
            case 5:
                eliminarDespesa(despesaService);
                break;
            case 0:
                break;
            default:
                System.out.println("❌ Opção inválida!");
                pausar();
        }
    }

    private static void registarDespesa(DespesaService despesaService, CategoryService categoryService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║       REGISTAR DESPESA                 ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        // Listar categorias disponíveis
        List<Category> categorias = categoryService.listarTodas();
        if (categorias.isEmpty()) {
            System.out.println("⚠️  Não existem categorias. Contacte um administrador.");
            pausar();
            return;
        }

        System.out.println("Categorias disponíveis:");
        for (int i = 0; i < categorias.size(); i++) {
            System.out.println((i + 1) + ". " + categorias.get(i).getNome());
        }

        System.out.print("\nEscolha a categoria (número): ");
        int catIndex = lerInteiro() - 1;

        if (catIndex < 0 || catIndex >= categorias.size()) {
            System.out.println("❌ Categoria inválida!");
            pausar();
            return;
        }

        System.out.print("Valor (€): ");
        double valor = lerDouble();

        System.out.print("Data (dd/MM/yyyy) ou ENTER para hoje: ");
        String dataStr = scanner.nextLine();
        LocalDate data = LocalDate.now();
        if (!dataStr.trim().isEmpty()) {
            try {
                data = LocalDate.parse(dataStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("⚠️  Data inválida, usando data de hoje.");
            }
        }

        System.out.print("Método de pagamento: ");
        String metodo = scanner.nextLine();

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();

        Despesa despesa = new Despesa();
        despesa.setUtilizador(utilizadorLogado);
        despesa.setCategoria(categorias.get(catIndex));
        despesa.setValor(valor);
        despesa.setData(data);
        despesa.setMetodoPagamento(metodo);
        despesa.setDescricao(descricao);

        try {
            despesa = despesaService.guardar(despesa);
            System.out.println("\n✓ Despesa registada com sucesso! (ID: " + despesa.getId() + ")");
        } catch (RuntimeException e) {
            System.out.println("\n❌ Erro: " + e.getMessage());
        }

        pausar();
    }

    private static void listarDespesas(DespesaService despesaService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║         MINHAS DESPESAS                ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        List<Despesa> despesas = despesaService.listarPorUtilizador(utilizadorLogado.getId());

        if (despesas.isEmpty()) {
            System.out.println("⚠️  Não existem despesas registadas.");
        } else {
            System.out.printf("%-5s %-12s %-10s %-15s %-15s %-25s%n", 
                "ID", "DATA", "VALOR", "CATEGORIA", "PAGAMENTO", "DESCRIÇÃO");
            System.out.println("─────────────────────────────────────────────────────────────────────────────");

            double total = 0;
            for (Despesa d : despesas) {
                System.out.printf("%-5d %-12s €%-9.2f %-15s %-15s %-25s%n",
                    d.getId(),
                    d.getData().format(DATE_FORMATTER),
                    d.getValor(),
                    d.getCategoria().getNome(),
                    d.getMetodoPagamento(),
                    d.getDescricao());
                total += d.getValor();
            }

            System.out.println("─────────────────────────────────────────────────────────────────────────────");
            System.out.printf("TOTAL: €%.2f%n", total);
        }

        pausar();
    }

    private static void filtrarDespesas(DespesaService despesaService, CategoryService categoryService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║       FILTRAR DESPESAS                 ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        System.out.print("Data início (dd/MM/yyyy) ou ENTER para ignorar: ");
        String dataInicioStr = scanner.nextLine();
        LocalDate dataInicio = null;
        if (!dataInicioStr.trim().isEmpty()) {
            try {
                dataInicio = LocalDate.parse(dataInicioStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("⚠️  Data inválida.");
            }
        }

        System.out.print("Data fim (dd/MM/yyyy) ou ENTER para ignorar: ");
        String dataFimStr = scanner.nextLine();
        LocalDate dataFim = null;
        if (!dataFimStr.trim().isEmpty()) {
            try {
                dataFim = LocalDate.parse(dataFimStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("⚠️  Data inválida.");
            }
        }

        System.out.print("Valor mínimo (€) ou ENTER para ignorar: ");
        String minStr = scanner.nextLine();
        Double valorMin = minStr.trim().isEmpty() ? null : Double.parseDouble(minStr);

        System.out.print("Valor máximo (€) ou ENTER para ignorar: ");
        String maxStr = scanner.nextLine();
        Double valorMax = maxStr.trim().isEmpty() ? null : Double.parseDouble(maxStr);

        // Listar categorias disponíveis
        List<Category> categorias = categoryService.listarTodas();
        if (!categorias.isEmpty()) {
            System.out.println("\nCategorias disponíveis:");
            for (int i = 0; i < categorias.size(); i++) {
                System.out.println((i + 1) + ". " + categorias.get(i).getNome() + " (ID: " + categorias.get(i).getId() + ")");
            }
        }

        System.out.print("\nIDs das categorias (separados por vírgula) ou ENTER para ignorar: ");
        String catStr = scanner.nextLine();
        List<Long> categoriaIds = null;
        if (!catStr.trim().isEmpty()) {
            categoriaIds = new ArrayList<>();
            String[] ids = catStr.split(",");
            for (String id : ids) {
                try {
                    categoriaIds.add(Long.parseLong(id.trim()));
                } catch (NumberFormatException e) {
                    System.out.println("⚠️  ID inválido ignorado: " + id);
                }
            }
        }

        List<Despesa> despesas = despesaService.filtrarDespesas(
            utilizadorLogado.getId(), dataInicio, dataFim, valorMin, valorMax, categoriaIds
        );

        System.out.println("\n📊 RESULTADOS:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────");

        if (despesas.isEmpty()) {
            System.out.println("⚠️  Nenhuma despesa encontrada com esses critérios.");
        } else {
            System.out.printf("%-5s %-12s %-10s %-15s %-25s%n", 
                "ID", "DATA", "VALOR", "CATEGORIA", "DESCRIÇÃO");
            System.out.println("─────────────────────────────────────────────────────────────────────────────");

            double total = 0;
            for (Despesa d : despesas) {
                System.out.printf("%-5d %-12s €%-9.2f %-15s %-25s%n",
                    d.getId(),
                    d.getData().format(DATE_FORMATTER),
                    d.getValor(),
                    d.getCategoria().getNome(),
                    d.getDescricao());
                total += d.getValor();
            }

            System.out.println("─────────────────────────────────────────────────────────────────────────────");
            System.out.printf("TOTAL: €%.2f | Despesas encontradas: %d%n", total, despesas.size());
        }

        pausar();
    }

    private static void editarDespesa(DespesaService despesaService, CategoryService categoryService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║         EDITAR DESPESA                 ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        listarDespesas(despesaService);

        System.out.print("\nID da despesa a editar: ");
        Long id = lerLong();

        // Listar categorias
        List<Category> categorias = categoryService.listarTodas();
        System.out.println("\nCategorias disponíveis:");
        for (int i = 0; i < categorias.size(); i++) {
            System.out.println((i + 1) + ". " + categorias.get(i).getNome());
        }

        System.out.print("\nEscolha a categoria (número): ");
        int catIndex = lerInteiro() - 1;

        System.out.print("Novo valor (€): ");
        double valor = lerDouble();

        System.out.print("Nova data (dd/MM/yyyy): ");
        String dataStr = scanner.nextLine();
        LocalDate data = LocalDate.parse(dataStr, DATE_FORMATTER);

        System.out.print("Novo método de pagamento: ");
        String metodo = scanner.nextLine();

        System.out.print("Nova descrição: ");
        String descricao = scanner.nextLine();

        Despesa novaDespesa = new Despesa();
        novaDespesa.setCategoria(categorias.get(catIndex));
        novaDespesa.setValor(valor);
        novaDespesa.setData(data);
        novaDespesa.setMetodoPagamento(metodo);
        novaDespesa.setDescricao(descricao);

        String resultado = despesaService.editar(id, novaDespesa);
        System.out.println("\n" + resultado);
        pausar();
    }

    private static void eliminarDespesa(DespesaService despesaService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║        ELIMINAR DESPESA                ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        listarDespesas(despesaService);

        System.out.print("\nID da despesa a eliminar: ");
        Long id = lerLong();

        System.out.print("⚠️  Tem a certeza? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            String resultado = despesaService.eliminar(id);
            System.out.println("\n" + resultado);
        } else {
            System.out.println("❌ Operação cancelada.");
        }

        pausar();
    }

    // ==================== ESTATÍSTICAS ====================

    private static void menuEstatisticas(DespesaService despesaService, CategoryService categoryService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║          ESTATÍSTICAS                  ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        List<Category> categorias = categoryService.listarTodas();
        
        System.out.println("📊 TOTAL POR CATEGORIA:");
        System.out.println("─────────────────────────────────────");

        double grandTotal = 0;
        for (Category cat : categorias) {
            Double total = despesaService.calcularTotalPorCategoria(utilizadorLogado.getId(), cat.getId());
            if (total > 0) {
                System.out.printf("%-20s: €%.2f%n", cat.getNome(), total);
                grandTotal += total;
            }
        }

        System.out.println("─────────────────────────────────────");
        System.out.printf("TOTAL GERAL: €%.2f%n", grandTotal);

        pausar();
    }

    // ==================== LOGIN E REGISTO ====================

    private static void fazerLogin(UserService userService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║              LOGIN                     ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            utilizadorLogado = userService.login(email, password);
            System.out.println("\n✓ Login efetuado com sucesso!");
            System.out.println("Bem-vindo, " + utilizadorLogado.getName() + "!");
            pausar();
        } catch (RuntimeException e) {
            System.out.println("\n❌ " + e.getMessage());
            pausar();
        }
    }

    private static void registarUtilizador(UserService userService) {
        limparConsola();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║         REGISTO DE UTILIZADOR          ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Role (USER/ADMIN): ");
        String role = scanner.nextLine().toUpperCase();

        if (!role.equals("USER") && !role.equals("ADMIN")) {
            System.out.println("❌ Role inválida! Usando USER por defeito.");
            role = "USER";
        }

        User user = new User();
        user.setName(nome);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        try {
            user = userService.registar(user);
            System.out.println("\n✓ Utilizador registado com sucesso!");
            System.out.println("ID: " + user.getId());
            pausar();
        } catch (RuntimeException e) {
            System.out.println("\n❌ " + e.getMessage());
            pausar();
        }
    }

    // ==================== UTILITÁRIOS ====================

    private static void limparConsola() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void mostrarCabecalho() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE GESTÃO DE DESPESAS        ║");
        System.out.println("║         Equipa 11 - UPT                ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
    }

    private static void pausar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }

    private static int lerInteiro() {
        while (true) {
            try {
                int valor = Integer.parseInt(scanner.nextLine());
                return valor;
            } catch (NumberFormatException e) {
                System.out.print("❌ Valor inválido. Tente novamente: ");
            }
        }
    }

    private static long lerLong() {
        while (true) {
            try {
                long valor = Long.parseLong(scanner.nextLine());
                return valor;
            } catch (NumberFormatException e) {
                System.out.print("❌ Valor inválido. Tente novamente: ");
            }
        }
    }

    private static double lerDouble() {
        while (true) {
            try {
                double valor = Double.parseDouble(scanner.nextLine());
                return valor;
            } catch (NumberFormatException e) {
                System.out.print("❌ Valor inválido. Tente novamente: ");
            }
        }
    }
}