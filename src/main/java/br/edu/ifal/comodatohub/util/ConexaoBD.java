package br.edu.ifal.comodatohub.util;

import br.edu.ifal.comodatohub.exception.PersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Padrão SINGLETON: existe uma única conexão com o banco em todo o sistema.
 * O construtor é privado; o acesso é feito por ConexaoBD.getInstancia().
 */
public final class ConexaoBD {

    private static String url = "jdbc:sqlite:comodatohub.db";
    private static ConexaoBD instancia;

    private final Connection conexao;

    private ConexaoBD() throws PersistenciaException {
        try {
            conexao = DriverManager.getConnection(url);
            try (Statement st = conexao.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Não foi possível conectar ao banco: " + url, e);
        }
    }

    public static synchronized ConexaoBD getInstancia() throws PersistenciaException {
        if (instancia == null) {
            instancia = new ConexaoBD();
        }
        return instancia;
    }

    public Connection getConexao() {
        return conexao;
    }

    /** Usado pelos testes para trocar o banco (ex.: banco em memória). */
    public static synchronized void configurar(String novaUrl) {
        fechar();
        url = novaUrl;
    }

    public static synchronized void fechar() {
        if (instancia != null) {
            try {
                instancia.conexao.close();
            } catch (SQLException e) {
                System.err.println("Aviso: erro ao fechar conexão: " + e.getMessage());
            }
            instancia = null;
        }
    }
}
