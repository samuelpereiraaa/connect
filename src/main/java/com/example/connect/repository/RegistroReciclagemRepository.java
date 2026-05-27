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

    
    @Query("""
        SELECT r FROM RegistroReciclagem r
        WHERE r.catadorValidador.idUsuario = :idCatador
          AND r.status <> com.example.connect.model.StatusReciclagem.PENDENTE
        ORDER BY r.dataValidacao DESC
    """)
    List<RegistroReciclagem> findValidacoesPorCatador(@Param("idCatador") Integer idCatador);
}
