package com.mazur.controle_de_horas.repository;

import com.mazur.controle_de_horas.model.Notificacao;
import com.mazur.controle_de_horas.model.StatusNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByDestinatarioIdAndStatus(Long destinatarioId, StatusNotificacao status);
    List<Notificacao> findByDestinatarioId(Long destinatarioId);
}
