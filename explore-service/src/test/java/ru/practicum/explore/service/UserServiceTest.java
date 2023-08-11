package ru.practicum.explore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.explore.dto.user.UserDto;
import ru.practicum.explore.exception.exceptions.EmailOrNameRegisteredException;
import ru.practicum.explore.exception.exceptions.UserNotFoundException;
import ru.practicum.explore.model.User;
import ru.practicum.explore.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserDto userToSave;
    private User savedUser;
    private UserDto expectedUser;

    @BeforeEach
    public void init() {
        userToSave = UserDto.builder()
                .email("email@email.ru")
                .name("name")
                .build();

        savedUser = User.builder()
                .id(1L)
                .email(userToSave.getEmail())
                .name(userToSave.getName())
                .build();

        expectedUser = UserDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .build();
    }


    @Test
    public void saveUser_Normal() {
        when(userRepository.save(any())).thenReturn(savedUser);
        UserDto expectedUser = UserDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .build();

        UserDto actualUser = userService.save(userToSave);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void saveUser_ThrowException() {
        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException(""));

        assertThrows(EmailOrNameRegisteredException.class, () -> userService.save(userToSave));
    }

    @Test
    public void findAllUsers_withIds() {
        when(userRepository.findByIdInOrderById(any(), any())).thenReturn(new PageImpl<>(List.of(savedUser)));

        List<UserDto> expectedUsers = List.of(expectedUser);

        List<UserDto> actualUsers = userService.findAll(List.of(1L), 0, 10);

        assertEquals(expectedUsers, actualUsers);
        verify(userRepository, never()).findAll(any(PageRequest.class));
    }

    @Test
    public void findAllUsers_withNulIds() {
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(savedUser)));

        List<UserDto> expectedUsers = List.of(expectedUser);

        List<UserDto> actualUsers = userService.findAll(null, 0, 10);

        assertEquals(expectedUsers, actualUsers);
        verify(userRepository, never()).findByIdInOrderById(any(), any(PageRequest.class));
    }

    @Test
    public void deleteUser_Normal() {
        when(userRepository.findById(any())).thenReturn(Optional.of(savedUser));

        UserDto deletedUser = userService.delete(1L);

        assertEquals(expectedUser, deletedUser);
        verify(userRepository, times(1)).deleteById(any());
    }

    @Test
    public void deleteUser_NoSuchUser() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.delete(1L));
    }
}
