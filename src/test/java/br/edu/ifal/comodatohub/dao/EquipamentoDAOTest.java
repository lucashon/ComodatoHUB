package br.edu.ifal.comodatohub.dao;

import br.edu.ifal.comodatohub.BancoTeste;
import br.edu.ifal.comodatohub.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquipamentoDAOTest {

    private final EquipamentoDAO dao = new EquipamentoDAO();

    @BeforeEach
    void preparar() throws Exception {
        BancoTeste.preparar();
    }

    @Test
    void crudCompleto() throws Exception {
        Equipamento e = new Projetor("PAT-9", "Projetor Epson", "Epson");
        int id = dao.inserir(e);                                   // INSERT

        Equipamento lido = dao.buscarPorId(id);                    // SELECT
        assertInstanceOf(Projetor.class, lido);
        assertEquals(StatusEquipamento.DISPONIVEL, lido.getStatus());

        lido.setDescricao("Projetor Epson X");
        dao.atualizar(lido);                                       // UPDATE
        assertEquals("Projetor Epson X", dao.buscarPorId(id).getDescricao());

        dao.atualizarStatus(lido, StatusEquipamento.MANUTENCAO);
        assertEquals(1, dao.listarPorStatus(StatusEquipamento.MANUTENCAO).size());
        assertTrue(dao.listarPatrimonios().contains("PAT-9"));

        dao.excluir(id);                                           // DELETE
        assertNull(dao.buscarPorId(id));
        assertTrue(dao.listarTodos().isEmpty());
    }
}
