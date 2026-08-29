package com.mazur.controle_de_horas.repository;

import com.mazur.controle_de_horas.model.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    List<Equipe> findByProjetoId(Long projetoId);
}
