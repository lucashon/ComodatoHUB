package br.edu.ifal.comodatohub.view;

import br.edu.ifal.comodatohub.bo.UsuarioBO;
import br.edu.ifal.comodatohub.exception.NegocioException;
import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.vo.Usuario;

/** Tela de login e menu principal. O menu muda conforme o perfil (polimorfismo). */
public class MenuPrincipalView {

    private final UsuarioBO usuarioBO = new UsuarioBO();

    public void iniciar() {
        System.out.println("==========================================");
        System.out.println("  ComodatoHub - Empréstimo de Equipamentos");
        System.out.println("==========================================");
        while (true) {
            Console.titulo("LOGIN (deixe o e-mail em branco para sair)");
            String email = Console.lerTexto("E-mail");
            if (email.isEmpty()) {
                System.out.println("Até logo!");
                return;
            }
            String senha = Console.lerTexto("Senha");
            try {
                Usuario logado = usuarioBO.autenticar(email, senha);
                menu(logado);
            } catch (NegocioException e) {
                System.out.println("[AVISO] " + e.getMessage());
            } catch (PersistenciaException e) {
                System.out.println("[ERRO] Falha no banco de dados: " + e.getMessage());
            }
        }
    }

    private void menu(Usuario logado) {
        EmprestimoView emprestimoView = new EmprestimoView(logado);
        EquipamentoView equipamentoView = new EquipamentoView(logado);
        UsuarioView usuarioView = new UsuarioView(logado);
        boolean admin = logado.podeGerenciarEquipamentos();

        while (true) {
            Console.titulo("MENU - " + logado.getNome() + " (" + logado.getPerfil() + ")");
            System.out.println("1 - Equipamentos disponíveis");
            System.out.println("2 - Solicitar empréstimo");
            System.out.println("3 - Meus empréstimos");
            System.out.println("4 - Cancelar solicitação");
            System.out.println("5 - Renovar empréstimo");
            if (logado.podeAcompanharSetor()) {
                System.out.println("6 - Empréstimos do meu setor");
            }
            if (admin) {
                System.out.println("7 - Retiradas e devoluções");
                System.out.println("8 - Gerenciar equipamentos");
                System.out.println("9 - Gerenciar usuários e setores");
                System.out.println("10 - Relatórios");
            }
            System.out.println("0 - Sair (logout)");

            int op = Console.lerInt("Opção");
            switch (op) {
                case 1 -> equipamentoView.listarDisponiveis();
                case 2 -> emprestimoView.solicitar();
                case 3 -> emprestimoView.meusEmprestimos();
                case 4 -> emprestimoView.cancelar();
                case 5 -> emprestimoView.renovar();
                case 6 -> emprestimoView.doSetor();
                case 7 -> emprestimoView.menuAdministrativo();
                case 8 -> equipamentoView.menu();
                case 9 -> usuarioView.menu();
                case 10 -> emprestimoView.relatorios();
                case 0 -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }
}
