package br.edu.ifal.comodatohub.factory;

import br.edu.ifal.comodatohub.exception.DadosInvalidosException;
import br.edu.ifal.comodatohub.vo.*;

/**
 * Padrão FACTORY: centraliza a decisão de qual subclasse de Equipamento criar.
 * O DAO só conhece a categoria gravada no banco ("NOTEBOOK", "PROJETOR"...).
 */
public final class EquipamentoFactory {

    public static final String[] CATEGORIAS = {"NOTEBOOK", "PROJETOR", "CELULAR", "KIT_FERRAMENTAS"};

    private EquipamentoFactory() {
    }

    public static Equipamento criar(String categoria, String patrimonio, String descricao, String marca)
            throws DadosInvalidosException {
        if (categoria == null) {
            throw new DadosInvalidosException("Categoria não informada.");
        }
        switch (categoria.toUpperCase()) {
            case "NOTEBOOK":        return new Notebook(patrimonio, descricao, marca);
            case "PROJETOR":        return new Projetor(patrimonio, descricao, marca);
            case "CELULAR":         return new Celular(patrimonio, descricao, marca);
            case "KIT_FERRAMENTAS": return new KitFerramentas(patrimonio, descricao, marca);
            default: throw new DadosInvalidosException("Categoria inválida: " + categoria);
        }
    }
}
