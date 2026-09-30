package br.com.playyourlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import javax.sql.DataSource;
import java.net.URI;
import java.net.http.*;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"server.port=18080", "app.base-url=http://localhost:18080"})
class EndpointsIntegrationTest {
    @Autowired DataSource dataSource;
    private final HttpClient http = HttpClient.newHttpClient();
    private final JsonMapper json = JsonMapper.builder().build();
    private static final String MUSICA = """
            {"titulo":"Hotel California","artista":"Eagles","album":"Hotel California",
             "duracao":391,"genero":"Rock"}
            """;

    @BeforeEach
    void restaurarDados() {
        new ResourceDatabasePopulator(new ClassPathResource("schema.sql"),
                new ClassPathResource("data.sql")).execute(dataSource);
    }

    private HttpResponse<String> chamar(String metodo, String caminho, String corpo) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:18080" + caminho))
                .header("Content-Type", "application/json")
                .method(metodo, corpo == null ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(corpo)).build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode corpo(HttpResponse<String> resposta) {
        return json.readTree(resposta.body());
    }

    @Test
    void crudDeMusicas() throws Exception {
        assertThat(corpo(chamar("GET", "/musicas", null)).size()).isEqualTo(5);
        var criada = chamar("POST", "/musicas", MUSICA);
        assertThat(criada.statusCode()).isEqualTo(201);
        int id = corpo(criada).get("id").asInt();
        assertThat(criada.headers().firstValue("Location")).contains("/musicas/" + id);
        assertThat(corpo(chamar("GET", "/musicas/" + id, null)).get("titulo").asText())
                .isEqualTo("Hotel California");
        var atualizada = chamar("PUT", "/musicas/" + id, MUSICA.replace("391", "400"));
        assertThat(atualizada.statusCode()).isEqualTo(200);
        assertThat(corpo(atualizada).get("duracao").asInt()).isEqualTo(400);
        assertThat(chamar("DELETE", "/musicas/" + id, null).statusCode()).isEqualTo(204);
        assertThat(chamar("GET", "/musicas/" + id, null).statusCode()).isEqualTo(404);
    }

    @Test
    void crudDePlaylistsEAssociacoes() throws Exception {
        assertThat(corpo(chamar("GET", "/playlists", null)).size()).isEqualTo(5);
        var criada = chamar("POST", "/playlists", "{\"nome\":\"Estudar\",\"descricao\":\"Foco\"}");
        assertThat(criada.statusCode()).isEqualTo(201);
        int id = corpo(criada).get("id").asInt();
        String caminho = "/playlists/" + id;
        assertThat(corpo(chamar("GET", caminho, null)).get("nome").asText()).isEqualTo("Estudar");
        var atualizada = chamar("PUT", caminho, "{\"nome\":\"Trabalhar\",\"descricao\":null}");
        assertThat(atualizada.statusCode()).isEqualTo(200);
        assertThat(corpo(atualizada).get("nome").asText()).isEqualTo("Trabalhar");
        assertThat(chamar("POST", caminho + "/musicas/1", null).statusCode()).isEqualTo(201);
        assertThat(corpo(chamar("GET", caminho + "/musicas", null)).get(0).asInt()).isEqualTo(1);
        assertThat(chamar("POST", caminho + "/musicas/1", null).statusCode()).isEqualTo(409);
        assertThat(chamar("DELETE", caminho + "/musicas/1", null).statusCode()).isEqualTo(204);
        assertThat(corpo(chamar("GET", caminho + "/musicas", null)).size()).isZero();
        assertThat(chamar("DELETE", caminho + "/musicas/1", null).statusCode()).isEqualTo(404);
        assertThat(chamar("DELETE", caminho, null).statusCode()).isEqualTo(204);
        assertThat(chamar("GET", caminho, null).statusCode()).isEqualTo(404);
    }

    @Test
    void excluirPlaylistLimpaVinculosEReproducoesSemExcluirCatalogo() throws Exception {
        assertThat(chamar("DELETE", "/playlists/1", null).statusCode()).isEqualTo(204);
        assertThat(chamar("GET", "/musicas/1", null).statusCode()).isEqualTo(200);
        assertThat(chamar("GET", "/musicas/3", null).statusCode()).isEqualTo(200);
        assertThat(chamar("GET", "/reproducao/1", null).statusCode()).isEqualTo(404);
        assertThat(chamar("GET", "/playlists/4/musicas", null).body()).isEqualTo("[1,4]");
        try (var connection = dataSource.getConnection(); var statement = connection.createStatement()) {
            var result = statement.executeQuery("SELECT COUNT(*) FROM reproducoes WHERE playlistid = 1");
            result.next();
            assertThat(result.getInt(1)).isZero();
        }
    }

    @Test
    void excluirMusicaLimpaVinculosSemExcluirPlaylists() throws Exception {
        assertThat(chamar("DELETE", "/musicas/1", null).statusCode()).isEqualTo(204);
        assertThat(chamar("GET", "/playlists/1/musicas", null).body()).isEqualTo("[3,5]");
        assertThat(chamar("GET", "/playlists/4/musicas", null).body()).isEqualTo("[4]");
        assertThat(chamar("GET", "/playlists/5/musicas", null).body()).isEqualTo("[2,5]");
    }

