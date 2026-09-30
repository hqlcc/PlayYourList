package br.com.playyourlist.playlist;

import jakarta.persistence.*;

@Entity
@Table(name = "playlists")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    @Column(nullable = false, length = 100)
    public String nome;
    @Column(length = 255)
    public String descricao;

    public void atualizar(PlaylistRequest dados) {
        nome = dados.nome();
        descricao = dados.descricao();
    }
}
