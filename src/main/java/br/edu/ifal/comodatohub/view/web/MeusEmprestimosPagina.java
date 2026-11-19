package br.edu.ifal.comodatohub.view.web;

import br.edu.ifal.comodatohub.bo.EmprestimoBO;
import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.vo.Emprestimo;
import br.edu.ifal.comodatohub.vo.StatusEmprestimo;

import java.util.ArrayList;
import java.util.List;

import static br.edu.ifal.comodatohub.view.web.Html.*;

public class MeusEmprestimosPagina extends PaginaWeb {

    private final EmprestimoBO emprestimoBO = new EmprestimoBO();

    public MeusEmprestimosPagina() {
        acao("cancelar", r -> {
            emprestimoBO.cancelar(r.getUsuario(), r.getInt("id"));
            return "Solicitação cancelada.";
        });
        acao("renovar", r -> {
            Emprestimo e = emprestimoBO.renovar(r.getUsuario(), r.getInt("id"));
            return "Renovado! Nova data de devolução: " + data(e.getDataPrevistaDevolucao());
        });
    }

    @Override
    public String caminho() {
        return "/meus-emprestimos";
    }

    @Override
    public String titulo() {
        return "Meus empréstimos";
    }

    @Override
    public String conteudo(Requisicao r) throws PersistenciaException {
        List<String[]> linhas = new ArrayList<>();
        for (Emprestimo e : emprestimoBO.listarDoUsuario(r.getUsuario())) {
            String acoes = "";
            if (e.getStatus() == StatusEmprestimo.SOLICITADO) {
                acoes = botaoConfirmar(url("cancelar"), e.getId(), "Cancelar", "Cancelar esta solicitação?");
            } else if (e.getStatus() == StatusEmprestimo.ATIVO) {
                acoes = botao(url("renovar"), e.getId(), "Renovar");
            }
            linhas.add(new String[]{String.valueOf(e.getId()), esc(e.getEquipamento().getDescricao()),
                    esc(e.getStatus()), data(e.getDataSolicitacao()), data(e.getDataPrevistaDevolucao()),
                    String.valueOf(e.getRenovacoes()), observacao(e), acoes});
        }
        return "<p>Limite do seu perfil: " + r.getUsuario().getLimiteEmprestimos() + " empréstimo(s) ao mesmo tempo.</p>"
                + tabela(new String[]{"Nº", "Equipamento", "Situação", "Solicitado em", "Devolver até",
                "Renovações", "Observação", "Ações"}, linhas);
    }
}
