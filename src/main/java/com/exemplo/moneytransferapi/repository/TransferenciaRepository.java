package com.exemplo.moneytransferapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exemplo.moneytransferapi.domain.Transferencia;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

}