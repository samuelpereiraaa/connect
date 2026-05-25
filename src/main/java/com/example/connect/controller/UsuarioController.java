package com.example.connect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.connect.model.Endereco;
import com.example.connect.model.Usuario;
import com.example.connect.service.UsuarioService;

@Controller
public class UsuarioController {

	@Autowired
	private UsuarioService service;

	@GetMapping("/cadastro")
	public String telaCadastro() {
		return "cadastro";
		
	}

	@PostMapping("/registro")
	public String cadastrar(
	        @ModelAttribute Usuario usuario,
	        @ModelAttribute Endereco endereco){

	    service.cadastrar(usuario, endereco);

	    return "redirect:/login";
	}
	
	
	@PostMapping("/inativar/{id}")
	public String inativar(@PathVariable Integer id){

	    service.inativar(id);

	    return "redirect:/usuarios";
	}
	
	
	
	
	
}
