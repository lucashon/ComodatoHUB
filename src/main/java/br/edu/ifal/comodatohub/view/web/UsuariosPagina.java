package br.edu.ifal.comodatohub.view.web;

import br.edu.ifal.comodatohub.bo.SetorBO;
import br.edu.ifal.comodatohub.bo.UsuarioBO;
import br.edu.ifal.comodatohub.exception.NegocioException;
import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.factory.UsuarioFactory;
import br.edu.ifal.comodatohub.vo.Setor;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.util.ArrayList;
import java.util.List;

import static br.edu.ifal.comodatohub.view.web.Html.*;

public class UsuariosPagina extends PaginaWeb {

    private final UsuarioBO usuarioBO = new UsuarioBO();
    private final SetorBO setorBO = new SetorBO();

    public UsuariosPagina() {
        acao("cadastrar", r -> {
            Usuario u = usuarioBO.cadastrar(r.getUsuario(), r.get("perfil"), r.get("nome"), r.get("email"),
                    r.get("matricula"), r.get("senha"), r.getIntOpcional("setor"));
            return "Usuário " + u.getNome() + " cadastrado.";
        });
        acao("editar", r -> {
            usuarioBO.atualizar(r.getUsuario(), r.getInt("id"), r.get("nome"), r.get("email"), r.getIntOpcional("setor"));
            return "Usuário atualizado.";
        });
        acao("ativar", r -> {
            Usuario u = usuarioBO.buscar(r.getInt("id"));
            usuarioBO.alterarAtivo(r.getUsuario(), u.getId(), !u.isAtivo());
            return "Situação de " + u.getNome() + " alterada.";
        });
        acao("excluir", r -> {
            usuarioBO.excluir(r.getUsuario(), r.getInt("id"));
            return "Usuário excluído.";
        });
    }

    @Override
    public String caminho() {
        return "/usuarios";
    }

    @Override
    public String titulo() {
        return "Usuários";
    }

    @Override
    public boolean permitido(Usuario usuario) {
        return usuario.podeGerenciarEquipamentos();
    }

    private List<String[]> opcoesSetor() throws PersistenciaException {
        List<String[]> opcoes = new ArrayList<>();
        opcoes.add(new String[]{"", "(sem setor)"});
        for (Setor s : setorBO.listar()) opcoes.add(new String[]{String.valueOf(s.getId()), s.getNome()});
        return opcoes;
    }

    @Override
    public String conteudo(Requisicao r) throws NegocioException, PersistenciaException {
        StringBuilder html = new StringBuilder();
        if (r.tem("editar")) {
            Usuario u = usuarioBO.buscar(r.getInt("editar"));
            String setorAtual = u.getSetor() == null ? "" : String.valueOf(u.getSetor().getId());
            html.append(formulario("Editar " + u.getNome(), url("editar"),
                    "<input type=\"hidden\" name=\"id\" value=\"" + u.getId() + "\">"
                            + campo("Nome", "nome", u.getNome())
                            + campo("E-mail", "email", u.getEmail())
                            + select("Setor", "setor", opcoesSetor(), setorAtual), "Salvar"));
        }

        List<String[]> linhas = new ArrayList<>();
        for (Usuario u : usuarioBO.listar()) {
            String acoes = link("?editar=" + u.getId(), "Editar") + " "
                    + botao(url("ativar"), u.getId(), u.isAtivo() ? "Inativar" : "Ativar")
                    + botaoConfirmar(url("excluir"), u.getId(), "Excluir", "Excluir este usuário?");
            linhas.add(new String[]{String.valueOf(u.getId()), esc(u.getNome()), esc(u.getEmail()),
                    esc(u.getPerfil()), String.valueOf(u.getLimiteEmprestimos()),
                    u.getSetor() == null ? "-" : esc(u.getSetor().getNome()), u.isAtivo() ? "Ativo" : "Inativo", acoes});
        }
        html.append(tabela(new String[]{"ID", "Nome", "E-mail", "Perfil", "Limite", "Setor", "Situação", "Ações"}, linhas));

        List<String[]> perfis = new ArrayList<>();
        for (String p : UsuarioFactory.PERFIS) perfis.add(new String[]{p, p});
        html.append(formulario("Cadastrar usuário", url("cadastrar"),
                select("Perfil", "perfil", perfis, null)
                        + campo("Nome", "nome", "")
                        + campo("E-mail", "email", "")
                        + campo("Matrícula funcional", "matricula", "")
                        + "<label>Senha<input name=\"senha\" type=\"password\"></label>"
                        + select("Setor", "setor", opcoesSetor(), ""), "Cadastrar"));
        return html.toString();
    }
}
