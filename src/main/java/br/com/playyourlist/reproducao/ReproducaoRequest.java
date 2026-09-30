package br.com.playyourlist.reproducao;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReproducaoRequest(
        @JsonAlias("playlistId")
        @NotNull(message = "ID da playlist é obrigatório.")
        @Positive(message = "ID da playlist deve ser maior que zero.") Integer playlistid) {
}
