package br.edu.ifal.comodatohub.bo;

import br.edu.ifal.comodatohub.BancoTeste;
import br.edu.ifal.comodatohub.dao.UsuarioDAO;
import br.edu.ifal.comodatohub.exception.*;
import br.edu.ifal.comodatohub.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquipamentoBOTest {

    private final EquipamentoBO bo = new EquipamentoBO();
    private Usuario admin;
    private Usuario colaborador;

    @BeforeEach
    void preparar() throws Exception {
        BancoTeste.preparar();
        admin = new Administrador("Admin", "admin@empresa.com", "A1", "1234");
        colaborador = new Colaborador("Carlos", "carlos@empresa.com", "C1", "1234");
        new UsuarioDAO().inserir(admin);
        new UsuarioDAO().inserir(colaborador);
    }

    @Test
    void cadastraUsandoAFactory() throws Exception {
        Equipamento e = bo.cadastrar(admin, "CELULAR", "PAT-1", "Celular Samsung", "Samsung");
        assertInstanceOf(Celular.class, e);
        assertEquals(1, bo.listarDisponiveis().size());
        assertTrue(bo.agruparPorCategoria().containsKey("CELULAR"));
    }

    @Test
    void naoPermitePatrimonioDuplicado() throws Exception {
        bo.cadastrar(admin, "NOTEBOOK", "PAT-1", "Notebook", "Dell");
        assertThrows(DadosInvalidosException.class, () -> bo.cadastrar(admin, "PROJETOR", "pat-1", "Projetor", "Epson"));
    }

    @Test
    void naoPermiteCategoriaInvalida() {
        assertThrows(DadosInvalidosException.class, () -> bo.cadastrar(admin, "TABLET", "PAT-1", "Tablet", "Apple"));
    }

    @Test
    void rn07ColaboradorNaoCadastra() {
        assertThrows(AcessoNegadoException.class, () -> bo.cadastrar(colaborador, "NOTEBOOK", "PAT-1", "Note", "Dell"));
    }

    @Test
    void rn08NaoExcluiEquipamentoComHistorico() throws Exception {
        Equipamento e = bo.cadastrar(admin, "NOTEBOOK", "PAT-1", "Notebook", "Dell");
        new EmprestimoBO().solicitar(colaborador, e.getId());
        assertThrows(OperacaoNaoPermitidaException.class, () -> bo.excluir(admin, e.getId()));
    }

    @Test
    void cicloDeManutencao() throws Exception {
        Equipamento e = bo.cadastrar(admin, "PROJETOR", "PAT-1", "Projetor", "Epson");
        bo.enviarParaManutencao(admin, e.getId());
        assertEquals(StatusEquipamento.MANUTENCAO, bo.buscar(e.getId()).getStatus());
        assertThrows(OperacaoNaoPermitidaException.class, () -> bo.enviarParaManutencao(admin, e.getId()));
        bo.retornarDaManutencao(admin, e.getId());
        assertEquals(StatusEquipamento.DISPONIVEL, bo.buscar(e.getId()).getStatus());
    }
}
