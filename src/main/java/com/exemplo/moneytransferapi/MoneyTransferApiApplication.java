package com.exemplo.moneytransferapi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // Habilita auditoria automática (@CreatedDate, @LastModifiedDate)
public class MoneyTransferApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoneyTransferApiApplication.class, args);
    }
}
