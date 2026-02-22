# Money Transfer API (Spring Boot MVP) 🚀

Uma API RESTful robusta desenvolvida em Java 21 com Spring Boot para simulação de serviços financeiros. O projeto foi construído seguindo os consolidados padrões de Arquitetura em Camadas (MVC) e focado nas melhores práticas do mercado corporativo.

## 🛠️ Tecnologias Utilizadas

- **Java 21:** Sintaxe moderna com _Java Records_ para DTOs.
- **Spring Boot 3:** Autoconfiguração, IoC e ambiente web embarcado (Tomcat).
- **Spring Data JPA & Hibernate:** Persistência relacional sem boilerplate SQL.
- **PostgreSQL:** Banco de dados relacional (transacional) robusto e de mercado.
- **Lombok:** Redução drástica de boilerplate code (Getters, Setters, Construtores).
- **JUnit 5 + Mockito:** Garantia de qualidade e isolamento das regras de negócio (Testes Unitários).

---

## 🏗️ Arquitetura do Projeto

O sistema foi rigidamente estruturado nas responsabilidades clássicas da **MVC Layered Architecture**:

1. **`domain` (Entidades):** As classes fundamentais (`Usuario`, `Transferencia`). Elas representam as tabelas do PostgreSQL e usam relacionamentos como `@ManyToOne` para assegurar a integridade referencial.
2. **`repository` (Acesso a Dados):** Interfaces herdeiras de `JpaRepository`. Encarregadas das consultas eficientes ao banco.
3. **`service` (Regras de Negócio):** O "Cérebro" do aplicativo. Aqui reside a matemática complexa usando `BigDecimal`, as validações precisas de saldo em conta, e a blindagem contra fraudes nas transações.
4. **`controller` (Apresentação Web):** A porta de entrada HTTP. Protegida pelo padrão **DTO**, garante que os endpoints recebam e enviem apenas os dados exatos (evitando vazamento de senhas ou logs do banco).
5. **`exception` (Tratamento Global):** Via `@RestControllerAdvice`, intercepta erros feios do Java (_HTTP 500_) e devolve mensagens formatadas e amigáveis ao front-end (_HTTP 400 Bad Request_).

---

## 🎯 Regras de Negócios e Funcionalidades (O MVP)

* **Segurança de Dados:** O sistema não salva um usuário se houver conflito de CPF ou E-mail (`@Column(unique=true)`).
* **Consistência Matemática:** Utilização 100% de `BigDecimal` nas operações de crédito e débito bancário para evitar a letal "perda de precisão" dos tipos de ponto flutuante clássicos (`float`/`double`).
* **Auditoria Básica:** Toda tentativa de transferência exige a dupla checagem de existência no banco e grava sua `LocalDateTime` exata no cofre relacional.
* **Isolamento Completo em Testes:** Toda a camada referida cima foi estressada via Testes Unitários com o Mockito mentindo propositalmente o comportamento do Banco de Dados para forçar erros de "Saldo Insuficiente" e comprovar a segurança do algoritmo.

---

## ⚙️ Como Rodar o Projeto

### Pré-Requisitos:
- Java JDK 21 ou superior.
- Maven (Opcional, pois o repositório traz o `mvnw`).
- PostgreSQL instalado localmente (na porta padrão `5432`).

### 1️⃣ Setup do Banco de Dados
1. Abra o `pgAdmin` e crie um banco de dados vazio chamado exato de: **`money_transfer_db`**.
2. Abra o arquivo `application.properties` (na pasta `src/main/resources`) e verifique se as credenciais (usuário e senha do Postgres) estão batendo com o seu ambiente local:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/money_transfer_db
spring.datasource.username=postgres
spring.datasource.password=sua_senha_aqui
```

### 2️⃣ Inicialização Simples
Rodando via terminal:
```bash
./mvnw spring-boot:run
```
*(No Windows, utilize `.\mvnw.cmd spring-boot:run`)*

O Hibernate entra em ação automaticamente via parâmetro `update` e **criará todas as tabelas fisicamente** assim que o projeto inicializar, não havendo necessidade de migrações externas ou scripts DDL brutos.

---

## 📡 Endpoints (Postman/Insomnia)

### Transferir Dinheiro entre Contas (`POST /transferencias`)

*Nota: Garanta que você já tenha incluído usuários fictícios com base diretamente no seu `pgAdmin` antes de testar a rota.*
```json
{
  "idPagador": 1,
  "idRecebedor": 2,
  "valor": 50.00
}
```

**Retorno de Sucesso (`200 OK`)**:
```json
{
  "id": 1,
  "pagador": {
     ...
  },
  "recebedor": {
     ...
  },
  "valor": 50.00,
  "dataTransferencia": "2024-03-12T14:30:15.123"
}
```

**Retorno de Falha/Erro Amigável (`400 Bad Request`)**:
```text
Saldo insuficiente na conta do pagador.
```

---

> Esse MVP foi idealizado e construído com práticas avançadas de mentoria em engenharia de software corporativa, simulando os moldes exatos da indústria atual de serviços transacionais REST.
