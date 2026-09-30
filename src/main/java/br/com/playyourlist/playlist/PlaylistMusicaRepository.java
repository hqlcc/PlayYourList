package br.com.playyourlist.playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaylistMusicaRepository extends JpaRepository<PlaylistMusica, Integer> {
    boolean existsByPlaylistidAndMusicaid(Integer playlistid, Integer musicaid);
    List<PlaylistMusica> findByPlaylistidOrderByIdAsc(Integer playlistid);
    void deleteByPlaylistidAndMusicaid(Integer playlistid, Integer musicaid);
    void deleteByPlaylistid(Integer playlistid);
    void deleteByMusicaid(Integer musicaid);
}
