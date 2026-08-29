package com.mazur.controle_de_horas.service;

import com.mazur.controle_de_horas.dto.MembroRequest;
import com.mazur.controle_de_horas.dto.NotificacaoResponse;
import com.mazur.controle_de_horas.dto.ProjetoMenuResponse;
import com.mazur.controle_de_horas.dto.ProjetoRequest;
import com.mazur.controle_de_horas.dto.ProjetoResponse;
import com.mazur.controle_de_horas.mapper.NotificacaoMapper;
import com.mazur.controle_de_horas.mapper.ProjetoMapper;
import com.mazur.controle_de_horas.model.Notificacao;
import com.mazur.controle_de_horas.model.Projeto;
import com.mazur.controle_de_horas.model.StatusNotificacao;
import com.mazur.controle_de_horas.model.Usuario;
import com.mazur.controle_de_horas.repository.NotificacaoRepository;
import com.mazur.controle_de_horas.repository.ProjetoRepository;
import com.mazur.controle_de_horas.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final ProjetoMapper projetoMapper;
    private final NotificacaoMapper notificacaoMapper;

    public ProjetoService(ProjetoRepository projetoRepository,
                          UsuarioRepository usuarioRepository,
                          NotificacaoRepository notificacaoRepository,
                          ProjetoMapper projetoMapper,
                          NotificacaoMapper notificacaoMapper) {
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoRepository = notificacaoRepository;
        this.projetoMapper = projetoMapper;
        this.notificacaoMapper = notificacaoMapper;
    }

    @Transactional
    public ProjetoResponse criarProjeto(ProjetoRequest request) {
        if (request.nome() == null || request.nome().isBlank()) {
            throw new IllegalArgumentException("Nome do projeto não pode estar vazio");
        }
        Usuario criador = usuarioRepository.findById(request.criadorId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + request.criadorId()));

        Projeto projeto = new Projeto();
        projeto.setNome(request.nome().trim());
        projeto.setDescricao(request.descricao());
        projeto.setCriador(criador);
        projeto.getMembros().add(criador);
        Projeto projetoSalvo = projetoRepository.save(projeto);
        return projetoMapper.paraDTO(projetoSalvo);
    }

    public List<ProjetoMenuResponse> listarProjetosDoUsuario(Long usuarioId) {
        return projetoMapper.paraListaMenuDTO(projetoRepository.findAllDoUsuario(usuarioId));
    }

    public ProjetoResponse findById(long id) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + id));
        return projetoMapper.paraDTO(projeto);
    }

    @Transactional
    public NotificacaoResponse adicionarMembro(Long projetoId, MembroRequest request) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + projetoId));

        Long operadorId = usuarioAutenticado();
        if (operadorId != null && !projeto.getCriador().getId().equals(operadorId)) {
            throw new IllegalArgumentException("Apenas o criador do projeto pode adicionar membros");
        }

        Usuario convidado = usuarioRepository.findByNomeIgnoreCase(request.nomeUsuario().trim())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com nome: " + request.nomeUsuario()));

        if (projeto.getCriador().getId().equals(convidado.getId()) || projeto.getMembros().contains(convidado)) {
            throw new IllegalArgumentException("Usuário já é membro do projeto");
        }
        boolean convitePendente = notificacaoRepository
                .findByDestinatarioIdAndStatus(convidado.getId(), StatusNotificacao.PENDENTE).stream()
                .anyMatch(n -> n.getProjeto().getId().equals(projeto.getId()));
        if (convitePendente) {
            throw new IllegalArgumentException("Já existe um convite pendente para esse usuário nesse projeto");
        }

        Notificacao notificacao = new Notificacao();
        notificacao.setDestinatario(convidado);
        notificacao.setProjeto(projeto);
        notificacao.setStatus(StatusNotificacao.PENDENTE);
        notificacao.setDataEnvio(LocalDateTime.now());
        Notificacao salva = notificacaoRepository.save(notificacao);
        return notificacaoMapper.paraDTO(salva);
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
