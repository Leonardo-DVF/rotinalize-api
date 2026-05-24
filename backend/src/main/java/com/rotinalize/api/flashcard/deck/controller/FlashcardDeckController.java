package com.rotinalize.api.flashcard.deck.controller;

import com.rotinalize.api.flashcard.deck.dto.FlashcardDeckRequestDTO;
import com.rotinalize.api.flashcard.deck.dto.FlashcardDeckResponseDTO;
import com.rotinalize.api.flashcard.deck.service.FlashcardDeckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/decks")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Decks (Baralhos)", description = "Gerenciamento de baralhos de flashcards")
public class FlashcardDeckController {

    private final FlashcardDeckService deckService;
    public FlashcardDeckController(
            FlashcardDeckService deckService
    ) {
        this.deckService = deckService;
    }

    @PostMapping
    @Operation(summary = "Criar deck", description = "Cria um novo baralho de flashcards para o usuário logado")
    @ApiResponse(responseCode = "200", description = "Deck criado com sucesso")
    public FlashcardDeckResponseDTO createDeck(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody FlashcardDeckRequestDTO dto
    ) {
        UUID ownerId = resolveUserId(jwt);
        return deckService.createDeck(ownerId, dto);
    }

    @GetMapping
    @Operation(summary = "Listar meus decks", description = "Retorna todos os baralhos do usuário autenticado")
    public List<FlashcardDeckResponseDTO> listMyDecks(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = resolveUserId(jwt);
        return deckService.listMyDecks(ownerId);
    }

    @DeleteMapping("/{deckId}")
    @Operation(summary = "Excluir deck", description = "Remove um baralho e todos os seus cards")
    @ApiResponse(responseCode = "204", description = "Deck excluído com sucesso")
    public void deleteDeck(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID deckId
    ) {
        UUID ownerId = resolveUserId(jwt);
        deckService.deleteDeck(ownerId, deckId);
    }

    @PutMapping("/{deckId}")
    @Operation(summary = "Atualizar deck", description = "Edita o título ou descrição de um baralho existente")
    public FlashcardDeckResponseDTO updateDeck(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID deckId,
            @RequestBody @Valid FlashcardDeckRequestDTO dto
    ) {
        UUID ownerId = resolveUserId(jwt);
        return deckService.updateDeck(ownerId, deckId, dto);
    }

    private UUID resolveUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("user_id"));
    }
}

