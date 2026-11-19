package br.edu.ifal.comodatohub.vo;

public class Celular extends Equipamento {

    public Celular(String patrimonio, String descricao, String marca) {
        super(patrimonio, descricao, marca);
    }

    @Override
    public int getPrazoMaximoDias() {
        return 15;
    }

    @Override
    public String getCategoria() {
        return "CELULAR";
    }
}
