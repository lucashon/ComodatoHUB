package br.edu.ifal.comodatohub.view.web;

import br.edu.ifal.comodatohub.exception.NegocioException;
import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classe ABSTRATA de toda página do sistema.
 * Cada página concreta define seu endereço, título, quem pode acessá-la e o conteúdo (polimorfismo).
 */
public abstract class PaginaWeb {

    private final Map<String, AcaoWeb> acoes = new LinkedHashMap<>();

    /** Endereço da página, ex.: "/equipamentos". */
    public abstract String caminho();

    /** Texto exibido no menu e no título. */
    public abstract String titulo();

    /** HTML do conteúdo principal da página. */
    public abstract String conteudo(Requisicao req) throws NegocioException, PersistenciaException;

    /** Por padrão todos podem acessar. Páginas restritas sobrescrevem. */
    public boolean permitido(Usuario usuario) {
        return true;
    }

    /** Registra uma ação de formulário, acessível em POST caminho()/nome. */
    protected void acao(String nome, AcaoWeb acao) {
        acoes.put(nome, acao);
    }

    public AcaoWeb buscarAcao(String nome) {
        return acoes.get(nome);
    }

    /** Endereço completo de uma ação desta página. */
    protected String url(String nomeAcao) {
        return caminho() + "/" + nomeAcao;
    }
}
