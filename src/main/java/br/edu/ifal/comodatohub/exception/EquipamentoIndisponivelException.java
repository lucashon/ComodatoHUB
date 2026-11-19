package br.edu.ifal.comodatohub.exception;

/** RN01 - O equipamento não está disponível para empréstimo. */
public class EquipamentoIndisponivelException extends NegocioException {

    private static final long serialVersionUID = 1L;

    public EquipamentoIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
