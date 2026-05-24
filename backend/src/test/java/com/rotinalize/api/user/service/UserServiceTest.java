package com.rotinalize.api.user.service;

import com.rotinalize.api.user.dto.UserRequestDTO;
import com.rotinalize.api.user.model.User;
import com.rotinalize.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository repository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final UserService service = new UserService(repository, passwordEncoder);

    @Test
    void createEncryptsPasswordBeforeSavingUser() {
        UserRequestDTO request = new UserRequestDTO("Leonardo", "leo@email.com", "123456");

        when(repository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = service.create(request);

        assertThat(saved.getPassword()).isEqualTo("encoded-password");
        verify(passwordEncoder).encode("123456");
    }

    @Test
    void getOwnProfileRejectsDifferentUserId() {
        UUID authenticatedUserId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();

        assertThatThrownBy(() -> service.getOwnProfile(authenticatedUserId, anotherUserId))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void updateRejectsDifferentUserId() {
        UUID authenticatedUserId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();

        assertThatThrownBy(() -> service.update(authenticatedUserId, anotherUserId, null))
                .isInstanceOf(SecurityException.class);
    }
}

