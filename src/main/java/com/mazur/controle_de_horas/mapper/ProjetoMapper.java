package com.mazur.controle_de_horas.mapper;

import com.mazur.controle_de_horas.dto.ProjetoMenuResponse;
import com.mazur.controle_de_horas.dto.ProjetoResponse;
import com.mazur.controle_de_horas.model.Projeto;
import com.mazur.controle_de_horas.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjetoMapper {

    ProjetoMenuResponse paraMenuDTO(Projeto entidade);

    List<ProjetoMenuResponse> paraListaMenuDTO(List<Projeto> entidades);

    @Mapping(target = "criadorId", source = "criador.id")
    @Mapping(target = "criadorNome", source = "criador.nome")
    @Mapping(target = "membrosNomes", source = "membros")
    ProjetoResponse paraDTO(Projeto entidade);

    default String mapNomeUsuario(Usuario usuario) {
        return usuario == null ? null : usuario.getNome();
    }
}
