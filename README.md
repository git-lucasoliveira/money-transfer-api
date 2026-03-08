# Money Transfer API (Spring Boot MVP) 🚀

Uma API RESTful desenvolvida em Java 21 com Spring Boot para simulação de transferências financeiras estilo Mini-Pix. O projeto segue arquitetura em camadas (MVC) com foco em boas práticas de mercado: tratamento de erros, segurança de dados, atomicidade nas transações e cobertura de testes.

## 🛠️ Tecnologias Utilizadas

- **Java 21:** Sintaxe moderna com _Java Records_ para DTOs.
- **Spring Boot 3:** Autoconfiguração, IoC e ambiente web embarcado (Tomcat).
- **Spring Security + JWT:** Autenticação stateless via token Bearer.
- **Spring Data JPA & Hibernate:** Persistência relacional sem boilerplate SQL.
- **PostgreSQL:** Banco de dados relacional robusto e de mercado.
- **Flyway:** Versionamento e migração do banco de dados.
- **BCrypt:** Hash seguro de senhas — nunca armazenadas em texto puro.
- **Lombok:** Redução de boilerplate code (Getters, Setters, Construtores).
- **Bean Validation:** Validação de dados de entrada via anotações (`@NotNull`, `@Positive`).
- **JUnit 5 + Mockito:** Testes unitários com isolamento completo da camada de banco.
- **H2 Database:** Banco em memória para testes de integração.

---

## 🏗️ Arquitetura do Projeto

Estrutura baseada na **MVC Layered Architecture**:

1. **`config`:** Configurações do Spring (ex: Bean do `BCryptPasswordEncoder`).
2. **`domain` (Entidades):** `Usuario` e `Transferencia` mapeadas para o banco. O campo `tipo` usa o enum `TipoUsuario` (`COMUM` ou `LOJISTA`) com `@Enumerated(EnumType.STRING)` para garantir valores válidos.
3. **`repository` (Acesso a Dados):** Interfaces herdeiras de `JpaRepository`, responsáveis pelas consultas ao banco.
4. **`service` (Regras de Negócio):** Cérebro da aplicação. Operações com `BigDecimal`, validação de saldo, regra de lojista e atomicidade via `@Transactional`.
5. **`controller` (Apresentação Web):** Porta de entrada HTTP. Valida o payload com `@Valid` e retorna DTOs — sem expor dados sensíveis.
6. **`dto` (Transferência de Dados):** DTOs de request validam entrada, DTOs de response controlam o que a API devolve ao cliente.
7. **`exception` (Tratamento Global):** `@RestControllerAdvice` intercepta cada exceção customizada e retorna o HTTP status semântico correto.

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
| **Senha segura** | Senhas são hasheadas com **BCrypt** antes de persistir no banco |

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

### Testes Unitários (TransferenciaService)
- ✅ Transferência realizada com sucesso
- ✅ Erro ao tentar transferir com saldo insuficiente
- ✅ Erro quando o pagador não existe no banco
- ✅ Erro quando o recebedor não existe no banco
- ✅ Erro quando o pagador é um lojista

### Testes de Integração (Controllers + H2)
- ✅ `POST /transferencias` — transferência válida retorna 200
- ✅ `POST /transferencias` — valor negativo retorna 400
- ✅ `POST /transferencias` — pagador inexistente retorna 404
- ✅ `POST /transferencias` — saldo insuficiente retorna 400
- ✅ `POST /usuarios` — cadastro válido retorna 201
- ✅ `POST /usuarios` — email inválido retorna 400
- ✅ `POST /usuarios` — saldo negativo retorna 400
- ✅ `POST /usuarios` — cadastro de lojista funciona

---

## ⚙️ Como Rodar o Projeto

### Pré-Requisitos
- Java JDK 21 ou superior
- Maven instalado (ou use o cache do wrapper em `~/.m2`)
- PostgreSQL rodando localmente na porta `5432`

### 1️⃣ Variáveis de Ambiente

