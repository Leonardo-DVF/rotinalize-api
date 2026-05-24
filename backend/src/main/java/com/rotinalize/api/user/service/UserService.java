package com.rotinalize.api.user.service;

import com.rotinalize.api.user.dto.UserRequestDTO;
import com.rotinalize.api.user.dto.UserUpdateDTO;
import com.rotinalize.api.user.model.User;
import com.rotinalize.api.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service //
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(UserRequestDTO userData) {
        repository.findByEmail(userData.email())
                .ifPresent(u -> { throw new IllegalArgumentException("Email já cadastrado."); });

        User newUser = new User();
        newUser.setName(userData.name());
        newUser.setEmail(userData.email());
        // >>> CODIFICA A SENHA <<<
        newUser.setPassword(passwordEncoder.encode(userData.password()));

        return repository.save(newUser);
    }

    /**
     * Busca um usuário pelo seu ID
     */
    public User get(UUID id) {
        // O método .orElseThrow() lança uma exceção se o usuário não for encontrado.
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + id));
    }

    public User getOwnProfile(UUID authenticatedUserId, UUID requestedUserId) {
        if (!authenticatedUserId.equals(requestedUserId)) {
            throw new SecurityException("Você não tem permissão para acessar este usuário.");
        }
        return get(requestedUserId);
    }

    /**
     * Deleta um usuário pelo seu ID
     */
    public void delete(UUID authenticatedUserId, UUID requestedUserId) {
        if (!authenticatedUserId.equals(requestedUserId)) {
            throw new SecurityException("Você não tem permissão para excluir este usuário.");
        }
        repository.deleteById(requestedUserId);
    }

    @Transactional
    public User update(UUID authenticatedUserId, UUID requestedUserId, UserUpdateDTO dataToUpdate) {
        if (!authenticatedUserId.equals(requestedUserId)) {
            throw new SecurityException("Você não tem permissão para atualizar este usuário.");
        }

        User existingUser = repository.findById(requestedUserId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + requestedUserId));

        if (dataToUpdate.name() != null && !dataToUpdate.name().isBlank()) {
            existingUser.setName(dataToUpdate.name());
        }
        if (dataToUpdate.password() != null && !dataToUpdate.password().isBlank()) {
            // >>> CODIFICA A SENHA TAMBÉM NO UPDATE <<<
            existingUser.setPassword(passwordEncoder.encode(dataToUpdate.password()));
        }
        return repository.save(existingUser);
    }

}

