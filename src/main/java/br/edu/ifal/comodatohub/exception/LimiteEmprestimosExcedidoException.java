package br.edu.ifal.comodatohub.exception;

/** RN02 - Usuário atingiu o limite de empréstimos do seu perfil. */
public class LimiteEmprestimosExcedidoException extends NegocioException {

    private static final long serialVersionUID = 1L;

    public LimiteEmprestimosExcedidoException(String mensagem) {
        super(mensagem);
    }
}
