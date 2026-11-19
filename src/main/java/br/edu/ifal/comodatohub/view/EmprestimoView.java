package br.edu.ifal.comodatohub.view;

import br.edu.ifal.comodatohub.bo.EmprestimoBO;
import br.edu.ifal.comodatohub.bo.EquipamentoBO;
import br.edu.ifal.comodatohub.vo.Emprestimo;
import br.edu.ifal.comodatohub.vo.EstadoConservacao;
import br.edu.ifal.comodatohub.vo.StatusEmprestimo;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.util.Map;

public class EmprestimoView {

    private final Usuario logado;
    private final EmprestimoBO emprestimoBO = new EmprestimoBO();
    private final EquipamentoBO equipamentoBO = new EquipamentoBO();

    public EmprestimoView(Usuario logado) {
        this.logado = logado;
    }

    public void solicitar() {
        Console.executar(() -> {
            Console.titulo("SOLICITAR EMPRÉSTIMO");
            Console.imprimirLista(equipamentoBO.listarDisponiveis());
            int id = Console.lerInt("ID do equipamento");
            Emprestimo e = emprestimoBO.solicitar(logado, id);
            System.out.println("Solicitação registrada! Nº " + e.getId()
                    + ". Retire o equipamento com o administrador. Prazo após a retirada: "
                    + e.getEquipamento().getPrazoMaximoDias() + " dia(s).");
        });
    }

    public void meusEmprestimos() {
        Console.executar(() -> {
            Console.titulo("MEUS EMPRÉSTIMOS");
            Console.imprimirLista(emprestimoBO.listarDoUsuario(logado));
        });
    }

    public void cancelar() {
        Console.executar(() -> {
            Console.titulo("CANCELAR SOLICITAÇÃO");
            emprestimoBO.cancelar(logado, Console.lerInt("Nº do empréstimo"));
            System.out.println("Solicitação cancelada.");
        });
    }

    public void renovar() {
        Console.executar(() -> {
            Console.titulo("RENOVAR EMPRÉSTIMO");
            Emprestimo e = emprestimoBO.renovar(logado, Console.lerInt("Nº do empréstimo"));
            System.out.println("Renovado! Nova data de devolução: " + e.getDataPrevistaDevolucao());
        });
    }

    public void doSetor() {
        Console.executar(() -> {
            Console.titulo("EMPRÉSTIMOS EM ABERTO DO MEU SETOR");
            Console.imprimirLista(emprestimoBO.listarDoSetor(logado));
        });
    }

    public void menuAdministrativo() {
        if (!logado.podeGerenciarEquipamentos()) {
            System.out.println("Opção inválida.");
            return;
        }
        Console.titulo("RETIRADAS E DEVOLUÇÕES");
        System.out.println("1 - Confirmar retirada");
        System.out.println("2 - Registrar devolução");
        System.out.println("3 - Cancelar solicitação de um usuário");
        System.out.println("0 - Voltar");
        switch (Console.lerInt("Opção")) {
            case 1 -> Console.executar(() -> {
                Console.imprimirLista(emprestimoBO.listarPorStatus(StatusEmprestimo.SOLICITADO));
                Emprestimo e = emprestimoBO.confirmarRetirada(logado, Console.lerInt("Nº do empréstimo"));
                System.out.println("Retirada confirmada. Devolver até " + e.getDataPrevistaDevolucao() + ".");
            });
            case 2 -> Console.executar(() -> {
                Console.imprimirLista(emprestimoBO.listarPorStatus(StatusEmprestimo.ATIVO));
                int id = Console.lerInt("Nº do empréstimo");
                EstadoConservacao estado = EstadoConservacao.valueOf(
                        Console.escolher("Estado do equipamento", new String[]{"BOM", "AVARIADO"}));
                String descricao = estado == EstadoConservacao.AVARIADO ? Console.lerTexto("Descreva a avaria") : null;
                Emprestimo e = emprestimoBO.devolver(logado, id, estado, descricao);
                System.out.println("Devolução registrada. Equipamento agora está " + e.getEquipamento().getStatus() + ".");
            });
            case 3 -> Console.executar(() -> {
                Console.imprimirLista(emprestimoBO.listarPorStatus(StatusEmprestimo.SOLICITADO));
                emprestimoBO.cancelar(logado, Console.lerInt("Nº do empréstimo"));
                System.out.println("Solicitação cancelada.");
            });
            default -> { }
        }
    }

    public void relatorios() {
        if (!logado.podeGerenciarEquipamentos()) {
            System.out.println("Opção inválida.");
            return;
        }
        Console.titulo("RELATÓRIOS");
        System.out.println("1 - Empréstimos atrasados");
        System.out.println("2 - Histórico de um equipamento");
        System.out.println("3 - Empréstimos por categoria");
        System.out.println("4 - Todos os empréstimos ativos");
        System.out.println("0 - Voltar");
        switch (Console.lerInt("Opção")) {
            case 1 -> Console.executar(() -> Console.imprimirLista(emprestimoBO.listarAtrasados()));
            case 2 -> Console.executar(() -> Console.imprimirLista(
                    emprestimoBO.historicoDoEquipamento(Console.lerInt("ID do equipamento"))));
            case 3 -> Console.executar(() -> {
                Map<String, Integer> ranking = emprestimoBO.rankingPorCategoria(logado);
                if (ranking.isEmpty()) {
                    System.out.println("(nenhum empréstimo realizado)");
                }
                ranking.forEach((categoria, total) -> System.out.println(categoria + ": " + total));
            });
            case 4 -> Console.executar(() -> Console.imprimirLista(emprestimoBO.listarPorStatus(StatusEmprestimo.ATIVO)));
            default -> { }
        }
    }
}
