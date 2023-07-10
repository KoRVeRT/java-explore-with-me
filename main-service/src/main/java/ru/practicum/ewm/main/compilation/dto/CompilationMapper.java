package ru.practicum.ewm.main.compilation.dto;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.compilation.model.Compilation;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.model.Event;

import java.util.List;
import java.util.Set;

@Component
public class CompilationMapper {
    public Compilation toCompilation(NewCompilationDto compilationDto, Set<Event> events) {
        return Compilation.builder()
                .title(compilationDto.getTitle())
                .pinned(compilationDto.getPinned() != null && compilationDto.getPinned())
                .events(events)
                .build();
    }

    public CompilationDto toDto(Compilation compilation, List<EventShortDto> list) {
        return CompilationDto.builder()
                .title(compilation.getTitle())
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .events(list)
                .build();
    }
}