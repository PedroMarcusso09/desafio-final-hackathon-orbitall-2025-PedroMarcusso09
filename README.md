# Customers & Transactions API

API desenvolvida em **Java com Spring Boot**, responsável por gerenciar **clientes** e suas **transações** financeiras.

- Permite cadastrar e consultar clientes.
- Permite registrar e consultar transações associadas a clientes.
- Documentada com exemplos de requests/responses.
- Inclui testes automatizados.

## 🛠️ Stack utilizada

- **Back-end:** Java 17 + Spring Boot
- **Banco de dados:** H2 Database (em memória para testes, ou outro configurado)
- **ORM:** Spring Data JPA
- **Validações:** Jakarta Validation (`@Valid`)
- **Utilitários:** Lombok
- **Testes:** JUnit + Spring Boot Starter Test

## 📌 Pré-requisitos

- Java 17+
- Maven
- IDE de sua preferência (IntelliJ, Eclipse, VS Code)
- Postman ou Insomnia para testar a API

## 🚀 Instalação e execução local

### Clone o repositório

```bash
  git clone https://github.com/PedroMarcusso09/desafio-final-hackathon-orbitall-2025-PedroMarcusso09
```

### Compile e rode o projeto

```bash
mvn spring-boot:run
```

### Acesse a API

```bash
  http://localhost:8080
```

# Desafio Final - Hackaton Orbitall 2025 - API de Gestão de Clientes e Transações

### 🚀 Como iniciar (Windows / PowerShell)
Este repositório contém um módulo Spring Boot dentro da pasta `channels`. Siga os passos abaixo para subir a aplicação localmente.

Pré-requisitos:
- Java 21 instalado e configurado no PATH (verifique com: `java -version`)
- Porta padrão 8080 livre

Iniciar via terminal (recomendado):
1. Abra o PowerShell na raiz do projeto.
2. Entre na pasta do módulo: `cd .\channels`
3. Dê permissão de execução ao wrapper (se necessário): `Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass`
4. Rode a aplicação: ` .\mvnw.cmd spring-boot:run`
5. Acesse no navegador: `http://localhost:8080/` (deve exibir "Welcome to Orbital Channels 2025!")
6. Console do H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:channels`)

Parar a aplicação:
- No terminal que está rodando, pressione `Ctrl + C`.

Build do JAR e execução manual:
1. `cd .\channels`
2. ` .\mvnw.cmd clean package`
3. Execute o jar gerado: `java -jar .\target\channels-0.0.1-SNAPSHOT.jar`

Rodar pelo IntelliJ IDEA:
- Certifique-se de que o SDK do projeto está em Java 21.
- Instale/ative o plugin Lombok e habilite Annotation Processing.
- Abra a classe `ChannelsApplication` e clique em Run.

---

### 📜 Contexto:
Você está participando do Hackaton do setor financeiro e sua missão é desenvolver uma API REST em Java + Spring Boot para gerenciamento de clientes e registro de transações.
A API será utilizada por sistemas internos para cadastrar, consultar, atualizar e remover dados, além de processar transações simuladas.

---

## 📚 Endpoints e exemplos de requisições/respostas (com Postman)

Base URL padrão em desenvolvimento: `http://localhost:8080`

- Saúde da aplicação:
  - GET `/` → retorna a mensagem: `"Welcome to Orbital Channels 2025!"`
  - H2 Console: `/h2-console` (JDBC URL: `jdbc:h2:mem:channels`)

Dica Postman:
- Crie uma variável de ambiente `baseUrl` = `http://localhost:8080` e use `{{baseUrl}}` nas requisições.

### 1) Customers
Recurso: `/customers`

Campos (Request):
- `fullName` (string, obrigatório)
- `email` (string, obrigatório, formato válido)
- `phone` (string, obrigatório)

Campos (Response):
- `id` (UUID)
- `fullName`, `email`, `phone`
- `createdAt`, `updatedAt` (ISO-8601)
- `active` (boolean)

1.1 Criar cliente
- Método: POST `{{baseUrl}}/customers`
- Body (raw, JSON):
```json
{
  "fullName": "Maria Silva",
  "email": "maria.silva@example.com",
  "phone": "+55 11 90000-0000"
}
```
- Exemplo cURL:
```bash
curl --request POST "http://localhost:8080/customers" \
  --header "Content-Type: application/json" \
  --data '{
    "fullName":"Maria Silva",
    "email":"maria.silva@example.com",
    "phone":"+55 11 90000-0000"
  }'
```
- Resposta 201/200 (exemplo):
```json
{
  "id": "7d9b4a9b-2f3e-4b4a-9f2b-2a9b4a9b2f3e",
  "fullName": "Maria Silva",
  "email": "maria.silva@example.com",
  "phone": "+55 11 90000-0000",
  "createdAt": "2025-09-20T12:55:00",
  "updatedAt": "2025-09-20T12:55:00",
  "active": true
}
```

