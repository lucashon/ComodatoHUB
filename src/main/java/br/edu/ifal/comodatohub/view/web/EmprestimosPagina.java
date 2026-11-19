package br.edu.ifal.comodatohub.view.web;

import br.edu.ifal.comodatohub.bo.EmprestimoBO;
import br.edu.ifal.comodatohub.exception.NegocioException;
import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.vo.Emprestimo;
import br.edu.ifal.comodatohub.vo.EstadoConservacao;
import br.edu.ifal.comodatohub.vo.StatusEmprestimo;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static br.edu.ifal.comodatohub.view.web.Html.*;

/** Página do administrador: todos os empréstimos, retiradas, devoluções e relatórios. */
public class EmprestimosPagina extends PaginaWeb {

    protected final EmprestimoBO emprestimoBO = new EmprestimoBO();

    public EmprestimosPagina() {
        acao("retirada", r -> {
            Emprestimo e = emprestimoBO.confirmarRetirada(r.getUsuario(), r.getInt("id"));
            return "Retirada confirmada. Devolver até " + data(e.getDataPrevistaDevolucao()) + ".";
        });
        acao("devolver", r -> {
            EstadoConservacao estado = EstadoConservacao.valueOf(r.get("estado"));
            Emprestimo e = emprestimoBO.devolver(r.getUsuario(), r.getInt("id"), estado, r.get("descricao"));
            return "Devolução registrada. Equipamento: " + e.getEquipamento().getStatus() + ".";
        });
        acao("cancelar", r -> {
            emprestimoBO.cancelar(r.getUsuario(), r.getInt("id"));
            return "Solicitação cancelada.";
        });
    }

    @Override
    public String caminho() {
        return "/emprestimos";
    }

    @Override
    public String titulo() {
        return "Empréstimos";
    }

    @Override
    public boolean permitido(Usuario usuario) {
        return usuario.podeGerenciarEquipamentos();
    }

    /** Quais empréstimos mostrar. A página "Meu setor" sobrescreve este método. */
    protected List<Emprestimo> buscar(Requisicao r) throws NegocioException, PersistenciaException {
        switch (r.get("filtro")) {
            case "solicitados": return emprestimoBO.listarPorStatus(StatusEmprestimo.SOLICITADO);
            case "ativos":      return emprestimoBO.listarPorStatus(StatusEmprestimo.ATIVO);
            case "atrasados":   return emprestimoBO.listarAtrasados();
            default:            return emprestimoBO.listarTodos(r.getUsuario());
        }
    }

    /** Topo da página (filtros e ranking). A página "Meu setor" sobrescreve. */
    protected String topo(Requisicao r) throws NegocioException, PersistenciaException {
        StringBuilder sb = new StringBuilder("<p class=\"filtros\">Mostrar: ")
                .append(link(caminho(), "Todos")).append(" | ")
                .append(link("?filtro=solicitados", "Aguardando retirada")).append(" | ")
                .append(link("?filtro=ativos", "Ativos")).append(" | ")
                .append(link("?filtro=atrasados", "Atrasados")).append("</p>");
        Map<String, Integer> ranking = emprestimoBO.rankingPorCategoria(r.getUsuario());
        List<String> itens = new ArrayList<>();
        ranking.forEach((categoria, total) -> itens.add(categoria + ": " + total + " empréstimo(s)"));
        return sb + lista("Empréstimos por categoria", itens);
    }

    /** Botões de cada linha. A página "Meu setor" sobrescreve para não ter ações. */
    protected String acoes(Emprestimo e) {
        if (e.getStatus() == StatusEmprestimo.SOLICITADO) {
            return botao(url("retirada"), e.getId(), "Confirmar retirada")
                    + botaoConfirmar(url("cancelar"), e.getId(), "Cancelar", "Cancelar esta solicitação?");
        }
        if (e.getStatus() == StatusEmprestimo.ATIVO) {
            return "<form method=\"post\" action=\"" + url("devolver") + "\" class=\"inline\">"
                    + "<input type=\"hidden\" name=\"id\" value=\"" + e.getId() + "\">"
                    + "<select name=\"estado\"><option value=\"BOM\">Bom estado</option>"
                    + "<option value=\"AVARIADO\">Avariado</option></select> "
                    + "<input name=\"descricao\" placeholder=\"Descrição da avaria\"> "
                    + "<button type=\"submit\">Registrar devolução</button></form>";
        }
        return "";
    }

    @Override
    public String conteudo(Requisicao r) throws NegocioException, PersistenciaException {
        List<String[]> linhas = new ArrayList<>();
        for (Emprestimo e : buscar(r)) {
            linhas.add(new String[]{String.valueOf(e.getId()), esc(e.getUsuario().getNome()),
                    esc(e.getEquipamento().getDescricao()), esc(e.getStatus()),
                    data(e.getDataPrevistaDevolucao()), observacao(e), acoes(e)});
        }
        return topo(r) + tabela(new String[]{"Nº", "Usuário", "Equipamento", "Situação", "Devolver até",
                "Observação", "Ações"}, linhas);
    }
}
