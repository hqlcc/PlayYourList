package br.com.playyourlist.playlist;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {
    private final PlaylistService service;

    public PlaylistController(PlaylistService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Playlist> criar(@Valid @RequestBody PlaylistRequest dados) {
        Playlist playlist = service.criar(dados);
        return ResponseEntity.created(URI.create("/playlists/" + playlist.id)).body(playlist);
    }

    @GetMapping
    public List<Playlist> listar() {
        return service.listar();
    }

    @GetMapping("/{playlistid}")
    public Playlist buscar(@PathVariable Integer playlistid) {
        return service.buscar(playlistid);
    }

    @PutMapping("/{playlistid}")
    public Playlist atualizar(@PathVariable Integer playlistid, @Valid @RequestBody PlaylistRequest dados) {
        return service.atualizar(playlistid, dados);
    }

    @DeleteMapping("/{playlistid}")
    public ResponseEntity<Void> excluir(@PathVariable Integer playlistid) {
        service.excluir(playlistid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{playlistid}/musicas/{musicaId}")
    public ResponseEntity<Void> adicionar(@PathVariable Integer playlistid, @PathVariable Integer musicaId) {
        service.adicionarMusica(playlistid, musicaId);
        return ResponseEntity.created(URI.create("/playlists/" + playlistid + "/musicas/" + musicaId)).build();
    }

    @DeleteMapping("/{playlistid}/musicas/{musicaId}")
    public ResponseEntity<Void> remover(@PathVariable Integer playlistid, @PathVariable Integer musicaId) {
        service.removerMusica(playlistid, musicaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{playlistid}/musicas")
    public List<Integer> listarMusicas(@PathVariable Integer playlistid) {
        return service.listarMusicas(playlistid);
    }
}
