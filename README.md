# Banco Digital API

API REST simplificada para um banco digital: transferência de valores entre contas, consulta de movimentações e notificações aos clientes.

## Requisitos

- **Java 17+**
- **Maven 3.8+**

## Como rodar o projeto

### Executar a aplicação

```bash
mvn spring-boot:run
```

A API sobe em **http://localhost:8080**.

### Executar os testes

```bash
mvn test
```

### Build (pacote JAR)

```bash
mvn clean package -DskipTests
java -jar target/banco-digital-api-1.0.0-SNAPSHOT.jar
```

## Endpoints principais

Toda a API usa **UUID** nos recursos (nunca ID numérico), para evitar enumeração e aumentar a segurança.

| Método | Recurso | Descrição |
|--------|---------|-----------|
| GET | `/api/accounts` | Listar contas (retorna `uuid`, nome, saldo) |
| GET | `/api/accounts/{uuid}` | Buscar conta por UUID |
| GET | `/api/accounts/{uuid}/movements` | Movimentações da conta |
| POST | `/api/accounts` | Cadastrar conta (nome, saldo inicial); resposta com `uuid` |
| POST | `/api/transfers` | Transferência: body com `fromAccountUuid`, `toAccountUuid`, `amount` |
| GET | `/api/transfers/account/{accountUuid}` | Transferências da conta |
| GET | `/api/notifications/account/{accountUuid}` | Notificações da conta |

## Swagger (OpenAPI)

- **UI:** http://localhost:8080/swagger-ui.html  
- **JSON:** http://localhost:8080/v3/api-docs  

## Banco de dados

- **H2** em memória (sem instalação).
- Console H2 (opcional): http://localhost:8080/h2-console  
  - JDBC URL: `jdbc:h2:mem:bancodigital`  
  - User: `sa` / Password: (vazio)

Ao subir, o sistema **pré-carrega 3 contas** para testes: Maria Silva (R$ 1.000), João Santos (R$ 500,50), Ana Oliveira (R$ 2.500).

---

## Estrutura do projeto

```
src/main/java/br/com/cwi/bancodigital/
├── config/           # DataLoader, OpenAPI
├── constant/         # ApiPaths, TableNames, ColumnNames (recursos e entidades)
├── controller/       # REST (Account, Transfer, Notification)
├── domain/           # Entidades JPA (Account, Transfer, Notification)
├── dto/              # Request/Response (records)
├── exception/        # BusinessException, ResourceNotFoundException, GlobalExceptionHandler
├── repository/       # JpaRepository + JpaSpecificationExecutor
├── service/          # Regras de negócio
└── specification/    # JPA Specifications (consultas dinâmicas)
```

- **Paths e nomes centralizados:** recursos da API em `ApiPaths`, tabelas em `TableNames`, colunas FK em `ColumnNames`, evitando strings espalhadas e facilitando refatoração.
- **UUID na API:** todas as entidades possuem campo `uuid` (único, gerado em `@PrePersist`). A chave primária continua sendo `id` (Long) apenas para uso interno; requisições e respostas usam apenas UUID.
- **Entidades com Lombok:** `@Getter`, `@Setter`, `@NoArgsConstructor` e construtores de conveniência; sem getters/setters manuais.
- **Serviços e controllers com Lombok:** `@RequiredArgsConstructor` para injeção de dependências.

---

## Decisões de design e arquitetura

### Arquitetura em camadas

- **Controller:** exposição REST, validação de entrada (Bean Validation), documentação Swagger. Utiliza constantes de `ApiPaths` para mapeamento de recursos.
- **Service:** regras de negócio, orquestração, transações.
- **Repository:** acesso a dados (Spring Data JPA + **JpaSpecificationExecutor**).
- **Domain:** entidades (Account, Transfer, Notification) e DTOs (records) de request/response.
- **Specification:** critérios reutilizáveis para consultas dinâmicas (JPA Criteria API).

Objetivo: responsabilidades claras, testes por camada e evolução sem acoplar HTTP ao domínio.

### Consultas com JPA Specifications

- As listagens (contas, transferências por conta, notificações por conta) utilizam **Specifications** em vez de `@Query` fixas.
- **AccountSpecification:** filtros opcionais por nome (LIKE) e faixa de saldo (min/max); composição via `withFilters(name, minBalance, maxBalance)`.
- **TransferSpecification:** filtro por conta (origem ou destino) com **fetch** de `fromAccount` e `toAccount` para evitar N+1.
- **NotificationSpecification:** filtro por conta com **fetch** de `transfer` e `account`.
- Repositórios estendem `JpaRepository` e `JpaSpecificationExecutor`; ordenação feita via `Sort` nos serviços (`findAll(spec, Sort.by(...))`).

### Consistência e alta concorrência

- **Lock pessimista (`PESSIMISTIC_WRITE`)** na leitura das contas envolvidas na transferência (`findByIdForUpdate`), garantindo serialização das operações sobre o mesmo par de contas e evitando condições de corrida e saldo negativo.
- **Transação única** na transferência: débito, crédito e persistência da transferência no mesmo `@Transactional`, com rollback em caso de falha.
- **Versionamento otimista** (`@Version` em `Account`) como camada extra de proteção contra concorrência.

### Notificações

- **Síncronas e na mesma transação** da transferência: assim o `Transfer` já está persistido e visível ao inserir as notificações (evita violação de FK em `transfer_id`).
- Notificações **persistidas** (entidade `Notification`) para consulta por conta e auditoria; em produção pode-se adicionar envio por e-mail/push reutilizando o mesmo serviço.

### Resiliência e erros

- **GlobalExceptionHandler** centraliza respostas de erro (404 para recurso não encontrado, 422 para regra de negócio, 400 para validação).
- Uso de **Bean Validation** nos DTOs (ex.: valor positivo, contas distintas) para reduzir erros na borda da API.

### Tecnologias

- **Spring Boot 3.2**, **Spring Data JPA**, **H2** (troca por PostgreSQL/MySQL é trivial via `application.yml` e dependência).
- **JPA Specifications** para consultas dinâmicas e reutilizáveis.
- **Lombok** nas entidades e nos serviços/controllers (getters, setters, construtores).
- **SpringDoc OpenAPI** para Swagger 3.
- **JUnit 5** e **Mockito** para testes unitários de serviços e testes de controller com **MockMvc**.

### Testes

- Testes **unitários** nos serviços (AccountService, TransferService, NotificationService) com mocks dos repositórios e de `findAll(Specification, Sort)` onde aplicável.
- Testes de **controller** (AccountController, TransferController) com MockMvc e serviços mockados, cobrindo fluxos felizes e erros (404, 422, validação). URLs dos testes utilizam constantes de `ApiPaths`.

---

## Licença

Uso livre para fins de avaliação/estudo.
