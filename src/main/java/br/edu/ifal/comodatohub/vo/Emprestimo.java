package br.edu.ifal.comodatohub.vo;

import java.time.LocalDate;

/**
 * Empréstimo de um equipamento a um usuário.
 * ASSOCIAÇÃO com Usuario e Equipamento; COMPOSIÇÃO com Ocorrencia (criada aqui dentro).
 */
public class Emprestimo {
    private int id;
    private Usuario usuario;
    private Equipamento equipamento;
    private LocalDate dataSolicitacao;
    private LocalDate dataRetirada;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private int renovacoes;
    private StatusEmprestimo status;
    private Ocorrencia ocorrencia;

    public Emprestimo(Usuario usuario, Equipamento equipamento) {
        this.usuario = usuario;
        this.equipamento = equipamento;
        this.dataSolicitacao = LocalDate.now();
        this.status = StatusEmprestimo.SOLICITADO;
    }

    /** Confirma a retirada: o prazo depende da categoria do equipamento (polimorfismo). */
    public void iniciar() {
        this.dataRetirada = LocalDate.now();
        this.dataPrevistaDevolucao = dataRetirada.plusDays(equipamento.getPrazoMaximoDias());
        this.status = StatusEmprestimo.ATIVO;
        equipamento.emprestar();
    }

    public void renovar() {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao.plusDays(equipamento.getPrazoMaximoDias());
        this.renovacoes++;
    }

    public void cancelar() {
        this.status = StatusEmprestimo.CANCELADO;
        equipamento.setStatus(StatusEquipamento.DISPONIVEL);
    }

    /** Composição: a Ocorrencia nasce dentro do empréstimo. */
    public void registrarDevolucao(EstadoConservacao estado, String descricao) {
        this.ocorrencia = new Ocorrencia(estado, descricao);
        this.dataDevolucao = LocalDate.now();
        this.status = StatusEmprestimo.DEVOLVIDO;
        equipamento.devolver(estado);
    }

    public boolean estaAtrasado() {
        return status == StatusEmprestimo.ATIVO
                && dataPrevistaDevolucao != null
                && LocalDate.now().isAfter(dataPrevistaDevolucao);
    }

    public boolean estaEmAberto() {
        return status == StatusEmprestimo.SOLICITADO || status == StatusEmprestimo.ATIVO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public Equipamento getEquipamento() { return equipamento; }
    public LocalDate getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDate d) { this.dataSolicitacao = d; }
    public LocalDate getDataRetirada() { return dataRetirada; }
    public void setDataRetirada(LocalDate d) { this.dataRetirada = d; }
    public LocalDate getDataPrevistaDevolucao() { return dataPrevistaDevolucao; }
    public void setDataPrevistaDevolucao(LocalDate d) { this.dataPrevistaDevolucao = d; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate d) { this.dataDevolucao = d; }
    public int getRenovacoes() { return renovacoes; }
    public void setRenovacoes(int renovacoes) { this.renovacoes = renovacoes; }
    public StatusEmprestimo getStatus() { return status; }
    public void setStatus(StatusEmprestimo status) { this.status = status; }
    public Ocorrencia getOcorrencia() { return ocorrencia; }
    public void setOcorrencia(Ocorrencia ocorrencia) { this.ocorrencia = ocorrencia; }

    @Override
    public String toString() {
        String prazo = dataPrevistaDevolucao == null ? "aguardando retirada" : "devolver até " + dataPrevistaDevolucao;
        String extra = estaAtrasado() ? " [ATRASADO]" : "";
        String oc = ocorrencia == null ? "" : " | devolução: " + ocorrencia;
        return String.format("%d - %s | %s | %s | %s%s%s", id, equipamento.getDescricao(), usuario.getNome(), status, prazo, extra, oc);
    }
}
