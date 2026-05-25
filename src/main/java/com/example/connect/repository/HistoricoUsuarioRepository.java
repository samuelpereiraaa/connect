package com.example.connect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.connect.model.HistoricoUsuario;

public interface HistoricoUsuarioRepository extends JpaRepository<HistoricoUsuario, Integer> {

    List<HistoricoUsuario> findByUsuarioIdUsuarioOrderByDataAcaoDesc(Integer idUsuario);
}
