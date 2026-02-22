package com.exemplo.moneytransferapi.controller;

import com.exemplo.moneytransferapi.domain.Transferencia;
import com.exemplo.moneytransferapi.dto.TransferenciaRequestDTO;
import com.exemplo.moneytransferapi.service.TransferenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Diz que essa classe vai receber requisições da web
@RequestMapping("/transferencias") // O endereço base no navegador (http://localhost:8080/transferencias)
@RequiredArgsConstructor
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    // Essa anotação diz "Quando a internet mandar um HTTP POST (um json de criar
    // algo) pra cá, execute esse método"
    @PostMapping
    public ResponseEntity<Transferencia> realizarTransferencia(@RequestBody TransferenciaRequestDTO dto) {

        Transferencia tr = transferenciaService.realizarTransferencia(
                dto.idPagador(),
                dto.idRecebedor(),
                dto.valor());
        return ResponseEntity.ok(tr);
    }
}
