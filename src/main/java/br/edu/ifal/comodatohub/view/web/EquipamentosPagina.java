package br.edu.ifal.comodatohub.view.web;

import br.edu.ifal.comodatohub.bo.EmprestimoBO;
import br.edu.ifal.comodatohub.bo.EquipamentoBO;
import br.edu.ifal.comodatohub.exception.NegocioException;
import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.factory.EquipamentoFactory;
import br.edu.ifal.comodatohub.vo.Emprestimo;
import br.edu.ifal.comodatohub.vo.Equipamento;
import br.edu.ifal.comodatohub.vo.StatusEquipamento;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.util.ArrayList;
import java.util.List;

import static br.edu.ifal.comodatohub.view.web.Html.*;

public class EquipamentosPagina extends PaginaWeb {

    private final EquipamentoBO equipamentoBO = new EquipamentoBO();
    private final EmprestimoBO emprestimoBO = new EmprestimoBO();

    public EquipamentosPagina() {
        acao("solicitar", r -> {
            Emprestimo e = emprestimoBO.solicitar(r.getUsuario(), r.getInt("id"));
            return "Solicitação nº " + e.getId() + " registrada. Retire o equipamento com o administrador.";
        });
        acao("cadastrar", r -> {
            Equipamento e = equipamentoBO.cadastrar(r.getUsuario(), r.get("categoria"), r.get("patrimonio"),
                    r.get("descricao"), r.get("marca"));
            return "Equipamento " + e.getDescricao() + " cadastrado.";
        });
        acao("editar", r -> {
            equipamentoBO.atualizar(r.getUsuario(), r.getInt("id"), r.get("descricao"), r.get("marca"));
            return "Equipamento atualizado.";
        });
        acao("excluir", r -> {
            equipamentoBO.excluir(r.getUsuario(), r.getInt("id"));
            return "Equipamento excluído.";
        });
        acao("manutencao", r -> {
            equipamentoBO.enviarParaManutencao(r.getUsuario(), r.getInt("id"));
            return "Equipamento enviado para manutenção.";
        });
        acao("retornar", r -> {
            equipamentoBO.retornarDaManutencao(r.getUsuario(), r.getInt("id"));
            return "Equipamento disponível novamente.";
        });
        acao("baixa", r -> {
            equipamentoBO.darBaixa(r.getUsuario(), r.getInt("id"));
            return "Baixa registrada.";
        });
    }

    @Override
    public String caminho() {
        return "/equipamentos";
    }

    @Override
    public String titulo() {
        return "Equipamentos";
    }

    @Override
    public String conteudo(Requisicao r) throws NegocioException, PersistenciaException {
        Usuario u = r.getUsuario();
        boolean admin = u.podeGerenciarEquipamentos();
        StringBuilder html = new StringBuilder();

        if (admin && r.tem("editar")) {
            Equipamento e = equipamentoBO.buscar(r.getInt("editar"));
            html.append(formulario("Editar equipamento " + e.getPatrimonio(), url("editar"),
                    "<input type=\"hidden\" name=\"id\" value=\"" + e.getId() + "\">"
                            + campo("Descrição", "descricao", e.getDescricao())
                            + campo("Marca", "marca", e.getMarca()), "Salvar"));
        }
        if (admin && r.tem("historico")) {
            Equipamento e = equipamentoBO.buscar(r.getInt("historico"));
            html.append(lista("Histórico: " + e.getDescricao(), emprestimoBO.historicoDoEquipamento(e.getId())));
        }

        List<Equipamento> equipamentos = admin ? equipamentoBO.listarTodos() : equipamentoBO.listarDisponiveis();
        List<String[]> linhas = new ArrayList<>();
        for (Equipamento e : equipamentos) {
            StringBuilder acoes = new StringBuilder();
            if (e.estaDisponivel()) acoes.append(botao(url("solicitar"), e.getId(), "Solicitar"));
            if (admin) {
                acoes.append(link("?editar=" + e.getId(), "Editar")).append(' ')
                     .append(link("?historico=" + e.getId(), "Histórico")).append(' ');
                if (e.estaDisponivel()) acoes.append(botao(url("manutencao"), e.getId(), "Manutenção"));
                if (e.getStatus() == StatusEquipamento.MANUTENCAO) acoes.append(botao(url("retornar"), e.getId(), "Voltar da manutenção"));
                if (e.estaDisponivel() || e.getStatus() == StatusEquipamento.MANUTENCAO) {
                    acoes.append(botaoConfirmar(url("baixa"), e.getId(), "Dar baixa", "Dar baixa neste equipamento?"));
                }
                acoes.append(botaoConfirmar(url("excluir"), e.getId(), "Excluir", "Excluir este equipamento?"));
            }
            linhas.add(new String[]{String.valueOf(e.getId()), esc(e.getCategoria()), esc(e.getDescricao()),
                    esc(e.getMarca()), esc(e.getPatrimonio()), esc(e.getStatus()),
                    String.valueOf(e.getPrazoMaximoDias()), acoes.toString()});
        }
        html.append(tabela(new String[]{"ID", "Categoria", "Descrição", "Marca", "Patrimônio", "Situação",
                "Prazo (dias)", "Ações"}, linhas));

        if (admin) {
            List<String[]> categorias = new ArrayList<>();
            for (String c : EquipamentoFactory.CATEGORIAS) categorias.add(new String[]{c, c});
            html.append(formulario("Cadastrar equipamento", url("cadastrar"),
                    select("Categoria", "categoria", categorias, null)
                            + campo("Patrimônio", "patrimonio", "")
                            + campo("Descrição", "descricao", "")
                            + campo("Marca", "marca", ""), "Cadastrar"));
        }
        return html.toString();
    }
}
