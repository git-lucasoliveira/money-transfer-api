package com.exemplo.moneytransferapi.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity(name = "Usuario")
@Table(name = "usuarios")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private BigDecimal saldo;
}