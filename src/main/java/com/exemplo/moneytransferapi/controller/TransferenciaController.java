package com.exemplo.moneytransferapi.controller;

import com.exemplo.moneytransferapi.domain.Transferencia;
import com.exemplo.moneytransferapi.dto.TransferenciaRequestDTO;
import com.exemplo.moneytransferapi.dto.TransferenciaResponseDTO;
import com.exemplo.moneytransferapi.service.TransferenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Diz que essa classe vai receber requisições da web
@RequestMapping("/transferencias") // O endereço base no navegador (http://localhost:8080/transferencias)
@RequiredArgsConstructor
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    // Essa anotação diz "Quando a internet mandar um HTTP POST (um json de criar
    // algo) pra cá, execute esse método"
    @PostMapping
    public ResponseEntity<TransferenciaResponseDTO> realizarTransferencia(@Valid @RequestBody TransferenciaRequestDTO dto) {

        Transferencia tr = transferenciaService.realizarTransferencia(
                dto.idPagador(),
                dto.idRecebedor(),
                dto.valor());
        return ResponseEntity.ok(toDTO(tr));
    }

    @GetMapping
    public ResponseEntity<List<TransferenciaResponseDTO>> listarTodas() {
        List<TransferenciaResponseDTO> lista = transferenciaService.listarTodas()
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    private TransferenciaResponseDTO toDTO(Transferencia tr) {
        return new TransferenciaResponseDTO(
                tr.getId(),
                tr.getPagador().getId(),
                tr.getRecebedor().getId(),
                tr.getValor(),
                tr.getDataTransferencia()
        );
    }
}
