# ComodatoHub

Sistema de Gestão de Empréstimo de Equipamentos Corporativos.
Projeto da disciplina **Programação Orientada a Objetos (PROO)**, IFAL, professor Kenji Kamei.

**Integrantes:** Amanda Nicolly Silva Cauponi (2024014103), Luan Honório Santos Aguiar (2024014293) e Lucas Honório Santos Aguiar (2024014284).

---

## Como executar

**Requisitos:** Java 17 ou superior e Maven (o IntelliJ e o VS Code já trazem o Maven embutido).

O sistema tem **duas interfaces**, que usam exatamente as mesmas camadas BO, VO e DAO:

| Interface | Classe para executar |
|---|---|
| Web (HTML + CSS, abre no navegador) | `MainWeb.java` |
| Console (texto) | `Main.java` |

O banco de dados é **SQLite**: é só um arquivo (`comodatohub.db`) criado automaticamente na pasta do projeto. Não é preciso instalar nenhum servidor de banco.

### No IntelliJ IDEA
1. `File > Open` e escolha a pasta `ComodatoHub` (ele reconhece o `pom.xml` e baixa as dependências).
2. Abra `src/main/java/br/edu/ifal/comodatohub/MainWeb.java` (ou `Main.java` para o console) e clique em **Run**.
3. O navegador abre sozinho em **http://localhost:8080**. Se não abrir, digite esse endereço. Para encerrar, clique em **Stop** na IDE.
4. Para os testes: clique com o botão direito na pasta `src/test/java` e escolha **Run 'All Tests'**.

### No VS Code
Instale o *Extension Pack for Java*, abra a pasta e clique em **Run** acima do método `main` da classe `MainWeb` (ou `Main`) e acesse http://localhost:8080.

### Pelo terminal
```bash
mvn compile exec:java     # executa a versão web (http://localhost:8080)
mvn compile exec:java -Dexec.mainClass=br.edu.ifal.comodatohub.Main   # executa o console
mvn test                  # roda os testes unitários
```

### Usuários de exemplo (criados na primeira execução)

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | admin@empresa.com | admin123 |
| Gestor | gestor@empresa.com | gestor123 |
| Colaborador | colaborador@empresa.com | colab123 |

Também são criados 2 setores e 5 equipamentos. Para recomeçar do zero, apague o arquivo `comodatohub.db`.

### Roteiro rápido para a apresentação (versão web)
1. Entre como **colaborador**. Em *Equipamentos*, clique em **Solicitar** no notebook. Faça o mesmo com outro equipamento.
2. Tente solicitar um terceiro: aparece o aviso da **RN02** (limite do perfil). Mostre a página *Meus empréstimos*.
3. Clique em **Sair** e entre como **admin**. Em *Empréstimos*, clique em **Confirmar retirada**. Depois, escolha **Avariado**, descreva a avaria e clique em **Registrar devolução**: o equipamento vai sozinho para MANUTENCAO (**RN06**). Se deixar a descrição vazia, aparece o aviso da regra.
4. Em *Equipamentos*, clique em **Histórico**; em *Empréstimos*, mostre o quadro **Empréstimos por categoria** e o filtro **Atrasados**.
5. Para mostrar que as camadas são independentes, rode também a classe `Main` (console): os mesmos dados aparecem lá.

---

## Estrutura em camadas

```
src/main/java/br/edu/ifal/comodatohub/
├── Main.java          ponto de entrada
├── MainWeb.java       ponto de entrada da versão web
├── view/              VIEW: telas de console (menus, leitura e mensagens)
│   └── web/           VIEW: páginas HTML e servidor web (HttpServer do próprio Java)
src/main/resources/web/estilo.css   CSS das páginas
├── bo/                BO: regras de negócio (RN01 a RN08)
├── vo/                VO: classes do domínio (Usuario, Equipamento, Emprestimo...)
├── dao/               DAO: SQL e CRUD (INSERT, SELECT, UPDATE, DELETE)
├── exception/         exceções personalizadas
├── factory/           padrão Factory
└── util/              conexão (Singleton), criação das tabelas e dados iniciais
src/test/java/...      testes unitários (JUnit 5) de BO, DAO e polimorfismo
docs/script-banco.sql  estrutura das tabelas (referência)
```

Fluxo de uma operação: **View → BO → DAO → Banco**. A View nunca acessa o DAO, e só o DAO tem SQL.

---

## Onde está cada critério de avaliação

