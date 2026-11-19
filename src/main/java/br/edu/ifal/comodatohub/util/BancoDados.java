package br.edu.ifal.comodatohub.util;

import br.edu.ifal.comodatohub.exception.PersistenciaException;

import java.sql.SQLException;
import java.sql.Statement;

/** Cria as tabelas (se não existirem) e oferece limpeza para os testes. */
public final class BancoDados {

    private BancoDados() {
    }

    public static void criarTabelas() throws PersistenciaException {
        String[] sqls = {
            "CREATE TABLE IF NOT EXISTS setor ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nome TEXT NOT NULL UNIQUE)",
            "CREATE TABLE IF NOT EXISTS usuario ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nome TEXT NOT NULL,"
                + " email TEXT NOT NULL UNIQUE,"
                + " matricula TEXT,"
                + " senha TEXT NOT NULL,"
                + " perfil TEXT NOT NULL,"
                + " ativo INTEGER NOT NULL DEFAULT 1,"
                + " setor_id INTEGER REFERENCES setor(id) ON DELETE SET NULL)",
            "CREATE TABLE IF NOT EXISTS equipamento ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " patrimonio TEXT NOT NULL UNIQUE,"
                + " descricao TEXT NOT NULL,"
                + " marca TEXT,"
                + " categoria TEXT NOT NULL,"
                + " status TEXT NOT NULL,"
                + " data_aquisicao TEXT)",
            "CREATE TABLE IF NOT EXISTS emprestimo ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " usuario_id INTEGER NOT NULL REFERENCES usuario(id),"
                + " equipamento_id INTEGER NOT NULL REFERENCES equipamento(id),"
                + " data_solicitacao TEXT NOT NULL,"
                + " data_retirada TEXT,"
                + " data_prevista TEXT,"
                + " data_devolucao TEXT,"
                + " renovacoes INTEGER NOT NULL DEFAULT 0,"
                + " status TEXT NOT NULL,"
                // dados da Ocorrencia (composição: ficam na mesma linha do empréstimo)
                + " estado_devolucao TEXT,"
                + " descricao_ocorrencia TEXT,"
                + " data_ocorrencia TEXT)"
        };
        try (Statement st = ConexaoBD.getInstancia().getConexao().createStatement()) {
            for (String sql : sqls) {
                st.execute(sql);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao criar tabelas.", e);
        }
    }

    public static void limparTudo() throws PersistenciaException {
        try (Statement st = ConexaoBD.getInstancia().getConexao().createStatement()) {
            st.execute("DELETE FROM emprestimo");
            st.execute("DELETE FROM usuario");
            st.execute("DELETE FROM equipamento");
            st.execute("DELETE FROM setor");
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao limpar tabelas.", e);
        }
    }
}
