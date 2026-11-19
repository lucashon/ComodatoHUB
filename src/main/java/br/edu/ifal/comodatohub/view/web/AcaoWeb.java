package br.edu.ifal.comodatohub.view.web;

import br.edu.ifal.comodatohub.exception.NegocioException;
import br.edu.ifal.comodatohub.exception.PersistenciaException;

/** Ação disparada por um formulário (POST). Retorna a mensagem de sucesso exibida na página. */
@FunctionalInterface
public interface AcaoWeb {
    String executar(Requisicao req) throws NegocioException, PersistenciaException;
}
