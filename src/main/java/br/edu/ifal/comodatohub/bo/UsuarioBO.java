package br.edu.ifal.comodatohub.bo;

import br.edu.ifal.comodatohub.dao.EmprestimoDAO;
import br.edu.ifal.comodatohub.dao.SetorDAO;
import br.edu.ifal.comodatohub.dao.UsuarioDAO;
import br.edu.ifal.comodatohub.exception.*;
import br.edu.ifal.comodatohub.factory.UsuarioFactory;
import br.edu.ifal.comodatohub.vo.Setor;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.util.List;

public class UsuarioBO extends BaseBO {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final SetorDAO setorDAO = new SetorDAO();
    private final EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

    public Usuario autenticar(String email, String senha) throws NegocioException, PersistenciaException {
        exigirTexto(email, "e-mail");
        exigirTexto(senha, "senha");
        Usuario u = usuarioDAO.buscarPorEmail(email.trim());
        if (u == null || !u.senhaConfere(senha)) {
            throw new DadosInvalidosException("E-mail ou senha inválidos.");
        }
        if (!u.isAtivo()) {
            throw new AcessoNegadoException("Usuário inativo. Procure o administrador.");
        }
        return u;
    }

    public Usuario cadastrar(Usuario executor, String perfil, String nome, String email, String matricula,
                             String senha, Integer setorId) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        validar(nome, email, senha);
        if (usuarioDAO.buscarPorEmail(email.trim()) != null) {
            throw new DadosInvalidosException("Já existe um usuário com esse e-mail.");
        }
        Usuario novo = UsuarioFactory.criar(perfil, nome.trim(), email.trim(), matricula, senha);
        novo.setSetor(buscarSetor(setorId));
        usuarioDAO.inserir(novo);
        return novo;
    }

    public List<Usuario> listar() throws PersistenciaException {
        return usuarioDAO.listarTodos();
    }

    public Usuario buscar(int id) throws NegocioException, PersistenciaException {
        Usuario u = usuarioDAO.buscarPorId(id);
        if (u == null) {
            throw new RegistroNaoEncontradoException("Usuário " + id + " não encontrado.");
        }
        return u;
    }

    public void atualizar(Usuario executor, int id, String nome, String email, Integer setorId)
            throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        Usuario u = buscar(id);
        validar(nome, email, u.getSenha());
        Usuario mesmoEmail = usuarioDAO.buscarPorEmail(email.trim());
        if (mesmoEmail != null && mesmoEmail.getId() != id) {
            throw new DadosInvalidosException("Já existe outro usuário com esse e-mail.");
        }
        u.setNome(nome.trim());
        u.setEmail(email.trim());
        u.setSetor(buscarSetor(setorId));
        usuarioDAO.atualizar(u);
    }

    public void alterarAtivo(Usuario executor, int id, boolean ativo) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        if (executor.getId() == id && !ativo) {
            throw new OperacaoNaoPermitidaException("Você não pode inativar o próprio usuário.");
        }
        Usuario u = buscar(id);
        u.setAtivo(ativo);
        usuarioDAO.atualizar(u);
    }

    /** RN08: usuário com histórico de empréstimos não é excluído, apenas inativado. */
    public void excluir(Usuario executor, int id) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        buscar(id);
        if (executor.getId() == id) {
            throw new OperacaoNaoPermitidaException("Você não pode excluir o próprio usuário.");
        }
        if (emprestimoDAO.existePorUsuario(id)) {
            throw new OperacaoNaoPermitidaException("Usuário possui empréstimos registrados. Use a inativação.");
        }
        usuarioDAO.excluir(id);
    }

    private void validar(String nome, String email, String senha) throws DadosInvalidosException {
        exigirTexto(nome, "nome");
        exigirTexto(email, "e-mail");
        if (!email.contains("@")) {
            throw new DadosInvalidosException("E-mail inválido.");
        }
        if (senha == null || senha.length() < 4) {
            throw new DadosInvalidosException("A senha deve ter pelo menos 4 caracteres.");
        }
    }

    private Setor buscarSetor(Integer setorId) throws NegocioException, PersistenciaException {
        if (setorId == null) {
            return null;
        }
        Setor s = setorDAO.buscarPorId(setorId);
        if (s == null) {
            throw new RegistroNaoEncontradoException("Setor " + setorId + " não encontrado.");
        }
        return s;
    }
}
