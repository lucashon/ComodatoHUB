package br.edu.ifal.comodatohub.exception;

/** RN07 - Operação permitida apenas para administradores. */
public class AcessoNegadoException extends NegocioException {

    private static final long serialVersionUID = 1L;

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
