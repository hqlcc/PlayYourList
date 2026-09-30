package br.com.playyourlist.api;

import br.com.playyourlist.playlist.Playlist;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "playlists", url = "${app.base-url}")
public interface PlaylistClient {
    @GetMapping("/playlists/{playlistid}")
    Playlist buscar(@PathVariable("playlistid") Integer playlistid);

    @PostMapping("/playlists/{playlistid}/musicas/{musicaId}")
    void adicionar(@PathVariable("playlistid") Integer playlistid, @PathVariable("musicaId") Integer musicaId);
}
