package br.edu.ifal.comodatohub.vo;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Testa herança e polimorfismo usando referências do tipo da superclasse. */
class PolimorfismoTest {

    @Test
    void cadaCategoriaTemSeuPrazo() {
        List<Equipamento> equipamentos = List.of(
                new Notebook("P1", "Note", "Dell"),
                new Projetor("P2", "Proj", "Epson"),
                new Celular("P3", "Cel", "Samsung"),
                new KitFerramentas("P4", "Kit", "Tramontina"));
        int[] esperados = {30, 1, 15, 7};
        for (int i = 0; i < equipamentos.size(); i++) {
            assertEquals(esperados[i], equipamentos.get(i).getPrazoMaximoDias());
        }
    }

    @Test
    void cadaPerfilTemSeuLimiteEPermissao() {
        Usuario colaborador = new Colaborador("C", "c@e.com", "1", "1234");
        Usuario gestor = new Gestor("G", "g@e.com", "2", "1234");
        Usuario admin = new Administrador("A", "a@e.com", "3", "1234");

        assertEquals(2, colaborador.getLimiteEmprestimos());
        assertEquals(4, gestor.getLimiteEmprestimos());
        assertEquals(3, admin.getLimiteEmprestimos());

        assertFalse(colaborador.podeGerenciarEquipamentos());
        assertFalse(gestor.podeGerenciarEquipamentos());
        assertTrue(admin.podeGerenciarEquipamentos());

        assertFalse(colaborador.podeAcompanharSetor());
        assertTrue(gestor.podeAcompanharSetor());
        assertTrue(admin.podeAcompanharSetor());
    }

    @Test
    void iniciarEmprestimoUsaPrazoDaCategoria() {
        Emprestimo e = new Emprestimo(new Colaborador("C", "c@e.com", "1", "1234"), new Projetor("P", "Proj", "Epson"));
        e.iniciar();
        assertEquals(StatusEmprestimo.ATIVO, e.getStatus());
        assertEquals(LocalDate.now().plusDays(1), e.getDataPrevistaDevolucao());
        assertEquals(StatusEquipamento.EMPRESTADO, e.getEquipamento().getStatus());
    }

    @Test
    void devolucaoAvariadaMandaParaManutencao() {
        Emprestimo e = new Emprestimo(new Colaborador("C", "c@e.com", "1", "1234"), new Notebook("P", "Note", "Dell"));
        e.iniciar();
        e.registrarDevolucao(EstadoConservacao.AVARIADO, "Tela trincada");
        assertEquals(StatusEmprestimo.DEVOLVIDO, e.getStatus());
        assertEquals(StatusEquipamento.MANUTENCAO, e.getEquipamento().getStatus());
        assertNotNull(e.getOcorrencia());
    }
}
