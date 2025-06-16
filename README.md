# Sprint 1 - Webservice API

Este projeto é uma API RESTful desenvolvida em Java com Spring Boot para gerenciamento de usuários, ativos (assets) e carteiras (wallets), incluindo funcionalidades de autenticação JWT e operações de adicionar, atualizar e remover ativos de carteiras.

**Nomes + RM dos integrantes:**
- Fabrício Saavedra - 97631
- Guilherme Akio - 98582
- Guilherme Morais - 551981
- Lorenzo Ferreira  - 97318
- Açussena Macedo Mautone - 552568

**Turma:** 3ESPW

**Ano:** 2025

## Tecnologias

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- H2 Database (para desenvolvimento/testes)
- Spring Security com JWT
- BCrypt Password Encoder
- Maven

## Pré-requisitos

- JDK 21 instalado
- Maven instalado
- IDE de sua preferência (IntelliJ, Eclipse, VS Code)

## Configuração

1. Clone o repositório:
   ```bash
   git clone https://github.com/Guimeb/Java-API
   ```
2. Ajuste as propriedades de `src/main/resources/application.properties` ou `application.yml`:
   ```properties
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driver-class-name=org.h2.Driver
   spring.datasource.username=galo
   spring.datasource.password=

   # JWT
   jwt.public-key-location=classpath:app.pub
   jwt.private-key-location=classpath:app.key
   ```

## Executando a aplicação

```bash
mvn clean spring-boot:run
```

Acesse `http://localhost:8080/swagger-ui.html` para a documentação interativa.

Conta para autenticação
Usuario: galo
Senha: password

## Endpoints exemplos

### Autenticação

- **POST** `/authenticate`
  - **Descrição:** Autentica usuário e retorna token JWT.
  - **Request Body:**
    ```json
    {
      "username": "galo",
      "password": "password"
    }
    ```
  - **Response:**
    ```json
    {
      "token": "eyJhbGciOiJI...",
      "type": "Bearer"
    }
    ```

### Usuários (User)

- **POST** `/users`

  - **Descrição:** Cria um novo usuário e uma carteira associada.
  - **Body:**
    ```json
    {
      "username": "alice",
      "email": "alice@example.com",
      "password": "SenhaForte123!"
    }
    ```
  - **Response:** Objeto `User` com `id`, `username`, `email` (senha não retornada).

- **GET** `/users`

  - **Descrição:** Lista todos os usuários.
  - **Response:** Array de usuários (sem senha).

- **GET** `/users/{id}`

  - **Descrição:** Busca usuário por ID.

- **PUT** `/users`

  - **Descrição:** Atualiza dados do usuário (re-hash da senha).
  - **Body:**
    ```json
    {
      "id": 1,
      "username": "alice2",
      "email": "alice2@example.com",
      "password": "NovaSenha!"
    }
    ```

- **DELETE** `/users/{id}`

  - **Descrição:** Remove usuário.

### Ativos (Asset)

- **POST** `/assets`

  - **Descrição:** Cria ativo.
  - **Body:**
    ```json
    {
      "symbol": "BTC",
      "name": "Bitcoin",
      "currentValue": 60000.00
    }
    ```

- **GET** `/assets`

  - **Descrição:** Lista todos ativos.

- **GET** `/assets/{id}`

  - **Descrição:** Busca ativo por ID.

- **PUT** `/assets`

  - **Descrição:** Atualiza ativo.

- **DELETE** `/assets/{id}`

  - **Descrição:** Remove ativo.

### Carteiras (Wallet)

- **GET** `/wallets/{walletId}`
  - **Descrição:** Busca carteira pelo ID (contém lista de ativos na carteira).

### Ativos na Carteira (WalletAsset)

- **POST** `/wallets/{userId}/assets`

  - **Descrição:** Adiciona ativo à carteira.
  - **Body:**
    ```json
    {
      "assetId": 1,
      "quantity": 0.5,
      "purchasePrice": 30000.00
    }
    ```

- **GET** `/wallets/{walletId}/assets`

  - **Descrição:** Lista ativos em uma carteira.

- **PUT** `/wallets/{walletId}/assets/{walletAssetId}`

  - **Descrição:** Atualiza quantidade ou preço de um ativo na carteira.
  - **Body:**
    ```json
    {
      "quantity": 0.75,
      "purchasePrice": 32000.00
    }
    ```

- **DELETE** `/wallets/{walletId}/assets/{walletAssetId}`

  - **Descrição:** Remove ativo da carteira.

### Rotas Privadas (Private)

- **GET** `/private`
  - **Descrição:** Exemplo de rota protegida que retorna "Hello from private API controller".
  - **Autorização:** Bearer Token no header `Authorization: Bearer <token>`.

## Validação e Respostas

- Erros de validação retornam HTTP 400 com detalhes de campos.
- Recursos não encontrados retornam HTTP 404.
- Operações bem-sucedidas retornam HTTP 200 ou 201.

## Segurança

- As senhas são *hash* com BCrypt e salt único por usuário.
- Fator de custo padrão: 10 (configurável em `BCryptPasswordEncoder(strength)`).
- Tokens JWT assinados com chaves RSA.
- Rota `/auth/login` é pública; demais endpoints REST requerem autenticação JWT.