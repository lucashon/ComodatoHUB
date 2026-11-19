package br.edu.ifal.comodatohub.view.web;

import br.edu.ifal.comodatohub.exception.DadosInvalidosException;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.util.Map;

/** Dados de uma requisição: usuário logado e campos enviados pelo formulário ou pela URL. */
public class Requisicao {

    private final Usuario usuario;
    private final Map<String, String> parametros;

    public Requisicao(Usuario usuario, Map<String, String> parametros) {
        this.usuario = usuario;
        this.parametros = parametros;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    /** Retorna o valor do campo ou texto vazio. */
    public String get(String nome) {
        return parametros.getOrDefault(nome, "").trim();
    }

    public boolean tem(String nome) {
        return !get(nome).isEmpty();
    }

    public int getInt(String nome) throws DadosInvalidosException {
        try {
            return Integer.parseInt(get(nome));
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Valor inválido no campo '" + nome + "'.");
        }
    }

    /** Retorna null se o campo vier vazio. */
    public Integer getIntOpcional(String nome) throws DadosInvalidosException {
        return tem(nome) ? getInt(nome) : null;
    }
}
