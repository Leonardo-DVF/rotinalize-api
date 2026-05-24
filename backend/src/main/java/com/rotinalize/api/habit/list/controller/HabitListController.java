package com.rotinalize.api.habit.list.controller;

import com.rotinalize.api.habit.dto.HabitsResponseDTO;
import com.rotinalize.api.habit.model.Habits;
import com.rotinalize.api.habit.list.dto.HabitListRequestDTO;
import com.rotinalize.api.habit.list.dto.HabitListResponseDTO;
import com.rotinalize.api.habit.list.model.HabitList;
import com.rotinalize.api.habit.list.service.HabitListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lists")
@SecurityRequirement(name = "bearer-key") // Protege todos os endpoints dessa classe com cadeado
@Tag(name = "Listas de Hábitos", description = "Gerenciamento de listas para agrupar hábitos")
public class HabitListController {

    private final HabitListService service;
    public HabitListController(HabitListService service) {
        this.service = service;
    }

    // GET /api/lists: LISTAR todas as listas
    @GetMapping
    @Operation(summary = "Listar todas as listas", description = "Retorna todas as listas de hábitos pertencentes ao usuário logado")
    public List<HabitListResponseDTO> list(@AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = resolveUserId(jwt);

        // Chama o novo método do serviço, passando o ID do dono
        return service.listAllByOwner(ownerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // GET /api/lists/{id}: BUSCAR uma lista específica por ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar lista por ID", description = "Retorna os detalhes de uma lista específica e seus hábitos")
    public HabitListResponseDTO get(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        UUID ownerId = resolveUserId(jwt);
        HabitList list = service.get(id, ownerId);
        return mapToResponse(list);
    }

    // POST /api/lists: CRIAR uma nova lista
    @PostMapping
    @Operation(summary = "Criar nova lista", description = "Cria uma nova lista de hábitos vinculada ao usuário")
    @ApiResponse(responseCode = "201", description = "Lista criada com sucesso")
    public ResponseEntity<HabitListResponseDTO> create(
            @AuthenticationPrincipal Jwt jwt, // << 1. LER O TOKEN
            @RequestBody @Valid HabitListRequestDTO body // << 2. USA O DTO SIMPLIFICADO
    ) {
        UUID ownerId = resolveUserId(jwt);

        // 4. Chama o novo método do serviço
        HabitList created = service.create(body, ownerId);

        return ResponseEntity
                .created(URI.create("/api/lists/" + created.getId()))
                .body(mapToResponse(created));
    }

    // Deletar lista
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir lista", description = "Remove uma lista de hábitos pelo ID")
    @ApiResponse(responseCode = "204", description = "Lista excluída com sucesso")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id){
        UUID ownerId = resolveUserId(jwt);
        service.delete(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    // Método auxiliar para converter HabitList (Entidade do banco) em HabitListResponseDTO (Saída da API)
    private HabitListResponseDTO mapToResponse(HabitList list) {
        // Mapeia a lista de hábitos da Entidade para a Lista de DTOs
        List<HabitsResponseDTO> mappedHabits = list.getHabits().stream()
                .map(this::mapHabitToResponse)
                .collect(Collectors.toList());

        UUID ownerId = (list.getOwner() != null) ? list.getOwner().getId() : null;
        return new HabitListResponseDTO(
                list.getId(),
                list.getName(),
                ownerId,
                mappedHabits,
                list.getCreatedAt(),
                list.getUpdatedAt()
        );
    }

    private HabitsResponseDTO mapHabitToResponse(Habits h) {
        return new HabitsResponseDTO(
                h.getId(),
                h.getTitle(),
                h.getDescription(),
                h.getDias(),
                h.getDueDate(),
                h.getIntervalDays(),
                h.getIntervalStartDate(),
                h.getWeeklyEndDate(),
                h.getActive(),
                h.getList() != null ? h.getList().getId() : null,
                h.getOwner() != null ? h.getOwner().getId() : null,
                h.getOwner() != null ? h.getOwner().getName() : null,
                h.getCreatedAt(),
                h.getUpdatedAt()
        );
    }

    private UUID resolveUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("user_id"));
    }
}

