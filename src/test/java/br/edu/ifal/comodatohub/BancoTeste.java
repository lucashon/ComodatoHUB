package br.edu.ifal.comodatohub;

import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.util.BancoDados;
import br.edu.ifal.comodatohub.util.ConexaoBD;

/** Prepara um banco SQLite em memória, separado do banco real, e o limpa antes de cada teste. */
public final class BancoTeste {

    private static boolean configurado = false;

    private BancoTeste() {
    }

    public static void preparar() throws PersistenciaException {
        if (!configurado) {
            ConexaoBD.configurar("jdbc:sqlite::memory:");
            BancoDados.criarTabelas();
            configurado = true;
        }
        BancoDados.limparTudo();
    }
}
