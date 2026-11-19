package br.edu.ifal.comodatohub.dao;

import br.edu.ifal.comodatohub.exception.DadosInvalidosException;
import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.factory.EquipamentoFactory;
import br.edu.ifal.comodatohub.util.ConexaoBD;
import br.edu.ifal.comodatohub.vo.Equipamento;
import br.edu.ifal.comodatohub.vo.StatusEquipamento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EquipamentoDAO implements GenericDAO<Equipamento> {

    private Connection con() throws PersistenciaException {
        return ConexaoBD.getInstancia().getConexao();
    }

    @Override
    public int inserir(Equipamento e) throws PersistenciaException {
        String sql = "INSERT INTO equipamento (patrimonio, descricao, marca, categoria, status, data_aquisicao) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, e);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                e.setId(rs.getInt(1));
                return e.getId();
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("Erro ao inserir equipamento.", ex);
        }
    }

    @Override
    public Equipamento buscarPorId(int id) throws PersistenciaException {
        List<Equipamento> lista = listar("SELECT * FROM equipamento WHERE id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Equipamento> listarTodos() throws PersistenciaException {
        return listar("SELECT * FROM equipamento ORDER BY categoria, descricao");
    }

    public List<Equipamento> listarPorStatus(StatusEquipamento status) throws PersistenciaException {
        return listar("SELECT * FROM equipamento WHERE status = ? ORDER BY categoria, descricao", status.name());
    }

    /** Set: patrimônios não se repetem, ideal para checar duplicidade. */
    public Set<String> listarPatrimonios() throws PersistenciaException {
        Set<String> patrimonios = new HashSet<>();
        try (PreparedStatement ps = con().prepareStatement("SELECT patrimonio FROM equipamento");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                patrimonios.add(rs.getString(1).toUpperCase());
            }
            return patrimonios;
        } catch (SQLException ex) {
            throw new PersistenciaException("Erro ao listar patrimônios.", ex);
        }
    }

    @Override
    public void atualizar(Equipamento e) throws PersistenciaException {
        String sql = "UPDATE equipamento SET patrimonio=?, descricao=?, marca=?, categoria=?, status=?, data_aquisicao=? WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            preencher(ps, e);
            ps.setInt(7, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PersistenciaException("Erro ao atualizar equipamento.", ex);
        }
    }

    public void atualizarStatus(Equipamento e, StatusEquipamento status) throws PersistenciaException {
        try (PreparedStatement ps = con().prepareStatement("UPDATE equipamento SET status = ? WHERE id = ?")) {
            ps.setString(1, status.name());
            ps.setInt(2, e.getId());
            ps.executeUpdate();
            e.setStatus(status);
        } catch (SQLException ex) {
            throw new PersistenciaException("Erro ao atualizar status do equipamento.", ex);
        }
    }

    @Override
    public void excluir(int id) throws PersistenciaException {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM equipamento WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PersistenciaException("Erro ao excluir equipamento.", ex);
        }
    }

    // ----------------- auxiliares -----------------

    private void preencher(PreparedStatement ps, Equipamento e) throws SQLException {
        ps.setString(1, e.getPatrimonio());
        ps.setString(2, e.getDescricao());
        ps.setString(3, e.getMarca());
        ps.setString(4, e.getCategoria()); // polimorfismo
        ps.setString(5, e.getStatus().name());
        ps.setString(6, e.getDataAquisicao() == null ? null : e.getDataAquisicao().toString());
    }

    private List<Equipamento> listar(String sql, Object... params) throws PersistenciaException {
        List<Equipamento> lista = new ArrayList<>();
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
        } catch (SQLException ex) {
            throw new PersistenciaException("Erro ao consultar equipamentos.", ex);
        }
    }

    private Equipamento montar(ResultSet rs) throws SQLException, PersistenciaException {
        try {
            // Factory decide qual subclasse instanciar (Notebook, Projetor...)
            Equipamento e = EquipamentoFactory.criar(rs.getString("categoria"),
                    rs.getString("patrimonio"), rs.getString("descricao"), rs.getString("marca"));
            e.setId(rs.getInt("id"));
            e.setStatus(StatusEquipamento.valueOf(rs.getString("status")));
            String data = rs.getString("data_aquisicao");
            e.setDataAquisicao(data == null ? null : LocalDate.parse(data));
            return e;
        } catch (DadosInvalidosException ex) {
            throw new PersistenciaException("Categoria inválida gravada no banco.", ex);
        }
    }
}
