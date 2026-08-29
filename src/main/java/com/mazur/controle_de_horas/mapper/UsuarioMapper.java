package com.mazur.controle_de_horas.mapper;

import com.mazur.controle_de_horas.dto.UsuarioRequest;
import com.mazur.controle_de_horas.dto.UsuarioResponse;
import com.mazur.controle_de_horas.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", ignore = true)
    Usuario paraEntidade(UsuarioRequest dto);

    UsuarioResponse paraDTO(Usuario entidade);
}
