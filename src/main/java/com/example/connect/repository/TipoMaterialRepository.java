package com.example.connect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.connect.model.TipoMaterial;

public interface TipoMaterialRepository extends JpaRepository<TipoMaterial, Integer> {
}