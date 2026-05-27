package com.example.connect.model;

/**
 * RF07 – Enum de níveis de pontuação.
 * Espelha o tipo ENUM 'nivel_usuario' do banco e a tabela nivel_pontuacao:
 *   INICIANTE  0–249 pts
 *   BRONZE   250–499 pts
 *   PRATA    500–1099 pts
 *   OURO    1100–2499 pts
 *   PLATINA 2500+ pts
 */
public enum NivelUsuario {
    INICIANTE,
    BRONZE,
    PRATA,
    OURO,
    PLATINA
}
