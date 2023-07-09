package ru.practicum.ewm.main.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequestDto, Long compId);

    void deleteCompilationById(Long compId);

    CompilationDto getCompilationById(Long compId);

    List<CompilationDto> getCompilationsByPinned(Boolean pinned, Pageable pageable);
}
