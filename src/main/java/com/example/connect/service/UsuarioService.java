package com.example.connect.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.connect.model.Endereco;
import com.example.connect.model.StatusUsuario;
import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.repository.EnderecoRepository;
import com.example.connect.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	public void cadastrar(Usuario usuario,
			Endereco endereco) {
		
		 if (usuarioRepository.existsByCpf(usuario.getCpf())) {
	            throw new RuntimeException("Cpf ja cadastrado");
	        }
		
		if(usuarioRepository.existsByCpf(usuario.getCpf())) {
			throw new RuntimeException("Cpf ja cadastrado");
		}
		
		if(usuarioRepository.existsByEmail(usuario.getEmail())){
			throw new RuntimeException("Email ja cadastrado");
		}
		
		if(usuario.getSenha().length() < 8) {
			throw new RuntimeException("Senha extremamente fraca");
		}
		
		usuario.setSenha(
				encoder.encode(usuario.getSenha())
				);
		
		usuario.setStatus(StatusUsuario.ATIVO);
		usuario.setDataCadastro(LocalDateTime.now());
		
		Usuario salvo = 
				usuarioRepository.save(usuario);
		
		endereco.setUsuario(salvo);
		enderecoRepository.save(endereco);
		
		
		
	}
	
	public boolean login(String email, String senha) {

	    Usuario usuario = usuarioRepository.findByEmail(email);

	    if(usuario == null) {
	        return false;
	    }

	    return encoder.matches(senha, usuario.getSenha());
	}

	public void inativar(Integer id){

	    Usuario usuario = usuarioRepository
	            .findById(id)
	            .orElseThrow();

	    usuario.setStatus(StatusUsuario.INATIVO);

	    usuarioRepository.save(usuario);
	}
	
	public List<Usuario> buscarPorNome(String nome){
	    return usuarioRepository.findByNomeContaining(nome);
	}

	public List<Usuario> buscarPorTipo(TipoUsuario tipo){
	    return usuarioRepository.findByTipo(tipo);
	}

	public List<Usuario> buscarPorStatus(StatusUsuario status){
	    return usuarioRepository.findByStatus(status);
	}
	
	
	
	
	
}
