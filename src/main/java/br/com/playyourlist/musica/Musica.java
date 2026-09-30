package br.com.playyourlist.musica;

import jakarta.persistence.*;

@Entity
@Table(name = "musicas")
public class Musica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    @Column(nullable = false, length = 150)
    public String titulo;
    @Column(nullable = false, length = 150)
    public String artista;
    @Column(length = 150)
    public String album;
    @Column(nullable = false)
    public Integer duracao;
    @Column(length = 50)
    public String genero;

    public void atualizar(MusicaRequest dados) {
        titulo = dados.titulo();
        artista = dados.artista();
        album = dados.album();
        duracao = dados.duracao();
        genero = dados.genero();
    }
}
