package com.rotinalize.api.flashcard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class FlashcardRequestDTO {

    @NotNull(message = "O deckId é obrigatório")
    private UUID deckId;

    @NotBlank(message = "A frente do card não pode ser vazia")
    @Size(max = 500, message = "A frente do card deve ter no máximo 500 caracteres")
    private String frontText;

    @NotBlank(message = "O verso do card não pode ser vazio")
    @Size(max = 2000, message = "O verso do card deve ter no máximo 2000 caracteres")
    private String backText;

    @Size(max = 60, message = "A tag deve ter no máximo 60 caracteres")
    private String tag;
}

