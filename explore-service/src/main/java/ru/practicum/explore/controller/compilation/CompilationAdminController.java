package ru.practicum.explore.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.dto.compilation.NewCompilationDto;
import ru.practicum.explore.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explore.service.CompilationService;

import javax.validation.constraints.Min;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Validated
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Validated NewCompilationDto newCompilationDto) {
        log.info("Adding new compilation: {}", newCompilationDto);
        CompilationDto savedCompilation = compilationService.save(newCompilationDto);
        log.info("Compilation saved: {}", savedCompilation);
        return savedCompilation;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Min(1) Long compId) {
        log.info("Deleting compilation id {}.", compId);
        compilationService.delete(compId);
        log.info("Compilation id {} deleted.", compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto patchCompilation(@PathVariable @Min(1) Long compId,
                                           @RequestBody @Validated UpdateCompilationRequest updateCompilationRequest) {
        log.info("Updating compilation id {} with {}", compId, updateCompilationRequest);
        CompilationDto updatedCompilation = compilationService.update(compId, updateCompilationRequest);
        log.info("Compilation updated: {}", updatedCompilation);
        return updatedCompilation;
    }
}