package br.com.playyourlist.api;

import br.com.playyourlist.musica.Musica;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "musicas", url = "${app.base-url}")
public interface MusicaClient {
    @GetMapping("/musicas/{id}")
    Musica buscar(@PathVariable("id") Integer id);
}
