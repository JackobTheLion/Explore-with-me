package ru.practicum.explore.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.user.UserDto;
import ru.practicum.explore.service.UserService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
public class UserAdminController {

    private final UserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@RequestBody @Validated UserDto userDto) {
        log.info("Saving user: {}.", userDto);
        UserDto savedUser = userService.save(userDto);
        log.info("User saved: {}.", savedUser);
        return savedUser;
    }

    @GetMapping()
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids, //TODO consider validation
                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Looking for user ids: {}.", ids);
        List<UserDto> users = userService.findAll(ids, from, size);
        log.info("Users found: {}.", users);
        return users;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Min(1) Long userId) {
        log.info("Deleting user id {}.", userId);
        userService.delete(userId);
    }
}