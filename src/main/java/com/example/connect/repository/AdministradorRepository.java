package com.example.connect.repository;

import com.example.connect.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RF11 – Repositório de Administrador.
 */
@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    /** Encontra o registro de administrador vinculado a um usuário específico. */
    Optional<Administrador> findByUsuarioIdUsuario(Integer idUsuario);

    /** Verifica se já existe um administrador vinculado ao usuário. */
    boolean existsByUsuarioIdUsuario(Integer idUsuario);
}
