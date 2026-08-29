package com.mazur.controle_de_horas.mapper;

import com.mazur.controle_de_horas.dto.EquipeResponse;
import com.mazur.controle_de_horas.model.Equipe;
import com.mazur.controle_de_horas.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EquipeMapper {

    @Mapping(target = "projetoId", source = "projeto.id")
    @Mapping(target = "projetoNome", source = "projeto.nome")
    @Mapping(target = "membrosNomes", source = "membros")
    EquipeResponse paraDTO(Equipe entidade);

    List<EquipeResponse> paraListaDTO(List<Equipe> entidades);

    default String mapNomeUsuario(Usuario usuario) {
        return usuario == null ? null : usuario.getNome();
    }
}
