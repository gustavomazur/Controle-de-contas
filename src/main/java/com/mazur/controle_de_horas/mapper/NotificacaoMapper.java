package com.mazur.controle_de_horas.mapper;

import com.mazur.controle_de_horas.dto.NotificacaoResponse;
import com.mazur.controle_de_horas.model.Notificacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificacaoMapper {

    @Mapping(target = "destinatarioNome", source = "destinatario.nome")
    @Mapping(target = "projetoNome", source = "projeto.nome")
    NotificacaoResponse paraDTO(Notificacao entidade);

    List<NotificacaoResponse> paraListaDTO(List<Notificacao> entidades);
}
