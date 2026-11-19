package br.edu.ifal.comodatohub.exception;

/** Registro não encontrado. */
public class RegistroNaoEncontradoException extends NegocioException {

    private static final long serialVersionUID = 1L;

    public RegistroNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
