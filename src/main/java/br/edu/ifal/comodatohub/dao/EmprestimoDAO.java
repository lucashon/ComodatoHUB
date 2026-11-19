package br.edu.ifal.comodatohub.dao;

import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.util.ConexaoBD;
import br.edu.ifal.comodatohub.vo.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmprestimoDAO implements GenericDAO<Emprestimo> {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EquipamentoDAO equipamentoDAO = new EquipamentoDAO();

    private Connection con() throws PersistenciaException {
        return ConexaoBD.getInstancia().getConexao();
    }

    @Override
    public int inserir(Emprestimo e) throws PersistenciaException {
        String sql = "INSERT INTO emprestimo (usuario_id, equipamento_id, data_solicitacao, data_retirada, data_prevista,"
                + " data_devolucao, renovacoes, status, estado_devolucao, descricao_ocorrencia, data_ocorrencia)"
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, e);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                e.setId(rs.getInt(1));
                return e.getId();
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("Erro ao inserir empréstimo.", ex);
        }
    }

    @Override
    public Emprestimo buscarPorId(int id) throws PersistenciaException {
        List<Emprestimo> lista = listar("SELECT * FROM emprestimo WHERE id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Emprestimo> listarTodos() throws PersistenciaException {
        return listar("SELECT * FROM emprestimo ORDER BY id DESC");
    }

    public List<Emprestimo> listarPorUsuario(int usuarioId) throws PersistenciaException {
        return listar("SELECT * FROM emprestimo WHERE usuario_id = ? ORDER BY id DESC", usuarioId);
    }

    /** Empréstimos SOLICITADOS ou ATIVOS de um usuário (usado nas regras RN02 e RN03). */
    public List<Emprestimo> listarEmAbertoPorUsuario(int usuarioId) throws PersistenciaException {
        return listar("SELECT * FROM emprestimo WHERE usuario_id = ? AND status IN ('SOLICITADO','ATIVO')", usuarioId);
    }

    public List<Emprestimo> listarPorEquipamento(int equipamentoId) throws PersistenciaException {
        return listar("SELECT * FROM emprestimo WHERE equipamento_id = ? ORDER BY id DESC", equipamentoId);
    }

    public List<Emprestimo> listarPorStatus(StatusEmprestimo status) throws PersistenciaException {
        return listar("SELECT * FROM emprestimo WHERE status = ? ORDER BY id", status.name());
    }

    public List<Emprestimo> listarAtrasados() throws PersistenciaException {
        return listar("SELECT * FROM emprestimo WHERE status = 'ATIVO' AND data_prevista < ? ORDER BY data_prevista",
                LocalDate.now().toString());
    }

    public List<Emprestimo> listarEmAbertoPorSetor(int setorId) throws PersistenciaException {
        return listar("SELECT e.* FROM emprestimo e JOIN usuario u ON u.id = e.usuario_id"
                + " WHERE u.setor_id = ? AND e.status IN ('SOLICITADO','ATIVO') ORDER BY e.id", setorId);
    }

    /** Map categoria -> quantidade de empréstimos (relatório F19). */
    public Map<String, Integer> contarPorCategoria() throws PersistenciaException {
        String sql = "SELECT q.categoria, COUNT(*) AS total FROM emprestimo e"
                + " JOIN equipamento q ON q.id = e.equipamento_id"
                + " WHERE e.status IN ('ATIVO','DEVOLVIDO') GROUP BY q.categoria ORDER BY total DESC";
        Map<String, Integer> mapa = new LinkedHashMap<>();
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mapa.put(rs.getString("categoria"), rs.getInt("total"));
            }
            return mapa;
        } catch (SQLException ex) {
            throw new PersistenciaException("Erro ao gerar relatório.", ex);
        }
    }

    public boolean existePorUsuario(int usuarioId) throws PersistenciaException {
        return existe("SELECT 1 FROM emprestimo WHERE usuario_id = ? LIMIT 1", usuarioId);
    }

    public boolean existePorEquipamento(int equipamentoId) throws PersistenciaException {
        return existe("SELECT 1 FROM emprestimo WHERE equipamento_id = ? LIMIT 1", equipamentoId);
    }

    @Override
    public void atualizar(Emprestimo e) throws PersistenciaException {
        String sql = "UPDATE emprestimo SET usuario_id=?, equipamento_id=?, data_solicitacao=?, data_retirada=?,"
                + " data_prevista=?, data_devolucao=?, renovacoes=?, status=?, estado_devolucao=?,"
                + " descricao_ocorrencia=?, data_ocorrencia=? WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            preencher(ps, e);
            ps.setInt(12, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PersistenciaException("Erro ao atualizar empréstimo.", ex);
        }
    }

    @Override
    public void excluir(int id) throws PersistenciaException {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM emprestimo WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PersistenciaException("Erro ao excluir empréstimo.", ex);
        }
    }

    // ----------------- auxiliares -----------------

    private static String texto(Object o) {
        return o == null ? null : o.toString();
    }

    private static LocalDate data(String s) {
        return s == null ? null : LocalDate.parse(s);
    }

    private void preencher(PreparedStatement ps, Emprestimo e) throws SQLException {
        ps.setInt(1, e.getUsuario().getId());
        ps.setInt(2, e.getEquipamento().getId());
        ps.setString(3, texto(e.getDataSolicitacao()));
        ps.setString(4, texto(e.getDataRetirada()));
        ps.setString(5, texto(e.getDataPrevistaDevolucao()));
        ps.setString(6, texto(e.getDataDevolucao()));
        ps.setInt(7, e.getRenovacoes());
        ps.setString(8, e.getStatus().name());
        Ocorrencia oc = e.getOcorrencia();
        ps.setString(9, oc == null ? null : oc.getEstado().name());
        ps.setString(10, oc == null ? null : oc.getDescricao());
        ps.setString(11, oc == null ? null : oc.getDataRegistro().toString());
    }

    private boolean existe(String sql, int id) throws PersistenciaException {
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("Erro ao consultar empréstimos.", ex);
        }
    }

    private List<Emprestimo> listar(String sql, Object... params) throws PersistenciaException {
        List<Emprestimo> lista = new ArrayList<>();
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
            throw new PersistenciaException("Erro ao consultar empréstimos.", ex);
        }
    }

    private Emprestimo montar(ResultSet rs) throws SQLException, PersistenciaException {
        Usuario u = usuarioDAO.buscarPorId(rs.getInt("usuario_id"));
        Equipamento eq = equipamentoDAO.buscarPorId(rs.getInt("equipamento_id"));
        Emprestimo e = new Emprestimo(u, eq);
        e.setId(rs.getInt("id"));
        e.setDataSolicitacao(data(rs.getString("data_solicitacao")));
        e.setDataRetirada(data(rs.getString("data_retirada")));
        e.setDataPrevistaDevolucao(data(rs.getString("data_prevista")));
        e.setDataDevolucao(data(rs.getString("data_devolucao")));
        e.setRenovacoes(rs.getInt("renovacoes"));
        e.setStatus(StatusEmprestimo.valueOf(rs.getString("status")));
        String estado = rs.getString("estado_devolucao");
        if (estado != null) {
            e.setOcorrencia(new Ocorrencia(EstadoConservacao.valueOf(estado),
                    rs.getString("descricao_ocorrencia"), LocalDateTime.parse(rs.getString("data_ocorrencia"))));
        }
        return e;
    }
}
