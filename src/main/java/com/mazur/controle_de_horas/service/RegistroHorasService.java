package com.mazur.controle_de_horas.service;

import com.mazur.controle_de_horas.dto.RegistroHorasRequest;
import com.mazur.controle_de_horas.dto.RegistroHorasResponse;
import com.mazur.controle_de_horas.dto.RelatorioProjetoResponse;
import com.mazur.controle_de_horas.dto.RelatorioUsuarioResponse;
import com.mazur.controle_de_horas.mapper.RegistroHorasMapper;
import com.mazur.controle_de_horas.model.Equipe;
import com.mazur.controle_de_horas.model.Projeto;
import com.mazur.controle_de_horas.model.RegistroDeHoras;
import com.mazur.controle_de_horas.model.Usuario;
import com.mazur.controle_de_horas.repository.EquipeRepository;
import com.mazur.controle_de_horas.repository.ProjetoRepository;
import com.mazur.controle_de_horas.repository.RegistroDeHorasRepository;
import com.mazur.controle_de_horas.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegistroHorasService {

    private final RegistroDeHorasRepository registroDeHorasRepository;
    private final ProjetoRepository projetoRepository;
    private final EquipeRepository equipeRepository;
    private final UsuarioRepository usuarioRepository;
    private final RegistroHorasMapper registroHorasMapper;

    public RegistroHorasService(RegistroDeHorasRepository registroDeHorasRepository,
                                ProjetoRepository projetoRepository,
                                EquipeRepository equipeRepository,
                                UsuarioRepository usuarioRepository,
                                RegistroHorasMapper registroHorasMapper) {
        this.registroDeHorasRepository = registroDeHorasRepository;
        this.projetoRepository = projetoRepository;
        this.equipeRepository = equipeRepository;
        this.usuarioRepository = usuarioRepository;
        this.registroHorasMapper = registroHorasMapper;
    }

    public RegistroHorasResponse registrar(RegistroHorasRequest request) {
        Integer minutos = parseHoras(request.horas());

        Projeto projeto = projetoRepository.findById(request.projetoId())
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + request.projetoId()));
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + request.usuarioId()));

        boolean membroDoProjeto = projeto.getMembros().contains(usuario)
                || projeto.getCriador().getId().equals(usuario.getId());
        if (!membroDoProjeto) {
            throw new IllegalArgumentException("Usuário precisa ser membro do projeto para registrar horas");
        }

        Equipe equipe = null;
        if (request.equipeId() != null) {
            equipe = equipeRepository.findById(request.equipeId())
                    .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada com ID: " + request.equipeId()));
            if (!equipe.getProjeto().getId().equals(projeto.getId())) {
                throw new IllegalArgumentException("Essa equipe não pertence ao projeto informado");
            }
            if (!equipe.getMembros().contains(usuario)) {
                throw new IllegalArgumentException("Usuário precisa ser membro da equipe para registrar horas nela");
            }
        }

        RegistroDeHoras registro = registroHorasMapper.paraEntidade(request);
        registro.setHorasMinutos(minutos);
        registro.setProjeto(projeto);
        registro.setEquipe(equipe);
        registro.setUsuario(usuario);
        RegistroDeHoras salvo = registroDeHorasRepository.save(registro);
        return registroHorasMapper.paraDTO(salvo);
    }

    public List<RegistroHorasResponse> listarPorUsuario(Long usuarioId) {
        return registroHorasMapper.paraListaDTO(registroDeHorasRepository.findByUsuarioId(usuarioId));
    }

    public List<RegistroHorasResponse> listarPorProjeto(Long projetoId) {
        return registroHorasMapper.paraListaDTO(registroDeHorasRepository.findByProjetoId(projetoId));
    }

    public List<RegistroHorasResponse> listarPorEquipe(Long equipeId) {
        return registroHorasMapper.paraListaDTO(registroDeHorasRepository.findByEquipeId(equipeId));
    }

    public RelatorioProjetoResponse gerarRelatorio(Long projetoId, Long usuarioIdQuePede) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + projetoId));

        if (!projeto.getCriador().getId().equals(usuarioIdQuePede)) {
            throw new IllegalArgumentException("Apenas o criador do projeto pode ver o relatório");
        }

        List<RegistroDeHoras> registros = registroDeHorasRepository.findByProjetoId(projetoId);

        int totalMinutos = 0;
        Map<String, long[]> porUsuario = new LinkedHashMap<>();
        for (RegistroDeHoras registro : registros) {
            totalMinutos += registro.getHorasMinutos();
            String nome = registro.getUsuario().getNome();
            long[] dados = porUsuario.computeIfAbsent(nome, k -> new long[2]);
            dados[0] += registro.getHorasMinutos();
            dados[1]++;
        }

        List<RelatorioUsuarioResponse> resumo = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : porUsuario.entrySet()) {
            resumo.add(new RelatorioUsuarioResponse(
                    entry.getKey(),
                    registroHorasMapper.formatarHoras((int) entry.getValue()[0]),
                    entry.getValue()[1]));
        }

        return new RelatorioProjetoResponse(
                projeto.getId(),
                projeto.getNome(),
                registroHorasMapper.formatarHoras(totalMinutos),
                resumo,
                registroHorasMapper.paraListaDTO(registros));
    }

    static Integer parseHoras(String horas) {
        if (horas == null || horas.isBlank()) {
            throw new IllegalArgumentException("Horas é obrigatória");
        }
        String valor = horas.trim()
                .replace(',', ':')
                .replace(';', ':')
                .replace('.', ':');
        String[] partes = valor.split(":");
        try {
            int h = Integer.parseInt(partes[0]);
            int m = partes.length > 1 ? Integer.parseInt(partes[1]) : 0;
            boolean invalido = partes.length > 2
                    || h < 0
                    || m < 0
                    || m > 59
                    || valor.startsWith(":")
                    || (h == 0 && m == 0);
            if (invalido) {
                throw new IllegalArgumentException("Formato de horas inválido. Use por exemplo 1:30");
            }
            return h * 60 + m;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato de horas inválido. Use por exemplo 1:30");
        }
    }
}
