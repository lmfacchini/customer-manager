# Client Manager API

API REST para gerenciamento de clientes, consulta de score de crédito e processamento assíncrono de eventos.

## Arquitetura da solução

A aplicação utiliza arquitetura hexagonal, mantendo as regras de negócio desacopladas de frameworks e serviços externos.

```text
src/main/java/com/datum/client/manager
├── client
│   └── Cliente HTTP do serviço de score
├── configuration
│   └── Configuração e criação dos beans
├── constants
│   └── Constantes e enumerações
├── controller
│   └── Adaptadores de entrada REST
├── domain
│   └── Entidades e objetos de domínio
├── dto
│   └── Objetos de entrada e saída
├── exception
│   └── Exceções e tratamento de erros
├── mapper
│   └── Conversão entre entidades e DTOs
├── mq
│   ├── consumer
│   │   └── Consumidores RabbitMQ
│   ├── event
│   │   └── Eventos e mensagens
│   └── publisher
│       └── Publicadores RabbitMQ
├── repository
│   ├── Portas de persistência
│   └── jpa
│       └── Adaptadores de persistência JPA
├── security
│   └── Configuração OAuth 2.0 e JWT
└── service
    ├── Regras de negócio
    ├── transactional
    │   └── Decoradores transacionais
    └── validation
        └── Validações de negócio
```

Fluxo principal:

```text
Controller
    |
    v
CustomerServiceTransactional
    |
    v
CustomerServiceImpl
    |
    +--> CustomerRepository --> PostgreSQL
    |
    +--> ScoresApiClient --> Serviço de Score
    |
    +--> CustomerCreatedPublisher --> RabbitMQ
```

### Portas de entrada

- `CustomerService`
- `ProcessedMessageService`

### Portas de saída

- `CustomerRepository`
- `ProcessedMessageRepository`
- `CustomerCreatedPublisher`
- `ScoresApiClient`

### Adaptadores de entrada

- `CustomerController`
- `CustomerStatusChangeConsumer`

### Adaptadores de saída

- Repositórios JPA
- Cliente HTTP do serviço de score
- Publicador RabbitMQ

## Tecnologias

- Java 26
- Spring Boot 4
- Maven
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- RabbitMQ
- Spring Security
- OAuth 2.0 Resource Server
- JWT
- Keycloak
- OpenAPI/Swagger
- WireMock
- JUnit 5
- Mockito
- MapStruct
- Lombok
- Docker
- Docker Compose

## Requisitos para execução

- JDK 26
- Maven 3.9 ou superior
- Docker
- Docker Compose

Verifique as instalações:

```bash
java --version
mvn --version
docker --version
docker compose version
```

## Dependências externas

| Serviço | Porta padrão | Finalidade |
|---|---:|---|
| PostgreSQL | `5432` | Persistência |
| RabbitMQ | `5672` | Mensageria |
| RabbitMQ Management | `15672` | Administração do RabbitMQ |
| Keycloak | `8080` | Emissão dos Access Tokens |
| WireMock | `8081` | Simulação do serviço de score |
| Client Manager API | `8082` | Aplicação |

As portas devem ser ajustadas conforme o `docker-compose.yaml` e o `application.yaml` do projeto.

## Como iniciar as dependências

Na raiz do projeto, execute:

```bash
docker compose up -d
```

Verifique os containers:

```bash
docker compose ps
```

Acompanhe os logs:

```bash
docker compose logs -f
```

Logs de um serviço específico:

```bash
docker compose logs -f postgres
docker compose logs -f rabbitmq
docker compose logs -f keycloak
docker compose logs -f wiremock
```

Para interromper os serviços:

```bash
docker compose down
```

Para remover os containers e volumes:

```bash
docker compose down -v
```

> O comando `docker compose down -v` remove os dados persistidos nos volumes.

## Configuração da aplicação

Exemplo de `application.yaml`:

