package com.mazur.controle_de_horas.service;

import com.mazur.controle_de_horas.dto.UsuarioRequest;
import com.mazur.controle_de_horas.dto.UsuarioResponse;
import com.mazur.controle_de_horas.mapper.UsuarioMapper;
import com.mazur.controle_de_horas.model.Usuario;
import com.mazur.controle_de_horas.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public UsuarioResponse criarUsuario(UsuarioRequest request) {
        if (request.nome() == null || request.nome().isBlank()) {
            throw new IllegalArgumentException("Nome do usuário não pode estar vazio");
        }
        usuarioRepository.findByNomeIgnoreCase(request.nome().trim())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Já existe um usuário com esse nome");
                });
        Usuario usuario = usuarioMapper.paraEntidade(request);
        usuario.setNome(request.nome().trim());
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return usuarioMapper.paraDTO(usuarioSalvo);
    }

    public UsuarioResponse findById(long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
        return usuarioMapper.paraDTO(usuario);
    }

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::paraDTO)
                .toList();
    }

    public UsuarioResponse buscarPorNome(String nome) {
        Usuario usuario = usuarioRepository.findByNomeIgnoreCase(nome)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com nome: " + nome));
        return usuarioMapper.paraDTO(usuario);
    }
}
