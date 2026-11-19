package br.edu.ifal.comodatohub.bo;

import br.edu.ifal.comodatohub.exception.AcessoNegadoException;
import br.edu.ifal.comodatohub.exception.DadosInvalidosException;
import br.edu.ifal.comodatohub.vo.Usuario;

/** Classe abstrata com validações reaproveitadas por todos os BOs. */
public abstract class BaseBO {

    /** RN07: só administrador gerencia cadastros. Usa polimorfismo (podeGerenciarEquipamentos). */
    protected void exigirAdministrador(Usuario executor) throws AcessoNegadoException {
        if (executor == null || !executor.podeGerenciarEquipamentos()) {
            throw new AcessoNegadoException("Apenas administradores podem realizar esta operação.");
        }
    }

    protected void exigirTexto(String valor, String campo) throws DadosInvalidosException {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo '" + campo + "' é obrigatório.");
        }
    }
}
