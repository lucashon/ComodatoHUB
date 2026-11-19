package br.edu.ifal.comodatohub.bo;

import br.edu.ifal.comodatohub.BancoTeste;
import br.edu.ifal.comodatohub.dao.EmprestimoDAO;
import br.edu.ifal.comodatohub.dao.EquipamentoDAO;
import br.edu.ifal.comodatohub.dao.UsuarioDAO;
import br.edu.ifal.comodatohub.exception.*;
import br.edu.ifal.comodatohub.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoBOTest {

    private final EmprestimoBO bo = new EmprestimoBO();
    private final EquipamentoDAO equipamentoDAO = new EquipamentoDAO();
    private Usuario admin;
    private Usuario colaborador;
    private Equipamento notebook;

    @BeforeEach
    void preparar() throws Exception {
        BancoTeste.preparar();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        admin = new Administrador("Admin", "admin@empresa.com", "A1", "1234");
        colaborador = new Colaborador("Carlos", "carlos@empresa.com", "C1", "1234");
        usuarioDAO.inserir(admin);
        usuarioDAO.inserir(colaborador);
        notebook = novoEquipamento(new Notebook("PAT-1", "Notebook Dell", "Dell"));
    }

    private Equipamento novoEquipamento(Equipamento e) throws Exception {
        equipamentoDAO.inserir(e);
        return e;
    }

    private StatusEquipamento statusNoBanco(Equipamento e) throws Exception {
        return equipamentoDAO.buscarPorId(e.getId()).getStatus();
    }

    @Test
    void solicitarReservaOEquipamento() throws Exception {
        Emprestimo e = bo.solicitar(colaborador, notebook.getId());
        assertEquals(StatusEmprestimo.SOLICITADO, e.getStatus());
        assertEquals(StatusEquipamento.RESERVADO, statusNoBanco(notebook));
    }

    @Test
    void rn01NaoPermiteEquipamentoIndisponivel() throws Exception {
        bo.solicitar(colaborador, notebook.getId());
        assertThrows(EquipamentoIndisponivelException.class, () -> bo.solicitar(admin, notebook.getId()));
    }

    @Test
    void rn02ColaboradorTemLimiteDeDois() throws Exception {
        Equipamento cel = novoEquipamento(new Celular("PAT-2", "Celular", "Samsung"));
        Equipamento kit = novoEquipamento(new KitFerramentas("PAT-3", "Kit", "Tramontina"));
        bo.solicitar(colaborador, notebook.getId());
        bo.solicitar(colaborador, cel.getId());
        assertThrows(LimiteEmprestimosExcedidoException.class, () -> bo.solicitar(colaborador, kit.getId()));
    }

    @Test
    void rn03UsuarioComAtrasoNaoSolicita() throws Exception {
        Emprestimo e = bo.solicitar(colaborador, notebook.getId());
        bo.confirmarRetirada(admin, e.getId());
        Emprestimo atrasado = bo.buscar(e.getId());
        atrasado.setDataPrevistaDevolucao(LocalDate.now().minusDays(1));
        new EmprestimoDAO().atualizar(atrasado);

        Equipamento projetor = novoEquipamento(new Projetor("PAT-2", "Projetor", "Epson"));
        assertThrows(UsuarioComPendenciaException.class, () -> bo.solicitar(colaborador, projetor.getId()));
    }

    @Test
    void rn04RetiradaDefinePrazoDaCategoria() throws Exception {
        Emprestimo e = bo.solicitar(colaborador, notebook.getId());
        Emprestimo ativo = bo.confirmarRetirada(admin, e.getId());
        assertEquals(LocalDate.now().plusDays(30), ativo.getDataPrevistaDevolucao());
        assertEquals(StatusEquipamento.EMPRESTADO, statusNoBanco(notebook));
    }

    @Test
    void rn05RenovaApenasUmaVez() throws Exception {
        Emprestimo e = bo.solicitar(colaborador, notebook.getId());
        bo.confirmarRetirada(admin, e.getId());
        Emprestimo renovado = bo.renovar(colaborador, e.getId());
        assertEquals(LocalDate.now().plusDays(60), renovado.getDataPrevistaDevolucao());
        assertThrows(RenovacaoNaoPermitidaException.class, () -> bo.renovar(colaborador, e.getId()));
    }

    @Test
    void rn06AvariaExigeDescricaoEVaiParaManutencao() throws Exception {
        Emprestimo e = bo.solicitar(colaborador, notebook.getId());
        bo.confirmarRetirada(admin, e.getId());
        assertThrows(DadosInvalidosException.class,
                () -> bo.devolver(admin, e.getId(), EstadoConservacao.AVARIADO, " "));

        bo.devolver(admin, e.getId(), EstadoConservacao.AVARIADO, "Tela quebrada");
        assertEquals(StatusEquipamento.MANUTENCAO, statusNoBanco(notebook));
        assertEquals(StatusEmprestimo.DEVOLVIDO, bo.buscar(e.getId()).getStatus());
    }

    @Test
    void devolucaoEmBomEstadoLiberaEquipamento() throws Exception {
        Emprestimo e = bo.solicitar(colaborador, notebook.getId());
        bo.confirmarRetirada(admin, e.getId());
        bo.devolver(admin, e.getId(), EstadoConservacao.BOM, null);
        assertEquals(StatusEquipamento.DISPONIVEL, statusNoBanco(notebook));
    }

    @Test
    void rn07ColaboradorNaoConfirmaRetirada() throws Exception {
        Emprestimo e = bo.solicitar(colaborador, notebook.getId());
        assertThrows(AcessoNegadoException.class, () -> bo.confirmarRetirada(colaborador, e.getId()));
    }

    @Test
    void cancelarLiberaEquipamento() throws Exception {
        Emprestimo e = bo.solicitar(colaborador, notebook.getId());
        bo.cancelar(colaborador, e.getId());
        assertEquals(StatusEmprestimo.CANCELADO, bo.buscar(e.getId()).getStatus());
        assertEquals(StatusEquipamento.DISPONIVEL, statusNoBanco(notebook));
    }

    @Test
    void emprestimoInexistenteLancaExcecao() {
        assertThrows(RegistroNaoEncontradoException.class, () -> bo.buscar(999));
    }
}
