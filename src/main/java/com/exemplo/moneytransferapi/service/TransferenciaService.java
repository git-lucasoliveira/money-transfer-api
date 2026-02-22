package com.exemplo.moneytransferapi.service;

import com.exemplo.moneytransferapi.domain.Transferencia;
import com.exemplo.moneytransferapi.domain.Usuario;
import com.exemplo.moneytransferapi.repository.TransferenciaRepository;
import com.exemplo.moneytransferapi.repository.UsuariosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service // Ensina o Spring que isso é o cérebro das regras de negócio
@RequiredArgsConstructor // Usa o Lombok para injetar os Repositórios magicamente!
public class TransferenciaService {

    private final UsuariosRepository usuariosRepository;
    private final TransferenciaRepository transferenciaRepository;

    public Transferencia realizarTransferencia(Long idPagador, Long idRecebedor, BigDecimal valor) {
        
        // 1. Busca os usuários (Se não achar, já estoura um erro usando o orElseThrow)
        Usuario pagador = usuariosRepository.findById(idPagador)
                .orElseThrow(() -> new RuntimeException("Pagador não encontrado."));
                
        Usuario recebedor = usuariosRepository.findById(idRecebedor)
                .orElseThrow(() -> new RuntimeException("Recebedor não encontrado."));

        // 2. Valida o Saldo (O método compareTo do BigDecimal retorna -1 se for menor)
        if (pagador.getSaldo().compareTo(valor) < 0) {
            throw new RuntimeException("Saldo insuficiente na conta do pagador.");
        }

        pagador.setSaldo(pagador.getSaldo().subtract(valor));
        recebedor.setSaldo(recebedor.getSaldo().add(valor));

        // 3. Salva os usuários atualizados no banco
        usuariosRepository.save(pagador);
        usuariosRepository.save(recebedor);

        // 4. Cria o registro da transferência
        Transferencia transferencia = new Transferencia();
        transferencia.setPagador(pagador);
        transferencia.setRecebedor(recebedor);
        transferencia.setValor(valor);
        transferencia.setDataTransferencia(LocalDateTime.now());

        return transferenciaRepository.save(transferencia);

    }
}
