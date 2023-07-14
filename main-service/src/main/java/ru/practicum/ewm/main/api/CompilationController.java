package ru.practicum.ewm.main.api;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/compilations")
@Slf4j
@Validated
@RequiredArgsConstructor
public class CompilationController {

    private final CompilationService compService;

    @GetMapping
    @ResponseStatus(OK)
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<CompilationDto> compilations = compService.getCompilationsByPinned(pinned, pageable);
        log.info("Compilation list with size={} has been got", compilations.size());
        return compilations;
    }

    @GetMapping("/{compId}")
    @ResponseStatus(OK)
    public CompilationDto getCompDtoById(@PathVariable Long compId) {
        CompilationDto compilation = compService.getCompilationById(compId);
        log.info("Compilation with id={} has been got", compId);
        return compilation;
    }
}
