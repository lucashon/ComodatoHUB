package br.edu.ifal.comodatohub.dao;

import br.edu.ifal.comodatohub.BancoTeste;
import br.edu.ifal.comodatohub.vo.Setor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetorDAOTest {

    private final SetorDAO dao = new SetorDAO();

    @BeforeEach
    void preparar() throws Exception {
        BancoTeste.preparar();
    }

    @Test
    void crudCompleto() throws Exception {
        Setor s = new Setor("Financeiro");
        int id = dao.inserir(s);                                  // INSERT
        assertTrue(id > 0);

        assertEquals("Financeiro", dao.buscarPorId(id).getNome()); // SELECT

        s.setNome("Financeiro e Contábil");
        dao.atualizar(s);                                          // UPDATE
        assertEquals("Financeiro e Contábil", dao.buscarPorId(id).getNome());
        assertEquals(1, dao.listarTodos().size());

        dao.excluir(id);                                           // DELETE
        assertNull(dao.buscarPorId(id));
    }
}
