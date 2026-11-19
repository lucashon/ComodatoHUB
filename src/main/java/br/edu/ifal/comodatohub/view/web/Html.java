package br.edu.ifal.comodatohub.view.web;

import br.edu.ifal.comodatohub.vo.Emprestimo;
import br.edu.ifal.comodatohub.vo.Usuario;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Funções auxiliares para montar o HTML das páginas. */
public final class Html {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Html() {
    }

    /** Evita que textos digitados pelo usuário quebrem a página (proteção contra HTML injetado). */
    public static String esc(Object valor) {
        if (valor == null) {
            return "";
        }
        return valor.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    public static String data(LocalDate d) {
        return d == null ? "-" : d.format(FORMATO);
    }

    public static String observacao(Emprestimo e) {
        if (e.estaAtrasado()) return "<b>ATRASADO</b>";
        if (e.getOcorrencia() != null) return "Devolvido: " + esc(e.getOcorrencia());
        return "";
    }

    /** Página completa com cabeçalho, menu e mensagens. */
    public static String layout(Usuario usuario, List<PaginaWeb> menu, PaginaWeb atual,
                                String corpo, String msg, String erro) {
        StringBuilder nav = new StringBuilder();
        for (PaginaWeb p : menu) {
            nav.append("<a href=\"").append(p.caminho()).append("\"")
               .append(p == atual ? " class=\"ativo\"" : "").append(">")
               .append(esc(p.titulo())).append("</a>");
        }
        return inicio(atual.titulo())
                + "<header><strong>ComodatoHub</strong><nav>" + nav + "</nav>"
                + "<span>" + esc(usuario.getNome()) + " (" + esc(usuario.getPerfil()) + ") "
                + "<a href=\"/sair\">Sair</a></span></header>"
                + "<main><h1>" + esc(atual.titulo()) + "</h1>"
                + mensagens(msg, erro) + corpo + "</main></body></html>";
    }

    public static String paginaLogin(String erro) {
        return inicio("Login")
                + "<div class=\"login\"><h1>ComodatoHub</h1><p>Empréstimo de equipamentos</p>"
                + mensagens(null, erro)
                + "<form method=\"post\" action=\"/login\">"
                + "<label>E-mail<input name=\"email\" type=\"email\" required autofocus></label>"
                + "<label>Senha<input name=\"senha\" type=\"password\" required></label>"
                + "<button type=\"submit\">Entrar</button></form></div></body></html>";
    }

    private static String inicio(String titulo) {
        return "<!DOCTYPE html><html lang=\"pt-BR\"><head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                + "<title>" + esc(titulo) + " - ComodatoHub</title>"
                + "<link rel=\"stylesheet\" href=\"/estilo.css\"></head><body>";
    }

    private static String mensagens(String msg, String erro) {
        String html = "";
        if (msg != null && !msg.isBlank()) html += "<p class=\"msg\">" + esc(msg) + "</p>";
        if (erro != null && !erro.isBlank()) html += "<p class=\"erro\">" + esc(erro) + "</p>";
        return html;
    }

    /** Tabela: o cabeçalho é escapado; as células já devem vir em HTML. */
    public static String tabela(String[] cabecalho, List<String[]> linhas) {
        StringBuilder sb = new StringBuilder("<div class=\"tabela\"><table><thead><tr>");
        for (String c : cabecalho) sb.append("<th>").append(esc(c)).append("</th>");
        sb.append("</tr></thead><tbody>");
        if (linhas.isEmpty()) {
            sb.append("<tr><td colspan=\"").append(cabecalho.length).append("\">Nenhum registro.</td></tr>");
        }
        for (String[] linha : linhas) {
            sb.append("<tr>");
            for (String celula : linha) sb.append("<td>").append(celula).append("</td>");
            sb.append("</tr>");
        }
        return sb.append("</tbody></table></div>").toString();
    }

    /** Botão que envia um formulário com o id do registro. */
    public static String botao(String acao, int id, String texto) {
        return "<form method=\"post\" action=\"" + acao + "\" class=\"inline\">"
                + "<input type=\"hidden\" name=\"id\" value=\"" + id + "\">"
                + "<button type=\"submit\">" + esc(texto) + "</button></form>";
    }

    /** Botão que pede confirmação antes de enviar. */
    public static String botaoConfirmar(String acao, int id, String texto, String pergunta) {
        return "<form method=\"post\" action=\"" + acao + "\" class=\"inline\" onsubmit=\"return confirm('"
                + esc(pergunta) + "')\"><input type=\"hidden\" name=\"id\" value=\"" + id + "\">"
                + "<button type=\"submit\">" + esc(texto) + "</button></form>";
    }

    public static String link(String href, String texto) {
        return "<a href=\"" + href + "\">" + esc(texto) + "</a>";
    }

    public static String campo(String rotulo, String nome, String valor) {
        return "<label>" + esc(rotulo) + "<input name=\"" + nome + "\" value=\"" + esc(valor) + "\"></label>";
    }

    /** Lista de opções; cada item é {valor, texto}. */
    public static String select(String rotulo, String nome, List<String[]> opcoes, String selecionado) {
        StringBuilder sb = new StringBuilder("<label>" + esc(rotulo) + "<select name=\"" + nome + "\">");
        for (String[] o : opcoes) {
            sb.append("<option value=\"").append(esc(o[0])).append("\"")
              .append(o[0].equals(selecionado) ? " selected" : "").append(">")
              .append(esc(o[1])).append("</option>");
        }
        return sb.append("</select></label>").toString();
    }

    /** Caixa com um formulário de cadastro/edição. */
    public static String formulario(String titulo, String acao, String campos, String textoBotao) {
        return "<section class=\"caixa\"><h2>" + esc(titulo) + "</h2>"
                + "<form method=\"post\" action=\"" + acao + "\" class=\"form\">" + campos
                + "<button type=\"submit\">" + esc(textoBotao) + "</button></form></section>";
    }

    public static String lista(String titulo, List<?> itens) {
        StringBuilder sb = new StringBuilder("<section class=\"caixa\"><h2>" + esc(titulo) + "</h2><ul>");
        if (itens.isEmpty()) sb.append("<li>Nenhum registro.</li>");
        for (Object i : itens) sb.append("<li>").append(esc(i)).append("</li>");
        return sb.append("</ul></section>").toString();
    }
}
