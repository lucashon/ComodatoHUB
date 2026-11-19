package br.edu.ifal.comodatohub.exception;

/** RN08 - Operação não permitida para este registro. */
public class OperacaoNaoPermitidaException extends NegocioException {

    private static final long serialVersionUID = 1L;

    public OperacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}
