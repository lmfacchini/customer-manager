# Client Manager API

API REST para gerenciamento de clientes, consulta de score de crédito e processamento assíncrono de eventos.

## Arquitetura da solução

A aplicação organiza a API REST, as regras de negócio, persistência, integrações
HTTP e mensageria em camadas com princípios hexagonais. O diagrama de componentes,
os fluxos e as justificativas das tecnologias estão em
[Arquitetura e decisões técnicas](docs/architecture.md).

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
| Keycloak | `8082` | Emissão dos Access Tokens |
| WireMock | `8081` | Simulação do serviço de score |
| ViaCEP | HTTPS | Consulta externa de endereços |
| Client Manager API | `8080` | Aplicação |

As portas publicadas estão definidas em `docker-compose.yml`. A aplicação Java é
iniciada separadamente e usa a porta padrão do Spring Boot (`8080`).

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

As configurações efetivas e seus valores padrão estão em
[`src/main/resources/application.yaml`](src/main/resources/application.yaml) e
em [Arquitetura e decisões técnicas](docs/architecture.md). Entre as variáveis
suportadas estão `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `RABBITMQ_HOST`,
`RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`, `JWT_ISSUER_URI` e
`VIACEP_BASE_URL`. Os nomes de exchange, filas e routing keys são configurados
diretamente no YAML.

## Banco de dados

A aplicação utiliza PostgreSQL e JPA/Hibernate.

O banco deve possuir:

- Tabela de clientes;
- Sequence para geração do identificador do cliente;
- Constraints únicas para CPF e e-mail;
- Tabela `prcssd_mssgs`;
- Constraint única para o identificador do evento;
- Índices necessários para consultas e filtros.

Os scripts de inicialização estão em `src/main/resources/db/postgresql` e são
montados pelo Compose no diretório de inicialização do PostgreSQL.

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

O consumidor tenta registrar o `eventId` em `prcssd_mssgs` para rejeitar duplicatas.
No código atual, esse consumidor ainda não aplica a alteração de status ao
cliente; consulte também [a visão de arquitetura](docs/architecture.md).

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

## Consulta de CEP e endereços prioritários

`GET /addresses/{cep}` procura primeiro na tabela local de endereços prioritários.
Se o CEP não estiver cadastrado, consulta o ViaCEP (`/ws/{cep}/json/`). O CEP aceita
o formato de oito dígitos, com hífen opcional (`01001000` ou `01001-000`). Consulta
requer o perfil `USER` ou `ADMIN`.

Cadastre ou atualize um endereço prioritário (requer `ADMIN`):

```bash
curl --request PUT \
  http://localhost:8080/addresses/prioritized \
  --header "Authorization: Bearer <ACCESS_TOKEN>" \
  --header "Content-Type: application/json" \
  --data '{
    "cep": "01001-000",
    "logradouro": "Praça da Sé",
    "complemento": "lado ímpar",
    "bairro": "Sé",
    "localidade": "São Paulo",
    "uf": "SP"
  }'
```

Consulte um CEP:

```bash
curl --header "Authorization: Bearer <ACCESS_TOKEN>" \
  http://localhost:8080/addresses/01001000
```

Remova um CEP prioritário (requer `ADMIN`):

```bash
curl --request DELETE \
  --header "Authorization: Bearer <ACCESS_TOKEN>" \
  http://localhost:8080/addresses/prioritized/01001000
```

Cada consulta publica um evento RabbitMQ `ADDRESS_LOOKUP`. O consumidor registra,
de forma assíncrona e idempotente, o horário, CEP, origem (base interna ou ViaCEP) e
todos os campos retornados em `address_lookup_logs`. O endpoint não espera a gravação
do log. O endereço consultado internamente tem precedência sobre o serviço externo.

Em uma instalação nova, o `v2_ddl.sql` é executado automaticamente pelo PostgreSQL
junto dos scripts de inicialização. Para uma base criada anteriormente, aplique o
script uma única vez:

```bash
psql -h localhost -U postgres -d db_dev \
  -f src/main/resources/db/postgresql/v2_ddl.sql
```

## Segurança

A aplicação funciona como um OAuth 2.0 Resource Server.

Ela não autentica usuários diretamente e não emite Access Tokens. O Access Token deve ser obtido no servidor de identidade, como o Keycloak.

Perfis disponíveis:

| Perfil | Permissões |
|---|---|
| `USER` | Consulta, filtro e score |
| `ADMIN` | Consulta, criação, atualização e exclusão |


| User     | Password   |
|----------|------------|
| `user2`  | 'user123'  |
| `admin2` | 'admin123' |

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
  --data-urlencode "client_id=customer-api" \
  --data-urlencode "username=<USERNAME>" \
  --data-urlencode "password=<PASSWORD>"
```

Se o cliente do Keycloak for confidencial:

```bash
curl --request POST \
  "http://localhost:8082/realms/customer/protocol/openid-connect/token" \
  --header "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "grant_type=password" \
  --data-urlencode "client_id=customer-api" \
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
| `422` | CEP inválido |
| `502` | Falha no serviço externo de score ou CEP |
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