package com.exemplo.moneytransferapi.service;

import com.exemplo.moneytransferapi.domain.Usuario;
import com.exemplo.moneytransferapi.dto.UsuarioRequestDTO;
import com.exemplo.moneytransferapi.dto.UsuarioResponseDTO;
import com.exemplo.moneytransferapi.exception.UsuarioNotFoundException;
import com.exemplo.moneytransferapi.repository.UsuariosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuariosRepository usuariosRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(dto.nomeCompleto());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setCpf(dto.cpf());
        usuario.setTipo(dto.tipo());
        usuario.setSaldo(dto.saldo());

        Usuario salvo = usuariosRepository.save(usuario);
        return toDTO(salvo);
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado."));
        return toDTO(usuario);
    }

    private UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getTipo(),
                usuario.getSaldo()
        );
    }
}
