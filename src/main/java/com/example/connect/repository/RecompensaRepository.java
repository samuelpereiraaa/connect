package com.example.connect.repository;

import com.example.connect.model.Recompensa;
import com.example.connect.model.StatusRecompensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecompensaRepository extends JpaRepository<Recompensa, Integer> {

    /** RF09 – todas, ordenadas por nome */
    List<Recompensa> findAllByOrderByNomeAsc();

    /** Filtra só por status */
    List<Recompensa> findByStatusOrderByNomeAsc(StatusRecompensa status);

    /** Filtra só por nome (contém, case-insensitive) */
    List<Recompensa> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    /** Filtra por nome E status ao mesmo tempo */
    List<Recompensa> findByNomeContainingIgnoreCaseAndStatusOrderByNomeAsc(
            String nome, StatusRecompensa status);
}