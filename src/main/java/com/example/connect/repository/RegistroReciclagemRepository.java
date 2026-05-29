package com.example.connect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.connect.model.RegistroReciclagem;
import com.example.connect.model.StatusReciclagem;

@Repository
public interface RegistroReciclagemRepository extends JpaRepository<RegistroReciclagem, Integer> {

    boolean existsByQrCode(String qrCode);

    List<RegistroReciclagem> findByUsuarioIdUsuarioOrderByDataRegistroDesc(Integer idUsuario);

    List<RegistroReciclagem> findByStatus(StatusReciclagem status);

    List<RegistroReciclagem> findByCatadorIdUsuarioAndStatusOrderByDataRegistroAsc(
            Integer idCatador, StatusReciclagem status);

    List<RegistroReciclagem> findByStatusOrderByDataRegistroAsc(StatusReciclagem status);

    /**
     * Busca o histórico de validações feitas por um catador.
     *
     * Usa query nativa para evitar o cast incorreto que o Hibernate gera
     * ao comparar ENUMs do PostgreSQL em JPQL (::StatusReciclagem em vez de ::status_reciclagem).
     */
    @Query(value = """
        SELECT * FROM registro_reciclagem
        WHERE id_catador_validador = :idCatador
          AND status <> 'PENDENTE'
        ORDER BY data_validacao DESC
    """, nativeQuery = true)
    List<RegistroReciclagem> findValidacoesPorCatador(@Param("idCatador") Integer idCatador);
}