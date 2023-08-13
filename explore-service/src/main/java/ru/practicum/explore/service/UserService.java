package ru.practicum.explore.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.user.UserDto;
import ru.practicum.explore.exception.exceptions.EmailOrNameRegisteredException;
import ru.practicum.explore.exception.exceptions.UserNotFoundException;
import ru.practicum.explore.mapper.UserMapper;
import ru.practicum.explore.model.User;
import ru.practicum.explore.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
//@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto save(UserDto userDto) {
        try {
            log.info("Saving user: {}.", userDto);
            User savedUser = userRepository.save(UserMapper.mapFromDto(userDto));
            log.info("User saved: {}.", savedUser);
            return UserMapper.mapToDto(savedUser);
        } catch (DataIntegrityViolationException e) {
            log.info("Fail to save user: {}", e.getMessage());
            throw new EmailOrNameRegisteredException(String.format("Fail to save user: %s", e.getMessage()));
        }
    }

    public List<UserDto> findAll(List<Long> ids, Integer from, Integer size) {
        List<User> allUsers;
        if (ids == null) {
            allUsers = findAllUsers(from, size);
        } else {
            allUsers = findAllUsersById(ids, from, size);
        }
        return allUsers.stream().map(UserMapper::mapToDto).collect(Collectors.toList());
    }

    public UserDto delete(Long userId) {
        log.info("Deleting user id: {}.", userId);
        User userToDelete = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User id {} not found.", userId);
            return new UserNotFoundException(String.format("User id %s not found.", userId));
        });
        userRepository.deleteById(userId);
        log.info("User {} deleted", userToDelete);
        return UserMapper.mapToDto(userToDelete);
    }

    private List<User> findAllUsersById(List<Long> ids, Integer from, Integer size) {
        log.info("Looking for user ids: {}. from {}, size {}.", ids, from, size);
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<User> allById = userRepository.findByIdInOrderById(ids, page).getContent();
        log.info("Users found: {}.", allById);
        return allById;
    }

    private List<User> findAllUsers(Integer from, Integer size) {
        log.info("Looking for all users from {}, size {}.", from, size);
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<User> allUsers = userRepository.findAllByOrderById(page).getContent();
        log.info("Users found: {}.", allUsers);
        return allUsers;
    }
}
