package br.edu.ifal.comodatohub.bo;

import br.edu.ifal.comodatohub.dao.SetorDAO;
import br.edu.ifal.comodatohub.dao.UsuarioDAO;
import br.edu.ifal.comodatohub.exception.*;
import br.edu.ifal.comodatohub.vo.Setor;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.util.List;

public class SetorBO extends BaseBO {

    private final SetorDAO setorDAO = new SetorDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Setor cadastrar(Usuario executor, String nome) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        exigirTexto(nome, "nome");
        for (Setor s : setorDAO.listarTodos()) {
            if (s.getNome().equalsIgnoreCase(nome.trim())) {
                throw new DadosInvalidosException("Já existe um setor com esse nome.");
            }
        }
        Setor setor = new Setor(nome.trim());
        setorDAO.inserir(setor);
        return setor;
    }

    public List<Setor> listar() throws PersistenciaException {
        return setorDAO.listarTodos();
    }

    public Setor buscar(int id) throws NegocioException, PersistenciaException {
        Setor s = setorDAO.buscarPorId(id);
        if (s == null) {
            throw new RegistroNaoEncontradoException("Setor " + id + " não encontrado.");
        }
        return s;
    }

    /** Carrega o setor junto com os usuários que ele agrega. */
    public Setor buscarComUsuarios(int id) throws NegocioException, PersistenciaException {
        Setor s = buscar(id);
        for (Usuario u : usuarioDAO.listarPorSetor(id)) {
            s.adicionarUsuario(u);
        }
        return s;
    }

    public void renomear(Usuario executor, int id, String novoNome) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        exigirTexto(novoNome, "nome");
        Setor s = buscar(id);
        s.setNome(novoNome.trim());
        setorDAO.atualizar(s);
    }

    /** Agregação: ao excluir o setor, os usuários continuam existindo (ficam sem setor). */
    public void excluir(Usuario executor, int id) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        buscar(id);
        setorDAO.excluir(id);
    }
}
