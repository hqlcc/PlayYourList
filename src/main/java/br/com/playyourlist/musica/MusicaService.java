package br.com.playyourlist.musica;

import br.com.playyourlist.playlist.PlaylistMusicaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class MusicaService {
    private final MusicaRepository repository;
    private final PlaylistMusicaRepository associacoes;

    public MusicaService(MusicaRepository repository, PlaylistMusicaRepository associacoes) {
        this.repository = repository;
        this.associacoes = associacoes;
    }

    public List<Musica> listar() {
        return repository.findAll(org.springframework.data.domain.Sort.by("id"));
    }

    public Musica buscar(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Música " + id + " não encontrada."));
    }

    @Transactional
    public Musica criar(MusicaRequest dados) {
        Musica musica = new Musica();
        musica.atualizar(dados);
        return repository.save(musica);
    }

    @Transactional
    public Musica atualizar(Integer id, MusicaRequest dados) {
        Musica musica = buscar(id);
        musica.atualizar(dados);
        return repository.save(musica);
    }

    @Transactional
    public void excluir(Integer id) {
        Musica musica = buscar(id);
        associacoes.deleteByMusicaid(id);
        repository.delete(musica);
    }
}
