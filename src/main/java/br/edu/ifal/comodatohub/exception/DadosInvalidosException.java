package br.edu.ifal.comodatohub.exception;

/** Dados obrigatórios ausentes ou inválidos. */
public class DadosInvalidosException extends NegocioException {

    private static final long serialVersionUID = 1L;

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
