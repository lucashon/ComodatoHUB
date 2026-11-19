package br.edu.ifal.comodatohub.vo;

public class Projetor extends Equipamento {

    public Projetor(String patrimonio, String descricao, String marca) {
        super(patrimonio, descricao, marca);
    }

    @Override
    public int getPrazoMaximoDias() {
        return 1;
    }

    @Override
    public String getCategoria() {
        return "PROJETOR";
    }
}
