package com.example.connect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.connect.model.PontoColeta;
import com.example.connect.model.StatusPontoColeta;

@Repository
public interface PontoColetaRepository extends JpaRepository<PontoColeta, Integer> {

    List<PontoColeta> findByNomeContainingIgnoreCase(String nome);

    List<PontoColeta> findByStatus(StatusPontoColeta status);
}
