package com.mazur.controle_de_horas.service;

import com.mazur.controle_de_horas.dto.EquipeRequest;
import com.mazur.controle_de_horas.dto.EquipeResponse;
import com.mazur.controle_de_horas.dto.MembroRequest;
import com.mazur.controle_de_horas.mapper.EquipeMapper;
import com.mazur.controle_de_horas.model.Equipe;
import com.mazur.controle_de_horas.model.Projeto;
import com.mazur.controle_de_horas.model.Usuario;
import com.mazur.controle_de_horas.repository.EquipeRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipeServiceTest {

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EquipeMapper equipeMapper;

    @InjectMocks
    private EquipeService equipeService;

    private Usuario usuario(Long id, String nome) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(nome);
        return usuario;
    }

    private Projeto projeto(Long id, Long criadorId) {
        Projeto projeto = new Projeto();
        projeto.setId(id);
        projeto.setNome("Horizonte");
        projeto.setCriador(usuario(criadorId, "Criador"));
        return projeto;
    }

    private Equipe equipe(Long id, Projeto projeto) {
        Equipe equipe = new Equipe();
        equipe.setId(id);
        equipe.setNome("Time Alfa");
        equipe.setProjeto(projeto);
        return equipe;
    }

    @Test
    void criadorDoProjetoPodeCriarEquipe() {
        Projeto projeto = projeto(10L, 1L);
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));
        when(equipeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(equipeMapper.paraDTO(any())).thenReturn(new EquipeResponse(
                1L, "Time Alfa", 10L, "Horizonte", List.of()));

        EquipeResponse response = equipeService.criarEquipe(new EquipeRequest("Time Alfa", 10L, 1L));

        ArgumentCaptor<Equipe> captor = ArgumentCaptor.forClass(Equipe.class);
        verify(equipeRepository).save(captor.capture());
        assertThat(captor.getValue().getProjeto()).isEqualTo(projeto);
        assertThat(response.nome()).isEqualTo("Time Alfa");
    }

    @Test
    void naoCriadorNaoPodeCriarEquipe() {
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto(10L, 1L)));

        assertThatThrownBy(() -> equipeService.criarEquipe(new EquipeRequest("Time B", 10L, 2L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Apenas o criador do projeto pode criar equipes");
    }

    @Test
    void naoDeveCriarEquipeSemNome() {
        assertThatThrownBy(() -> equipeService.criarEquipe(new EquipeRequest("", 10L, 1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome da equipe não pode estar vazio");
    }

    @Test
    void naoDeveCriarEquipeDeProjetoInexistente() {
        when(projetoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipeService.criarEquipe(new EquipeRequest("Time A", 99L, 1L)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveAdicionarMembroDoProjetoNaEquipe() {
        Projeto projeto = projeto(10L, 1L);
        Usuario membro = usuario(2L, "Ana");
        projeto.getMembros().add(membro);
        Equipe equipe = equipe(20L, projeto);

        when(equipeRepository.findById(20L)).thenReturn(Optional.of(equipe));
        when(usuarioRepository.findByNomeIgnoreCase("Ana")).thenReturn(Optional.of(membro));
        when(equipeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(equipeMapper.paraDTO(any())).thenReturn(new EquipeResponse(
                20L, "Time Alfa", 10L, "Horizonte", List.of("Ana")));

        EquipeResponse response = equipeService.adicionarMembro(20L, new MembroRequest("Ana"));

        assertThat(response.membrosNomes()).containsExactly("Ana");
    }

    @Test
    void naoDeveAdicionarUsuarioForaDoProjetoNaEquipe() {
        Projeto projeto = projeto(10L, 1L);
        Equipe equipe = equipe(20L, projeto);

        when(equipeRepository.findById(20L)).thenReturn(Optional.of(equipe));
        when(usuarioRepository.findByNomeIgnoreCase("Fulano")).thenReturn(Optional.of(usuario(9L, "Fulano")));

        assertThatThrownBy(() -> equipeService.adicionarMembro(20L, new MembroRequest("Fulano")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precisa ser membro do projeto");
    }

    @Test
    void naoDeveAdicionarMembroDuplicadoNaEquipe() {
        Projeto projeto = projeto(10L, 1L);
        Usuario membro = usuario(2L, "Ana");
        projeto.getMembros().add(membro);
        Equipe equipe = equipe(20L, projeto);
        equipe.getMembros().add(membro);

        when(equipeRepository.findById(20L)).thenReturn(Optional.of(equipe));
        when(usuarioRepository.findByNomeIgnoreCase("Ana")).thenReturn(Optional.of(membro));

        assertThatThrownBy(() -> equipeService.adicionarMembro(20L, new MembroRequest("Ana")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já é membro dessa equipe");
    }

    @Test
    void deveListarEquipesDoProjeto() {
        when(equipeRepository.findByProjetoId(10L)).thenReturn(List.of(equipe(20L, projeto(10L, 1L))));
        when(equipeMapper.paraListaDTO(any())).thenReturn(List.of(new EquipeResponse(
                20L, "Time Alfa", 10L, "Horizonte", List.of())));

        List<EquipeResponse> lista = equipeService.listarPorProjeto(10L);

        assertThat(lista).hasSize(1);
    }
}
