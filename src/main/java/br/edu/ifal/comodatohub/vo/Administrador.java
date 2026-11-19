package br.edu.ifal.comodatohub.vo;

public class Administrador extends Usuario {

    public Administrador(String nome, String email, String matriculaFuncional, String senha) {
        super(nome, email, matriculaFuncional, senha);
    }

    @Override
    public int getLimiteEmprestimos() {
        return 3;
    }

    @Override
    public String getPerfil() {
        return "ADMINISTRADOR";
    }

    @Override
    public boolean podeGerenciarEquipamentos() {
        return true;
    }
}
