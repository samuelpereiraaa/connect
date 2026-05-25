package com.example.connect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.connect.model.Usuario;
import com.example.connect.repository.UsuarioRepository;
import com.example.connect.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/entrar")
    public String login(
            @RequestParam String email,
            @RequestParam String senha,
            HttpSession session) {

        if (service.login(email, senha)) {
            // Salva o usuário na sessão para uso em todo o sistema
            Usuario usuario = usuarioRepository.findByEmail(email);
            session.setAttribute("usuarioLogado", usuario);
            return "redirect:/home";
        }

        return "redirect:/login?erro=true";
    }
}