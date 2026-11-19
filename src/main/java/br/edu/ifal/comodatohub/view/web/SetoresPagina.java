package br.edu.ifal.comodatohub.view.web;

import br.edu.ifal.comodatohub.bo.SetorBO;
import br.edu.ifal.comodatohub.exception.NegocioException;
import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.vo.Setor;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.util.ArrayList;
import java.util.List;

import static br.edu.ifal.comodatohub.view.web.Html.*;

public class SetoresPagina extends PaginaWeb {

    private final SetorBO setorBO = new SetorBO();

    public SetoresPagina() {
        acao("cadastrar", r -> {
            Setor s = setorBO.cadastrar(r.getUsuario(), r.get("nome"));
            return "Setor " + s.getNome() + " cadastrado.";
        });
        acao("renomear", r -> {
            setorBO.renomear(r.getUsuario(), r.getInt("id"), r.get("nome"));
            return "Setor renomeado.";
        });
        acao("excluir", r -> {
            setorBO.excluir(r.getUsuario(), r.getInt("id"));
            return "Setor excluído. Os usuários continuam cadastrados, sem setor.";
        });
    }

    @Override
    public String caminho() {
        return "/setores";
    }

    @Override
    public String titulo() {
        return "Setores";
    }

    @Override
    public boolean permitido(Usuario usuario) {
        return usuario.podeGerenciarEquipamentos();
    }

    @Override
    public String conteudo(Requisicao r) throws NegocioException, PersistenciaException {
        StringBuilder html = new StringBuilder();
        if (r.tem("usuarios")) {
            Setor s = setorBO.buscarComUsuarios(r.getInt("usuarios"));
            html.append(lista("Usuários do setor " + s.getNome(), s.getUsuarios()));
        }
        List<String[]> linhas = new ArrayList<>();
        for (Setor s : setorBO.listar()) {
            String renomear = "<form method=\"post\" action=\"" + url("renomear") + "\" class=\"inline\">"
                    + "<input type=\"hidden\" name=\"id\" value=\"" + s.getId() + "\">"
                    + "<input name=\"nome\" placeholder=\"Novo nome\"> <button type=\"submit\">Renomear</button></form>";
            String acoes = link("?usuarios=" + s.getId(), "Ver usuários") + " " + renomear
                    + botaoConfirmar(url("excluir"), s.getId(), "Excluir", "Excluir este setor?");
            linhas.add(new String[]{String.valueOf(s.getId()), esc(s.getNome()), acoes});
        }
        html.append(tabela(new String[]{"ID", "Nome", "Ações"}, linhas));
        html.append(formulario("Cadastrar setor", url("cadastrar"), campo("Nome", "nome", ""), "Cadastrar"));
        return html.toString();
    }
}