```yaml
server:
  port: ${SERVER_PORT:8082}

spring:
  application:
    name: client-manager

  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/client_manager}
    username: ${DATABASE_USERNAME:postgres}
    password: ${DATABASE_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver

  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME:guest}
    password: ${RABBITMQ_PASSWORD:guest}

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI:http://localhost:8080/realms/client-manager}

application:
  clients:
    score:
      base-url: ${SCORE_API_BASE_URL:http://localhost:8081}

  rabbitmq:
    exchange: ${CUSTOMER_EXCHANGE:customer.exchange}

    queues:
      customer-status-change: ${CUSTOMER_STATUS_CHANGE_QUEUE:customer.status-change.queue}

    routing-keys:
      customer-created: ${CUSTOMER_CREATED_ROUTING_KEY:customer.created}
      customer-status-change: ${CUSTOMER_STATUS_CHANGE_ROUTING_KEY:customer.status-change}
```

## Variáveis de ambiente

| Variável | Valor padrão | Descrição |
|---|---|---|
| `SERVER_PORT` | `8082` | Porta da aplicação |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/client_manager` | URL do PostgreSQL |
| `DATABASE_USERNAME` | `postgres` | Usuário do PostgreSQL |
| `DATABASE_PASSWORD` | `postgres` | Senha do PostgreSQL |
| `RABBITMQ_HOST` | `localhost` | Host do RabbitMQ |
| `RABBITMQ_PORT` | `5672` | Porta do RabbitMQ |
| `RABBITMQ_USERNAME` | `guest` | Usuário do RabbitMQ |
| `RABBITMQ_PASSWORD` | `guest` | Senha do RabbitMQ |
| `JWT_ISSUER_URI` | `http://localhost:8080/realms/client-manager` | Emissor do JWT |
| `SCORE_API_BASE_URL` | `http://localhost:8081` | URL do serviço de score |
| `CUSTOMER_EXCHANGE` | `customer.exchange` | Exchange dos eventos |
| `CUSTOMER_CREATED_ROUTING_KEY` | `customer.created` | Routing key de criação |
| `CUSTOMER_STATUS_CHANGE_QUEUE` | `customer.status-change.queue` | Fila de alteração de status |
| `CUSTOMER_STATUS_CHANGE_ROUTING_KEY` | `customer.status-change` | Routing key de alteração de status |

Exemplo:

```bash
export SERVER_PORT="8082"
export DATABASE_URL="jdbc:postgresql://localhost:5432/client_manager"
export DATABASE_USERNAME="postgres"
export DATABASE_PASSWORD="postgres"
export RABBITMQ_HOST="localhost"
export RABBITMQ_PORT="5672"
export RABBITMQ_USERNAME="guest"
export RABBITMQ_PASSWORD="guest"
export JWT_ISSUER_URI="http://localhost:8080/realms/client-manager"
export SCORE_API_BASE_URL="http://localhost:8081"
```

## Banco de dados

A aplicação utiliza PostgreSQL e JPA/Hibernate.

O banco deve possuir:

- Tabela de clientes;
- Sequence para geração do identificador do cliente;
- Constraints únicas para CPF e e-mail;
- Tabela `processed_messages`;
- Constraint única para o identificador do evento;
- Índices necessários para consultas e filtros.

Caso o projeto utilize Flyway, os scripts devem ficar em:

```text
src/main/resources/db/migration
```

Exemplo:

```text
src/main/resources/db/migration/V1__create_customer.sql
src/main/resources/db/migration/V2__create_processed_messages.sql
```

## RabbitMQ

Quando um cliente é criado, a aplicação publica um evento:

```json
{
  "eventId": "7ba85b17-7d33-4d17-9101-569a362239f2",
  "eventType": "CUSTOMER_CREATED",
  "customerId": 123,
  "createdAt": "2026-08-11T15:30:00Z"
}
```

A aplicação também consome mensagens solicitando alteração do status de um cliente:

```json
{
  "eventId": "cbca5352-22ad-48f2-aaf2-704735bc7737",
  "eventType": "CUSTOMER_STATUS_CHANGE",
  "customerId": 123,
  "status": "INACTIVE"
}
```

A tabela `processed_messages` controla a idempotência. Quando uma mensagem com o mesmo `eventId` é entregue mais de uma vez, somente a primeira deve ser processada.

## Serviço de score

A aplicação consulta o seguinte serviço externo:

```http
GET /scores/{cpf}
```

Exemplo:

```bash
curl http://localhost:8081/scores/12345678901
```