1.2 Buscar todos os clientes
- Método: GET `{{baseUrl}}/customers`
- Resposta 200 (exemplo):
```json
[
  {
    "id": "7d9b4a9b-2f3e-4b4a-9f2b-2a9b4a9b2f3e",
    "fullName": "Maria Silva",
    "email": "maria.silva@example.com",
    "phone": "+55 11 90000-0000",
    "createdAt": "2025-09-20T12:55:00",
    "updatedAt": "2025-09-20T12:55:00",
    "active": true
  }
]
```

1.3 Buscar cliente por ID
- Método: GET `{{baseUrl}}/customers/{id}`
- Exemplo cURL:
```bash
curl "http://localhost:8080/customers/7d9b4a9b-2f3e-4b4a-9f2b-2a9b4a9b2f3e"
```
- Resposta 200: mesmo formato do item 1.1
- Possíveis erros:
  - 404 (não encontrado) se o ID não existir

1.4 Atualizar cliente
- Método: PUT `{{baseUrl}}/customers/{id}`
- Body (raw, JSON):
```json
{
  "fullName": "Maria S. Almeida",
  "email": "maria.almeida@example.com",
  "phone": "+55 11 98888-7777"
}
```
- Resposta 200: cliente atualizado (mesma estrutura do response de criação)

1.5 Remover cliente
- Método: DELETE `{{baseUrl}}/customers/{id}`
- Resposta 200: cliente removido logicamente (se aplicável) retornando os dados do cliente
- Possíveis erros:
  - 404 (não encontrado) se o ID não existir

Observações de validação (400 Bad Request):
- Campos obrigatórios vazios ou inválidos retornam 400 com mensagem de validação do Spring/Bean Validation.

### 2) Transactions
Recurso: `/transactions`

Campos (Request):
- `customerId` (UUID, obrigatório)
- `amount` (number decimal > 0, obrigatório)
- `cardType` (string, obrigatório)

Campos (Response):
- `id` (UUID)
- `customerId` (UUID)
- `amount` (number decimal)
- `cardType` (string)
- `createdAt` (ISO-8601)
- `active` (boolean)

2.1 Criar transação
- Método: POST `{{baseUrl}}/transactions`
- Body (raw, JSON):
```json
{
  "customerId": "7d9b4a9b-2f3e-4b4a-9f2b-2a9b4a9b2f3e",
  "amount": 150.75,
  "cardType": "VISA"
}
```
- Exemplo cURL:
```bash
curl --request POST "http://localhost:8080/transactions" \
  --header "Content-Type: application/json" \
  --data '{
    "customerId":"7d9b4a9b-2f3e-4b4a-9f2b-2a9b4a9b2f3e",
    "amount":150.75,
    "cardType":"VISA"
  }'
```
- Resposta 201/200 (exemplo):
```json
{
  "id": "5c8e7d46-2a1b-4e9f-b0c3-9d6f4a2b1c7e",
  "customerId": "7d9b4a9b-2f3e-4b4a-9f2b-2a9b4a9b2f3e",
  "amount": 150.75,
  "cardType": "VISA",
  "createdAt": "2025-09-20T13:00:00",
  "active": true
}
```

2.2 Buscar transação por ID
- Método: GET `{{baseUrl}}/transactions/{id}`
- Exemplo cURL:
```bash
curl "http://localhost:8080/transactions/5c8e7d46-2a1b-4e9f-b0c3-9d6f4a2b1c7e"
```
- Resposta 200: conforme estrutura de 2.1
- Possíveis erros: 404 (não encontrado)

2.3 Listar transações de um cliente
- Método: GET `{{baseUrl}}/transactions?customerId={UUID}`
- Exemplo cURL:
```bash
curl "http://localhost:8080/transactions?customerId=7d9b4a9b-2f3e-4b4a-9f2b-2a9b4a9b2f3e"
```
- Resposta 200 (exemplo):
```json
[
  {
    "id": "5c8e7d46-2a1b-4e9f-b0c3-9d6f4a2b1c7e",
    "customerId": "7d9b4a9b-2f3e-4b4a-9f2b-2a9b4a9b2f3e",
    "amount": 150.75,
    "cardType": "VISA",
    "createdAt": "2025-09-20T13:00:00",
    "active": true
  }
]
```

2.4 Remover transação
- Método: DELETE `{{baseUrl}}/transactions/{id}`
- Resposta 200: transação removida retornando seus dados
- Possíveis erros: 404 (não encontrado)

### Exemplos de configuração no Postman
- Crie um ambiente "Local" com a variável `baseUrl = http://localhost:8080`.
- Nas requisições, use `{{baseUrl}}` + o caminho (ex.: `{{baseUrl}}/customers`).
- Body deve ser do tipo `raw` e `JSON` para POST/PUT.
- Para autenticação: não há autenticação nesta API (ambiente de desenvolvimento).

### Códigos de status observados
- 200 OK: requisição bem-sucedida (GET/PUT/DELETE)
- 201 Created: criação bem-sucedida (pode variar para 200 dependendo da implementação do controller)
- 400 Bad Request: validação falhou (ex.: campos obrigatórios, formatos)
- 404 Not Found: recurso não encontrado

Observação: os formatos de data seguem ISO-8601 (`yyyy-MM-dd'T'HH:mm:ss`). Os exemplos de UUIDs são ilustrativos.