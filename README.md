# ⚔️ CardGame API

Web API RESTful para gerenciar as **cartas** de um card game (estilo Magic / Hearthstone), com CRUD completo, validações e persistência em banco **H2**.

**Stack:** Java 17+ · Spring Boot 3.3 (Web, Data JPA, Validation) · H2 · Maven

---

## 1. Como compilar e rodar (CLI)

Pré-requisitos: **JDK 17+** e **Maven 3.6+** (`java -version` e `mvn -version`).

```bash
# Rodar os testes automatizados
mvn test

# Subir a aplicação (http://localhost:8080)
mvn spring-boot:run

# Ou gerar o .jar e executar
mvn clean package
java -jar target/cardgame-api-1.0.0.jar
```

> Se preferir o Maven Wrapper, gere-o uma vez com `mvn wrapper:wrapper` e use `./mvnw spring-boot:run`.

Ao iniciar, 4 cartas de exemplo são inseridas automaticamente (desative com `app.seed.enabled=false` em `application.properties`).

Console do banco: <http://localhost:8080/h2-console>
JDBC URL: `jdbc:h2:mem:cartasdb` · usuário: `sa` · senha: *(vazia)*

Para persistir em arquivo, troque a URL em `application.properties` por `jdbc:h2:file:./data/cartasdb`.

---

## 2. Entidade `Carta` (tabela `cartas`)

| Campo         | Tipo           | Regras                                                           |
|---------------|----------------|------------------------------------------------------------------|
| `id`          | Long           | Chave primária, auto-incremento (`@GeneratedValue IDENTITY`)     |
| `nome`        | String         | **Obrigatório**, 2–100 caracteres, **único** (case-insensitive)  |
| `tipo`        | Enum           | **Obrigatório**: `CRIATURA`, `FEITICO`, `ARTEFATO`               |
| `raridade`    | Enum           | **Obrigatório**: `COMUM`, `INCOMUM`, `RARA`, `EPICA`, `LENDARIA` |
| `custoMana`   | Integer        | **Obrigatório**, 0 a 10                                          |
| `ataque`      | Integer        | Opcional, 0 a 99                                                 |
| `defesa`      | Integer        | Opcional, 0 a 99                                                 |
| `descricao`   | String         | Opcional, até 300 caracteres                                     |
| `dataCriacao` | LocalDateTime  | Gerado automaticamente (`@PrePersist`), somente leitura          |

### Estrutura do projeto

```
src/main/java/com/cardgame
├── controller/   CartaController          (endpoints REST)
├── service/      CartaService             (regras de negócio)
├── repository/   CartaRepository          (JpaRepository)
├── model/        Carta, TipoCarta, Raridade
├── dto/          CartaRequest, CartaPatchRequest, CartaResponse
├── exception/    GlobalExceptionHandler, ErroResponse, ...
└── config/       DataLoader               (carga inicial)
```

---

## 3. Endpoints

| Verbo    | Rota                | Descrição                                   | Sucesso          | Erros          |
|----------|---------------------|---------------------------------------------|------------------|----------------|
| `GET`    | `/api/cartas`       | Lista cartas (filtros `?nome=` e `?raridade=`) | `200 OK`      | `400`          |
| `GET`    | `/api/cartas/{id}`  | Busca carta por ID                          | `200 OK`         | `400`, `404`   |
| `POST`   | `/api/cartas`       | Cria uma carta                              | `201 Created`    | `400`, `409`   |
| `PUT`    | `/api/cartas/{id}`  | Atualiza completamente                      | `200 OK`         | `400`, `404`, `409` |
| `PATCH`  | `/api/cartas/{id}`  | Atualiza parcialmente *(bônus)*             | `200 OK`         | `400`, `404`, `409` |
| `DELETE` | `/api/cartas/{id}`  | Remove uma carta                            | `204 No Content` | `404`          |

---

## 4. Exemplos de requisição e resposta

### GET /api/cartas → 200
```bash
curl -s http://localhost:8080/api/cartas
```
```json
[
  {
    "id": 1,
    "nome": "Dragão das Cinzas",
    "tipo": "CRIATURA",
    "raridade": "LENDARIA",
    "custoMana": 8,
    "ataque": 9,
    "defesa": 7,
    "descricao": "Ao entrar em campo, causa 3 de dano a todas as criaturas inimigas.",
    "dataCriacao": "2026-11-09T10:00:00.123"
  }
]
```
Filtros: `curl "http://localhost:8080/api/cartas?nome=goblin&raridade=COMUM"`

### GET /api/cartas/{id} → 200
```bash
curl -s http://localhost:8080/api/cartas/1
```

### POST /api/cartas → 201 Created
```bash
curl -i -X POST http://localhost:8080/api/cartas \
  -H "Content-Type: application/json" \
  -d '{
        "nome": "Cavaleiro Sombrio",
        "tipo": "CRIATURA",
        "raridade": "EPICA",
        "custoMana": 5,
        "ataque": 6,
        "defesa": 4,
        "descricao": "Ataca duas vezes se houver outra criatura aliada."
      }'
```
Resposta (header `Location: http://localhost:8080/api/cartas/5`):
```json
{
  "id": 5,
  "nome": "Cavaleiro Sombrio",
  "tipo": "CRIATURA",
  "raridade": "EPICA",
  "custoMana": 5,
  "ataque": 6,
  "defesa": 4,
  "descricao": "Ataca duas vezes se houver outra criatura aliada.",
  "dataCriacao": "2026-11-09T10:05:00.456"
}
```

### PUT /api/cartas/{id} → 200 (todos os campos obrigatórios devem ser enviados)
```bash
curl -s -X PUT http://localhost:8080/api/cartas/2 \
  -H "Content-Type: application/json" \
  -d '{"nome":"Goblin Veterano","tipo":"CRIATURA","raridade":"INCOMUM","custoMana":2,"ataque":3,"defesa":2,"descricao":"Sobreviveu a mil batalhas."}'
```

### PATCH /api/cartas/{id} → 200 (apenas os campos enviados mudam)
```bash
curl -s -X PATCH http://localhost:8080/api/cartas/1 \
  -H "Content-Type: application/json" \
  -d '{"ataque": 10}'
```

### DELETE /api/cartas/{id} → 204
```bash
curl -i -X DELETE http://localhost:8080/api/cartas/4
```

### Erros

**400 Bad Request** — validação:
```bash
curl -s -X POST http://localhost:8080/api/cartas -H "Content-Type: application/json" \
  -d '{"nome":"","tipo":"CRIATURA","raridade":"COMUM","custoMana":99}'
```
```json
{
  "timestamp": "2026-11-09T10:10:00.000",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Erro de validação dos dados enviados",
  "caminho": "/api/cartas",
  "campos": {
    "nome": "O nome é obrigatório",
    "custoMana": "O custo de mana máximo é 10"
  }
}
```

**404 Not Found**:
```json
{
  "timestamp": "2026-11-09T10:11:00.000",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Carta com id 9999 não encontrada",
  "caminho": "/api/cartas/9999"
}
```

**409 Conflict** — nome duplicado:
```json
{
  "timestamp": "2026-11-09T10:12:00.000",
  "status": 409,
  "erro": "Conflict",
  "mensagem": "Já existe uma carta com o nome 'Goblin Batedor'",
  "caminho": "/api/cartas"
}
```

---

## 5. Testando com arquivo `.http`

Abra `requests.http` no VS Code com a extensão **REST Client** (ou no IntelliJ) e clique em *Send Request* em cada bloco. Também funciona importando os cURLs acima no Postman.
