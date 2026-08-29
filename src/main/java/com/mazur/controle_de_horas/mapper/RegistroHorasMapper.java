package com.mazur.controle_de_horas.mapper;

import com.mazur.controle_de_horas.dto.RegistroHorasRequest;
import com.mazur.controle_de_horas.dto.RegistroHorasResponse;
import com.mazur.controle_de_horas.model.RegistroDeHoras;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RegistroHorasMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "horasMinutos", ignore = true)
    @Mapping(target = "projeto", ignore = true)
    @Mapping(target = "equipe", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    RegistroDeHoras paraEntidade(RegistroHorasRequest dto);

    @Mapping(target = "horas", source = "horasMinutos", qualifiedByName = "formatarHoras")
    @Mapping(target = "projetoNome", source = "projeto.nome")
    @Mapping(target = "equipeNome", source = "equipe.nome")
    @Mapping(target = "usuarioNome", source = "usuario.nome")
    RegistroHorasResponse paraDTO(RegistroDeHoras entidade);

    List<RegistroHorasResponse> paraListaDTO(List<RegistroDeHoras> entidades);

    @Named("formatarHoras")
    default String formatarHoras(Integer minutos) {
        if (minutos == null) {
            return null;
        }
        return (minutos / 60) + ":" + String.format("%02d", minutos % 60);
    }
}
