package br.com.playyourlist.api;

import br.com.playyourlist.musica.Musica;
import br.com.playyourlist.playlist.Playlist;
import br.com.playyourlist.reproducao.Reproducao;
import br.com.playyourlist.reproducao.ReproducaoRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final MusicaClient musicas;
    private final PlaylistClient playlists;
    private final ReproducaoClient reproducoes;

    public ApiController(MusicaClient musicas, PlaylistClient playlists, ReproducaoClient reproducoes) {
        this.musicas = musicas;
        this.playlists = playlists;
        this.reproducoes = reproducoes;
    }

    @PostMapping(value = "/adicionar/{playlistId}/musicas/{musicaId}", produces = "text/plain;charset=UTF-8")
    public String adicionar(@PathVariable Integer playlistId, @PathVariable Integer musicaId) {
        Musica musica = musicas.buscar(musicaId);
        Playlist playlist = playlists.buscar(playlistId);
        playlists.adicionar(playlistId, musicaId);
        return "Música " + musica.titulo + " adicionada com sucesso à playlist " + playlist.nome + ".";
    }

    @PutMapping("/executar/{playlistId}")
    public Reproducao executar(@PathVariable Integer playlistId) {
        playlists.buscar(playlistId);
        return reproducoes.registrar(new ReproducaoRequest(playlistId));
    }
}
