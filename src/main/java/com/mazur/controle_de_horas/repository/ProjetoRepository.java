package com.mazur.controle_de_horas.repository;

import com.mazur.controle_de_horas.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    @Query("SELECT DISTINCT p FROM Projeto p LEFT JOIN p.membros m " +
            "WHERE p.criador.id = :usuarioId OR m.id = :usuarioId")
    List<Projeto> findAllDoUsuario(@Param("usuarioId") Long usuarioId);
}
