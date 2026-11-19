package br.edu.ifal.comodatohub.dao;

import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.util.ConexaoBD;
import br.edu.ifal.comodatohub.vo.Setor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SetorDAO implements GenericDAO<Setor> {

    private Connection con() throws PersistenciaException {
        return ConexaoBD.getInstancia().getConexao();
    }

    @Override
    public int inserir(Setor s) throws PersistenciaException {
        String sql = "INSERT INTO setor (nome) VALUES (?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getNome());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                s.setId(rs.getInt(1));
                return s.getId();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao inserir setor.", e);
        }
    }

    @Override
    public Setor buscarPorId(int id) throws PersistenciaException {
        try (PreparedStatement ps = con().prepareStatement("SELECT * FROM setor WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Setor(rs.getInt("id"), rs.getString("nome")) : null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar setor.", e);
        }
    }

    @Override
    public List<Setor> listarTodos() throws PersistenciaException {
        List<Setor> lista = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement("SELECT * FROM setor ORDER BY nome");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Setor(rs.getInt("id"), rs.getString("nome")));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar setores.", e);
        }
    }

    @Override
    public void atualizar(Setor s) throws PersistenciaException {
        try (PreparedStatement ps = con().prepareStatement("UPDATE setor SET nome = ? WHERE id = ?")) {
            ps.setString(1, s.getNome());
            ps.setInt(2, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar setor.", e);
        }
    }

    @Override
    public void excluir(int id) throws PersistenciaException {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM setor WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao excluir setor.", e);
        }
    }
}
