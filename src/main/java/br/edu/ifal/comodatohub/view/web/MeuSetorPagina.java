package br.edu.ifal.comodatohub.view.web;

import br.edu.ifal.comodatohub.exception.NegocioException;
import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.vo.Emprestimo;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.util.List;

/** HERANÇA: reaproveita a tabela de empréstimos, mas mostra só o setor do usuário e sem ações. */
public class MeuSetorPagina extends EmprestimosPagina {

    @Override
    public String caminho() {
        return "/meu-setor";
    }

    @Override
    public String titulo() {
        return "Meu setor";
    }

    @Override
    public boolean permitido(Usuario usuario) {
        return usuario.podeAcompanharSetor() && usuario.getSetor() != null;
    }

    @Override
    protected List<Emprestimo> buscar(Requisicao r) throws NegocioException, PersistenciaException {
        return emprestimoBO.listarDoSetor(r.getUsuario());
    }

    @Override
    protected String topo(Requisicao r) {
        return "<p>Empréstimos em aberto do setor <b>" + Html.esc(r.getUsuario().getSetor().getNome()) + "</b>.</p>";
    }

    @Override
    protected String acoes(Emprestimo e) {
        return "";
    }
}
