package br.edu.ifal.comodatohub.exception;

/** RN05 - Renovação não permitida. */
public class RenovacaoNaoPermitidaException extends NegocioException {

    private static final long serialVersionUID = 1L;

    public RenovacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}
