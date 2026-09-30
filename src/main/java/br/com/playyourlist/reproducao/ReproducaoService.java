package br.com.playyourlist.reproducao;

import br.com.playyourlist.playlist.PlaylistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReproducaoService {
    private final ReproducaoRepository repository;
    private final PlaylistRepository playlists;

    public ReproducaoService(ReproducaoRepository repository, PlaylistRepository playlists) {
        this.repository = repository;
        this.playlists = playlists;
    }

    private void validarPlaylist(Integer playlistid) {
        if (!playlists.existsById(playlistid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist " + playlistid + " não encontrada.");
        }
    }

    @Transactional
    public Reproducao registrar(ReproducaoRequest dados) {
        validarPlaylist(dados.playlistid());
        Reproducao reproducao = new Reproducao();
        reproducao.playlistid = dados.playlistid();
        reproducao.datahora = LocalDateTime.now();
        return repository.save(reproducao);
    }

    public List<Reproducao> listar(Integer playlistid) {
        validarPlaylist(playlistid);
        return repository.findByPlaylistidOrderByDatahoraAscIdAsc(playlistid);
    }

    public long total(Integer playlistid) {
        validarPlaylist(playlistid);
        return repository.countByPlaylistid(playlistid);
    }
}
