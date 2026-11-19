package br.edu.ifal.comodatohub.vo;

/** Contrato de tudo o que pode ser emprestado pela empresa. */
public interface Emprestavel {
    void reservar();
    void emprestar();
    void devolver(EstadoConservacao estado);
    boolean estaDisponivel();
}
