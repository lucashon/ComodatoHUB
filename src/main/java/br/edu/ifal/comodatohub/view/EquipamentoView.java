package br.edu.ifal.comodatohub.view;

import br.edu.ifal.comodatohub.bo.EquipamentoBO;
import br.edu.ifal.comodatohub.factory.EquipamentoFactory;
import br.edu.ifal.comodatohub.vo.Equipamento;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.util.List;
import java.util.Map;

public class EquipamentoView {

    private final Usuario logado;
    private final EquipamentoBO equipamentoBO = new EquipamentoBO();

    public EquipamentoView(Usuario logado) {
        this.logado = logado;
    }

    public void listarDisponiveis() {
        Console.executar(() -> {
            Console.titulo("EQUIPAMENTOS DISPONÍVEIS");
            Console.imprimirLista(equipamentoBO.listarDisponiveis());
        });
    }

    public void menu() {
        if (!logado.podeGerenciarEquipamentos()) {
            System.out.println("Opção inválida.");
            return;
        }
        while (true) {
            Console.titulo("GERENCIAR EQUIPAMENTOS");
            System.out.println("1 - Listar todos (por categoria)");
            System.out.println("2 - Cadastrar");
            System.out.println("3 - Editar");
            System.out.println("4 - Excluir");
            System.out.println("5 - Enviar para manutenção");
            System.out.println("6 - Retornar da manutenção");
            System.out.println("7 - Dar baixa");
            System.out.println("0 - Voltar");
            switch (Console.lerInt("Opção")) {
                case 1 -> Console.executar(() -> {
                    Map<String, List<Equipamento>> grupos = equipamentoBO.agruparPorCategoria();
                    if (grupos.isEmpty()) {
                        System.out.println("(nenhum equipamento)");
                    }
                    grupos.forEach((categoria, lista) -> {
                        System.out.println("-- " + categoria + " (" + lista.size() + ")");
                        lista.forEach(e -> System.out.println("   " + e));
                    });
                });
                case 2 -> Console.executar(() -> {
                    String categoria = Console.escolher("Categoria", EquipamentoFactory.CATEGORIAS);
                    Equipamento e = equipamentoBO.cadastrar(logado, categoria,
                            Console.lerTexto("Patrimônio"), Console.lerTexto("Descrição"), Console.lerTexto("Marca"));
                    System.out.println("Cadastrado: " + e);
                });
                case 3 -> Console.executar(() -> {
                    int id = Console.lerInt("ID do equipamento");
                    System.out.println("Atual: " + equipamentoBO.buscar(id));
                    equipamentoBO.atualizar(logado, id, Console.lerTexto("Nova descrição"), Console.lerTexto("Nova marca"));
                    System.out.println("Equipamento atualizado.");
                });
                case 4 -> Console.executar(() -> {
                    equipamentoBO.excluir(logado, Console.lerInt("ID do equipamento"));
                    System.out.println("Equipamento excluído.");
                });
                case 5 -> Console.executar(() -> {
                    equipamentoBO.enviarParaManutencao(logado, Console.lerInt("ID do equipamento"));
                    System.out.println("Enviado para manutenção.");
                });
                case 6 -> Console.executar(() -> {
                    equipamentoBO.retornarDaManutencao(logado, Console.lerInt("ID do equipamento"));
                    System.out.println("Equipamento disponível novamente.");
                });
                case 7 -> Console.executar(() -> {
                    equipamentoBO.darBaixa(logado, Console.lerInt("ID do equipamento"));
                    System.out.println("Baixa registrada.");
                });
                case 0 -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }
}
