package com.example.connect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.connect.model.StatusUsuario;
import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    Usuario findByEmail(String email);

    List<Usuario> findByNomeContaining(String nome);

    List<Usuario> findByTipo(TipoUsuario tipo);

    List<Usuario> findByStatus(StatusUsuario status);
}