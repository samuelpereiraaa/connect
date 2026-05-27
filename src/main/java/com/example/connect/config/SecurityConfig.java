package com.example.connect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * RF03 – Configuração de segurança da aplicação.
 *
 * O Spring Security é mantido apenas para o BCryptPasswordEncoder.
 * O controle de acesso por tipo de usuário (USUARIO / CATADOR / ADMINISTRADOR)
 * é feito via sessão HTTP no LoginController e nos controllers protegidos,
 * pois o projeto utiliza autenticação manual (não UserDetailsService).
 */
@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // CSRF desabilitado (formulários Thymeleaf sem token CSRF configurado)
            .csrf(csrf -> csrf.disable())

            // Todas as requisições são permitidas pelo Spring Security;
            // o controle de acesso real é feito pelos controllers via HttpSession.
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // Desabilita o login padrão do Spring Security
            .formLogin(form -> form.disable())

            // Desabilita o HTTP Basic
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
