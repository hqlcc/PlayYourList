# Play Your List

Projeto da avaliação **MS-AV-01-Endpoints**, com CRUD de músicas e playlists,
associação de músicas, registro de reproduções e orquestração via OpenFeign.

[Enunciado do professor](https://github.com/esensato/ms-2026-02/blob/main/MS-AV-01-Endpoints.md).

Todos os endpoints ficam no mesmo projeto, conforme permitido no enunciado.
A implementação usa Java 17, Spring Boot 4.1.1, Spring Cloud 2025.1.3,
Spring Data JPA, Bean Validation e H2. Swagger facilita a demonstração.

## Como executar

Instale um **JDK 17 ou superior**. O Maven Wrapper já está incluído; não é necessário
instalar Maven. A primeira execução precisa de internet para baixar as dependências.

Abra um terminal **dentro da pasta que contém o `pom.xml`**.

Windows — PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux / macOS:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Se preferir, importe a pasta como projeto Maven no IntelliJ ou Eclipse e execute
`PlayYourListApplication`. Confira que a IDE também está usando JDK 17 ou superior.

Após a mensagem `Started PlayYourListApplication`, acesse:

- **Swagger:** http://localhost:8080/swagger-ui.html
- **Console H2:** http://localhost:8080/h2-console
- **Músicas:** http://localhost:8080/musicas

No console H2, use:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:playyourlist` |
| User Name | `sa` |
| Password | deixe vazio |

O banco é **em memória**: os dados são restaurados ao reiniciar a aplicação.
`schema.sql` cria as quatro tabelas; `data.sql` contém os exemplos do professor:
5 músicas, 5 playlists, 10 associações e 12 reproduções. Os totais iniciais das
playlists 1 a 5 são, respectivamente, **5, 3, 2, 1 e 1**.

Para mudar a porta, defina a variável `PORT`. Exemplo no PowerShell:

```powershell
$env:PORT = "8081"
.\mvnw.cmd spring-boot:run
```

A URL do OpenFeign acompanha a porta configurada.

## Estrutura e responsabilidades

| Caminho | Responsabilidade |
|---|---|
| `pom.xml` | Dependências e versões compatíveis de Spring Boot e Spring Cloud |
| `mvnw` / `mvnw.cmd` / `.mvn/` | Execução do Maven no Linux, macOS e Windows |
| `src/main/java/br/com/playyourlist/PlayYourListApplication.java` | Inicialização do Spring Boot e habilitação do OpenFeign |
| `src/main/java/br/com/playyourlist/musica/` | Entidade, dados de entrada, repository, service e controller de músicas |
| `src/main/java/br/com/playyourlist/playlist/` | Playlists e tabela associativa `playlist_musicas` |
| `src/main/java/br/com/playyourlist/reproducao/` | Registro, listagem e contagem das reproduções |
| `src/main/java/br/com/playyourlist/api/` | Controller de orquestração e três interfaces OpenFeign |
| `src/main/java/br/com/playyourlist/erro/` | Respostas de erro e tratamento de validações |
| `src/main/resources/application.properties` | Porta, H2, JPA e OpenFeign |
| `src/main/resources/schema.sql` | Tabelas, chaves estrangeiras e restrição contra duplicatas |
| `src/main/resources/data.sql` | Dados iniciais do enunciado |
| `src/test/java/br/com/playyourlist/EndpointsIntegrationTest.java` | Testes com servidor HTTP real, banco e OpenFeign |
| `requisicoes.http` | Exemplos de todas as operações e dos principais erros |

O **controller** recebe a requisição; o **service** aplica as regras; o
**repository** acessa o banco. Os `Request` são os dados de entrada e validam os
campos com `@Valid`. As entidades usam campos públicos, como nos exemplos de aula,
para manter o código curto e evitar getters, setters e dependências adicionais.

## Endpoints

| Método | Caminho | Resultado |
|---|---|---|
| POST | `/musicas` | Cadastra música — 201 |
| GET | `/musicas` | Lista músicas — 200 |
| GET | `/musicas/{id}` | Busca música — 200 |
| PUT | `/musicas/{id}` | Atualiza música — 200 |
| DELETE | `/musicas/{id}` | Exclui música — 204 |
| POST | `/playlists` | Cria playlist — 201 |
| GET | `/playlists` | Lista playlists — 200 |
| GET | `/playlists/{playlistid}` | Busca playlist — 200 |
| PUT | `/playlists/{playlistid}` | Atualiza nome e descrição — 200 |
| DELETE | `/playlists/{playlistid}` | Exclui playlist e vínculos — 204 |
| POST | `/playlists/{playlistid}/musicas/{musicaId}` | Adiciona música — 201 |
| DELETE | `/playlists/{playlistid}/musicas/{musicaId}` | Remove música da playlist — 204 |
| GET | `/playlists/{playlistid}/musicas` | Retorna lista de IDs — 200 |
| POST | `/reproducao` | Registra reprodução — 201 |
| GET | `/reproducao/{playlistid}` | Lista reproduções — 200 |
| GET | `/reproducao/total/{playlistid}` | Retorna um número com o total — 200 |
| POST | `/api/adicionar/{playlistId}/musicas/{musicaId}` | Valida e adiciona via OpenFeign — 200 |
| PUT | `/api/executar/{playlistId}` | Valida e registra reprodução via OpenFeign — 200 |
| POST | `/statistic` | Alias do registro de reprodução — 201 |

### Dados de entrada

Cadastro e atualização de música:

```json
{
  "titulo": "Hotel California",
  "artista": "Eagles",
  "album": "Hotel California",
  "duracao": 391,
  "genero": "Rock"
}
```

Cadastro e atualização de playlist:

```json
{
  "nome": "Para Estudar",
  "descricao": "Músicas para manter o foco"
}
```

Registro em `/reproducao` ou `/statistic`:

```json
{
  "playlistid": 1
}
```

Também é aceito `playlistId`. O servidor gera o ID do registro e `datahora`
usando `LocalDateTime.now()`. `/api/executar/{playlistId}` não recebe corpo.
A execução representa o registro estatístico solicitado; não há reprodução de áudio.

### OpenFeign e a divergência do enunciado

`POST /api/adicionar/2/musicas/1` faz três chamadas HTTP via OpenFeign:
busca a música, busca a playlist e solicita a associação. Na base inicial, retorna:

```text
Música Imagine adicionada com sucesso à playlist Música Brasileira.
```

Se a música ou a playlist não existir, retorna 404. Se o vínculo já existir,
retorna 409. O controller de orquestração não acessa repositories diretamente.

O enunciado define `POST /reproducao`, mas cita `POST /statistic` ao explicar
`/api/executar`. Ambos os caminhos registram a mesma operação. O OpenFeign da
execução chama **`POST /statistic`**, atendendo também à descrição literal.
Cada chamada de execução gera apenas um registro.

## Validações e exclusões

- Título e artista são obrigatórios e não aceitam somente espaços. Ambos têm limite de 150 caracteres, conforme as colunas do banco.
- Duração é obrigatória e maior que zero, em segundos.
- Álbum é opcional e tem limite de 150 caracteres; gênero é opcional e tem limite de 50.
- Nome da playlist é obrigatório e não aceita somente espaços; o limite de 100 segue a coluna do banco.
- Descrição é opcional e tem limite de 255 caracteres.
- Reproduções e associações só são criadas para recursos existentes.
- Uma mesma música não pode ser adicionada duas vezes à mesma playlist.
- Excluir playlist remove suas associações e suas reproduções; preserva todas as músicas do catálogo.
- Excluir música remove seus vínculos; preserva todas as playlists.
- As operações com múltiplas alterações no banco usam `@Transactional`.

Erros retornam 400 para dados inválidos, 404 para recursos inexistentes e 409
para conflitos. Exemplo de validação:

```json
{
  "status": 400,
  "mensagem": "Dados inválidos.",
  "detalhes": ["duracao: Duração deve ser maior que zero."]
}
```

## Testar e demonstrar

Execute os testes com a aplicação normal parada. Eles usam a porta 18080 e
restauram os dados antes de cada cenário.

```powershell
.\mvnw.cmd test
```

```bash
./mvnw test
```

Verificação realizada: **22 testes de integração passaram**, sem falhas ou erros.
Os testes cobrem CRUDs, associações, validações em POST e PUT, exclusões,
reproduções e chamadas reais de OpenFeign. Para gerar o JAR:

```powershell
.\mvnw.cmd clean package
java -jar target/play-your-list-1.0.0.jar
```

No Linux/macOS, substitua `.\mvnw.cmd` por `./mvnw`.

Para demonstrar no Swagger, use esta sequência em uma inicialização nova:

1. Liste músicas e playlists para mostrar os dados iniciais.
2. Cadastre uma música e uma playlist com dados válidos.
3. Envie dados inválidos para mostrar o erro 400 e as mensagens.
4. Execute `POST /api/adicionar/2/musicas/1` e confira a mensagem com os nomes.
5. Consulte `GET /playlists/2/musicas` para conferir a associação.
6. Consulte `GET /reproducao/total/1`: o valor inicial é 5.
7. Execute `PUT /api/executar/1` e consulte o total novamente: será 6.
8. Demonstre um ID inexistente (404) e uma associação repetida (409).

O arquivo `requisicoes.http` contém exemplos adicionais. Pode ser usado com
REST Client no VS Code ou com o cliente HTTP do IntelliJ. Também é possível
copiar os mesmos caminhos e JSONs para o Postman.

## Entrega

Publique **o conteúdo desta pasta** em um repositório Git, incluindo `pom.xml`,
`src/`, o Maven Wrapper, README e exemplos. A pasta `target/` é ignorada.
Confira o código e as explicações para conseguir apresentar a implementação.
Entregue o link do repositório, conforme solicitado no enunciado.
