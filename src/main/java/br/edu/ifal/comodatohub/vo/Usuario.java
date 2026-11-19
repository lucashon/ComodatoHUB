package br.edu.ifal.comodatohub.vo;

/**
 * Classe ABSTRATA: não existe "usuário genérico" na empresa.
 * Cada perfil (Colaborador, Gestor, Administrador) define seu próprio limite e permissões.
 */
public abstract class Usuario {
    private int id;
    private String nome;
    private String email;
    private String matriculaFuncional;
    private String senha;
    private boolean ativo = true;
    private Setor setor;

    protected Usuario(String nome, String email, String matriculaFuncional, String senha) {
        this.nome = nome;
        this.email = email;
        this.matriculaFuncional = matriculaFuncional;
        this.senha = senha;
    }

    /** Quantos empréstimos (solicitados + ativos) o perfil pode ter ao mesmo tempo. */
    public abstract int getLimiteEmprestimos();

    /** Nome do perfil, gravado no banco e usado pela UsuarioFactory. */
    public abstract String getPerfil();

    /** Por padrão ninguém gerencia o cadastro. O Administrador sobrescreve. */
    public boolean podeGerenciarEquipamentos() {
        return false;
    }

    /** Pode acompanhar os empréstimos do setor? Gestor e Administrador sobrescrevem/herdam o comportamento. */
    public boolean podeAcompanharSetor() {
        return podeGerenciarEquipamentos();
    }

    public boolean senhaConfere(String senhaInformada) {
        return senha != null && senha.equals(senhaInformada);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMatriculaFuncional() { return matriculaFuncional; }
    public void setMatriculaFuncional(String m) { this.matriculaFuncional = m; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public Setor getSetor() { return setor; }
    public void setSetor(Setor setor) { this.setor = setor; }

    @Override
    public String toString() {
        return String.format("%d - %s (%s) %s%s", id, nome, getPerfil(), email, ativo ? "" : " [INATIVO]");
    }
}
