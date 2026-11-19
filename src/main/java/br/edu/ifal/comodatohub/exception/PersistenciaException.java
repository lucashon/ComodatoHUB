package br.edu.ifal.comodatohub.exception;

/** Lançada pela camada DAO quando ocorre falha no banco de dados. Guarda a SQLException original como causa. */
public class PersistenciaException extends Exception {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
