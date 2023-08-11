package ru.practicum.explore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.dto.compilation.NewCompilationDto;
import ru.practicum.explore.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.UpdateEventAdminRequest;
import ru.practicum.explore.dto.search.AdminSearchCriteria;
import ru.practicum.explore.dto.user.UserDto;
import ru.practicum.explore.mapper.EventState;
import ru.practicum.explore.service.CategoryService;
import ru.practicum.explore.service.CompilationService;
import ru.practicum.explore.service.EventService;
import ru.practicum.explore.service.UserService;

import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
@Validated
public class AdminController {

    private final UserService userService;

    private final CategoryService categoryService;

    private final CompilationService compilationService;

    private final EventService eventService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@RequestBody @Validated UserDto userDto) {
        log.info("Saving user: {}.", userDto);
        UserDto savedUser = userService.save(userDto);
        log.info("User saved: {}.", savedUser);
        return savedUser;
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids, //TODO consider validation
                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Looking for user ids: {}.", ids);
        List<UserDto> users = userService.findAll(ids, from, size);
        log.info("Users found: {}.", users);
        return users;
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Min(1) Long userId) {
        log.info("Deleting user id {}.", userId);
        userService.delete(userId);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Validated CategoryDto categoryDto) {
        log.info("Adding category: {}.", categoryDto);
        CategoryDto savedCategory = categoryService.save(categoryDto);
        log.info("Category saved: {}", savedCategory);
        return savedCategory;
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto patchCategory(@RequestBody @Validated CategoryDto categoryDto,
                                     @PathVariable @Min(1) Long catId) {
        log.info("Patching category id {} with {}.", catId, categoryDto);
        CategoryDto patchedCategory = categoryService.patch(catId, categoryDto);
        log.info("Category patched: {}", patchedCategory);
        return patchedCategory;
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Min(1) Long catId) {
        log.info("Deleting category id {}.", catId);
        categoryService.delete(catId);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Validated NewCompilationDto newCompilationDto) {
        log.info("Adding new compilation: {}", newCompilationDto);
        CompilationDto savedCompilation = compilationService.save(newCompilationDto);
        log.info("Compilation saved: {}", savedCompilation);
        return savedCompilation;
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Min(1) Long compId) {
        log.info("Deleting compilation id {}.", compId);
        compilationService.delete(compId);
        log.info("Compilation id {} deleted.", compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto patchCompilation(@PathVariable @Min(1) Long compId,
                                           @RequestBody @Validated UpdateCompilationRequest updateCompilationRequest) {
        log.info("Updating compilation id {} with {}", compId, updateCompilationRequest);
        CompilationDto updatedCompilation = compilationService.update(compId, updateCompilationRequest);
        log.info("Compilation updated: {}", updatedCompilation);
        return updatedCompilation;
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventState> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {

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

        AdminSearchCriteria adminSearchCriteria = AdminSearchCriteria.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .from(from)
                .size(size)
                .rangeStart(start)
                .rangeEnd(end)
                .build();

        log.info("Searching events based on criteria: {}.", adminSearchCriteria);
        List<EventFullDto> events = eventService.adminGetEvents(adminSearchCriteria);
        log.info("Events found: {}", events);
        return events;
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto patchEvent(@PathVariable @Min(1) Long eventId,
                                   @RequestBody @Validated UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Admin updating event id {}: {}", eventId, updateEventAdminRequest);
        EventFullDto eventFullDto = eventService.adminUpdateEvent(eventId, updateEventAdminRequest);
        log.info("Event updated: {}.", eventFullDto);
        return eventFullDto;
    }
}