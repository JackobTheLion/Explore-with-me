package ru.practicum.explore.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.service.CompilationService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Validated
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping()
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all compilations.");
        List<CompilationDto> compilations = compilationService.findAll(pinned, from, size);
        log.info("Compilations found: {}.", compilations);
        return compilations;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable @Min(1) Long compId) {
        log.info("Looking for compilation id {}", compId);
        CompilationDto compilation = compilationService.get(compId);
        log.info("Compilation found: {}.", compilation);
        return compilation;
    }
}
