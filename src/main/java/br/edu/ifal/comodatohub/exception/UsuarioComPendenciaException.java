package br.edu.ifal.comodatohub.exception;

/** RN03 - Usuário possui empréstimo em atraso. */
public class UsuarioComPendenciaException extends NegocioException {

    private static final long serialVersionUID = 1L;

    public UsuarioComPendenciaException(String mensagem) {
        super(mensagem);
    }
}
