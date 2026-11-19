package br.edu.ifal.comodatohub.dao;

import br.edu.ifal.comodatohub.BancoTeste;
import br.edu.ifal.comodatohub.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioDAOTest {

    private final UsuarioDAO dao = new UsuarioDAO();
    private final SetorDAO setorDAO = new SetorDAO();

    @BeforeEach
    void preparar() throws Exception {
        BancoTeste.preparar();
    }

    @Test
    void crudCompletoERecriaSubclasseCorreta() throws Exception {
        Setor setor = new Setor("Comercial");
        setorDAO.inserir(setor);
        Usuario u = new Gestor("Maria", "maria@empresa.com", "G10", "1234");
        u.setSetor(setor);

        int id = dao.inserir(u);                                     // INSERT
        Usuario lido = dao.buscarPorId(id);                          // SELECT
        assertInstanceOf(Gestor.class, lido);                        // Factory recriou a subclasse
        assertEquals("Comercial", lido.getSetor().getNome());
        assertNotNull(dao.buscarPorEmail("MARIA@empresa.com"));
        assertEquals(1, dao.listarPorSetor(setor.getId()).size());

        lido.setNome("Maria Silva");
        lido.setAtivo(false);
        dao.atualizar(lido);                                         // UPDATE
        Usuario atualizado = dao.buscarPorId(id);
        assertEquals("Maria Silva", atualizado.getNome());
        assertFalse(atualizado.isAtivo());

        dao.excluir(id);                                             // DELETE
        assertNull(dao.buscarPorId(id));
    }

    @Test
    void excluirSetorDeixaUsuarioSemSetor() throws Exception {
        Setor setor = new Setor("RH");
        setorDAO.inserir(setor);
        Usuario u = new Colaborador("João", "joao@empresa.com", "C10", "1234");
        u.setSetor(setor);
        dao.inserir(u);

        setorDAO.excluir(setor.getId());

        assertNull(dao.buscarPorId(u.getId()).getSetor()); // agregação: o usuário continua existindo
    }
}
