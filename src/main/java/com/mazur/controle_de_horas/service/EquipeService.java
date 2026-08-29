package com.mazur.controle_de_horas.service;

import com.mazur.controle_de_horas.dto.EquipeRequest;
import com.mazur.controle_de_horas.dto.EquipeResponse;
import com.mazur.controle_de_horas.dto.MembroRequest;
import com.mazur.controle_de_horas.mapper.EquipeMapper;
import com.mazur.controle_de_horas.model.Equipe;
import com.mazur.controle_de_horas.model.Projeto;
import com.mazur.controle_de_horas.model.Usuario;
import com.mazur.controle_de_horas.repository.EquipeRepository;
import com.mazur.controle_de_horas.repository.ProjetoRepository;
import com.mazur.controle_de_horas.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipeService {

    private final EquipeRepository equipeRepository;
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EquipeMapper equipeMapper;

    public EquipeService(EquipeRepository equipeRepository,
                         ProjetoRepository projetoRepository,
                         UsuarioRepository usuarioRepository,
                         EquipeMapper equipeMapper) {
        this.equipeRepository = equipeRepository;
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
        this.equipeMapper = equipeMapper;
    }

    @Transactional
    public EquipeResponse criarEquipe(EquipeRequest request) {
        if (request.nome() == null || request.nome().isBlank()) {
            throw new IllegalArgumentException("Nome da equipe não pode estar vazio");
        }
        Projeto projeto = projetoRepository.findById(request.projetoId())
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + request.projetoId()));

        if (!projeto.getCriador().getId().equals(request.criadorId())) {
            throw new IllegalArgumentException("Apenas o criador do projeto pode criar equipes");
        }
        Long operadorId = usuarioAutenticado();
        if (operadorId != null && !operadorId.equals(request.criadorId())) {
            throw new IllegalArgumentException("Apenas o criador do projeto pode criar equipes");
        }

        Usuario criador = usuarioRepository.findById(request.criadorId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + request.criadorId()));

        Equipe equipe = new Equipe();
        equipe.setNome(request.nome().trim());
        equipe.setProjeto(projeto);
        equipe.getMembros().add(criador);
        Equipe salva = equipeRepository.save(equipe);
        return equipeMapper.paraDTO(salva);
    }

    public List<EquipeResponse> listarPorProjeto(Long projetoId) {
        return equipeMapper.paraListaDTO(equipeRepository.findByProjetoId(projetoId));
    }

    @Transactional
    public EquipeResponse adicionarMembro(Long equipeId, MembroRequest request) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada com ID: " + equipeId));

        Projeto projeto = equipe.getProjeto();
        Long operadorId = usuarioAutenticado();
        if (operadorId != null && !projeto.getCriador().getId().equals(operadorId)) {
            throw new IllegalArgumentException("Apenas o criador do projeto pode adicionar membros na equipe");
        }

        Usuario membro = usuarioRepository.findByNomeIgnoreCase(request.nomeUsuario().trim())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com nome: " + request.nomeUsuario()));

        boolean membroDoProjeto = projeto.getMembros().contains(membro)
                || projeto.getCriador().getId().equals(membro.getId());
        if (!membroDoProjeto) {
            throw new IllegalArgumentException("Usuário precisa ser membro do projeto antes de entrar na equipe");
        }
        if (equipe.getMembros().contains(membro)) {
            throw new IllegalArgumentException("Usuário já é membro dessa equipe");
        }

        equipe.getMembros().add(membro);
        Equipe salva = equipeRepository.save(equipe);
        return equipeMapper.paraDTO(salva);
    }

    private Long usuarioAutenticado() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        return principal instanceof Long ? (Long) principal : null;
    }
}
