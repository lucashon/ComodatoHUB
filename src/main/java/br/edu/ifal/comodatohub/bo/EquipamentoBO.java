package br.edu.ifal.comodatohub.bo;

import br.edu.ifal.comodatohub.dao.EmprestimoDAO;
import br.edu.ifal.comodatohub.dao.EquipamentoDAO;
import br.edu.ifal.comodatohub.exception.*;
import br.edu.ifal.comodatohub.factory.EquipamentoFactory;
import br.edu.ifal.comodatohub.vo.Equipamento;
import br.edu.ifal.comodatohub.vo.StatusEquipamento;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class EquipamentoBO extends BaseBO {

    private final EquipamentoDAO equipamentoDAO = new EquipamentoDAO();
    private final EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

    public Equipamento cadastrar(Usuario executor, String categoria, String patrimonio, String descricao, String marca)
            throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        exigirTexto(patrimonio, "patrimônio");
        exigirTexto(descricao, "descrição");
        Set<String> patrimonios = equipamentoDAO.listarPatrimonios();
        if (patrimonios.contains(patrimonio.trim().toUpperCase())) {
            throw new DadosInvalidosException("Já existe um equipamento com o patrimônio " + patrimonio + ".");
        }
        Equipamento e = EquipamentoFactory.criar(categoria, patrimonio.trim(), descricao.trim(), marca);
        equipamentoDAO.inserir(e);
        return e;
    }

    public Equipamento buscar(int id) throws NegocioException, PersistenciaException {
        Equipamento e = equipamentoDAO.buscarPorId(id);
        if (e == null) {
            throw new RegistroNaoEncontradoException("Equipamento " + id + " não encontrado.");
        }
        return e;
    }

    public List<Equipamento> listarTodos() throws PersistenciaException {
        return equipamentoDAO.listarTodos();
    }

    public List<Equipamento> listarDisponiveis() throws PersistenciaException {
        return equipamentoDAO.listarPorStatus(StatusEquipamento.DISPONIVEL);
    }

    /** Map categoria -> equipamentos (TreeMap deixa as categorias em ordem alfabética). */
    public Map<String, List<Equipamento>> agruparPorCategoria() throws PersistenciaException {
        Map<String, List<Equipamento>> grupos = new TreeMap<>();
        for (Equipamento e : equipamentoDAO.listarTodos()) {
            grupos.computeIfAbsent(e.getCategoria(), k -> new ArrayList<>()).add(e);
        }
        return grupos;
    }

    public void atualizar(Usuario executor, int id, String descricao, String marca)
            throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        exigirTexto(descricao, "descrição");
        Equipamento e = buscar(id);
        e.setDescricao(descricao.trim());
        e.setMarca(marca);
        equipamentoDAO.atualizar(e);
    }

    /** RN08: equipamento com histórico não é excluído, apenas baixado. */
    public void excluir(Usuario executor, int id) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        buscar(id);
        if (emprestimoDAO.existePorEquipamento(id)) {
            throw new OperacaoNaoPermitidaException("Equipamento possui empréstimos registrados. Use a baixa.");
        }
        equipamentoDAO.excluir(id);
    }

    public void enviarParaManutencao(Usuario executor, int id) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        Equipamento e = buscar(id);
        if (!e.estaDisponivel()) {
            throw new OperacaoNaoPermitidaException("Só é possível enviar para manutenção um equipamento DISPONIVEL.");
        }
        equipamentoDAO.atualizarStatus(e, StatusEquipamento.MANUTENCAO);
    }

    public void retornarDaManutencao(Usuario executor, int id) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        Equipamento e = buscar(id);
        if (e.getStatus() != StatusEquipamento.MANUTENCAO) {
            throw new OperacaoNaoPermitidaException("O equipamento não está em manutenção.");
        }
        equipamentoDAO.atualizarStatus(e, StatusEquipamento.DISPONIVEL);
    }

    public void darBaixa(Usuario executor, int id) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        Equipamento e = buscar(id);
        if (e.getStatus() != StatusEquipamento.DISPONIVEL && e.getStatus() != StatusEquipamento.MANUTENCAO) {
            throw new OperacaoNaoPermitidaException("Equipamento reservado ou emprestado não pode receber baixa.");
        }
        equipamentoDAO.atualizarStatus(e, StatusEquipamento.BAIXADO);
    }
}
