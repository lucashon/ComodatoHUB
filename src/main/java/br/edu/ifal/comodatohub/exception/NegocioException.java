package br.edu.ifal.comodatohub.exception;

/** Superclasse de todas as exceções de regra de negócio do sistema. */
public class NegocioException extends Exception {

    private static final long serialVersionUID = 1L;

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
