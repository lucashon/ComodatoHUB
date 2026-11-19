package br.edu.ifal.comodatohub.vo;

import java.time.LocalDateTime;

/** Registro do estado do equipamento na devolução. Só existe dentro de um Emprestimo (COMPOSIÇÃO). */
public class Ocorrencia {
    private final EstadoConservacao estado;
    private final String descricao;
    private final LocalDateTime dataRegistro;

    public Ocorrencia(EstadoConservacao estado, String descricao) {
        this(estado, descricao, LocalDateTime.now());
    }

    public Ocorrencia(EstadoConservacao estado, String descricao, LocalDateTime dataRegistro) {
        this.estado = estado;
        this.descricao = descricao;
        this.dataRegistro = dataRegistro;
    }

    public EstadoConservacao getEstado() { return estado; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }

    @Override
    public String toString() {
        return estado + (descricao == null || descricao.isBlank() ? "" : " - " + descricao);
    }
}
