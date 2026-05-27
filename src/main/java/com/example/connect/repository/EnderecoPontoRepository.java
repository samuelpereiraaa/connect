package com.example.connect.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.connect.model.EnderecoPonto;

public interface EnderecoPontoRepository extends JpaRepository<EnderecoPonto, Integer> {

    Optional<EnderecoPonto> findByPontoColetaIdPonto(Integer idPonto);
}
