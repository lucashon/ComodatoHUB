
package br.edu.ifal.comodatohub;

import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.util.BancoDados;
import br.edu.ifal.comodatohub.util.ConexaoBD;
import br.edu.ifal.comodatohub.util.DadosIniciais;
import br.edu.ifal.comodatohub.view.MenuPrincipalView;

public class Main {

    public static void main(String[] args) {
        try {
            BancoDados.criarTabelas();
            DadosIniciais.popularSeVazio();
            new MenuPrincipalView().iniciar();
        } catch (PersistenciaException e) {
            System.out.println("[ERRO] Não foi possível iniciar o banco de dados: " + e.getMessage());
        } finally {
            ConexaoBD.fechar();
        }
    }
}
