package com.exemplo.moneytransferapi.controller;

import com.exemplo.moneytransferapi.domain.TipoUsuario;
import com.exemplo.moneytransferapi.domain.Usuario;
import com.exemplo.moneytransferapi.repository.UsuariosRepository;
import com.exemplo.moneytransferapi.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuariosRepository usuariosRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private TokenService tokenService;

    private String token;
    private Long idUsuarioCadastrado;

    @BeforeEach
    void setup() {
        usuariosRepository.deleteAll();

        Usuario usuario = usuariosRepository.save(new Usuario(
                null, "Admin", "admin@email.com",
                passwordEncoder.encode("admin123"),
                "000.000.000-00", TipoUsuario.COMUM, new BigDecimal("500.00")
        ));

        token = "Bearer " + tokenService.gerarToken(usuario);
        idUsuarioCadastrado = usuario.getId();
    }

    @Test
    void deveRetornar201QuandoCadastrarUsuarioValido() throws Exception {
        String payload = """
                {
                  "nomeCompleto": "Maria Silva",
                  "email": "maria@email.com",
                  "senha": "senha123",
                  "cpf": "333.333.333-33",
                  "tipo": "COMUM",
                  "saldo": 500.00
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nomeCompleto").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.senha").doesNotExist()); // Senha NÃO deve aparecer na resposta
    }

    @Test
    void deveRetornar400QuandoEmailForInvalido() throws Exception {
        String payload = """
                {
                  "nomeCompleto": "Maria Silva",
                  "email": "email-invalido",
                  "senha": "senha123",
                  "cpf": "333.333.333-33",
                  "tipo": "COMUM",
                  "saldo": 500.00
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoSaldoForNegativo() throws Exception {
        String payload = """
                {
                  "nomeCompleto": "Maria Silva",
                  "email": "maria@email.com",
                  "senha": "senha123",
                  "cpf": "333.333.333-33",
                  "tipo": "COMUM",
                  "saldo": -100.00
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveCadastrarLojista() throws Exception {
        String payload = """
                {
                  "nomeCompleto": "Loja do João",
                  "email": "loja@email.com",
                  "senha": "senha123",
                  "cpf": "444.444.444-44",
                  "tipo": "LOJISTA",
                  "saldo": 0.00
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("LOJISTA"));
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        mockMvc.perform(get("/usuarios/" + idUsuarioCadastrado)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idUsuarioCadastrado))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void deveRetornar404ParaUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/usuarios/9999")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}
