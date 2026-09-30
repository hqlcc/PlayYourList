package br.com.playyourlist.musica;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/musicas")
public class MusicaController {
    private final MusicaService service;

    public MusicaController(MusicaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Musica> criar(@Valid @RequestBody MusicaRequest dados) {
        Musica musica = service.criar(dados);
        return ResponseEntity.created(URI.create("/musicas/" + musica.id)).body(musica);
    }

    @GetMapping
    public List<Musica> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Musica buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public Musica atualizar(@PathVariable Integer id, @Valid @RequestBody MusicaRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
