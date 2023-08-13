package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.user.UserDto;
import ru.practicum.explore.dto.user.UserShortDto;
import ru.practicum.explore.model.User;

public class UserMapper {

    public static UserDto mapToDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User mapFromDto(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static UserShortDto mapToShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
