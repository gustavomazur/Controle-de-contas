package com.mazur.controle_de_horas.service;

import com.mazur.controle_de_horas.dto.*;
import com.mazur.controle_de_horas.mapper.NotificacaoMapper;
import com.mazur.controle_de_horas.mapper.ProjetoMapper;
import com.mazur.controle_de_horas.model.Notificacao;
import com.mazur.controle_de_horas.model.Projeto;
import com.mazur.controle_de_horas.model.StatusNotificacao;
import com.mazur.controle_de_horas.model.Usuario;
import com.mazur.controle_de_horas.repository.NotificacaoRepository;
import com.mazur.controle_de_horas.repository.ProjetoRepository;
import com.mazur.controle_de_horas.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @Mock
    private ProjetoMapper projetoMapper;

    @Mock
    private NotificacaoMapper notificacaoMapper;

    @InjectMocks
    private ProjetoService projetoService;

    private Usuario usuario(Long id, String nome) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(nome);
        return usuario;
    }

    private Projeto projeto(Long id, Usuario criador) {
        Projeto projeto = new Projeto();
        projeto.setId(id);
        projeto.setNome("Horizonte");
        projeto.setCriador(criador);
        return projeto;
    }

    @Test
    void deveCriarProjetoECriadorViraMembro() {
        Usuario criador = usuario(1L, "Gustavo");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(criador));
        when(projetoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(projetoMapper.paraDTO(any())).thenReturn(new ProjetoResponse(
                1L, "Horizonte", null, 1L, "Gustavo", List.of("Gustavo")));

        ProjetoResponse response = projetoService.criarProjeto(new ProjetoRequest("Horizonte", null, 1L));

        ArgumentCaptor<Projeto> captor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(captor.capture());
        assertThat(captor.getValue().getMembros()).contains(criador);
        assertThat(response.criadorNome()).isEqualTo("Gustavo");
    }

    @Test
    void naoDeveCriarProjetoSemNome() {
        assertThatThrownBy(() -> projetoService.criarProjeto(new ProjetoRequest(" ", null, 1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome do projeto não pode estar vazio");
    }

    @Test
    void naoDeveCriarProjetoComCriadorInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetoService.criarProjeto(new ProjetoRequest("Horizonte", null, 99L)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveListarProjetosDoUsuarioParaOMenu() {
        Usuario criador = usuario(1L, "Gustavo");
        when(projetoRepository.findAllDoUsuario(1L)).thenReturn(List.of(projeto(10L, criador)));
        when(projetoMapper.paraListaMenuDTO(any())).thenReturn(List.of(new ProjetoMenuResponse(10L, "Horizonte")));

        List<ProjetoMenuResponse> menu = projetoService.listarProjetosDoUsuario(1L);

        assertThat(menu).extracting(ProjetoMenuResponse::nome).containsExactly("Horizonte");
    }

    @Test
    void deveAdicionarMembroCriandoNotificacaoPendente() {
        Usuario criador = usuario(1L, "Gustavo");
        Usuario convidado = usuario(2L, "Ana");
        Projeto projeto = projeto(10L, criador);

        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));
        when(usuarioRepository.findByNomeIgnoreCase("Ana")).thenReturn(Optional.of(convidado));
        when(notificacaoRepository.findByDestinatarioIdAndStatus(2L, StatusNotificacao.PENDENTE))
                .thenReturn(List.of());
        when(notificacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(notificacaoMapper.paraDTO(any())).thenReturn(new NotificacaoResponse(
                5L, "Ana", "Horizonte", StatusNotificacao.PENDENTE, null));

        NotificacaoResponse response = projetoService.adicionarMembro(10L, new MembroRequest("Ana"));

        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(notificacaoRepository).save(captor.capture());
        assertThat(captor.getValue().getDestinatario()).isEqualTo(convidado);
        assertThat(captor.getValue().getStatus()).isEqualTo(StatusNotificacao.PENDENTE);
        assertThat(response.destinatarioNome()).isEqualTo("Ana");
    }

    @Test
    void naoDeveAdicionarMembroQueJaEstaNoProjeto() {
        Usuario criador = usuario(1L, "Gustavo");
        Projeto projeto = projeto(10L, criador);
        projeto.getMembros().add(usuario(2L, "Ana"));

        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));
        when(usuarioRepository.findByNomeIgnoreCase("Ana")).thenReturn(Optional.of(usuario(2L, "Ana")));

        assertThatThrownBy(() -> projetoService.adicionarMembro(10L, new MembroRequest("Ana")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já é membro do projeto");
    }

    @Test
    void naoDeveAdicionarUsuarioInexistente() {
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto(10L, usuario(1L, "Gustavo"))));
        when(usuarioRepository.findByNomeIgnoreCase("Fulano")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetoService.adicionarMembro(10L, new MembroRequest("Fulano")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Fulano");
    }
}