Exemplo de resposta:

```json
{
  "cpf": "12345678901",
  "score": 750,
  "classification": "LOW_RISK"
}
```

Classificação utilizada pela aplicação:

| Pontuação | Classificação |
|---:|---|
| Menor que `500` | `HIGH_RISK` |
| De `500` até `749` | `MEDIUM_RISK` |
| Maior ou igual a `750` | `LOW_RISK` |

## Segurança

A aplicação funciona como um OAuth 2.0 Resource Server.

Ela não autentica usuários diretamente e não emite Access Tokens. O Access Token deve ser obtido no servidor de identidade, como o Keycloak.

Perfis disponíveis:

| Perfil | Permissões |
|---|---|
| `USER` | Consulta, filtro e score |
| `ADMIN` | Consulta, criação, atualização e exclusão |

O JWT deve possuir as roles no claim `realm_access.roles`:

```json
{
  "realm_access": {
    "roles": [
      "USER",
      "ADMIN"
    ]
  }
}
```

As roles são convertidas para:

```text
ROLE_USER
ROLE_ADMIN
```

## Como obter o Access Token

Exemplo para ambiente de desenvolvimento:

```bash
curl --request POST \
  "http://localhost:8082/realms/customer/protocol/openid-connect/token" \
  --header "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "grant_type=password" \
  --data-urlencode "client_id=client-manager-api" \
  --data-urlencode "username=<USERNAME>" \
  --data-urlencode "password=<PASSWORD>"
```

Se o cliente do Keycloak for confidencial:

```bash
curl --request POST \
  "http://localhost:8082/realms/customer/protocol/openid-connect/token" \
  --header "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "grant_type=password" \
  --data-urlencode "client_id=client-manager-api" \
  --data-urlencode "client_secret=<CLIENT_SECRET>" \
  --data-urlencode "username=<USERNAME>" \
  --data-urlencode "password=<PASSWORD>"
```

Exemplo de resposta:

```json
{
  "access_token": "<ACCESS_TOKEN>",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "<REFRESH_TOKEN>",
  "token_type": "Bearer"
}
```

Utilize o token no cabeçalho:

```http
Authorization: Bearer <ACCESS_TOKEN>
```

Exemplo:

```bash
curl \
  --header "Authorization: Bearer <ACCESS_TOKEN>" \
  http://localhost:8080/customers/1
```

### Usuários

Substitua pelos usuários configurados no Keycloak:

| Usuário | Senha | Perfil |
|---|---|---|
| `<USER_USERNAME>` | `<USER_PASSWORD>` | `USER` |
| `<ADMIN_USERNAME>` | `<ADMIN_PASSWORD>` | `ADMIN` |

## Como iniciar a aplicação

### Utilizando Maven Wrapper

Linux:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
mvnw.cmd spring-boot:run
```

### Utilizando Maven instalado

```bash
mvn spring-boot:run
```

### Utilizando o arquivo JAR

Compile:

```bash
./mvnw clean package
```

Execute:

```bash
java -jar target/<NOME_DO_ARQUIVO>.jar
```

Exemplo:

```bash
java -jar target/client-manager-0.0.1-SNAPSHOT.jar
```

## Como executar os testes

Todos os testes:

```bash
./mvnw test
```

Um teste específico:

```bash
./mvnw -Dtest=CustomerServiceImplTest test
```

Um método específico:

```bash
./mvnw \
  -Dtest=CustomerServiceImplTest#shouldCreateCustomerAndPublishEvent \
  test
```

Testes e validações do Maven:

```bash
./mvnw clean verify
```

Gerar o pacote sem executar os testes:

```bash
./mvnw clean package -DskipTests
```

## Endpoints disponíveis

URL base:

```text
http://localhost:8082
```

### Criar cliente

```http
POST /customers
```

Permissão:

```text
ADMIN
```

Exemplo:

```bash
curl --request POST \
  http://localhost:8080/customers \
  --header "Authorization: Bearer <ACCESS_TOKEN>" \
  --header "Content-Type: application/json" \
  --data '{
    "name": "Leonardo Facchini",
    "email": "leonardo@example.com",
    "cpf": "12345678901"
  }'
