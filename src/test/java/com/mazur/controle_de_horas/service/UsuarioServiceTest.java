package com.mazur.controle_de_horas.service;

import com.mazur.controle_de_horas.dto.UsuarioRequest;
import com.mazur.controle_de_horas.dto.UsuarioResponse;
import com.mazur.controle_de_horas.mapper.UsuarioMapper;
import com.mazur.controle_de_horas.model.Usuario;
import com.mazur.controle_de_horas.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario(Long id, String nome) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(nome);
        return usuario;
    }

    @Test
    void deveCriarUsuario() {
        UsuarioRequest request = new UsuarioRequest("Gustavo", "gustavo@teste.com", "123456");
        when(usuarioRepository.findByNomeIgnoreCase("Gustavo")).thenReturn(Optional.empty());
        when(usuarioMapper.paraEntidade(request)).thenReturn(new Usuario());
        when(usuarioRepository.save(any())).thenReturn(usuario(1L, "Gustavo"));
        when(usuarioMapper.paraDTO(any())).thenReturn(new UsuarioResponse(1L, "Gustavo", "gustavo@teste.com"));

        UsuarioResponse response = usuarioService.criarUsuario(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Gustavo");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void naoDeveCriarUsuarioComNomeVazio() {
        assertThatThrownBy(() -> usuarioService.criarUsuario(new UsuarioRequest("  ", "g@t.com", "123456")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome do usuário não pode estar vazio");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarUsuarioComNomeDuplicado() {
        when(usuarioRepository.findByNomeIgnoreCase("Gustavo"))
                .thenReturn(Optional.of(usuario(1L, "Gustavo")));

        assertThatThrownBy(() -> usuarioService.criarUsuario(new UsuarioRequest("Gustavo", "g@t.com", "123456")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Já existe um usuário com esse nome");
    }

    @Test
    void deveBuscarPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "Gustavo")));
        when(usuarioMapper.paraDTO(any())).thenReturn(new UsuarioResponse(1L, "Gustavo", "gustavo@teste.com"));

        UsuarioResponse response = usuarioService.findById(1L);

        assertThat(response.nome()).isEqualTo("Gustavo");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveListarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario(1L, "Gustavo"), usuario(2L, "Ana")));
        when(usuarioMapper.paraDTO(any())).thenReturn(new UsuarioResponse(1L, "Gustavo", "gustavo@teste.com"));

        List<UsuarioResponse> lista = usuarioService.listar();

        assertThat(lista).hasSize(2);
    }

    @Test
    void deveBuscarPorNome() {
        when(usuarioRepository.findByNomeIgnoreCase("Ana")).thenReturn(Optional.of(usuario(2L, "Ana")));
        when(usuarioMapper.paraDTO(any())).thenReturn(new UsuarioResponse(2L, "Ana", "ana@teste.com"));

        UsuarioResponse response = usuarioService.buscarPorNome("Ana");

        assertThat(response.id()).isEqualTo(2L);
    }
}
