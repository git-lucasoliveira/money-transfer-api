package com.exemplo.moneytransferapi.service;

import com.exemplo.moneytransferapi.domain.TipoUsuario;
import com.exemplo.moneytransferapi.domain.Transferencia;
import com.exemplo.moneytransferapi.domain.Usuario;
import com.exemplo.moneytransferapi.exception.SaldoInsuficienteExeception;
import com.exemplo.moneytransferapi.exception.UsuarioNotFoundException;
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
        Usuario pagador = new Usuario(1L, "Ana", "ana@email.com", "senha123", "111.111.111-11", TipoUsuario.COMUM, new BigDecimal("1000.00"));
        Usuario recebedor = new Usuario(2L, "Carlos", "carlos@email.com", "senha456", "222.222.222-22", TipoUsuario.COMUM, new BigDecimal("100.00"));
        BigDecimal valorTransferido = new BigDecimal("100.00");

        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(pagador));
        when(usuariosRepository.findById(2L)).thenReturn(Optional.of(recebedor));
        when(transferenciaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Execução
        Transferencia resultado = transferenciaService.realizarTransferencia(1L, 2L, valorTransferido);

        // 3. Verificação (Asserts)
        assertNotNull(resultado);
        assertEquals(new BigDecimal("900.00"), pagador.getSaldo());
        assertEquals(new BigDecimal("200.00"), recebedor.getSaldo());

        verify(usuariosRepository, times(2)).save(any());
        verify(transferenciaRepository, times(1)).save(any());
    }

    @Test
    void deveLancarErroSePagadorNaoExistir() {
        // 1. Preparação
        when(usuariosRepository.findById(1L)).thenReturn(Optional.empty());

        // 2. Execução e Verificação
        Exception exception = assertThrows(UsuarioNotFoundException.class, () -> {
            transferenciaService.realizarTransferencia(1L, 2L, new BigDecimal("100.00"));
        });

        assertEquals("Pagador não encontrado.", exception.getMessage());
    }

    @Test
    void deveLancarErroSeRecebedorNaoExistir() {
        // 1. Preparação
        Usuario pagador = new Usuario(1L, "Ana", "ana@email.com", "senha123", "111.111.111-11", TipoUsuario.COMUM, new BigDecimal("1000.00"));

        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(pagador));
        when(usuariosRepository.findById(2L)).thenReturn(Optional.empty());

        // 2. Execução e Verificação
        Exception exception = assertThrows(UsuarioNotFoundException.class, () -> {
            transferenciaService.realizarTransferencia(1L, 2L, new BigDecimal("100.00"));
        });

        assertEquals("Recebedor não encontrado.", exception.getMessage());
    }

    @Test
    void deveLancarErroSeSaldoForInsuficiente() {
        // 1. Preparação
        Usuario pagador = new Usuario(1L, "Ana", "ana@email.com", "senha123", "111.111.111-11", TipoUsuario.COMUM, new BigDecimal("50.00"));
        Usuario recebedor = new Usuario(2L, "Carlos", "carlos@email.com", "senha456", "222.222.222-22", TipoUsuario.COMUM, new BigDecimal("100.00"));

        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(pagador));
        when(usuariosRepository.findById(2L)).thenReturn(Optional.of(recebedor));

        // 2. Execução e Verificação
        Exception exception = assertThrows(SaldoInsuficienteExeception.class, () -> {
            transferenciaService.realizarTransferencia(1L, 2L, new BigDecimal("100.00"));
        });

        assertEquals("Saldo insuficiente na conta do pagador.", exception.getMessage());
    }

    @Test
    void deveLancarErroSeLojistaTransferir() {
        // 1. Preparação — pagador é LOJISTA
        Usuario pagador = new Usuario(1L, "Loja do João", "loja@email.com", "senha123", "333.333.333-33", TipoUsuario.LOJISTA, new BigDecimal("5000.00"));

        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(pagador));

        // 2. Execução e Verificação
        Exception exception = assertThrows(RuntimeException.class, () -> {
            transferenciaService.realizarTransferencia(1L, 2L, new BigDecimal("100.00"));
        });

        assertEquals("Lojistas não podem realizar transferências.", exception.getMessage());
    }
}
