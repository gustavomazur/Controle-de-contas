package com.mazur.controle_de_horas.service;

import com.mazur.controle_de_horas.dto.NotificacaoResponse;
import com.mazur.controle_de_horas.mapper.NotificacaoMapper;
import com.mazur.controle_de_horas.model.*;
import com.mazur.controle_de_horas.repository.NotificacaoRepository;
import com.mazur.controle_de_horas.repository.ProjetoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private NotificacaoMapper notificacaoMapper;

    @InjectMocks
    private NotificacaoService notificacaoService;

    private Notificacao notificacaoPendente() {
        Usuario criador = new Usuario();
        criador.setId(1L);
        criador.setNome("Gustavo");

        Usuario convidado = new Usuario();
        convidado.setId(2L);
        convidado.setNome("Ana");

        Projeto projeto = new Projeto();
        projeto.setId(10L);
        projeto.setNome("Horizonte");
        projeto.setCriador(criador);

        Notificacao notificacao = new Notificacao();
        notificacao.setId(5L);
        notificacao.setDestinatario(convidado);
        notificacao.setProjeto(projeto);
        notificacao.setStatus(StatusNotificacao.PENDENTE);
        notificacao.setDataEnvio(LocalDateTime.now());
        return notificacao;
    }

    @Test
    void aceitarNotificacaoAdicionaMembroAoProjeto() {
        Notificacao notificacao = notificacaoPendente();
        when(notificacaoRepository.findById(5L)).thenReturn(Optional.of(notificacao));
        when(notificacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(projetoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(notificacaoMapper.paraDTO(any())).thenReturn(new NotificacaoResponse(
                5L, "Ana", "Horizonte", StatusNotificacao.ACEITA, null));

        NotificacaoResponse response = notificacaoService.aceitar(5L);

        assertThat(response.status()).isEqualTo(StatusNotificacao.ACEITA);
        assertThat(notificacao.getProjeto().getMembros()).contains(notificacao.getDestinatario());
        verify(projetoRepository).save(notificacao.getProjeto());
    }

    @Test
    void recusarNotificacaoNaoAdicionaMembro() {
        Notificacao notificacao = notificacaoPendente();
        when(notificacaoRepository.findById(5L)).thenReturn(Optional.of(notificacao));
        when(notificacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(notificacaoMapper.paraDTO(any())).thenReturn(new NotificacaoResponse(
                5L, "Ana", "Horizonte", StatusNotificacao.RECUSADA, null));

        NotificacaoResponse response = notificacaoService.recusar(5L);

        assertThat(response.status()).isEqualTo(StatusNotificacao.RECUSADA);
        assertThat(notificacao.getProjeto().getMembros()).isEmpty();
    }

    @Test
    void naoPodeResponderNotificacaoDuasVezes() {
        Notificacao notificacao = notificacaoPendente();
        notificacao.setStatus(StatusNotificacao.ACEITA);
        when(notificacaoRepository.findById(5L)).thenReturn(Optional.of(notificacao));

        assertThatThrownBy(() -> notificacaoService.aceitar(5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já foi respondida");
    }

    @Test
    void notificacaoInexistenteLancaExcecao() {
        when(notificacaoRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificacaoService.recusar(77L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("77");
    }

    @Test
    void deveListarPendentesDoUsuario() {
        when(notificacaoRepository.findByDestinatarioIdAndStatus(2L, StatusNotificacao.PENDENTE))
                .thenReturn(List.of(notificacaoPendente()));
        when(notificacaoMapper.paraListaDTO(any())).thenReturn(List.of(new NotificacaoResponse(
                5L, "Ana", "Horizonte", StatusNotificacao.PENDENTE, null)));

        List<NotificacaoResponse> lista = notificacaoService.listarPendentes(2L);

        assertThat(lista).hasSize(1);
    }
}
