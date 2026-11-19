package br.edu.ifal.comodatohub.view;

import br.edu.ifal.comodatohub.bo.SetorBO;
import br.edu.ifal.comodatohub.bo.UsuarioBO;
import br.edu.ifal.comodatohub.factory.UsuarioFactory;
import br.edu.ifal.comodatohub.vo.Setor;
import br.edu.ifal.comodatohub.vo.Usuario;

public class UsuarioView {

    private final Usuario logado;
    private final UsuarioBO usuarioBO = new UsuarioBO();
    private final SetorBO setorBO = new SetorBO();

    public UsuarioView(Usuario logado) {
        this.logado = logado;
    }

    public void menu() {
        if (!logado.podeGerenciarEquipamentos()) {
            System.out.println("Opção inválida.");
            return;
        }
        while (true) {
            Console.titulo("USUÁRIOS E SETORES");
            System.out.println("1 - Listar usuários");
            System.out.println("2 - Cadastrar usuário");
            System.out.println("3 - Editar usuário");
            System.out.println("4 - Ativar/Inativar usuário");
            System.out.println("5 - Excluir usuário");
            System.out.println("6 - Listar setores");
            System.out.println("7 - Cadastrar setor");
            System.out.println("8 - Renomear setor");
            System.out.println("9 - Excluir setor");
            System.out.println("10 - Ver usuários de um setor");
            System.out.println("0 - Voltar");
            switch (Console.lerInt("Opção")) {
                case 1 -> Console.executar(() -> Console.imprimirLista(usuarioBO.listar()));
                case 2 -> Console.executar(() -> {
                    String perfil = Console.escolher("Perfil", UsuarioFactory.PERFIS);
                    String nome = Console.lerTexto("Nome");
                    String email = Console.lerTexto("E-mail");
                    String matricula = Console.lerTexto("Matrícula funcional");
                    String senha = Console.lerTexto("Senha (mín. 4 caracteres)");
                    Console.imprimirLista(setorBO.listar());
                    Integer setorId = Console.lerIntOpcional("ID do setor");
                    Usuario u = usuarioBO.cadastrar(logado, perfil, nome, email, matricula, senha, setorId);
                    System.out.println("Cadastrado: " + u);
                });
                case 3 -> Console.executar(() -> {
                    int id = Console.lerInt("ID do usuário");
                    System.out.println("Atual: " + usuarioBO.buscar(id));
                    String nome = Console.lerTexto("Novo nome");
                    String email = Console.lerTexto("Novo e-mail");
                    Console.imprimirLista(setorBO.listar());
                    Integer setorId = Console.lerIntOpcional("ID do setor");
                    usuarioBO.atualizar(logado, id, nome, email, setorId);
                    System.out.println("Usuário atualizado.");
                });
                case 4 -> Console.executar(() -> {
                    int id = Console.lerInt("ID do usuário");
                    boolean ativar = Console.escolher("Ação", new String[]{"ATIVAR", "INATIVAR"}).equals("ATIVAR");
                    usuarioBO.alterarAtivo(logado, id, ativar);
                    System.out.println("Situação alterada.");
                });
                case 5 -> Console.executar(() -> {
                    usuarioBO.excluir(logado, Console.lerInt("ID do usuário"));
                    System.out.println("Usuário excluído.");
                });
                case 6 -> Console.executar(() -> Console.imprimirLista(setorBO.listar()));
                case 7 -> Console.executar(() -> {
                    Setor s = setorBO.cadastrar(logado, Console.lerTexto("Nome do setor"));
                    System.out.println("Setor cadastrado: " + s);
                });
                case 8 -> Console.executar(() -> {
                    setorBO.renomear(logado, Console.lerInt("ID do setor"), Console.lerTexto("Novo nome"));
                    System.out.println("Setor renomeado.");
                });
                case 9 -> Console.executar(() -> {
                    setorBO.excluir(logado, Console.lerInt("ID do setor"));
                    System.out.println("Setor excluído (os usuários ficaram sem setor).");
                });
                case 10 -> Console.executar(() -> {
                    Setor s = setorBO.buscarComUsuarios(Console.lerInt("ID do setor"));
                    System.out.println("Setor " + s.getNome() + ":");
                    Console.imprimirLista(s.getUsuarios());
                });
                case 0 -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }
}
