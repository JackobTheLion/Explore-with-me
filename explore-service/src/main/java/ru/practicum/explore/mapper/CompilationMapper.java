package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.model.Compilation;

import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationDto mapToDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream().map(EventMapper::mapToShortDto).collect(Collectors.toList()))
                .pinned(compilation.getPinned())
                .build();
    }
}
