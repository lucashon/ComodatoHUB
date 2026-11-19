package br.edu.ifal.comodatohub.view;

import br.edu.ifal.comodatohub.exception.NegocioException;
import br.edu.ifal.comodatohub.exception.PersistenciaException;

import java.util.List;
import java.util.Scanner;

/** Utilitário da camada View: leitura do teclado e tratamento padrão das exceções. */
public final class Console {

    private static final Scanner ENTRADA = new Scanner(System.in);

    private Console() {
    }

    /** Ação de tela que pode lançar exceções de negócio ou de banco. */
    @FunctionalInterface
    public interface Acao {
        void executar() throws NegocioException, PersistenciaException;
    }

    /** Executa a ação tratando cada tipo de exceção de forma específica. */
    public static void executar(Acao acao) {
        try {
            acao.executar();
        } catch (NegocioException e) {
            System.out.println("[AVISO] " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("[ERRO] Falha no banco de dados: " + e.getMessage());
        }
    }

    public static String lerTexto(String rotulo) {
        System.out.print(rotulo + ": ");
        if (!ENTRADA.hasNextLine()) {          // entrada encerrada (Ctrl+D / Ctrl+Z)
            System.out.println();
            System.out.println("Entrada encerrada. Até logo!");
            System.exit(0);
        }
        return ENTRADA.nextLine().trim();
    }

    public static int lerInt(String rotulo) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(rotulo));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    /** Retorna null se o usuário deixar em branco. */
    public static Integer lerIntOpcional(String rotulo) {
        while (true) {
            String txt = lerTexto(rotulo + " (Enter para nenhum)");
            if (txt.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(txt);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    public static String escolher(String rotulo, String[] opcoes) {
        System.out.println(rotulo + ":");
        for (int i = 0; i < opcoes.length; i++) {
            System.out.println("  " + (i + 1) + " - " + opcoes[i]);
        }
        while (true) {
            int n = lerInt("Opção");
            if (n >= 1 && n <= opcoes.length) {
                return opcoes[n - 1];
            }
            System.out.println("Opção inválida.");
        }
    }

    public static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    public static void imprimirLista(List<?> itens) {
        if (itens.isEmpty()) {
            System.out.println("(nenhum registro)");
        }
        itens.forEach(System.out::println);
    }
}
