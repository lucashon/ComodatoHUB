package br.edu.ifal.comodatohub.vo;

public class Colaborador extends Usuario {

    public Colaborador(String nome, String email, String matriculaFuncional, String senha) {
        super(nome, email, matriculaFuncional, senha);
    }

    @Override
    public int getLimiteEmprestimos() {
        return 2;
    }

    @Override
    public String getPerfil() {
        return "COLABORADOR";
    }
}
