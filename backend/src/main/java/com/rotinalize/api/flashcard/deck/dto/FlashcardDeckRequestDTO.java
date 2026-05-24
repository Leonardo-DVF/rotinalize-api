package com.rotinalize.api.flashcard.deck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlashcardDeckRequestDTO {

    @NotBlank(message = "O título do deck não pode ser vazio")
    @Size(max = 100, message = "O título do deck deve ter no máximo 100 caracteres")
    private String title;

    @Size(max = 500, message = "A descrição do deck deve ter no máximo 500 caracteres")
    private String description;
}