    @Test
    void adicionarViaFeignValidaRecursosERetornaMensagem() throws Exception {
        var resposta = chamar("POST", "/api/adicionar/2/musicas/1", null);
        assertThat(resposta.statusCode()).isEqualTo(200);
        assertThat(resposta.body()).isEqualTo(
                "Música Imagine adicionada com sucesso à playlist Música Brasileira.");
        assertThat(chamar("GET", "/playlists/2/musicas", null).body()).isEqualTo("[4,1]");
        assertThat(chamar("POST", "/api/adicionar/2/musicas/1", null).statusCode()).isEqualTo(409);
        assertThat(chamar("POST", "/api/adicionar/999/musicas/1", null).statusCode()).isEqualTo(404);
        var ausente = chamar("POST", "/api/adicionar/2/musicas/999", null);
        assertThat(ausente.statusCode()).isEqualTo(404);
        assertThat(corpo(ausente).get("mensagem").asText()).contains("Música 999");
        assertThat(chamar("GET", "/playlists/2/musicas", null).body()).isEqualTo("[4,1]");
    }

    @Test
    void reproducaoDiretaAliasEExecucaoViaFeignContabilizamUmaVezCada() throws Exception {
        assertThat(chamar("GET", "/reproducao/total/1", null).body()).isEqualTo("5");
        var direta = chamar("POST", "/reproducao", "{\"playlistid\":1}");
        assertThat(direta.statusCode()).isEqualTo(201);
        assertThat(corpo(direta).get("datahora").asText()).isNotBlank();
        assertThat(chamar("POST", "/statistic", "{\"playlistId\":1}").statusCode()).isEqualTo(201);
        var executada = chamar("PUT", "/api/executar/1", null);
        assertThat(executada.statusCode()).isEqualTo(200);
        assertThat(corpo(executada).get("playlistid").asInt()).isEqualTo(1);
        assertThat(chamar("GET", "/reproducao/total/1", null).body()).isEqualTo("8");
        assertThat(corpo(chamar("GET", "/reproducao/1", null)).size()).isEqualTo(8);
        assertThat(chamar("PUT", "/api/executar/999", null).statusCode()).isEqualTo(404);
        assertThat(chamar("POST", "/reproducao", "{\"playlistid\":999}").statusCode()).isEqualTo(404);
    }

    static Stream<String> musicasInvalidas() {
        return Stream.of(
                MUSICA.replace("\"Hotel California\"", "\"   \""),
                MUSICA.replace("\"Eagles\"", "\"   \""),
                MUSICA.replace("391", "0"), MUSICA.replace("391", "-1"),
                MUSICA.replace("391", "null"),
                "{\"titulo\":\"Música\",\"artista\":\"Artista\"}",
                MUSICA.replace("\"Eagles\"", "null"),
                MUSICA.replace("Hotel California", "x".repeat(151)),
                MUSICA.replace("Rock", "x".repeat(51)),
                "{\"titulo\":\"Música\",\"artista\":\"Artista\",\"duracao\":1,\"album\":\""
                        + "x".repeat(151) + "\"}");
    }

    @ParameterizedTest
    @MethodSource("musicasInvalidas")
    void validaMusicaNoCadastroENaAtualizacao(String dados) throws Exception {
        for (String metodo : new String[]{"POST", "PUT"}) {
            var resposta = chamar(metodo, metodo.equals("POST") ? "/musicas" : "/musicas/1", dados);
            assertThat(resposta.statusCode()).isEqualTo(400);
            assertThat(corpo(resposta).get("detalhes").size()).isPositive();
        }
        assertThat(corpo(chamar("GET", "/musicas", null)).size()).isEqualTo(5);
        assertThat(corpo(chamar("GET", "/musicas/1", null)).get("titulo").asText()).isEqualTo("Imagine");
    }

    static Stream<String> playlistsInvalidas() {
        return Stream.of("{}", "{\"nome\":null}", "{\"nome\":\"   \"}",
                "{\"nome\":\"" + "x".repeat(101) + "\"}",
                "{\"nome\":\"Teste\",\"descricao\":\"" + "x".repeat(256) + "\"}");
    }

    @ParameterizedTest
    @MethodSource("playlistsInvalidas")
    void validaPlaylistNoCadastroENaAtualizacao(String dados) throws Exception {
        assertThat(chamar("POST", "/playlists", dados).statusCode()).isEqualTo(400);
        assertThat(chamar("PUT", "/playlists/1", dados).statusCode()).isEqualTo(400);
        assertThat(corpo(chamar("GET", "/playlists/1", null)).get("nome").asText()).isEqualTo("Clássicos do Rock");
    }

    @Test
    void aceitaOpcionaisAusentesERejeitaFormatosInvalidos() throws Exception {
        assertThat(chamar("POST", "/musicas", "{\"titulo\":\"Teste\",\"artista\":\"Teste\",\"duracao\":1}")
                .statusCode()).isEqualTo(201);
        assertThat(chamar("POST", "/playlists", "{\"nome\":\"Teste\"}").statusCode()).isEqualTo(201);
        assertThat(chamar("GET", "/musicas/abc", null).statusCode()).isEqualTo(400);
        assertThat(chamar("POST", "/musicas", "{quebrado}").statusCode()).isEqualTo(400);
        for (String payload : new String[]{"{}", "{\"playlistid\":0}", "{\"playlistid\":-1}"}) {
            assertThat(chamar("POST", "/reproducao", payload).statusCode()).isEqualTo(400);
        }
        assertThat(chamar("POST", "/playlists/1/musicas/999", null).statusCode()).isEqualTo(404);
        assertThat(chamar("POST", "/playlists/999/musicas/1", null).statusCode()).isEqualTo(404);
    }
}
