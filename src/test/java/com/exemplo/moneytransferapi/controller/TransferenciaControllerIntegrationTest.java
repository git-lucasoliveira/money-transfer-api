package com.exemplo.moneytransferapi.controller;

import com.exemplo.moneytransferapi.domain.TipoUsuario;
import com.exemplo.moneytransferapi.domain.Usuario;
import com.exemplo.moneytransferapi.repository.TransferenciaRepository;
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
class TransferenciaControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuariosRepository usuariosRepository;
    @Autowired private TransferenciaRepository transferenciaRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private TokenService tokenService;

    private Long idPagador;
    private Long idRecebedor;
    private String token;

    @BeforeEach
    void setup() {
        transferenciaRepository.deleteAll();
        usuariosRepository.deleteAll();

        Usuario pagador = usuariosRepository.save(new Usuario(
                null, "Ana Silva", "ana@email.com",
                passwordEncoder.encode("senha123"),
                "111.111.111-11", TipoUsuario.COMUM, new BigDecimal("1000.00")
        ));

        Usuario recebedor = usuariosRepository.save(new Usuario(
                null, "Carlos Santos", "carlos@email.com",
                passwordEncoder.encode("senha456"),
                "222.222.222-22", TipoUsuario.COMUM, new BigDecimal("100.00")
        ));

        idPagador = pagador.getId();
        idRecebedor = recebedor.getId();
        token = "Bearer " + tokenService.gerarToken(pagador);
    }

    @Test
    void deveRetornar200QuandoTransferenciaForValida() throws Exception {
        String payload = """
                {
                  "idPagador": %d,
                  "idRecebedor": %d,
                  "valor": 100.00
                }
                """.formatted(idPagador, idRecebedor);

        mockMvc.perform(post("/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(100.00));
    }

    @Test
    void deveRetornar400QuandoValorForNegativo() throws Exception {
        String payload = """
                {
                  "idPagador": %d,
                  "idRecebedor": %d,
                  "valor": -50.00
                }
                """.formatted(idPagador, idRecebedor);

        mockMvc.perform(post("/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404QuandoPagadorNaoExistir() throws Exception {
        String payload = """
                {
                  "idPagador": 9999,
                  "idRecebedor": %d,
                  "valor": 100.00
                }
                """.formatted(idRecebedor);

        mockMvc.perform(post("/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(payload))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400QuandoSaldoForInsuficiente() throws Exception {
        String payload = """
                {
                  "idPagador": %d,
                  "idRecebedor": %d,
                  "valor": 5000.00
                }
                """.formatted(idPagador, idRecebedor);

        mockMvc.perform(post("/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar401SemToken() throws Exception {
        String payload = """
                {
                  "idPagador": %d,
                  "idRecebedor": %d,
                  "valor": 100.00
                }
                """.formatted(idPagador, idRecebedor);

        mockMvc.perform(post("/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarTransferencias() throws Exception {
        mockMvc.perform(get("/transferencias")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }
}
