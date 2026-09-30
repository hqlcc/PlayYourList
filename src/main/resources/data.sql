
INSERT INTO musicas (titulo, artista, album, duracao, genero)
VALUES ('Imagine', 'John Lennon', 'Imagine', 183, 'Rock');

INSERT INTO musicas (titulo, artista, album, duracao, genero)
VALUES ('Billie Jean', 'Michael Jackson', 'Thriller', 294, 'Pop');

INSERT INTO musicas (titulo, artista, album, duracao, genero)
VALUES ('Bohemian Rhapsody', 'Queen', 'A Night at the Opera', 354, 'Rock');

INSERT INTO musicas (titulo, artista, album, duracao, genero)
VALUES ('Garota de Ipanema', 'Tom Jobim', 'Getz/Gilberto', 328, 'Bossa Nova');

INSERT INTO musicas (titulo, artista, album, duracao, genero)
VALUES ('Tempo Perdido', 'Legião Urbana', 'Dois', 301, 'Rock');

INSERT INTO playlists (nome, descricao)
VALUES ('Clássicos do Rock', 'Grandes clássicos do rock internacional');

INSERT INTO playlists (nome, descricao)
VALUES ('Música Brasileira', 'Clássicos da música brasileira');

INSERT INTO playlists (nome, descricao)
VALUES ('Pop Internacional', 'Sucessos do pop internacional');

INSERT INTO playlists (nome, descricao)
VALUES ('Para Relaxar', 'Músicas para momentos tranquilos');

INSERT INTO playlists (nome, descricao)
VALUES ('Favoritas', 'Minha seleção pessoal de músicas favoritas');

INSERT INTO playlist_musicas (playlistid, musicaid) VALUES (1, 1);

INSERT INTO playlist_musicas (playlistid, musicaid) VALUES (1, 3);

INSERT INTO playlist_musicas (playlistid, musicaid) VALUES (1, 5);

INSERT INTO playlist_musicas (playlistid, musicaid) VALUES (2, 4);

INSERT INTO playlist_musicas (playlistid, musicaid) VALUES (3, 2);

INSERT INTO playlist_musicas (playlistid, musicaid) VALUES (4, 1);

INSERT INTO playlist_musicas (playlistid, musicaid) VALUES (4, 4);

INSERT INTO playlist_musicas (playlistid, musicaid) VALUES (5, 1);

INSERT INTO playlist_musicas (playlistid, musicaid) VALUES (5, 2);

INSERT INTO playlist_musicas (playlistid, musicaid) VALUES (5, 5);

INSERT INTO reproducoes (playlistid, datahora) VALUES (1, CURRENT_TIMESTAMP);

INSERT INTO reproducoes (playlistid, datahora) VALUES (1, CURRENT_TIMESTAMP);

INSERT INTO reproducoes (playlistid, datahora) VALUES (1, CURRENT_TIMESTAMP);

INSERT INTO reproducoes (playlistid, datahora) VALUES (1, CURRENT_TIMESTAMP);

INSERT INTO reproducoes (playlistid, datahora) VALUES (1, CURRENT_TIMESTAMP);

INSERT INTO reproducoes (playlistid, datahora) VALUES (2, CURRENT_TIMESTAMP);

INSERT INTO reproducoes (playlistid, datahora) VALUES (2, CURRENT_TIMESTAMP);

INSERT INTO reproducoes (playlistid, datahora) VALUES (2, CURRENT_TIMESTAMP);

INSERT INTO reproducoes (playlistid, datahora) VALUES (3, CURRENT_TIMESTAMP);

INSERT INTO reproducoes (playlistid, datahora) VALUES (3, CURRENT_TIMESTAMP);

INSERT INTO reproducoes (playlistid, datahora) VALUES (4, CURRENT_TIMESTAMP);

INSERT INTO reproducoes (playlistid, datahora) VALUES (5, CURRENT_TIMESTAMP);
