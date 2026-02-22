package com.exemplo.moneytransferapi.service;

import com.exemplo.moneytransferapi.domain.Transferencia;
import com.exemplo.moneytransferapi.domain.Usuario;
import com.exemplo.moneytransferapi.repository.TransferenciaRepository;
import com.exemplo.moneytransferapi.repository.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita o Mockito
public class TransferenciaServiceTest {

    @Mock // Simula o banco de dados
    private UsuariosRepository usuariosRepository;

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @InjectMocks // Injeta os mocks dentro do serviço que queremos testar
    private TransferenciaService transferenciaService;

    @Test
    void deveRealizarTransferenciaComSucesso() {
        // 1. Preparação (Setup)
        Usuario pagador = new Usuario(1L, "Ana", "123", new BigDecimal("1000.00"));
        Usuario recebedor = new Usuario(2L, "Carlos", "456", new BigDecimal("100.00"));
        BigDecimal valorTransferido = new BigDecimal("100.00");

        // Dizemos para o Mock: "Se alguém pedir o usuário 1, devolva a Ana"
        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(pagador));
        when(usuariosRepository.findById(2L)).thenReturn(Optional.of(recebedor));

        // 2. Execução
        Transferencia resultado = transferenciaService.realizarTransferencia(1L, 2L, valorTransferido);

        // 3. Verificação (Asserts)
        assertNotNull(resultado); // Verifique se retornou algo
        assertEquals(new BigDecimal("900.00"), pagador.getSaldo()); // Verifique se o saldo mudou
        assertEquals(new BigDecimal("200.00"), recebedor.getSaldo()); // Verifique se o saldo mudou

        // Verifica se o método de salvar foi chamado (garante que o banco foi
        // atualizado)
        verify(usuariosRepository, times(2)).save(any());
        verify(transferenciaRepository, times(1)).save(any());
    }

    @Test
    void deveLancarErroSePagadorNaoExistir() {
        // 1. Preparação
        when(usuariosRepository.findById(1L)).thenReturn(Optional.empty()); // Devolve vazio (não existe)

        // 2. Execução e Verificação
        // Usamos assertThrows para garantir que vai dar erro
        Exception exception = assertThrows(RuntimeException.class, () -> {
            transferenciaService.realizarTransferencia(1L, 2L, new BigDecimal("100.00"));
        });

        // Verifica se a mensagem de erro é a esperada
        assertEquals("Pagador não encontrado.", exception.getMessage());
    }

    @Test
    void deveLancarErroSeSaldoForInsuficiente() {
        // 1. Preparação
        Usuario pagador = new Usuario(1L, "Ana", "123", new BigDecimal("50.00")); // Só tem 50
        Usuario recebedor = new Usuario(2L, "Carlos", "456", new BigDecimal("100.00"));

        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(pagador));
        when(usuariosRepository.findById(2L)).thenReturn(Optional.of(recebedor));

        // 2. Execução e Verificação
        Exception exception = assertThrows(RuntimeException.class, () -> {
            transferenciaService.realizarTransferencia(1L, 2L, new BigDecimal("100.00")); // Tenta tirar 100
        });

        assertEquals("Saldo insuficiente na conta do pagador.", exception.getMessage());
    }
}
