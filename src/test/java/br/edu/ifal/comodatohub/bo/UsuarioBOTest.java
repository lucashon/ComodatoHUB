package br.edu.ifal.comodatohub.bo;

import br.edu.ifal.comodatohub.BancoTeste;
import br.edu.ifal.comodatohub.dao.EquipamentoDAO;
import br.edu.ifal.comodatohub.dao.UsuarioDAO;
import br.edu.ifal.comodatohub.exception.*;
import br.edu.ifal.comodatohub.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioBOTest {

    private final UsuarioBO bo = new UsuarioBO();
    private Usuario admin;

    @BeforeEach
    void preparar() throws Exception {
        BancoTeste.preparar();
        admin = new Administrador("Admin", "admin@empresa.com", "A1", "admin123");
        new UsuarioDAO().inserir(admin);
    }

    @Test
    void autenticaComSenhaCorreta() throws Exception {
        Usuario u = bo.autenticar("admin@empresa.com", "admin123");
        assertInstanceOf(Administrador.class, u);
    }

    @Test
    void recusaSenhaErrada() {
        assertThrows(DadosInvalidosException.class, () -> bo.autenticar("admin@empresa.com", "errada"));
    }

    @Test
    void usuarioInativoNaoEntra() throws Exception {
        Usuario c = bo.cadastrar(admin, "COLABORADOR", "Carlos", "c@empresa.com", "C1", "1234", null);
        bo.alterarAtivo(admin, c.getId(), false);
        assertThrows(AcessoNegadoException.class, () -> bo.autenticar("c@empresa.com", "1234"));
    }

    @Test
    void naoPermiteEmailDuplicadoNemSenhaCurta() {
        assertThrows(DadosInvalidosException.class,
                () -> bo.cadastrar(admin, "GESTOR", "Outro", "admin@empresa.com", "G1", "1234", null));
        assertThrows(DadosInvalidosException.class,
                () -> bo.cadastrar(admin, "GESTOR", "Gestor", "g@empresa.com", "G1", "12", null));
    }

    @Test
    void rn08NaoExcluiUsuarioComHistorico() throws Exception {
        Usuario c = bo.cadastrar(admin, "COLABORADOR", "Carlos", "c@empresa.com", "C1", "1234", null);
        Equipamento e = new Notebook("PAT-1", "Notebook", "Dell");
        new EquipamentoDAO().inserir(e);
        new EmprestimoBO().solicitar(c, e.getId());
        assertThrows(OperacaoNaoPermitidaException.class, () -> bo.excluir(admin, c.getId()));
    }

    @Test
    void excluiUsuarioSemHistorico() throws Exception {
        Usuario c = bo.cadastrar(admin, "COLABORADOR", "Carlos", "c@empresa.com", "C1", "1234", null);
        bo.excluir(admin, c.getId());
        assertThrows(RegistroNaoEncontradoException.class, () -> bo.buscar(c.getId()));
    }
}
