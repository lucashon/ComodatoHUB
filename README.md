
# ComodatoHub

**Sistema de gestão de empréstimo de equipamentos corporativos**

Notebooks, projetores, celulares e kits de ferramentas: do pedido à devolução, com regras de negócio, histórico e controle por perfil.

Projeto da disciplina **Programação Orientada a Objetos (PROO)** · IFAL · Prof. Kenji Kamei

---

## Equipe

| Integrante | Função | Matrícula |
|---|---|---|
| **Amanda Nicolly Silva Cauponi** | Analista de Business Intelligence | 2024014103 |
| **Luan Honório Santos Aguiar** | Engenheiro de Dados | 2024014293 |
| **Lucas Honório Santos Aguiar** | Engenheiro de Software | 2024014284 |

---

## Sumário

1. [Início rápido (para quem já tem Java e Maven)](#início-rápido)
2. [Passo a passo do zero (nunca instalei Java)](#passo-a-passo-do-zero)
3. [Usando o sistema](#usando-o-sistema)
4. [Problemas comuns](#problemas-comuns)
5. [Como o código está organizado](#como-o-código-está-organizado)
6. [Critérios de avaliação](#onde-está-cada-critério-de-avaliação)
7. [Regras de negócio](#regras-de-negócio)

---

## Início rápido

Já tem o **JDK 17 ou mais novo** e o **Maven**? Abra um terminal na pasta do projeto e rode:

```bash
mvn compile exec:java
```

Depois abra **http://localhost:8080** no navegador.

---

## Passo a passo do zero

Siga estes 5 passos se você nunca instalou Java na máquina.

### Passo 1: Instale o Java (JDK)

> Instale o **JDK**, não só o JRE. O JRE só *roda* programas Java; o JDK também *compila*, e é isso que o projeto precisa.

<details>
<summary><b>Windows</b> (clique para abrir)</summary>

**Opção A: instalador (mais fácil)**
1. Acesse **https://adoptium.net** e clique em **Download** (versão LTS mais recente, 21 ou 25).
2. Abra o arquivo `.msi` baixado e clique em **Next** até o fim.
3. Na tela *Custom Setup*, clique em **Set JAVA_HOME variable** e escolha *"Will be installed on local hard drive"*.
4. Termine a instalação e **feche e abra o terminal de novo**.

**Opção B: pelo terminal** (PowerShell)
```powershell
winget install EclipseAdoptium.Temurin.21.JDK
```

</details>

<details>
<summary><b>Linux (Ubuntu, Debian, Mint)</b></summary>

```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

Sem senha de administrador (ex.: laboratório)? Instale só para o seu usuário com o SDKMAN:
```bash
curl -s "https://get.sdkman.io" | bash
source ~/.sdkman/bin/sdkman-init.sh
sdk install java 21-tem
```

</details>

<details>
<summary><b>macOS</b></summary>

Com o [Homebrew](https://brew.sh):
```bash
brew install --cask temurin
```
Ou baixe o instalador `.pkg` em **https://adoptium.net**.

</details>

### Passo 2: Instale o Maven

O Maven baixa as bibliotecas do projeto (SQLite, JUnit) e compila tudo. Se for usar o IntelliJ ou o VS Code, pode pular este passo: eles já trazem o Maven embutido (veja [Prefere usar uma IDE?](#prefere-usar-uma-ide)).

<details>
<summary><b>Windows</b></summary>

1. Acesse **https://maven.apache.org/download.cgi** e baixe o arquivo **Binary zip archive** (`apache-maven-x.x.x-bin.zip`).
2. Extraia para uma pasta fixa, por exemplo `C:\maven`.
3. No menu Iniciar, procure **"Editar as variáveis de ambiente do sistema"** → **Variáveis de Ambiente**.
4. Em *Variáveis do usuário*, selecione **Path** → **Editar** → **Novo** e adicione `C:\maven\bin` (ajuste se a pasta tiver outro nome).
5. Clique em **OK** em todas as janelas e **abra um terminal novo**.

</details>

<details>
<summary><b>Linux (Ubuntu, Debian, Mint)</b></summary>

```bash
sudo apt install maven
```

Sem senha de administrador, com o SDKMAN do passo anterior:
```bash
sdk install maven
```

</details>

<details>
<summary><b>macOS</b></summary>

```bash
brew install maven
```

</details>

### Passo 3: Confirme que deu certo

Abra um terminal **novo** e digite:

```bash
javac -version
mvn -v
```

O primeiro deve mostrar algo como `javac 21.0.5` e o segundo `Apache Maven 3.9.x`. Se aparecer *"comando não encontrado"*, veja [Problemas comuns](#problemas-comuns).

### Passo 4: Baixe o projeto

Com Git:
```bash
git clone <endereço-do-repositório>
cd <pasta-do-projeto>
```
Ou baixe o `.zip`, extraia e abra um terminal **dentro da pasta extraída** (a que tem o arquivo `pom.xml`).

> No Windows: abra a pasta no Explorador de Arquivos, clique na barra de endereço, digite `cmd` e aperte Enter.

### Passo 5: Rode o sistema

| O que você quer | Comando |
|---|---|
| **Versão web** (recomendado) | `mvn compile exec:java` |
| Versão console (texto) | `mvn compile exec:java "-Dexec.mainClass=br.edu.ifal.comodatohub.Main"` |
| Testes unitários | `mvn test` |

Os comandos são os mesmos no Windows, Linux e macOS. A primeira execução demora um pouco mais porque o Maven baixa as dependências (precisa de internet).

Quando aparecer `ComodatoHub rodando!`, o navegador abre sozinho em **http://localhost:8080**. Se não abrir, digite o endereço. Para encerrar, aperte **Ctrl + C** no terminal.

### Prefere usar uma IDE?

<details>
<summary><b>IntelliJ IDEA</b></summary>

1. `File > Open` e escolha a pasta do projeto (o IntelliJ reconhece o `pom.xml` e baixa as dependências).
2. Abra `src/main/java/br/edu/ifal/comodatohub/MainWeb.java` e clique no botão verde ao lado do `main`.
3. Testes: botão direito em `src/test/java` → **Run 'All Tests'**.

</details>

<details>
<summary><b>VS Code</b></summary>

1. Instale a extensão **Extension Pack for Java**.
2. `File > Open Folder` e escolha a pasta do projeto.
3. Abra `MainWeb.java` e clique em **Run** acima do método `main`.

</details>

---

## Usando o sistema

O sistema tem **duas interfaces** que usam exatamente as mesmas camadas BO, VO e DAO:

| Interface | Classe | Como é |
|---|---|---|
| Web | `MainWeb.java` | Páginas HTML + CSS no navegador (servidor `HttpServer` do próprio Java, sem framework) |
| Console | `Main.java` | Menus em texto no terminal |

O banco é **SQLite**: um único arquivo, `comodatohub.db`, criado sozinho na pasta do projeto. Não precisa instalar servidor de banco. Para recomeçar do zero, é só apagar esse arquivo.

### Usuários de exemplo

Criados automaticamente na primeira execução, junto com 2 setores e 5 equipamentos:

| Perfil | E-mail | Senha | O que pode fazer |
|---|---|---|---|
| Administrador | `admin@empresa.com` | `admin123` | Tudo: cadastros, retiradas, devoluções e relatórios |
| Gestor | `gestor@empresa.com` | `gestor123` | Solicitar e acompanhar os empréstimos do setor |
| Colaborador | `colaborador@empresa.com` | `colab123` | Solicitar equipamentos e ver os próprios empréstimos |

### Roteiro rápido para a apresentação

1. Entre como **colaborador**. Em *Equipamentos*, clique em **Solicitar** no notebook e depois em outro equipamento.
2. Tente solicitar um terceiro: aparece o aviso da **RN02** (limite do perfil). Mostre *Meus empréstimos*.
3. Clique em **Sair** e entre como **admin**. Em *Empréstimos*, clique em **Confirmar retirada**. Depois escolha **Avariado**, descreva a avaria e clique em **Registrar devolução**: o equipamento vai sozinho para manutenção (**RN06**). Com a descrição vazia, aparece o aviso da regra.
4. Em *Equipamentos*, clique em **Gerenciar** no notebook para ver o histórico de empréstimos dele. Em *Empréstimos*, mostre o quadro **Empréstimos por categoria** e o filtro **Atrasados**.
5. Para mostrar que as camadas são independentes, rode também a versão console (`Main`): os mesmos dados aparecem lá.

---

## Problemas comuns

| Mensagem / sintoma | Causa | Solução |
|---|---|---|
| `release version 17 not supported` | Só o **JRE** está instalado, falta o JDK | Instale o JDK ([Passo 1](#passo-1-instale-o-java-jdk)). No Ubuntu: `sudo apt install openjdk-21-jdk` |
| `javac: command not found` / `'java' não é reconhecido` | Java não instalado ou terminal antigo | Instale o JDK e **abra um terminal novo** |
| `JAVA_HOME not found` / `JAVA_HOME is not defined correctly` | Variável não configurada | Windows: reinstale marcando *Set JAVA_HOME*. Linux/macOS: `export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which javac))))` |
| `mvn: command not found` / `'mvn' não é reconhecido` | Maven não instalado ou fora do PATH | Veja o [Passo 2](#passo-2-instale-o-maven) e abra um terminal novo |
| `Não foi possível usar a porta 8080` | O sistema já está aberto em outro terminal ou IDE | Feche a outra execução (Ctrl + C ou Stop) |
| Dados estranhos ou quero começar de novo | Banco antigo | Apague o arquivo `comodatohub.db` e rode de novo |
| Primeira execução demora | O Maven está baixando as dependências | Normal, só acontece uma vez (precisa de internet) |

---

## Como o código está organizado

Fluxo de toda operação: **View → BO → DAO → Banco**. A View nunca acessa o DAO, e só o DAO tem SQL.

```
src/main/java/br/edu/ifal/comodatohub/
├── Main.java            ponto de entrada (console)
├── MainWeb.java         ponto de entrada (web)
├── view/                VIEW: telas de console (menus, leitura e mensagens)
│   └── web/             VIEW: páginas HTML e servidor web
├── bo/                  BO: regras de negócio (RN01 a RN08)
├── vo/                  VO: classes do domínio (Usuario, Equipamento, Emprestimo...)
├── dao/                 DAO: SQL e CRUD (INSERT, SELECT, UPDATE, DELETE)
├── exception/           exceções personalizadas
├── factory/             padrão Factory
└── util/                conexão (Singleton), criação das tabelas e dados iniciais

src/main/resources/web/estilo.css   visual das páginas web
src/test/java/...                   testes unitários (JUnit 5) de BO, DAO e polimorfismo
docs/script-banco.sql               estrutura das tabelas (referência)
```

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
| **Polimorfismo** | `getPrazoMaximoDias()`, `getLimiteEmprestimos()`, `podeGerenciarEquipamentos()`, `podeAcompanharSetor()`. Ver `EmprestimoBO.solicitar()` e `Emprestimo.iniciar()`, que usam a referência genérica sem saber a subclasse. Na web, cada página sobrescreve `caminho()`, `titulo()`, `permitido()` e `conteudo()` de `PaginaWeb` (o menu pergunta a cada página se o usuário pode acessá-la), e `MeuSetorPagina` herda de `EmprestimosPagina` mudando só os dados |
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
| **Testes unitários** | `src/test/java`: 35 testes cobrindo as regras do BO, o CRUD de cada DAO e o polimorfismo. Usam um banco **em memória**, então não alteram o banco real |

---

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
3. Em `util/BancoDados.java`, troque `INTEGER PRIMARY KEY AUTOINCREMENT` por `INT PRIMARY KEY AUTO_INCREMENT` e `TEXT` por `VARCHAR(255)`.

O resto do código não muda: essa é a vantagem da camada DAO.
