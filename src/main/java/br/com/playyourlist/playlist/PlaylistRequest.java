package br.com.playyourlist.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlaylistRequest(
        @NotBlank(message = "Nome é obrigatório e não pode conter apenas espaços.")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres.") String nome,
        @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres.") String descricao) {
}
