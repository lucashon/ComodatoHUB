package br.edu.ifal.comodatohub.factory;

import br.edu.ifal.comodatohub.exception.DadosInvalidosException;
import br.edu.ifal.comodatohub.vo.*;

/** Padrão FACTORY: cria o tipo certo de Usuario a partir do perfil. */
public final class UsuarioFactory {

    public static final String[] PERFIS = {"COLABORADOR", "GESTOR", "ADMINISTRADOR"};

    private UsuarioFactory() {
    }

    public static Usuario criar(String perfil, String nome, String email, String matricula, String senha)
            throws DadosInvalidosException {
        if (perfil == null) {
            throw new DadosInvalidosException("Perfil não informado.");
        }
        switch (perfil.toUpperCase()) {
            case "COLABORADOR":   return new Colaborador(nome, email, matricula, senha);
            case "GESTOR":        return new Gestor(nome, email, matricula, senha);
            case "ADMINISTRADOR": return new Administrador(nome, email, matricula, senha);
            default: throw new DadosInvalidosException("Perfil inválido: " + perfil);
        }
    }
}
