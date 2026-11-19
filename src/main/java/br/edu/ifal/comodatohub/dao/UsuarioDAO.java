package br.edu.ifal.comodatohub.dao;

import br.edu.ifal.comodatohub.exception.DadosInvalidosException;
import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.factory.UsuarioFactory;
import br.edu.ifal.comodatohub.util.ConexaoBD;
import br.edu.ifal.comodatohub.vo.Setor;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements GenericDAO<Usuario> {

    private static final String SELECT =
            "SELECT u.*, s.nome AS setor_nome FROM usuario u LEFT JOIN setor s ON s.id = u.setor_id ";

    private Connection con() throws PersistenciaException {
        return ConexaoBD.getInstancia().getConexao();
    }

    @Override
    public int inserir(Usuario u) throws PersistenciaException {
        String sql = "INSERT INTO usuario (nome, email, matricula, senha, perfil, ativo, setor_id) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, u);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                u.setId(rs.getInt(1));
                return u.getId();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao inserir usuário.", e);
        }
    }

    @Override
    public Usuario buscarPorId(int id) throws PersistenciaException {
        return buscarUm(SELECT + "WHERE u.id = ?", id);
    }

    public Usuario buscarPorEmail(String email) throws PersistenciaException {
        return buscarUm(SELECT + "WHERE lower(u.email) = lower(?)", email);
    }

    @Override
    public List<Usuario> listarTodos() throws PersistenciaException {
        return listar(SELECT + "ORDER BY u.nome");
    }

    public List<Usuario> listarPorSetor(int setorId) throws PersistenciaException {
        return listar(SELECT + "WHERE u.setor_id = ? ORDER BY u.nome", setorId);
    }

    @Override
    public void atualizar(Usuario u) throws PersistenciaException {
        String sql = "UPDATE usuario SET nome=?, email=?, matricula=?, senha=?, perfil=?, ativo=?, setor_id=? WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            preencher(ps, u);
            ps.setInt(8, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar usuário.", e);
        }
    }

    @Override
    public void excluir(int id) throws PersistenciaException {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM usuario WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao excluir usuário.", e);
        }
    }

    // ----------------- auxiliares -----------------

    private void preencher(PreparedStatement ps, Usuario u) throws SQLException {
        ps.setString(1, u.getNome());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getMatriculaFuncional());
        ps.setString(4, u.getSenha());
        ps.setString(5, u.getPerfil()); // polimorfismo: cada subclasse informa seu perfil
        ps.setInt(6, u.isAtivo() ? 1 : 0);
        if (u.getSetor() == null) {
            ps.setNull(7, Types.INTEGER);
        } else {
            ps.setInt(7, u.getSetor().getId());
        }
    }

    private Usuario buscarUm(String sql, Object param) throws PersistenciaException {
        List<Usuario> lista = listar(sql, param);
        return lista.isEmpty() ? null : lista.get(0);
    }

    private List<Usuario> listar(String sql, Object... params) throws PersistenciaException {
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao consultar usuários.", e);
        }
    }

    private Usuario montar(ResultSet rs) throws SQLException, PersistenciaException {
        try {
            // Factory decide qual subclasse instanciar
            Usuario u = UsuarioFactory.criar(rs.getString("perfil"), rs.getString("nome"),
                    rs.getString("email"), rs.getString("matricula"), rs.getString("senha"));
            u.setId(rs.getInt("id"));
            u.setAtivo(rs.getInt("ativo") == 1);
            int setorId = rs.getInt("setor_id");
            if (!rs.wasNull()) {
                u.setSetor(new Setor(setorId, rs.getString("setor_nome")));
            }
            return u;
        } catch (DadosInvalidosException e) {
            throw new PersistenciaException("Perfil inválido gravado no banco.", e);
        }
    }
}
