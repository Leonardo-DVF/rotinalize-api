package com.rotinalize.api.flashcard.service;

import com.rotinalize.api.flashcard.dto.FlashcardResponseDTO;
import com.rotinalize.api.flashcard.enums.DifficultyLevel;
import com.rotinalize.api.flashcard.model.Flashcard;
import com.rotinalize.api.flashcard.repository.FlashcardRepository;
import com.rotinalize.api.flashcard.deck.model.FlashcardDeck;
import com.rotinalize.api.flashcard.deck.repository.FlashcardDeckRepository;
import com.rotinalize.api.user.model.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FlashcardServiceTest {

    private final FlashcardRepository flashcardRepository = mock(FlashcardRepository.class);
    private final FlashcardDeckRepository deckRepository = mock(FlashcardDeckRepository.class);
    private final FlashcardService service = new FlashcardService(flashcardRepository, deckRepository);

    @Test
    void reviewCardUpdatesCountersAndNextInterval() {
        UUID ownerId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        Flashcard card = ownedCard(ownerId, cardId);
        card.setIntervalDays(1);

        when(flashcardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(flashcardRepository.save(card)).thenReturn(card);

        FlashcardResponseDTO response = service.reviewCard(ownerId, cardId, DifficultyLevel.FACIL);

        assertThat(response.getReviewCount()).isEqualTo(1);
        assertThat(response.getSuccessCount()).isEqualTo(1);
        assertThat(response.getIntervalDays()).isEqualTo(5);
        assertThat(response.getDifficultyLevel()).isEqualTo(DifficultyLevel.FACIL);
        assertThat(response.getNextReviewAt()).isNotNull();
    }

    @Test
    void reviewCardRejectsCardFromAnotherOwner() {
        UUID ownerId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        Flashcard card = ownedCard(UUID.randomUUID(), cardId);

        when(flashcardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> service.reviewCard(ownerId, cardId, DifficultyLevel.BOM))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Acesso negado");
    }

    private Flashcard ownedCard(UUID ownerId, UUID cardId) {
        User owner = new User();
        owner.setId(ownerId);

        FlashcardDeck deck = new FlashcardDeck();
        deck.setId(UUID.randomUUID());
        deck.setOwner(owner);

        Flashcard card = new Flashcard();
        card.setId(cardId);
        card.setDeck(deck);
        card.setFrontText("Pergunta");
        card.setBackText("Resposta");
        return card;
    }
}

