package com.mazur.controle_de_horas.service;

import com.mazur.controle_de_horas.dto.RegistroHorasRequest;
import com.mazur.controle_de_horas.dto.RegistroHorasResponse;
import com.mazur.controle_de_horas.dto.RelatorioProjetoResponse;
import com.mazur.controle_de_horas.dto.RelatorioUsuarioResponse;
import com.mazur.controle_de_horas.mapper.RegistroHorasMapper;
import com.mazur.controle_de_horas.model.*;
import com.mazur.controle_de_horas.repository.EquipeRepository;
import com.mazur.controle_de_horas.repository.ProjetoRepository;
import com.mazur.controle_de_horas.repository.RegistroDeHorasRepository;
import com.mazur.controle_de_horas.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroHorasServiceTest {

    @Mock
    private RegistroDeHorasRepository registroDeHorasRepository;

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RegistroHorasMapper registroHorasMapper;

    @InjectMocks
    private RegistroHorasService registroHorasService;

    private Usuario usuario(Long id, String nome) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(nome);
        return usuario;
    }

    private Projeto projetoComMembro() {
        Projeto projeto = new Projeto();
        projeto.setId(10L);
        projeto.setNome("Horizonte");
        projeto.setCriador(usuario(1L, "Gustavo"));
        return projeto;
    }

    private RegistroHorasRequest request(String horas, Long projetoId, Long equipeId) {
        return new RegistroHorasRequest("Estudando MYSQL", horas, LocalDate.of(2026, 8, 22), projetoId, equipeId, 1L);
    }

    @Test
    void deveConverterHorasParaMinutosAoRegistrar() {
        Projeto projeto = projetoComMembro();
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "Gustavo")));
        when(registroHorasMapper.paraEntidade(any())).thenAnswer(inv -> {
            RegistroHorasRequest req = inv.getArgument(0, RegistroHorasRequest.class);
            RegistroDeHoras entidade = new RegistroDeHoras();
            entidade.setDescricao(req.descricao());
            entidade.setData(req.data());
            return entidade;
        });
        when(registroDeHorasRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(registroHorasMapper.paraDTO(any())).thenReturn(new RegistroHorasResponse(
                1L, "Estudando MYSQL", "1:30", LocalDate.of(2026, 8, 22),
                "Horizonte", null, "Gustavo"));

        registroHorasService.registrar(request("1:30", 10L, null));

        ArgumentCaptor<RegistroDeHoras> captor = ArgumentCaptor.forClass(RegistroDeHoras.class);
        verify(registroDeHorasRepository).save(captor.capture());
        assertThat(captor.getValue().getHorasMinutos()).isEqualTo(90);
        assertThat(captor.getValue().getData()).isEqualTo(LocalDate.of(2026, 8, 22));
    }

    @Test
    void aceitaFormatosVariadosDeHoras() {
        assertThat(RegistroHorasService.parseHoras("2")).isEqualTo(120);
        assertThat(RegistroHorasService.parseHoras("1;30")).isEqualTo(90);
        assertThat(RegistroHorasService.parseHoras("1,15")).isEqualTo(75);
        assertThat(RegistroHorasService.parseHoras("0:45")).isEqualTo(45);
    }

    @Test
    void rejeitaFormatosInvalidosDeHoras() {
        assertThatThrownBy(() -> registroHorasService.registrar(request("abc", 10L, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Formato de horas inválido");
        assertThatThrownBy(() -> registroHorasService.registrar(request("", 10L, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Horas é obrigatória");
        assertThatThrownBy(() -> registroHorasService.registrar(request("0:00", 10L, null)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> registroHorasService.registrar(request("1:90", 10L, null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void naoMembroNaoPodeRegistrarHoras() {
        Projeto projeto = projetoComMembro();
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));
        when(usuarioRepository.findById(9L)).thenReturn(Optional.of(usuario(9L, "Fora do projeto")));

        RegistroHorasRequest request = new RegistroHorasRequest(
                "desc", "1:30", LocalDate.of(2026, 8, 22), 10L, null, 9L);

        assertThatThrownBy(() -> registroHorasService.registrar(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precisa ser membro do projeto");
    }

    @Test
    void naoPodeRegistrarEmEquipeDeOutroProjeto() {
        Projeto projeto = projetoComMembro();
        Projeto outroProjeto = new Projeto();
        outroProjeto.setId(99L);
        Equipe equipe = new Equipe();
        equipe.setId(20L);
        equipe.setNome("Time Alfa");
        equipe.setProjeto(outroProjeto);

        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "Gustavo")));
        when(equipeRepository.findById(20L)).thenReturn(Optional.of(equipe));

        assertThatThrownBy(() -> registroHorasService.registrar(request("1:30", 10L, 20L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pertence ao projeto");
    }

    @Test
    void naoPodeRegistrarEmEquipeQueNaoParticipa() {
        Projeto projeto = projetoComMembro();
        Equipe equipe = new Equipe();
        equipe.setId(20L);
        equipe.setProjeto(projeto);
        equipe.getMembros().add(usuario(2L, "Ana"));

        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "Gustavo")));
        when(equipeRepository.findById(20L)).thenReturn(Optional.of(equipe));

        assertThatThrownBy(() -> registroHorasService.registrar(request("1:30", 10L, 20L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("membro da equipe");
    }

    @Test
    void projetoInexistenteLancaExcecao() {
        when(projetoRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registroHorasService.registrar(request("1:30", 77L, null)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("77");
    }

    @Test
    void somenteCriadorVeORelatorio() {
        Projeto projeto = projetoComMembro();
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));

        assertThatThrownBy(() -> registroHorasService.gerarRelatorio(10L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Apenas o criador do projeto pode ver o relatório");
    }

    @Test
    void relatorioSomaTempoPorUsuario() {
        Projeto projeto = projetoComMembro();
        Usuario gustavo = usuario(1L, "Gustavo");
        Usuario ana = usuario(2L, "Ana");

        RegistroDeHoras r1 = new RegistroDeHoras();
        r1.setDescricao("MYSQL");
        r1.setHorasMinutos(90);
        r1.setData(LocalDate.of(2026, 8, 21));
        r1.setProjeto(projeto);
        r1.setUsuario(gustavo);

        RegistroDeHoras r2 = new RegistroDeHoras();
        r2.setDescricao("Spring");
        r2.setHorasMinutos(30);
        r2.setData(LocalDate.of(2026, 8, 22));
        r2.setProjeto(projeto);
        r2.setUsuario(gustavo);

        RegistroDeHoras r3 = new RegistroDeHoras();
        r3.setDescricao("Front");
        r3.setHorasMinutos(60);
        r3.setData(LocalDate.of(2026, 8, 22));
        r3.setProjeto(projeto);
        r3.setUsuario(ana);

        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));
        when(registroDeHorasRepository.findByProjetoId(10L)).thenReturn(List.of(r1, r2, r3));
        when(registroHorasMapper.formatarHoras(180)).thenReturn("3:00");
        when(registroHorasMapper.formatarHoras(120)).thenReturn("2:00");
        when(registroHorasMapper.formatarHoras(60)).thenReturn("1:00");
        when(registroHorasMapper.paraListaDTO(List.of(r1, r2, r3))).thenReturn(List.of(
                new RegistroHorasResponse(1L, "MYSQL", "1:30", r1.getData(), "Horizonte", null, "Gustavo"),
                new RegistroHorasResponse(2L, "Spring", "0:30", r2.getData(), "Horizonte", null, "Gustavo"),
                new RegistroHorasResponse(3L, "Front", "1:00", r3.getData(), "Horizonte", null, "Ana")));

        RelatorioProjetoResponse relatorio = registroHorasService.gerarRelatorio(10L, 1L);

        assertThat(relatorio.tempoTotal()).isEqualTo("3:00");
        assertThat(relatorio.porUsuario()).extracting(RelatorioUsuarioResponse::usuarioNome)
                .containsExactly("Gustavo", "Ana");
        assertThat(relatorio.porUsuario().get(0).tempoTotal()).isEqualTo("2:00");
        assertThat(relatorio.porUsuario().get(0).quantidadeRegistros()).isEqualTo(2L);
        assertThat(relatorio.porUsuario().get(1).tempoTotal()).isEqualTo("1:00");
        assertThat(relatorio.porUsuario().get(1).quantidadeRegistros()).isEqualTo(1L);
        assertThat(relatorio.registros()).hasSize(3);
    }
}
