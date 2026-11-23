# API de Tarefas - Projeto Final (Testes Automatizados I)

## Integrantes da equipe

- Alysson Victor Silva Farias de Souza
- Daniele Branco Nalifico
- Igor Thiago de Andrade Lima
- Larissa Roque Carvalho
- Raphael Nobre Brandao

## Como executar os testes

Pré-requisitos: Java 17 e Maven 3.9+ (na primeira execução é necessária ligação à internet para baixar as dependências).

Dentro da pasta que contém o `pom.xml`:

```bash
mvn test
```

No IntelliJ IDEA, também é possível clicar com o botão direito em `src/test/java` e escolher **Run 'All Tests'**.

### Testes implementados (10 no total)

| Classe | Tipo | Teste |
|---|---|---|
| `model/TarefaTest` | Unitário (JUnit 5) | `givenTituloNulo_whenCriarTarefa_thenLancarExcecao` |
| | | `givenDescricaoNula_whenCriarTarefa_thenLancarExcecao` |
| | | `givenDadosValidos_whenCriarTarefa_thenTarefaCriadaCorretamente` |
| `service/TarefaServiceTest` | Unitário (Mockito) | `givenTarefaValida_whenCadastrar_thenSalvarTarefa` |
| | | `givenTarefaExistente_whenBuscarPorId_thenRetornarResponse` |
| | | `givenTarefaInexistente_whenBuscarPorId_thenLancarExcecao` |
| `controller/TarefaControllerTest` | Unitário (Mockito + MockMvc standalone) | `givenTarefasExistentes_whenListar_thenRetornarStatus200` |
| | | `givenTarefaInexistente_whenBuscarPorId_thenRetornarStatus404` |
| `integration/TarefaIntegracaoTest` | Integração (`@SpringBootTest` + H2) | `givenTarefaValida_whenCadastrarPorPost_thenRetornar201ESalvarNoH2` |
| | | `givenTarefaExistente_whenExcluirPorDelete_thenRetornar204ERemoverDoH2` |

---

# Descrição original do projeto

Aplicação **simples e funcional** de Gestão de Tarefas, com CRUD completo, Spring Boot e banco H2.

O código da API já está implementado. **O trabalho dos alunos é criar os testes**, e não desenvolver outra API.

## 1. Pré-requisitos

- Java 17
- Maven 3.9+
- IntelliJ IDEA (ou outra IDE Java)
- Postman (opcional, para explorar a API)

Não é necessário instalar H2, Docker ou PostgreSQL.

**Atenção ao IntelliJ IDEA:** em `File > Project Structure`, selecione **JDK 17** como SDK do projeto. Confirme também que o Maven está a utilizar um JDK compatível (17 ou superior) e que o projeto está configurado para compilar em Java 17.

Verifique a versão do Java no terminal:

```bash
java -version
```

## 2. Iniciar a aplicação

1. Abra esta pasta como projeto Maven na IDE.
2. Aguarde o download das dependências (é necessária ligação à internet na primeira vez).
3. Execute o comando abaixo no terminal, dentro da pasta que contém o `pom.xml`:

```bash
mvn spring-boot:run
```

A aplicação usa a porta **8095**.

Endpoint inicial: http://localhost:8095/api/tarefas

Uma listagem vazia (`[]`) é normal quando ainda não foram cadastradas tarefas.

### Configuração do H2 (opcional)

- Console: http://localhost:8095/h2-console
- JDBC URL: `jdbc:h2:mem:tarefasdb`
- Usuário: `sa`
- Senha: deixe em branco

Os dados ficam **em memória** e são reiniciados ao parar a aplicação.

## 3. Importar a coleção Postman

1. Abra o Postman.
2. Clique em **Import**.
3. Selecione o arquivo `postman/API_Tarefas.postman_collection.json`.
4. Abra a coleção **API de Tarefas - Projeto Final**.
5. A variável `baseUrl` está configurada para `http://localhost:8095`.
6. Comece por **Criar tarefa**, depois explore as outras requisições.
7. Se o ID da tarefa criada for diferente de `1`, atualize a variável `tarefaId` na coleção.

Exemplo de corpo JSON:

```json
{
  "titulo": "Estudar JUnit",
  "descricao": "Resolver exercicios da aula",
  "concluida": false
}
```

### Endpoints

