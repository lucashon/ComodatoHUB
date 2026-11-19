package br.edu.ifal.comodatohub.view.web;

import br.edu.ifal.comodatohub.bo.UsuarioBO;
import br.edu.ifal.comodatohub.exception.NegocioException;
import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.vo.Usuario;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Servidor web simples usando o HttpServer que já vem no Java (sem bibliotecas extras).
 * Recebe as requisições do navegador, identifica o usuário logado e chama a página certa.
 */
public class ServidorWeb {

    private static final String COOKIE = "SESSAO";

    private final int porta;
    private final UsuarioBO usuarioBO = new UsuarioBO();
    private final List<PaginaWeb> paginas = List.of(
            new EquipamentosPagina(),
            new MeusEmprestimosPagina(),
            new MeuSetorPagina(),
            new EmprestimosPagina(),
            new UsuariosPagina(),
            new SetoresPagina());
    /** Map: código da sessão (cookie) -> usuário logado. */
    private final Map<String, Usuario> sessoes = new HashMap<>();

    public ServidorWeb(int porta) {
        this.porta = porta;
    }

    public void iniciar() throws IOException {
        HttpServer servidor = HttpServer.create(new InetSocketAddress("localhost", porta), 0);
        servidor.createContext("/", this::tratar);
        servidor.start(); // atende uma requisição por vez (adequado para o banco SQLite)
    }

    // ---------------------------------------------------------------- roteamento

    private void tratar(HttpExchange ex) throws IOException {
        try {
            String caminho = ex.getRequestURI().getPath();
            boolean post = "POST".equalsIgnoreCase(ex.getRequestMethod());
            Map<String, String> params = lerParametros(ex, post);

            if (caminho.equals("/estilo.css")) {
                enviarCss(ex);
                return;
            }
            if (caminho.equals("/login")) {
                login(ex, post, params);
                return;
            }
            if (caminho.equals("/sair")) {
                sessoes.remove(lerCookie(ex));
                redirecionar(ex, "/login");
                return;
            }

            Usuario usuario = sessoes.get(lerCookie(ex));
            if (usuario == null) {
                redirecionar(ex, "/login");
                return;
            }
            if (caminho.equals("/")) {
                redirecionar(ex, "/equipamentos");
                return;
            }
            Requisicao req = new Requisicao(usuario, params);
            if (post) {
                executarAcao(ex, caminho, req);
            } else {
                exibirPagina(ex, caminho, req);
            }
        } catch (RuntimeException e) {
            // erro inesperado: mostra uma mensagem em vez de derrubar o servidor
            enviarHtml(ex, 500, "<p>Erro inesperado: " + Html.esc(e.getMessage()) + "</p><a href=\"/\">Voltar</a>");
        }
    }

    private void login(HttpExchange ex, boolean post, Map<String, String> params) throws IOException {
        if (!post) {
            enviarHtml(ex, 200, Html.paginaLogin(params.get("erro")));
            return;
        }
        try {
            Usuario u = usuarioBO.autenticar(params.get("email"), params.get("senha"));
            String codigo = UUID.randomUUID().toString();
            sessoes.put(codigo, u);
            ex.getResponseHeaders().add("Set-Cookie", COOKIE + "=" + codigo + "; Path=/; HttpOnly");
            redirecionar(ex, "/equipamentos");
        } catch (NegocioException e) {
            enviarHtml(ex, 200, Html.paginaLogin(e.getMessage()));
        } catch (PersistenciaException e) {
            enviarHtml(ex, 200, Html.paginaLogin("Falha no banco de dados: " + e.getMessage()));
        }
    }

    private void exibirPagina(HttpExchange ex, String caminho, Requisicao req) throws IOException {
        PaginaWeb pagina = buscarPagina(caminho, req.getUsuario());
        if (pagina == null) {
            redirecionar(ex, "/equipamentos");
            return;
        }
        String corpo;
        String erro = req.get("erro");
        try {
            corpo = pagina.conteudo(req);
        } catch (NegocioException e) {
            corpo = "";
            erro = e.getMessage();
        } catch (PersistenciaException e) {
            corpo = "";
            erro = "Falha no banco de dados: " + e.getMessage();
        }
        enviarHtml(ex, 200, Html.layout(req.getUsuario(), menu(req.getUsuario()), pagina, corpo, req.get("msg"), erro));
    }

