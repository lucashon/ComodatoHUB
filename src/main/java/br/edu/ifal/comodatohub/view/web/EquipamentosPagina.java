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

        if (admin && r.tem("gerenciar")) {
            html.append(painelGerenciar(equipamentoBO.buscar(r.getInt("gerenciar"))));
        }

        List<Equipamento> equipamentos = admin ? equipamentoBO.listarTodos() : equipamentoBO.listarDisponiveis();
        List<String[]> linhas = new ArrayList<>();
        for (Equipamento e : equipamentos) {
            StringBuilder acoes = new StringBuilder();
            if (e.estaDisponivel()) acoes.append(botao(url("solicitar"), e.getId(), "Solicitar"));
            if (admin) acoes.append(link("?gerenciar=" + e.getId(), "Gerenciar"));
            linhas.add(new String[]{String.valueOf(e.getId()), esc(e.getCategoria()), esc(e.getDescricao()),
                    esc(e.getMarca()), esc(e.getPatrimonio()), situacao(e.getStatus()),
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

    /** Painel do administrador com as ações de um equipamento, separadas por assunto. */
    private String painelGerenciar(Equipamento e) throws NegocioException, PersistenciaException {
        StringBuilder sb = new StringBuilder("<section class=\"caixa gerenciar\">")
                .append("<h2>").append(esc(e.getDescricao())).append(" (").append(esc(e.getPatrimonio()))
                .append(") <a class=\"fechar\" href=\"").append(caminho()).append("\">Fechar</a></h2>");

        // 1. Dados cadastrais
        sb.append("<div class=\"bloco\"><h3>Dados</h3>")
          .append("<form method=\"post\" action=\"").append(url("editar")).append("\" class=\"form\">")
          .append("<input type=\"hidden\" name=\"id\" value=\"").append(e.getId()).append("\">")
          .append(campo("Descrição", "descricao", e.getDescricao()))
          .append(campo("Marca", "marca", e.getMarca()))
          .append("<button type=\"submit\">Salvar</button></form></div>");

        // 2. Situação (manutenção e baixa)
        sb.append("<div class=\"bloco\"><h3>Situação</h3><p>Atual: ").append(situacao(e.getStatus())).append("</p>");
        boolean temAcao = false;
        if (e.estaDisponivel()) {
            sb.append(botao(url("manutencao"), e.getId(), "Enviar para manutenção"));
            temAcao = true;
        }
        if (e.getStatus() == StatusEquipamento.MANUTENCAO) {
            sb.append(botao(url("retornar"), e.getId(), "Voltar da manutenção"));
            temAcao = true;
        }
        if (e.estaDisponivel() || e.getStatus() == StatusEquipamento.MANUTENCAO) {
            sb.append(botaoConfirmar(url("baixa"), e.getId(), "Dar baixa", "Dar baixa neste equipamento?"));
            temAcao = true;
        }
        if (!temAcao) sb.append("<p class=\"nota\">Nenhuma alteração possível nesta situação.</p>");
        sb.append("</div>");

        // 3. Histórico de empréstimos
        sb.append("<div class=\"bloco\"><h3>Histórico de empréstimos</h3><ul>");
        List<Emprestimo> historico = emprestimoBO.historicoDoEquipamento(e.getId());
        if (historico.isEmpty()) sb.append("<li>Nenhum empréstimo registrado.</li>");
        for (Emprestimo h : historico) sb.append("<li>").append(esc(h)).append("</li>");
        sb.append("</ul></div>");

        // 4. Exclusão
        sb.append("<div class=\"bloco\"><h3>Excluir</h3>")
          .append("<p class=\"nota\">Só é possível excluir equipamentos sem empréstimos registrados. ")
          .append("Caso contrário, use \"Dar baixa\".</p>")
          .append(botaoConfirmar(url("excluir"), e.getId(), "Excluir equipamento", "Excluir este equipamento?"))
          .append("</div>");

        return sb.append("</section>").toString();
    }
}
