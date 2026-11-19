package br.edu.ifal.comodatohub.dao;

import br.edu.ifal.comodatohub.BancoTeste;
import br.edu.ifal.comodatohub.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoDAOTest {

    private final EmprestimoDAO dao = new EmprestimoDAO();
    private Usuario usuario;
    private Equipamento equipamento;

    @BeforeEach
    void preparar() throws Exception {
        BancoTeste.preparar();
        usuario = new Colaborador("Ana", "ana@empresa.com", "C1", "1234");
        new UsuarioDAO().inserir(usuario);
        equipamento = new Notebook("PAT-1", "Notebook Dell", "Dell");
        new EquipamentoDAO().inserir(equipamento);
    }

    @Test
    void crudCompletoComOcorrencia() throws Exception {
        Emprestimo e = new Emprestimo(usuario, equipamento);
        int id = dao.inserir(e);                                     // INSERT
        assertEquals(StatusEmprestimo.SOLICITADO, dao.buscarPorId(id).getStatus()); // SELECT
        assertEquals(1, dao.listarEmAbertoPorUsuario(usuario.getId()).size());

        e.iniciar();
        e.registrarDevolucao(EstadoConservacao.AVARIADO, "Teclado com defeito");
        dao.atualizar(e);                                            // UPDATE
        Emprestimo lido = dao.buscarPorId(id);
        assertEquals(StatusEmprestimo.DEVOLVIDO, lido.getStatus());
        assertEquals(EstadoConservacao.AVARIADO, lido.getOcorrencia().getEstado());
        assertEquals("Teclado com defeito", lido.getOcorrencia().getDescricao());
        assertEquals(1, dao.contarPorCategoria().get("NOTEBOOK"));
        assertTrue(dao.existePorEquipamento(equipamento.getId()));

        dao.excluir(id);                                             // DELETE
        assertNull(dao.buscarPorId(id));
    }

    @Test
    void listarAtrasadosTrazApenasVencidos() throws Exception {
        Emprestimo e = new Emprestimo(usuario, equipamento);
        e.iniciar();
        e.setDataPrevistaDevolucao(LocalDate.now().minusDays(2));
        dao.inserir(e);

        assertEquals(1, dao.listarAtrasados().size());
        assertTrue(dao.listarAtrasados().get(0).estaAtrasado());
    }
}