| Método | Rota | O que faz | Status de sucesso |
|--------|------|-----------|-------------------|
| GET | `/api/tarefas` | Lista todas | 200 |
| GET | `/api/tarefas/{id}` | Busca por ID | 200 |
| POST | `/api/tarefas` | Cria tarefa | 201 |
| PUT | `/api/tarefas/{id}` | Atualiza tarefa | 200 |
| DELETE | `/api/tarefas/{id}` | Exclui tarefa | 204 |

Outros casos: ID inexistente retorna **404**; título ou descrição nulos no cadastro/atualização retornam **400**.

### Contrato de entrada, saída e tratamento de erros

A API recebe um **`TarefaRequest`** nos endpoints POST e PUT, e devolve um **`TarefaResponse`** nos endpoints de consulta e cadastro/atualização. São DTOs simples (classes Java), não entidades JPA.

Fluxo de uma requisição:

```text
Cliente (JSON) -> Controller (TarefaRequest)
              -> Service (validação, conversão e regras)
              -> Repository (Tarefa Entity no H2)
              -> Service (converte Entity em TarefaResponse)
              -> Controller (responde TarefaResponse em JSON)
```

O `TarefaController` **não importa** `Tarefa` nem `TarefaRepository` e não realiza transformações. Toda conversão de Entity para Response está no `TarefaService`.

Um GET/POST/PUT com sucesso devolve apenas os campos `id`, `titulo`, `descricao` e `concluida` do DTO `TarefaResponse`.

Quando uma tarefa não existe, o `ApiExceptionHandler` devolve **HTTP 404** com um erro JSON padronizado, semelhante ao projeto da API de Produtos:

```json
{
  "codigo": "TAREFA_NAO_ENCONTRADA",
  "mensagem": "Tarefa nao encontrada: 999999",
  "timestamp": "2026-11-18T10:00:00Z",
  "path": "/api/tarefas/999999"
}
```

O `timestamp` acima é ilustrativo e será gerado no momento da requisição. Para campos nulos, a resposta usa código `DADOS_INVALIDOS` e HTTP 400. Para JSON inválido, usa `JSON_INVALIDO` e HTTP 400. Não existem integrações externas, autenticação, frontend, Micrometer ou Health.


## 4. Executar os testes

Dentro da pasta que contém o `pom.xml`:

```bash
mvn test
```

Há quatro **classes de teste preparadas**, uma para cada categoria do trabalho:

- `model/TarefaTest.java`: testes unitários do construtor da Entity `Tarefa`.
- `service/TarefaServiceTest.java`: testes unitários do serviço com Mockito (Entity no mock, DTO no retorno).
- `controller/TarefaControllerTest.java`: testes do controlador com MockMvc e serviço simulado (apenas DTOs).
- `integration/TarefaIntegracaoTest.java`: testes de integração com Spring Boot e H2.

O enunciado pede **2 a 3 testes por categoria (8 a 12 no total)**. Os exemplos do PDF são sugestões, não cenários obrigatórios.

## 5. Diferença importante entre as partes

- **Classe Tarefa:** apenas JUnit, sem Spring e sem banco.
- **Service:** Mockito simula `TarefaRepository`; não deve acessar H2.
- **Controller:** Mockito simula `TarefaService`; MockMvc testa as respostas HTTP sem iniciar toda a aplicação.
- **Integração:** `@SpringBootTest`, MockMvc e H2 real em memória; não simular o repositório.

## 6. Estrutura principal

```
src/main/java/br/com/ada/tarefas/
  TarefasApplication.java
  controller/TarefaController.java
  dto/TarefaRequest.java
  dto/TarefaResponse.java
  service/TarefaService.java
  repository/TarefaRepository.java
  model/Tarefa.java
  exception/TarefaNaoEncontradaException.java
  exception/ApiExceptionHandler.java
  exception/ErroResponse.java
src/test/java/br/com/ada/tarefas/
  model/TarefaTest.java
  service/TarefaServiceTest.java
  controller/TarefaControllerTest.java
  integration/TarefaIntegracaoTest.java
```

## 7. Entrega e apresentação

Veja o documento `ENUNCIADO_Projeto_Final_Testes_Automatizados_I.pdf` incluído no projeto.

Apresentação: **30/11/2026**, de **15 a 20 minutos por equipe**. Todos os testes desenvolvidos devem passar, e a equipe deve explicar cada classe e cada método de teste.
