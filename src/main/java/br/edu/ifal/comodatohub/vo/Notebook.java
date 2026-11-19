package br.edu.ifal.comodatohub.vo;

public class Notebook extends Equipamento {

    public Notebook(String patrimonio, String descricao, String marca) {
        super(patrimonio, descricao, marca);
    }

    @Override
    public int getPrazoMaximoDias() {
        return 30;
    }

    @Override
    public String getCategoria() {
        return "NOTEBOOK";
    }
}
