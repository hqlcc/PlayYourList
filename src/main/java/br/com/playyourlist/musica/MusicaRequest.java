package br.com.playyourlist.musica;

import jakarta.validation.constraints.*;

public record MusicaRequest(
        @NotBlank(message = "Título é obrigatório e não pode conter apenas espaços.")
        @Size(max = 150, message = "Título deve ter no máximo 150 caracteres.") String titulo,
        @NotBlank(message = "Artista é obrigatório e não pode conter apenas espaços.")
        @Size(max = 150, message = "Artista deve ter no máximo 150 caracteres.") String artista,
        @Size(max = 150, message = "Álbum deve ter no máximo 150 caracteres.") String album,
        @NotNull(message = "Duração é obrigatória.")
        @Positive(message = "Duração deve ser maior que zero.") Integer duracao,
        @Size(max = 50, message = "Gênero deve ter no máximo 50 caracteres.") String genero) {
}
