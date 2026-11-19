package br.edu.ifal.comodatohub.bo;

import br.edu.ifal.comodatohub.dao.EmprestimoDAO;
import br.edu.ifal.comodatohub.dao.EquipamentoDAO;
import br.edu.ifal.comodatohub.exception.*;
import br.edu.ifal.comodatohub.vo.*;

import java.util.List;
import java.util.Map;

/** Núcleo do sistema: aplica as regras RN01 a RN06 sobre os empréstimos. */
public class EmprestimoBO extends BaseBO {

    private final EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    private final EquipamentoDAO equipamentoDAO = new EquipamentoDAO();

    public Emprestimo solicitar(Usuario solicitante, int equipamentoId) throws NegocioException, PersistenciaException {
        Equipamento equipamento = buscarEquipamento(equipamentoId);
        List<Emprestimo> emAberto = emprestimoDAO.listarEmAbertoPorUsuario(solicitante.getId());

        verificarPendencias(emAberto);                                       // RN03
        if (emAberto.size() >= solicitante.getLimiteEmprestimos()) {         // RN02 (polimorfismo)
            throw new LimiteEmprestimosExcedidoException("Limite de " + solicitante.getLimiteEmprestimos()
                    + " empréstimo(s) do perfil " + solicitante.getPerfil() + " atingido.");
        }
        if (!equipamento.estaDisponivel()) {                                 // RN01
            throw new EquipamentoIndisponivelException("O equipamento está " + equipamento.getStatus() + ".");
        }

        Emprestimo emprestimo = new Emprestimo(solicitante, equipamento);
        equipamento.reservar();
        equipamentoDAO.atualizarStatus(equipamento, StatusEquipamento.RESERVADO);
        emprestimoDAO.inserir(emprestimo);
        return emprestimo;
    }

    public void cancelar(Usuario executor, int emprestimoId) throws NegocioException, PersistenciaException {
        Emprestimo e = buscar(emprestimoId);
        if (e.getUsuario().getId() != executor.getId() && !executor.podeGerenciarEquipamentos()) {
            throw new AcessoNegadoException("Você só pode cancelar as suas próprias solicitações.");
        }
        if (e.getStatus() != StatusEmprestimo.SOLICITADO) {
            throw new OperacaoNaoPermitidaException("Só é possível cancelar empréstimos com status SOLICITADO.");
        }
        e.cancelar();
        emprestimoDAO.atualizar(e);
        equipamentoDAO.atualizarStatus(e.getEquipamento(), StatusEquipamento.DISPONIVEL);
    }

    /** Administrador confirma a entrega; o prazo começa a contar (RN04). */
    public Emprestimo confirmarRetirada(Usuario executor, int emprestimoId) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        Emprestimo e = buscar(emprestimoId);
        if (e.getStatus() != StatusEmprestimo.SOLICITADO) {
            throw new OperacaoNaoPermitidaException("Este empréstimo não está aguardando retirada.");
        }
        e.iniciar();
        emprestimoDAO.atualizar(e);
        equipamentoDAO.atualizarStatus(e.getEquipamento(), StatusEquipamento.EMPRESTADO);
        return e;
    }

    /** RN05: uma única renovação, só se não houver atraso. */
    public Emprestimo renovar(Usuario executor, int emprestimoId) throws NegocioException, PersistenciaException {
        Emprestimo e = buscar(emprestimoId);
        if (e.getUsuario().getId() != executor.getId()) {
            throw new AcessoNegadoException("Você só pode renovar os seus próprios empréstimos.");
        }
        if (e.getStatus() != StatusEmprestimo.ATIVO) {
            throw new RenovacaoNaoPermitidaException("Só empréstimos ATIVOS podem ser renovados.");
        }
        if (e.getRenovacoes() >= 1) {
            throw new RenovacaoNaoPermitidaException("Este empréstimo já foi renovado uma vez.");
        }
        verificarPendencias(emprestimoDAO.listarEmAbertoPorUsuario(executor.getId()));
        e.renovar();
        emprestimoDAO.atualizar(e);
        return e;
    }

    /** RN06: avaria exige descrição e manda o equipamento para manutenção. */
    public Emprestimo devolver(Usuario executor, int emprestimoId, EstadoConservacao estado, String descricao)
            throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        if (estado == null) {
            throw new DadosInvalidosException("Informe o estado de conservação.");
        }
        if (estado == EstadoConservacao.AVARIADO && (descricao == null || descricao.isBlank())) {
            throw new DadosInvalidosException("Descreva a avaria encontrada no equipamento.");
        }
        Emprestimo e = buscar(emprestimoId);
        if (e.getStatus() != StatusEmprestimo.ATIVO) {
            throw new OperacaoNaoPermitidaException("Só empréstimos ATIVOS podem ser devolvidos.");
        }
        e.registrarDevolucao(estado, descricao);
        emprestimoDAO.atualizar(e);
        equipamentoDAO.atualizarStatus(e.getEquipamento(), e.getEquipamento().getStatus());
        return e;
    }

    // ----------------- consultas -----------------

    public Emprestimo buscar(int id) throws NegocioException, PersistenciaException {
        Emprestimo e = emprestimoDAO.buscarPorId(id);
        if (e == null) {
            throw new RegistroNaoEncontradoException("Empréstimo " + id + " não encontrado.");
        }
        return e;
    }

    public List<Emprestimo> listarTodos(Usuario executor) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        return emprestimoDAO.listarTodos();
    }

    public List<Emprestimo> listarDoUsuario(Usuario usuario) throws PersistenciaException {
        return emprestimoDAO.listarPorUsuario(usuario.getId());
    }

    public List<Emprestimo> listarPorStatus(StatusEmprestimo status) throws PersistenciaException {
        return emprestimoDAO.listarPorStatus(status);
    }

    public List<Emprestimo> listarAtrasados() throws PersistenciaException {
        return emprestimoDAO.listarAtrasados();
    }

    public List<Emprestimo> historicoDoEquipamento(int equipamentoId) throws NegocioException, PersistenciaException {
        buscarEquipamento(equipamentoId);
        return emprestimoDAO.listarPorEquipamento(equipamentoId);
    }

    public Map<String, Integer> rankingPorCategoria(Usuario executor) throws NegocioException, PersistenciaException {
        exigirAdministrador(executor);
        return emprestimoDAO.contarPorCategoria();
    }

    public List<Emprestimo> listarDoSetor(Usuario executor) throws NegocioException, PersistenciaException {
        if (!executor.podeAcompanharSetor()) {
            throw new AcessoNegadoException("Apenas gestores e administradores acompanham o setor.");
        }
        if (executor.getSetor() == null) {
            throw new DadosInvalidosException("Seu usuário não está vinculado a um setor.");
        }
        return emprestimoDAO.listarEmAbertoPorSetor(executor.getSetor().getId());
    }

    // ----------------- auxiliares -----------------

    private void verificarPendencias(List<Emprestimo> emAberto) throws UsuarioComPendenciaException {
        for (Emprestimo e : emAberto) {
            if (e.estaAtrasado()) {
                throw new UsuarioComPendenciaException("Há um empréstimo em atraso (" + e.getEquipamento().getDescricao()
                        + "). Devolva-o antes de continuar.");
            }
        }
    }

    private Equipamento buscarEquipamento(int id) throws NegocioException, PersistenciaException {
        Equipamento e = equipamentoDAO.buscarPorId(id);
        if (e == null) {
            throw new RegistroNaoEncontradoException("Equipamento " + id + " não encontrado.");
        }
        return e;
    }
}
