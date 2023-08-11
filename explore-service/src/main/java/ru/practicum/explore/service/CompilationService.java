package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.dto.compilation.NewCompilationDto;
import ru.practicum.explore.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explore.dto.event.EventShortDto;
import ru.practicum.explore.exception.exceptions.CompilationNotFoundException;
import ru.practicum.explore.mapper.CompilationMapper;
import ru.practicum.explore.mapper.EventMapper;
import ru.practicum.explore.model.Compilation;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.repository.CompilationRepository;
import ru.practicum.explore.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatService statService;

    public CompilationDto save(NewCompilationDto newCompilation) {
        log.info("Saving new compilation: {}", newCompilation);
        List<Event> events = getEvents(newCompilation.getEvents());

        Compilation compilation = Compilation.builder()
                .title(newCompilation.getTitle())
                .pinned(newCompilation.getPinned())
                .events(events)
                .build();

        Compilation savedCompilation = compilationRepository.save(compilation);

        log.info("Compilation saved: {}.", savedCompilation);
        return mapCompilationToDto(savedCompilation);
    }

    public void delete(Long compId) {
        log.info("Deleting compilation id {}.", compId);
        Compilation compilation = getCompilation(compId);
        compilationRepository.delete(compilation);
        log.info("Compilation id {} deleted.", compId);
    }

    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Updating compilation id {} with {}", compId, updateCompilationRequest);
        Compilation compilation = getCompilation(compId);

        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(getEvents(updateCompilationRequest.getEvents()));
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);
        log.info("Compilation updated: {}", updatedCompilation);
        return mapCompilationToDto(updatedCompilation);
    }

    public List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size) {
        log.info("Getting all compilations");
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Compilation> compilations = compilationRepository.findByPinned(pinned, page).getContent();
        log.info("Compilations found: {}", compilations);
        return compilations.stream().map(this::mapCompilationToDto).collect(Collectors.toList());
    }

    public CompilationDto get(Long id) {
        log.info("Looking for compilation id {}.", id);
        Compilation compilation = getCompilation(id);
        log.info("Compilation found: {}", compilation);
        return mapCompilationToDto(compilation);
    }

    private Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> {
            log.error("Compilation id {} not found", compId);
            return new CompilationNotFoundException(String.format("Compilation id %s not found", compId));
        });
    }

    private List<Event> getEvents(List<Long> ids) {
        List<Event> events = eventRepository.findAllByIdIn(ids);
        if (events.size() != ids.size()) {
            ids.removeAll(events.stream().map(Event::getId).collect(Collectors.toList()));
            log.info("Events id {} not found.", ids);
        }
        return events;
    }

    private CompilationDto mapCompilationToDto(Compilation compilation) {
        CompilationDto compilationDto = CompilationMapper.mapToDto(compilation);
        List<EventShortDto> eventShortDto = compilation.getEvents().stream()
                .map(EventMapper::mapToShortDto)
                .collect(Collectors.toList());
        compilationDto.setEvents(statService.setViewsNumber(eventShortDto));
        return compilationDto;
    }

}
