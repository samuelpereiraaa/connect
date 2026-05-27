package com.example.connect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.connect.model.Endereco;
import com.example.connect.model.Usuario;
import com.example.connect.service.UsuarioService;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // tela cadastro
    @GetMapping("/cadastro")
    public String telaCadastro() {
        return "cadastro";
    }

    // salvar cadastro
    @PostMapping("/registro")
    public String cadastrar(
            @ModelAttribute Usuario usuario,
            @ModelAttribute Endereco endereco,
            RedirectAttributes redirect) {

        try {
            service.cadastrar(usuario, endereco);
            redirect.addFlashAttribute("mensagem", "Cadastro realizado com sucesso! Faça login.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/registro";
        }

        return "redirect:/login";
    }

    // inativar usuário
    @PostMapping("/inativar/{id}")
    public String inativar(@PathVariable Integer id) {
        service.inativar(id);
        return "redirect:/usuarios";
    }
}
