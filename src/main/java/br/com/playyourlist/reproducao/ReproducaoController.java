package br.com.playyourlist.reproducao;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
public class ReproducaoController {
    private final ReproducaoService service;

    public ReproducaoController(ReproducaoService service) {
        this.service = service;
    }

    @PostMapping({"/reproducao", "/statistic"})
    public ResponseEntity<Reproducao> registrar(@Valid @RequestBody ReproducaoRequest dados) {
        Reproducao reproducao = service.registrar(dados);
        return ResponseEntity.created(URI.create("/reproducao/" + reproducao.playlistid)).body(reproducao);
    }

    @GetMapping("/reproducao/{playlistid}")
    public List<Reproducao> listar(@PathVariable Integer playlistid) {
        return service.listar(playlistid);
    }

    @GetMapping("/reproducao/total/{playlistid}")
    public long total(@PathVariable Integer playlistid) {
        return service.total(playlistid);
    }
}
