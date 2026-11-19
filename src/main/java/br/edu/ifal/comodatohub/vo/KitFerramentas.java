package br.edu.ifal.comodatohub.vo;

public class KitFerramentas extends Equipamento {

    public KitFerramentas(String patrimonio, String descricao, String marca) {
        super(patrimonio, descricao, marca);
    }

    @Override
    public int getPrazoMaximoDias() {
        return 7;
    }

    @Override
    public String getCategoria() {
        return "KIT_FERRAMENTAS";
    }
}
