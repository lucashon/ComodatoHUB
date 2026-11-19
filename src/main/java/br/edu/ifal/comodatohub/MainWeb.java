package br.edu.ifal.comodatohub;

import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.util.BancoDados;
import br.edu.ifal.comodatohub.util.ConexaoBD;
import br.edu.ifal.comodatohub.util.DadosIniciais;
import br.edu.ifal.comodatohub.view.web.ServidorWeb;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * Inicia o sistema com a interface web (HTML + CSS).
 * Depois de executar, abra http://localhost:8080 no navegador.
 * Para a versão de console, rode a classe Main.
 */
public class MainWeb {

    private static final int PORTA = 8080;

    public static void main(String[] args) {
        try {
            BancoDados.criarTabelas();
            DadosIniciais.popularSeVazio();
            new ServidorWeb(PORTA).iniciar();
        } catch (PersistenciaException e) {
            System.out.println("[ERRO] Não foi possível iniciar o banco de dados: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("[ERRO] Não foi possível usar a porta " + PORTA + ". Feche outro programa que a use.");
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(ConexaoBD::fechar));

        String endereco = "http://localhost:" + PORTA;
        System.out.println("ComodatoHub rodando! Abra no navegador: " + endereco);
        System.out.println("Para encerrar, pare a execução (botão Stop da IDE ou Ctrl+C).");
        abrirNavegador(endereco);
    }

    private static void abrirNavegador(String endereco) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(endereco));
            }
        } catch (IOException | UnsupportedOperationException e) {
            // se não conseguir abrir sozinho, o usuário abre o endereço manualmente
        }
    }
}
