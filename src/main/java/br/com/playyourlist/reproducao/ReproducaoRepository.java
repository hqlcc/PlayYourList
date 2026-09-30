package br.com.playyourlist.reproducao;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReproducaoRepository extends JpaRepository<Reproducao, Integer> {
    List<Reproducao> findByPlaylistidOrderByDatahoraAscIdAsc(Integer playlistid);
    long countByPlaylistid(Integer playlistid);
    void deleteByPlaylistid(Integer playlistid);
}
