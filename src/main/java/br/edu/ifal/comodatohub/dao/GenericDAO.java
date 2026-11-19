package br.edu.ifal.comodatohub.dao;

import br.edu.ifal.comodatohub.exception.PersistenciaException;

import java.util.List;

/** Contrato de CRUD seguido por todos os DAOs. */
public interface GenericDAO<T> {
    int inserir(T obj) throws PersistenciaException;           // INSERT
    T buscarPorId(int id) throws PersistenciaException;         // SELECT (retorna null se não existir)
    List<T> listarTodos() throws PersistenciaException;         // SELECT
    void atualizar(T obj) throws PersistenciaException;         // UPDATE
    void excluir(int id) throws PersistenciaException;          // DELETE
}