```

Resposta:

```http
HTTP/1.1 201 Created
Location: /customers/1
```

### Atualizar cliente

```http
PUT /customers/{id}
```

Permissão:

```text
ADMIN
```

Exemplo:

```bash
curl --request PUT \
  http://localhost:8080/customers/1 \
  --header "Authorization: Bearer <ACCESS_TOKEN>" \
  --header "Content-Type: application/json" \
  --data '{
    "name": "Leonardo Facchini",
    "email": "leonardo@example.com",
    "cpf": "12345678901",
    "status": "ACTIVE"
  }'
```

Resposta:

```http
HTTP/1.1 202 Accepted
```

### Excluir cliente

```http
DELETE /customers/{id}
```

Permissão:

```text
ADMIN
```

Exemplo:

```bash
curl --request DELETE \
  http://localhost:8080/customers/1 \
  --header "Authorization: Bearer <ACCESS_TOKEN>"
```

Resposta:

```http
HTTP/1.1 204 No Content
```

### Consultar cliente por ID

```http
GET /customers/{id}
```

Permissões:

```text
USER
ADMIN
```

Exemplo:

```bash
curl \
  --header "Authorization: Bearer <ACCESS_TOKEN>" \
  http://localhost:8080/customers/1
```

### Filtrar clientes

```http
GET /customers
```

Permissões:

```text
USER
ADMIN
```

Parâmetros:

| Parâmetro | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `name` | `String` | Não | Parte do nome |
| `email` | `String` | Não | Parte do e-mail |
| `cpf` | `String` | Não | CPF exato |
| `status` | `CustomerStatus` | Não | Status do cliente |
| `createdAtStart` | `Long` | Não | Data inicial de criação em epoch milliseconds |
| `createdAtEnd` | `Long` | Não | Data final de criação em epoch milliseconds |
| `updatedAtStart` | `Long` | Não | Data inicial de atualização em epoch milliseconds |
| `updatedAtEnd` | `Long` | Não | Data final de atualização em epoch milliseconds |
| `page` | `Integer` | Não | Página iniciando em zero |
| `max` | `Integer` | Não | Quantidade máxima de resultados |

Exemplo:

```bash
curl \
  --header "Authorization: Bearer <ACCESS_TOKEN>" \
  "http://localhost:8080/customers?name=Leonardo&status=ACTIVE&page=0&max=20"
```

### Consultar score

```http
GET /customers/{id}/score
```

Permissões:

```text
USER
ADMIN
```

Exemplo:

```bash
curl \
  --header "Authorization: Bearer <ACCESS_TOKEN>" \
  http://localhost:8080/customers/1/score
```

Exemplo de resposta:

```json
{
  "cpf": "12345678901",
  "score": 750,
  "classification": "LOW_RISK"
}
```

## Swagger

Caso o Springdoc esteja configurado:

```text
http://localhost:8080/swagger-ui/index.html
```

Contrato OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

## Health check

Caso o Spring Boot Actuator esteja configurado:

```text
http://localhost:8080/actuator/health
```

Exemplo:

```bash
curl http://localhost:8080/actuator/health
```

## Códigos HTTP

| Código | Descrição |
|---:|---|
| `200` | Requisição concluída |
| `201` | Cliente criado |
| `202` | Atualização aceita |
| `204` | Exclusão concluída |
| `400` | Dados inválidos ou erro de negócio |
| `401` | Access Token ausente ou inválido |
| `403` | Usuário sem autorização |
| `404` | Cliente não encontrado |
| `409` | Conflito de CPF, e-mail ou evento |
| `500` | Erro interno |

## Regras principais

- CPF deve ser único.
- E-mail deve ser único.
- O e-mail é armazenado em letras minúsculas.
- Espaços externos do CPF e do e-mail são removidos.
- A criação de um cliente publica o evento `CUSTOMER_CREATED`.
- O consumidor aceita somente mensagens `CUSTOMER_STATUS_CHANGE`.
- O processamento de mensagens deve ser idempotente.
- Operações de escrita exigem o perfil `ADMIN`.
- Operações de consulta permitem os perfis `USER` e `ADMIN`.
- A API valida Access Tokens, mas não os emite.