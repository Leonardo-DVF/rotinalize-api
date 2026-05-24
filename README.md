# Rotinalize API

Backend REST para gerenciamento de hábitos, listas de rotina e estudos com flashcards usando repetição espaçada.

Este repositório é focado apenas no backend. A aplicação foi desenvolvida com Spring Boot, Spring Security, JWT, Spring Data JPA e banco H2 por padrão para facilitar a execução local.

## Funcionalidades

- Cadastro de usuários com senha criptografada usando BCrypt.
- Login com Basic Auth e emissão de token JWT assinado com RSA.
- Proteção dos endpoints com Bearer Token.
- Isolamento de dados por usuário autenticado.
- CRUD de listas de hábitos.
- CRUD de hábitos com três modelos de agenda:
  - dias específicos da semana;
  - data de vencimento;
  - intervalo em dias.
- Lembretes por e-mail para hábitos com vencimento hoje ou amanhã.
- CRUD de decks e flashcards.
- Revisão de flashcards com algoritmo simples de repetição espaçada.
- Documentação interativa com Swagger UI.
- Testes unitários para regras de usuário e flashcards, além de teste de contexto da aplicação.

## Stack

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Security
- OAuth2 Resource Server / JWT
- Spring Data JPA / Hibernate
- Bean Validation
- H2 Database
- PostgreSQL via Docker
- Spring Mail
- Springdoc OpenAPI
- Maven
- Lombok

## Como Rodar com Docker

Crie o arquivo `.env` a partir do exemplo:

```bash
cp .env.example .env
```

No Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Suba a API e o PostgreSQL:

```bash
docker compose up --build
```

A API ficará disponível em:

```text
http://localhost:8081
```

Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

O PostgreSQL roda no container `rotinalize-postgres` e usa as credenciais definidas no `.env`.

Para parar os containers:

```bash
docker compose down
```

Para apagar também o volume do banco:

```bash
docker compose down -v
```

## Como Rodar Localmente sem Docker

Entre na pasta do backend:

```bash
cd backend
```

Execute a aplicação:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

Sem Docker, por padrão, a API usa H2 em memória.

Console H2:

```text
http://localhost:8080/h2-console
```

## Testes

Entre na pasta `backend` e execute:

```bash
./mvnw test
```

No Windows:

```bash
mvnw.cmd test
```

## Swagger / OpenAPI

A documentação interativa da API está disponível pelo Swagger UI.

Com Docker:

```text
http://localhost:8081/swagger-ui.html
```

Rodando localmente sem Docker:

```text
http://localhost:8080/swagger-ui.html
```

O contrato OpenAPI em JSON fica disponível em:

```text
/v3/api-docs
```

Para testar endpoints protegidos no Swagger:

1. Crie um usuário em `POST /api/users`.
2. Faça login em `POST /api/users/authenticate`.
3. Copie o JWT retornado.
4. Clique em `Authorize` no Swagger UI.
5. Informe o token no campo `bearer-key`.

## Postman

A collection do Postman está disponível em:

```text
docs/postman/Rotinalize API.postman_collection.json
```

O environment local está disponível em:

```text
docs/postman/Rotinalize API.postman_environment.json
```

Para usar:

1. Importe a collection no Postman.
2. Importe o environment `Rotinalize Local`.
3. Selecione o environment importado.
4. Execute primeiro `Auth / Criar Usuário`.
5. Execute `Auth / Login` para preencher automaticamente a variável `token`.

## Configuração

A aplicação usa variáveis de ambiente. O arquivo `.env.example` documenta os valores esperados, e o `.env` local é ignorado pelo Git.

| Variável | Padrão |
| --- | --- |
| `DATABASE_URL` | `jdbc:h2:mem:rotinalize;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE` |
| `DATABASE_USERNAME` | `sa` |
| `DATABASE_PASSWORD` | vazio |
| `DATABASE_DRIVER` | `org.h2.Driver` |
| `JPA_DDL_AUTO` | `update` |
| `JPA_SHOW_SQL` | `false` |
| `H2_CONSOLE_ENABLED` | `true` |
| `MAIL_HOST` | `smtp.gmail.com` |
| `MAIL_PORT` | `587` |
| `MAIL_USERNAME` | vazio |
| `MAIL_PASSWORD` | vazio |
| `MAIL_HEALTH_ENABLED` | `false` |
| `REMINDER_CRON` | `0 0 7 * * *` |

### JWT

O backend procura as chaves em:

```text
classpath:app.key
classpath:app.pub
```

Se elas não existirem, a aplicação gera um par RSA em memória. Isso facilita o desenvolvimento e os testes locais. Para um ambiente real, configure chaves persistentes via `JWT_PRIVATE_KEY` e `JWT_PUBLIC_KEY`, apontando para os recursos desejados.

## Endpoints Principais

### Usuários e Autenticação

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/api/users` | Cria um usuário. |
| `POST` | `/api/users/authenticate` | Autentica com Basic Auth e retorna um JWT. |
| `GET` | `/api/users/me` | Retorna o usuário autenticado. |
| `GET` | `/api/users/{id}` | Retorna o usuário, se for o próprio autenticado. |
| `PUT` | `/api/users/{id}` | Atualiza o usuário, se for o próprio autenticado. |
| `DELETE` | `/api/users/{id}` | Remove o usuário, se for o próprio autenticado. |

### Listas e Hábitos

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/api/lists` | Lista as listas do usuário autenticado. |
| `POST` | `/api/lists` | Cria uma lista. |
| `GET` | `/api/lists/{id}` | Busca uma lista do usuário autenticado. |
| `DELETE` | `/api/lists/{id}` | Remove uma lista do usuário autenticado. |
| `GET` | `/api/habits` | Lista os hábitos do usuário autenticado. |
| `POST` | `/api/habits` | Cria um hábito. |
| `GET` | `/api/habits/{id}` | Busca um hábito do usuário autenticado. |
| `PUT` | `/api/habits/{id}` | Atualiza um hábito do usuário autenticado. |
| `DELETE` | `/api/habits/{id}` | Remove um hábito do usuário autenticado. |

### Flashcards

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/api/decks` | Lista os decks do usuário autenticado. |
| `POST` | `/api/decks` | Cria um deck. |
| `PUT` | `/api/decks/{deckId}` | Atualiza um deck do usuário autenticado. |
| `DELETE` | `/api/decks/{deckId}` | Remove um deck do usuário autenticado. |
| `POST` | `/api/flashcards` | Cria um flashcard em um deck do usuário. |
| `GET` | `/api/flashcards/deck/{deckId}` | Lista cards de um deck do usuário. |
| `GET` | `/api/flashcards/review-today` | Lista cards pendentes para revisão. |
| `POST` | `/api/flashcards/{cardId}/review?rating=FACIL` | Registra uma revisão e recalcula o próximo estudo. |
| `PUT` | `/api/flashcards/{cardId}` | Atualiza um flashcard do usuário. |
| `DELETE` | `/api/flashcards/{deckId}/{cardId}` | Remove um flashcard do usuário. |

Valores aceitos para `rating`:

```text
FACIL
BOM
DIFICIL
```

## Pontos de Backend Demonstrados

- Modelagem de entidades e relacionamentos JPA.
- DTOs para entrada e saída da API.
- Validação declarativa com Bean Validation.
- Autenticação stateless com JWT.
- Regras de autorização por dono do recurso.
- Agendamento com `@Scheduled`.
- Envio assíncrono de e-mail com `@Async`.
- Testes automatizados com JUnit, AssertJ e Mockito.
