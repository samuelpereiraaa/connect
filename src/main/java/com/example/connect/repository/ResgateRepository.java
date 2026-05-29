package com.example.connect.repository;

import com.example.connect.model.Resgate;
import com.example.connect.model.enums.StatusResgate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RF10 – Repositório de resgates de recompensas.
 */
@Repository
public interface ResgateRepository extends JpaRepository<Resgate, Integer> {

    /** RF10 – Resgates do usuário, do mais recente ao mais antigo. */
    List<Resgate> findByUsuarioIdUsuarioOrderByDataResgateDesc(Integer idUsuario);

    /** RF10 – Todos os resgates de uma recompensa específica. */
    List<Resgate> findByRecompensaIdRecompensa(Integer idRecompensa);

    /** RF10 – Listagem geral ordenada por data (painel admin). */
    List<Resgate> findAllByOrderByDataResgateDesc();

    /** RF10 – Filtro por status (ex: listar apenas SOLICITADO para aprovação). */
    List<Resgate> findByStatusOrderByDataResgateDesc(StatusResgate status);
}
