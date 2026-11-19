package br.edu.ifal.comodatohub.vo;

import java.time.LocalDate;

/**
 * Classe ABSTRATA que implementa a interface Emprestavel.
 * Cada categoria define o próprio prazo de devolução (polimorfismo).
 */
public abstract class Equipamento implements Emprestavel {
    private int id;
    private String patrimonio;
    private String descricao;
    private String marca;
    private StatusEquipamento status = StatusEquipamento.DISPONIVEL;
    private LocalDate dataAquisicao;

    protected Equipamento(String patrimonio, String descricao, String marca) {
        this.patrimonio = patrimonio;
        this.descricao = descricao;
        this.marca = marca;
        this.dataAquisicao = LocalDate.now();
    }

    /** Prazo de devolução em dias, contado a partir da retirada (RN04). */
    public abstract int getPrazoMaximoDias();

    /** Categoria gravada no banco e usada pela EquipamentoFactory. */
    public abstract String getCategoria();

    @Override
    public void reservar() {
        this.status = StatusEquipamento.RESERVADO;
    }

    @Override
    public void emprestar() {
        this.status = StatusEquipamento.EMPRESTADO;
    }

    @Override
    public void devolver(EstadoConservacao estado) {
        // RN06: equipamento avariado vai direto para manutenção
        this.status = (estado == EstadoConservacao.AVARIADO)
                ? StatusEquipamento.MANUTENCAO
                : StatusEquipamento.DISPONIVEL;
    }

    @Override
    public boolean estaDisponivel() {
        return status == StatusEquipamento.DISPONIVEL;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPatrimonio() { return patrimonio; }
    public void setPatrimonio(String patrimonio) { this.patrimonio = patrimonio; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public StatusEquipamento getStatus() { return status; }
    public void setStatus(StatusEquipamento status) { this.status = status; }
    public LocalDate getDataAquisicao() { return dataAquisicao; }
    public void setDataAquisicao(LocalDate d) { this.dataAquisicao = d; }

    @Override
    public String toString() {
        return String.format("%d - [%s] %s %s (patrimônio %s) - %s", id, getCategoria(), descricao, marca, patrimonio, status);
    }
}
