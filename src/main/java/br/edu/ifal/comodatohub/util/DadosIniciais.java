package br.edu.ifal.comodatohub.util;

import br.edu.ifal.comodatohub.dao.EquipamentoDAO;
import br.edu.ifal.comodatohub.dao.SetorDAO;
import br.edu.ifal.comodatohub.dao.UsuarioDAO;
import br.edu.ifal.comodatohub.exception.PersistenciaException;
import br.edu.ifal.comodatohub.vo.*;

/** Insere dados de exemplo na primeira execução, para facilitar a demonstração. */
public final class DadosIniciais {

    private DadosIniciais() {
    }

    public static void popularSeVazio() throws PersistenciaException {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        if (!usuarioDAO.listarTodos().isEmpty()) {
            return;
        }
        SetorDAO setorDAO = new SetorDAO();
        Setor ti = new Setor("Tecnologia da Informação");
        Setor comercial = new Setor("Comercial");
        setorDAO.inserir(ti);
        setorDAO.inserir(comercial);

        Usuario admin = new Administrador("Administrador", "admin@empresa.com", "A001", "admin123");
        admin.setSetor(ti);
        Usuario gestor = new Gestor("Gabriela Gestora", "gestor@empresa.com", "G001", "gestor123");
        gestor.setSetor(comercial);
        Usuario colaborador = new Colaborador("Carlos Colaborador", "colaborador@empresa.com", "C001", "colab123");
        colaborador.setSetor(comercial);
        usuarioDAO.inserir(admin);
        usuarioDAO.inserir(gestor);
        usuarioDAO.inserir(colaborador);

        EquipamentoDAO equipamentoDAO = new EquipamentoDAO();
        equipamentoDAO.inserir(new Notebook("PAT-0001", "Notebook Dell Latitude", "Dell"));
        equipamentoDAO.inserir(new Notebook("PAT-0002", "Notebook Lenovo ThinkPad", "Lenovo"));
        equipamentoDAO.inserir(new Projetor("PAT-0003", "Projetor Epson PowerLite", "Epson"));
        equipamentoDAO.inserir(new Celular("PAT-0004", "Celular Samsung Galaxy A55", "Samsung"));
        equipamentoDAO.inserir(new KitFerramentas("PAT-0005", "Kit de ferramentas de rede", "Tramontina"));
    }
}
