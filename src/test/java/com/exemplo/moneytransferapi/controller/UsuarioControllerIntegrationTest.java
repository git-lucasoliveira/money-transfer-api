package com.exemplo.moneytransferapi.controller;

import com.exemplo.moneytransferapi.repository.UsuariosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @BeforeEach
    void setup() {
        usuariosRepository.deleteAll();
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
}