    /** POST /pagina/acao: executa a ação e volta para a página com a mensagem. */
    private void executarAcao(HttpExchange ex, String caminho, Requisicao req) throws IOException {
        int barra = caminho.lastIndexOf('/');
        PaginaWeb pagina = buscarPagina(caminho.substring(0, barra), req.getUsuario());
        AcaoWeb acao = pagina == null ? null : pagina.buscarAcao(caminho.substring(barra + 1));
        if (acao == null) {
            redirecionar(ex, "/equipamentos");
            return;
        }
        String destino = pagina.caminho();
        try {
            redirecionar(ex, destino + "?msg=" + codificar(acao.executar(req)));
        } catch (NegocioException e) {
            redirecionar(ex, destino + "?erro=" + codificar(e.getMessage()));
        } catch (PersistenciaException e) {
            redirecionar(ex, destino + "?erro=" + codificar("Falha no banco de dados: " + e.getMessage()));
        }
    }

    /** Polimorfismo: cada página responde se o usuário pode acessá-la. */
    private List<PaginaWeb> menu(Usuario usuario) {
        List<PaginaWeb> menu = new ArrayList<>();
        for (PaginaWeb p : paginas) {
            if (p.permitido(usuario)) menu.add(p);
        }
        return menu;
    }

    private PaginaWeb buscarPagina(String caminho, Usuario usuario) {
        for (PaginaWeb p : menu(usuario)) {
            if (p.caminho().equals(caminho)) return p;
        }
        return null;
    }

    // ---------------------------------------------------------------- HTTP

    private Map<String, String> lerParametros(HttpExchange ex, boolean post) throws IOException {
        Map<String, String> params = new HashMap<>();
        decodificar(ex.getRequestURI().getRawQuery(), params);
        if (post) {
            try (InputStream in = ex.getRequestBody()) {
                decodificar(new String(in.readAllBytes(), StandardCharsets.UTF_8), params);
            }
        }
        return params;
    }

    private void decodificar(String texto, Map<String, String> params) {
        if (texto == null || texto.isEmpty()) return;
        for (String par : texto.split("&")) {
            String[] kv = par.split("=", 2);
            String chave = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String valor = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            params.put(chave, valor);
        }
    }

    private String lerCookie(HttpExchange ex) {
        List<String> cookies = ex.getRequestHeaders().get("Cookie");
        if (cookies == null) return null;
        for (String linha : cookies) {
            for (String c : linha.split(";")) {
                String[] kv = c.trim().split("=", 2);
                if (kv.length == 2 && kv[0].equals(COOKIE)) return kv[1];
            }
        }
        return null;
    }

    private static String codificar(String texto) {
        return URLEncoder.encode(texto, StandardCharsets.UTF_8);
    }

    private void redirecionar(HttpExchange ex, String destino) throws IOException {
        ex.getResponseHeaders().add("Location", destino);
        ex.sendResponseHeaders(303, -1);
        ex.close();
    }

    private void enviarHtml(HttpExchange ex, int status, String html) throws IOException {
        enviar(ex, status, "text/html; charset=UTF-8", html.getBytes(StandardCharsets.UTF_8));
    }

    private void enviarCss(HttpExchange ex) throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/web/estilo.css")) {
            byte[] css = in == null ? new byte[0] : in.readAllBytes();
            enviar(ex, 200, "text/css; charset=UTF-8", css);
        }
    }

    private void enviar(HttpExchange ex, int status, String tipo, byte[] corpo) throws IOException {
        ex.getResponseHeaders().add("Content-Type", tipo);
        ex.sendResponseHeaders(status, corpo.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(corpo);
        }
    }
}
