package com.example.connect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.connect.model.Pontuacao;

/**
 * RF07 – Repositório de pontuação.
 * A escrita é feita pela trigger do banco; o Java apenas consulta.
 */
@Repository
public interface PontuacaoRepository extends JpaRepository<Pontuacao, Integer> {

    /** RF07 – Busca a pontuação do usuário logado. */
    Optional<Pontuacao> findByUsuarioIdUsuario(Integer idUsuario);
}
