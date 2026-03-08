# Money Transfer API (Spring Boot MVP) 🚀

Uma API RESTful desenvolvida em Java 21 com Spring Boot para simulação de transferências financeiras estilo Mini-Pix. O projeto segue arquitetura em camadas (MVC) com foco em boas práticas de mercado: tratamento de erros, segurança de dados, atomicidade nas transações e cobertura de testes.

## 🛠️ Tecnologias Utilizadas

- **Java 21:** Sintaxe moderna com _Java Records_ para DTOs.
- **Spring Boot 3:** Autoconfiguração, IoC e ambiente web embarcado (Tomcat).
- **Spring Data JPA & Hibernate:** Persistência relacional sem boilerplate SQL.
- **PostgreSQL:** Banco de dados relacional robusto e de mercado.
- **Lombok:** Redução de boilerplate code (Getters, Setters, Construtores).
- **Bean Validation:** Validação de dados de entrada via anotações (`@NotNull`, `@Positive`).
- **JUnit 5 + Mockito:** Testes unitários com isolamento completo da camada de banco.

---

## 🏗️ Arquitetura do Projeto

Estrutura baseada na **MVC Layered Architecture**:

1. **`domain` (Entidades):** `Usuario` e `Transferencia` mapeadas para o banco. O campo `tipo` usa o enum `TipoUsuario` (`COMUM` ou `LOJISTA`) com `@Enumerated(EnumType.STRING)` para garantir valores válidos.
2. **`repository` (Acesso a Dados):** Interfaces herdeiras de `JpaRepository`, responsáveis pelas consultas ao banco.
3. **`service` (Regras de Negócio):** Cérebro da aplicação. Operações com `BigDecimal`, validação de saldo, regra de lojista e atomicidade via `@Transactional`.
4. **`controller` (Apresentação Web):** Porta de entrada HTTP. Valida o payload com `@Valid` e retorna um `TransferenciaResponseDTO` — sem expor dados sensíveis do usuário.
5. **`dto` (Transferência de Dados):** `TransferenciaRequestDTO` valida os dados de entrada. `TransferenciaResponseDTO` controla exatamente o que a API devolve ao cliente.
6. **`exception` (Tratamento Global):** `@RestControllerAdvice` intercepta cada exceção customizada e retorna o HTTP status semântico correto.

---

## 🎯 Regras de Negócio

| Regra | Detalhe |
|---|---|
| **Lojista não transfere** | Usuários do tipo `LOJISTA` só recebem, nunca enviam transferências |
| **Saldo insuficiente** | Valida antes de debitar, lança `SaldoInsuficienteException` (422) |
| **Usuário não encontrado** | Lança `UsuarioNotFoundException` (404) para pagador ou recebedor inexistente |
| **Atomicidade** | `@Transactional` garante que débito e crédito acontecem juntos — se um falhar, tudo reverte |
| **Validação de entrada** | `valor` negativo ou nulo é rejeitado antes de chegar na regra de negócio |
| **Dados únicos** | CPF e e-mail possuem `@Column(unique=true)` — sem duplicatas no banco |
| **Precisão financeira** | `BigDecimal` em todas as operações monetárias — sem perda de precisão de `float`/`double` |

---

## 🚨 Tratamento de Erros

| Exceção | HTTP Status | Quando ocorre |
|---|---|---|
| `UsuarioNotFoundException` | `404 Not Found` | Pagador ou recebedor não existe |
| `SaldoInsuficienteException` | `422 Unprocessable Entity` | Saldo do pagador menor que o valor da transferência |
| `TipoUsuarioInvalidoException` | `400 Bad Request` | Lojista tentando realizar uma transferência |
| `MethodArgumentNotValidException` | `400 Bad Request` | Payload inválido (valor nulo, negativo, etc.) |

---

## 🧪 Cobertura de Testes

5 cenários unitários cobrindo os fluxos do `TransferenciaService`:

- ✅ Transferência realizada com sucesso (saldos debitados e creditados corretamente)
- ✅ Erro ao tentar transferir com saldo insuficiente
- ✅ Erro quando o pagador não existe no banco
- ✅ Erro quando o recebedor não existe no banco
- ✅ Erro quando o pagador é um lojista

---

## ⚙️ Como Rodar o Projeto

### Pré-Requisitos
- Java JDK 21 ou superior
- Maven instalado (ou use o cache do wrapper em `~/.m2`)
- PostgreSQL rodando localmente na porta `5432`

### 1️⃣ Setup do Banco de Dados
1. No `pgAdmin`, crie um banco chamado exatamente: **`money_transfer_db`**
2. Verifique as credenciais em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/money_transfer_db
spring.datasource.username=postgres
spring.datasource.password=sua_senha_aqui
```

### 2️⃣ Rodando a Aplicação

```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run
```

O Hibernate cria as tabelas automaticamente no primeiro boot via `ddl-auto=update`.

### 3️⃣ Rodando os Testes

```bash
# Linux/Mac
./mvnw test

# Windows
.\mvnw.cmd test
```

---

## 📡 Endpoints

### `POST /transferencias` — Realizar uma transferência

**Request body:**
```json
{
  "idPagador": 1,
  "idRecebedor": 2,
  "valor": 50.00
}
```

**Resposta de sucesso `200 OK`:**
```json
{
  "id": 1,
  "idPagador": 1,
  "idRecebedor": 2,
  "valor": 50.00,
  "dataTransferencia": "2026-03-07T14:30:15.123"
}
```

**Resposta de erro `404 Not Found`:**
```json
{
  "status": 404,
  "mensagem": "Pagador não encontrado."
}
```

**Resposta de erro `422 Unprocessable Entity`:**
```json
{
  "status": 422,
  "mensagem": "Saldo insuficiente na conta do pagador."
}
```

---

## 🗒️ Dívidas Técnicas (próximos passos)

- [ ] Hash de senha com **BCrypt** — hoje a senha é salva em texto puro
- [ ] Endpoint de **cadastro de usuários** via API (hoje é feito direto no banco)
- [ ] **Autenticação JWT** para proteger os endpoints
- [ ] Migração do banco com **Flyway** no lugar do `ddl-auto=update`
- [ ] Testes de integração com banco em memória (**H2**)
