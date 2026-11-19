package br.edu.ifal.comodatohub.vo;

public class Gestor extends Usuario {

    public Gestor(String nome, String email, String matriculaFuncional, String senha) {
        super(nome, email, matriculaFuncional, senha);
    }

    @Override
    public int getLimiteEmprestimos() {
        return 4;
    }

    @Override
    public String getPerfil() {
        return "GESTOR";
    }

    @Override
    public boolean podeAcompanharSetor() {
        return true;
    }
}
