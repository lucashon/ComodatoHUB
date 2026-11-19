-- ComodatoHub - estrutura do banco de dados (SQLite)
-- Observação: as tabelas são criadas automaticamente pela classe util/BancoDados.java
-- na primeira execução. Este script serve apenas como referência/documentação.

CREATE TABLE IF NOT EXISTS setor (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuario (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    nome      TEXT NOT NULL,
    email     TEXT NOT NULL UNIQUE,
    matricula TEXT,
    senha     TEXT NOT NULL,
    perfil    TEXT NOT NULL,              -- COLABORADOR | GESTOR | ADMINISTRADOR
    ativo     INTEGER NOT NULL DEFAULT 1,
    setor_id  INTEGER REFERENCES setor(id) ON DELETE SET NULL   -- agregação
);

CREATE TABLE IF NOT EXISTS equipamento (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    patrimonio     TEXT NOT NULL UNIQUE,
    descricao      TEXT NOT NULL,
    marca          TEXT,
    categoria      TEXT NOT NULL,         -- NOTEBOOK | PROJETOR | CELULAR | KIT_FERRAMENTAS
    status         TEXT NOT NULL,         -- DISPONIVEL | RESERVADO | EMPRESTADO | MANUTENCAO | BAIXADO
    data_aquisicao TEXT
);

CREATE TABLE IF NOT EXISTS emprestimo (
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id           INTEGER NOT NULL REFERENCES usuario(id),
    equipamento_id       INTEGER NOT NULL REFERENCES equipamento(id),
    data_solicitacao     TEXT NOT NULL,
    data_retirada        TEXT,
    data_prevista        TEXT,
    data_devolucao       TEXT,
    renovacoes           INTEGER NOT NULL DEFAULT 0,
    status               TEXT NOT NULL,   -- SOLICITADO | ATIVO | DEVOLVIDO | CANCELADO
    -- Ocorrência da devolução (composição: só existe dentro do empréstimo)
    estado_devolucao     TEXT,            -- BOM | AVARIADO
    descricao_ocorrencia TEXT,
    data_ocorrencia      TEXT
);
