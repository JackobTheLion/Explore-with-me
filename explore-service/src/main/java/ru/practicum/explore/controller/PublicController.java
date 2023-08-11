package ru.practicum.explore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.EventShortDto;
import ru.practicum.explore.dto.search.PublicSearchCriteria;
import ru.practicum.explore.dto.search.Sort;
import ru.practicum.explore.service.CategoryService;
import ru.practicum.explore.service.CompilationService;
import ru.practicum.explore.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping()
@Validated
public class PublicController {
    private final EventService eventService;
    private final CompilationService compilationService;
    private final CategoryService categoryService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @GetMapping("/events")
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(defaultValue = "EVENT_DATE") Sort sort,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {

        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        }

        if (start != null && end != null) {
            if (start.isAfter(end)) {
                log.info("Start date {} is after end date {}.", start, end);
                throw new ValidationException(String.format("Start date %s is after end date %s.", start, end));
            }
        }

        PublicSearchCriteria publicSearchCriteria = PublicSearchCriteria.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        log.info("Searching events based on criteria: {}.", publicSearchCriteria);
        String requestIp = request.getRemoteAddr();
        String uri = request.getRequestURI();
        List<EventShortDto> events = eventService.publicGetEvents(publicSearchCriteria, requestIp);
        log.info("Events found: {}", events);
        return events;
    }

    @GetMapping("/events/{id}")
    public EventFullDto findEvent(@PathVariable @Min(1) Long id,
                                  HttpServletRequest request) {
        log.info("Looking for event id {}.", id);
        String requestIp = request.getRemoteAddr();
        String uri = request.getRequestURI();
        EventFullDto event = eventService.getEventPublic(id, requestIp, uri);
        log.info("Event found: {}.", event);
        return event;
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all categories.");
        List<CategoryDto> categories = categoryService.findAllCategories(from, size);
        log.info("Categories found: {}.", categories);
        return categories;
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto findCategory(@PathVariable @Min(1) Long catId) {
        log.info("Looking for category: {}.", catId);
        CategoryDto category = categoryService.findCategory(catId);
        log.info("Category found: {}.", category);
        return category;
    }

    @GetMapping("/compilations")
        public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "true") Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all compilations.");
        List<CompilationDto> compilations = compilationService.findAll(pinned, from, size);
        log.info("Compilations found: {}.", compilations);
        return compilations;
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable @Min(1) Long compId) {
        log.info("Looking for compilation id {}", compId);
        CompilationDto compilation = compilationService.get(compId);
        log.info("Compilation found: {}.", compilation);
        return compilation;
    }
}
