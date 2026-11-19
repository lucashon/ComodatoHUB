package br.edu.ifal.comodatohub.bo;

import br.edu.ifal.comodatohub.BancoTeste;
import br.edu.ifal.comodatohub.dao.UsuarioDAO;
import br.edu.ifal.comodatohub.exception.*;
import br.edu.ifal.comodatohub.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetorBOTest {

    private final SetorBO bo = new SetorBO();
    private Usuario admin;

    @BeforeEach
    void preparar() throws Exception {
        BancoTeste.preparar();
        admin = new Administrador("Admin", "admin@empresa.com", "A1", "1234");
        new UsuarioDAO().inserir(admin);
    }

    @Test
    void setorAgregaUsuarios() throws Exception {
        Setor s = bo.cadastrar(admin, "Comercial");
        new UsuarioBO().cadastrar(admin, "COLABORADOR", "Carlos", "c@empresa.com", "C1", "1234", s.getId());
        assertEquals(1, bo.buscarComUsuarios(s.getId()).getUsuarios().size());
    }

    @Test
    void naoPermiteNomeRepetido() throws Exception {
        bo.cadastrar(admin, "Comercial");
        assertThrows(DadosInvalidosException.class, () -> bo.cadastrar(admin, "comercial"));
    }
}
