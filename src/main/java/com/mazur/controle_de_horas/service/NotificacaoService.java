package com.mazur.controle_de_horas.service;

import com.mazur.controle_de_horas.dto.NotificacaoResponse;
import com.mazur.controle_de_horas.mapper.NotificacaoMapper;
import com.mazur.controle_de_horas.model.Notificacao;
import com.mazur.controle_de_horas.model.StatusNotificacao;
import com.mazur.controle_de_horas.repository.NotificacaoRepository;
import com.mazur.controle_de_horas.repository.ProjetoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final ProjetoRepository projetoRepository;
    private final NotificacaoMapper notificacaoMapper;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                              ProjetoRepository projetoRepository,
                              NotificacaoMapper notificacaoMapper) {
        this.notificacaoRepository = notificacaoRepository;
        this.projetoRepository = projetoRepository;
        this.notificacaoMapper = notificacaoMapper;
    }

    public List<NotificacaoResponse> listarPendentes(Long usuarioId) {
        return notificacaoMapper.paraListaDTO(notificacaoRepository
                .findByDestinatarioIdAndStatus(usuarioId, StatusNotificacao.PENDENTE));
    }

    public List<NotificacaoResponse> listarTodas(Long usuarioId) {
        return notificacaoMapper.paraListaDTO(notificacaoRepository.findByDestinatarioId(usuarioId));
    }

    @Transactional
    public NotificacaoResponse aceitar(Long id) {
        Notificacao notificacao = responder(id, StatusNotificacao.ACEITA);
        var projeto = notificacao.getProjeto();
        projeto.getMembros().add(notificacao.getDestinatario());
        projetoRepository.save(projeto);
        return notificacaoMapper.paraDTO(notificacao);
    }

    @Transactional
    public NotificacaoResponse recusar(Long id) {
        return notificacaoMapper.paraDTO(responder(id, StatusNotificacao.RECUSADA));
    }

    private Notificacao responder(Long id, StatusNotificacao novoStatus) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada com ID: " + id));
        if (notificacao.getStatus() != StatusNotificacao.PENDENTE) {
            throw new IllegalArgumentException("Essa notificação já foi respondida");
        }
        notificacao.setStatus(novoStatus);
        return notificacaoRepository.save(notificacao);
    }
}
