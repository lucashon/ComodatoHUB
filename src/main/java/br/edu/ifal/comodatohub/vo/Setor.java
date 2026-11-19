package br.edu.ifal.comodatohub.vo;

import java.util.ArrayList;
import java.util.List;

/** Setor da empresa. AGREGA usuários: se o setor for excluído, os usuários continuam existindo. */
public class Setor {
    private int id;
    private String nome;
    private final List<Usuario> usuarios = new ArrayList<>();

    public Setor(String nome) {
        this.nome = nome;
    }

    public Setor(int id, String nome) {
        this(nome);
        this.id = id;
    }

    public void adicionarUsuario(Usuario u) {
        if (!usuarios.contains(u)) {
            usuarios.add(u);
        }
    }

    public void removerUsuario(Usuario u) {
        usuarios.remove(u);
    }

    /** Retorna uma cópia somente leitura, protegendo a lista interna. */
    public List<Usuario> getUsuarios() {
        return List.copyOf(usuarios);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public String toString() {
        return id + " - " + nome;
    }
}
