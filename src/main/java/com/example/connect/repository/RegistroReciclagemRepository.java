package com.example.connect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.connect.model.RegistroReciclagem;
import com.example.connect.model.StatusReciclagem;

@Repository
public interface RegistroReciclagemRepository extends JpaRepository<RegistroReciclagem, Integer> {

    /** RF05 - Regra 2: verificação de unicidade do QR Code */
    boolean existsByQrCode(String qrCode);

    List<RegistroReciclagem> findByUsuarioIdUsuarioOrderByDataRegistroDesc(Integer idUsuario);

    List<RegistroReciclagem> findByStatus(StatusReciclagem status);
}
