package br.com.playyourlist.playlist;

import br.com.playyourlist.musica.MusicaRepository;
import br.com.playyourlist.reproducao.ReproducaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class PlaylistService {
    private final PlaylistRepository repository;
    private final PlaylistMusicaRepository associacoes;
    private final MusicaRepository musicas;
    private final ReproducaoRepository reproducoes;

    public PlaylistService(PlaylistRepository repository, PlaylistMusicaRepository associacoes,
                           MusicaRepository musicas, ReproducaoRepository reproducoes) {
        this.repository = repository;
        this.associacoes = associacoes;
        this.musicas = musicas;
        this.reproducoes = reproducoes;
    }

    public List<Playlist> listar() {
        return repository.findAll(org.springframework.data.domain.Sort.by("id"));
    }

    public Playlist buscar(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist " + id + " não encontrada."));
    }

    @Transactional
    public Playlist criar(PlaylistRequest dados) {
        Playlist playlist = new Playlist();
        playlist.atualizar(dados);
        return repository.save(playlist);
    }

    @Transactional
    public Playlist atualizar(Integer id, PlaylistRequest dados) {
        Playlist playlist = buscar(id);
        playlist.atualizar(dados);
        return repository.save(playlist);
    }

    @Transactional
    public void excluir(Integer id) {
        Playlist playlist = buscar(id);
        associacoes.deleteByPlaylistid(id);
        reproducoes.deleteByPlaylistid(id);
        // As músicas do catálogo não são excluídas.
        repository.delete(playlist);
    }

    @Transactional
    public void adicionarMusica(Integer playlistid, Integer musicaid) {
        buscar(playlistid);
        if (!musicas.existsById(musicaid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Música " + musicaid + " não encontrada.");
        }
        if (associacoes.existsByPlaylistidAndMusicaid(playlistid, musicaid)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A música já está nesta playlist.");
        }
        PlaylistMusica associacao = new PlaylistMusica();
        associacao.playlistid = playlistid;
        associacao.musicaid = musicaid;
        associacoes.save(associacao);
    }

    @Transactional
    public void removerMusica(Integer playlistid, Integer musicaid) {
        buscar(playlistid);
        if (!associacoes.existsByPlaylistidAndMusicaid(playlistid, musicaid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A música não está nesta playlist.");
        }
        associacoes.deleteByPlaylistidAndMusicaid(playlistid, musicaid);
    }

    public List<Integer> listarMusicas(Integer playlistid) {
        buscar(playlistid);
        return associacoes.findByPlaylistidOrderByIdAsc(playlistid).stream()
                .map(associacao -> associacao.musicaid).toList();
    }
}