| Critério | Onde ver no código |
|---|---|
| **Classe abstrata** | `vo/Usuario.java`, `vo/Equipamento.java`, `bo/BaseBO.java`, `view/web/PaginaWeb.java` |
| **Classes concretas** | `Colaborador`, `Gestor`, `Administrador`, `Notebook`, `Projetor`, `Celular`, `KitFerramentas` |
| **Interface** | `vo/Emprestavel.java` (implementada por `Equipamento`) e `dao/GenericDAO.java` (implementada por todos os DAOs) |
| **Encapsulamento** | todos os atributos são `private`; `Setor.getUsuarios()` devolve uma cópia somente leitura |
| **Construtores** | todos os VOs exigem os dados obrigatórios no construtor; `Usuario` e `Equipamento` têm construtor `protected` |
| **Herança** | `Usuario` → 3 perfis; `Equipamento` → 4 categorias; `NegocioException` → 8 exceções; todos os BOs herdam de `BaseBO` |
| **Polimorfismo** | `getPrazoMaximoDias()`, `getLimiteEmprestimos()`, `podeGerenciarEquipamentos()`, `podeAcompanharSetor()`. Ver `EmprestimoBO.solicitar()` e `Emprestimo.iniciar()`, que usam a referência genérica sem saber a subclasse. Na versão web, cada página sobrescreve `caminho()`, `titulo()`, `permitido()` e `conteudo()` de `PaginaWeb` (o menu é montado perguntando a cada página se o usuário pode acessá-la), e `MeuSetorPagina` herda de `EmprestimosPagina` mudando só os dados |
| **Associação** | `Emprestimo` → `Usuario` e `Equipamento` |
| **Agregação** | `Setor` agrega `Usuario` (ao excluir o setor, o usuário continua existindo: `ON DELETE SET NULL`) |
| **Composição** | `Emprestimo` cria e contém a `Ocorrencia` (`registrarDevolucao()`) |
| **Padrão Singleton** | `util/ConexaoBD.java`: construtor privado + `getInstancia()` |
| **Padrão Factory** | `factory/EquipamentoFactory.java` e `factory/UsuarioFactory.java`, usadas pelos DAOs e BOs |
| **Padrão DAO** | pacote `dao` + interface `GenericDAO<T>` |
| **Exceções personalizadas** | pacote `exception`: `NegocioException` (base) + 8 específicas + `PersistenciaException` |
| **try-catch adequado** | DAOs capturam `SQLException` e relançam `PersistenciaException`; a View trata cada tipo em `view/Console.executar()` (console) e em `view/web/ServidorWeb` (web, mostrando a mensagem na página) |
| **CRUD completo** | `SetorDAO`, `UsuarioDAO`, `EquipamentoDAO`, `EmprestimoDAO` (inserir, buscarPorId, listarTodos, atualizar, excluir) |
| **Collections** | `List` (consultas), `Set<String>` (patrimônios duplicados em `EquipamentoBO.cadastrar`), `Map<String,Integer>` (ranking em `EmprestimoDAO.contarPorCategoria`), `Map<String,List<Equipamento>>` (`EquipamentoBO.agruparPorCategoria`) |
| **Testes unitários** | `src/test/java`: 35 testes cobrindo as regras do BO, o CRUD de cada DAO e o polimorfismo. Os testes usam um banco **em memória**, então não alteram o banco real |

## Regras de negócio

| ID | Regra | Exceção |
|---|---|---|
| RN01 | Só empresta equipamento DISPONIVEL | `EquipamentoIndisponivelException` |
| RN02 | Limite simultâneo: Colaborador 2, Gestor 4, Administrador 3 | `LimiteEmprestimosExcedidoException` |
| RN03 | Quem tem empréstimo atrasado não solicita nem renova | `UsuarioComPendenciaException` |
| RN04 | Prazo por categoria: Projetor 1, Kit 7, Celular 15, Notebook 30 dias | – |
| RN05 | Renovação só uma vez e sem atraso | `RenovacaoNaoPermitidaException` |
| RN06 | Devolução avariada exige descrição e vai para manutenção | `DadosInvalidosException` |
| RN07 | Só administrador gerencia cadastros, retiradas e devoluções | `AcessoNegadoException` |
| RN08 | Não exclui usuário/equipamento com empréstimos registrados (usar inativação/baixa) | `OperacaoNaoPermitidaException` |

---

## Trocar SQLite por MySQL (opcional)
1. No `pom.xml`, troque a dependência `sqlite-jdbc` por `com.mysql:mysql-connector-j`.
2. Em `util/ConexaoBD.java`, troque a URL por `jdbc:mysql://localhost:3306/comodatohub?user=root&password=SENHA`.
3. Em `util/BancoDados.java`, troque `INTEGER PRIMARY KEY AUTOINCREMENT` por `INT PRIMARY KEY AUTO_INCREMENT` e `TEXT` por `VARCHAR(255)`. O resto do código não muda: essa é a vantagem da camada DAO.