O projeto usa variáveis de ambiente para configuração. Crie um arquivo `.env` ou configure no sistema:

| Variável | Descrição | Valor padrão |
|---|---|---|
| `DB_URL` | URL de conexão JDBC | `jdbc:postgresql://localhost:5432/money_transfer_db` |
| `DB_USER` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | `admin` |

**Exemplo no Windows (PowerShell):**
```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/money_transfer_db"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "sua_senha"
```

**Exemplo no Linux/Mac:**
```bash
export DB_URL="jdbc:postgresql://localhost:5432/money_transfer_db"
export DB_USER="postgres"
export DB_PASSWORD="sua_senha"
```

### 2️⃣ Setup do Banco de Dados
1. No `pgAdmin`, crie um banco chamado exatamente: **`money_transfer_db`**
2. As variáveis de ambiente sobrescrevem o `application.properties` automaticamente

### 3️⃣ Rodando a Aplicação

```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run
```

O Hibernate cria as tabelas automaticamente no primeiro boot via `ddl-auto=update`.

### 4️⃣ Rodando os Testes

```bash
# Linux/Mac
./mvnw test

# Windows
.\mvnw.cmd test
```

> Os testes de integração usam banco H2 em memória — não precisam de PostgreSQL rodando.

---

## 📡 Endpoints

### `POST /auth/login` — Autenticar e obter token JWT

**Request body:**
```json
{
  "email": "ana@email.com",
  "senha": "minhasenha123"
}
```

**Resposta de sucesso `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

> Use o token nas próximas requisições no header: `Authorization: Bearer <token>`

---

### `POST /usuarios` — Cadastrar usuário _(público)_

**Request body:**
```json
{
  "nomeCompleto": "Ana Silva",
  "email": "ana@email.com",
  "senha": "minhasenha123",
  "cpf": "111.111.111-11",
  "tipo": "COMUM",
  "saldo": 1000.00
}
```

**Resposta de sucesso `201 Created`:**
```json
{
  "id": 1,
  "nomeCompleto": "Ana Silva",
  "email": "ana@email.com",
  "cpf": "111.111.111-11",
  "tipo": "COMUM",
  "saldo": 1000.00
}
```

> ⚠️ A senha **não** é retornada na resposta — é armazenada com hash BCrypt.

---

### `GET /usuarios/{id}` — Buscar usuário por ID 🔒

**Resposta de sucesso `200 OK`:**
```json
{
  "id": 1,
  "nomeCompleto": "Ana Silva",
  "email": "ana@email.com",
  "cpf": "111.111.111-11",
  "tipo": "COMUM",
  "saldo": 1000.00
}
```

---

### `POST /transferencias` — Realizar uma transferência 🔒

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
  "dataTransferencia": "2026-03-08T18:30:00.000"
}
```

---

### `GET /transferencias` — Listar histórico de transferências 🔒

**Resposta de sucesso `200 OK`:**
```json
[
  {
    "id": 1,
    "idPagador": 1,
    "idRecebedor": 2,
    "valor": 50.00,
    "dataTransferencia": "2026-03-08T18:30:00.000"
  }
]
```

> 🔒 Endpoints marcados exigem o header `Authorization: Bearer <token>`

---
## 📌 Considerações Finais

Este projeto foi construído como um exercício prático para consolidar conceitos de back-end com Java e Spring Boot. A ideia foi ir além do "hello world" e trabalhar com um domínio real: transferências financeiras, com regras de negócio, segurança e testes.

Algumas decisões foram tomadas conscientemente por se tratar de um MVP de aprendizado:

- O JWT foi implementado sem refresh token — em produção, isso seria necessário para não forçar o usuário a fazer login a cada 2 horas.
- O Flyway gerencia as migrações, mas ainda não há scripts de rollback (`V1__undo`).
- Não há paginação no `GET /transferencias` — com volume alto de dados, isso seria obrigatório.

Foi um projeto que cresceu bastante ao longo do desenvolvimento e pretendo continuar evoluindo ele conforme avanço nos estudos.
