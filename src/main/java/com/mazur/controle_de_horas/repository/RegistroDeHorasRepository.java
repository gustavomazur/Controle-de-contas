package com.mazur.controle_de_horas.repository;

import com.mazur.controle_de_horas.model.RegistroDeHoras;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroDeHorasRepository extends JpaRepository<RegistroDeHoras, Long> {
    List<RegistroDeHoras> findByUsuarioId(Long usuarioId);
    List<RegistroDeHoras> findByProjetoId(Long projetoId);
    List<RegistroDeHoras> findByEquipeId(Long equipeId);
}
