package com.exemplo.moneytransferapi.service;

import com.exemplo.moneytransferapi.domain.Usuario;
import com.exemplo.moneytransferapi.dto.UsuarioRequestDTO;
import com.exemplo.moneytransferapi.dto.UsuarioResponseDTO;
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

        return new UsuarioResponseDTO(
                salvo.getId(),
                salvo.getNomeCompleto(),
                salvo.getEmail(),
                salvo.getCpf(),
                salvo.getTipo(),
                salvo.getSaldo()
        );
    }
}

