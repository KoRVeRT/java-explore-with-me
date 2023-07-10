package ru.practicum.ewm.main.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.CompilationMapper;
import ru.practicum.ewm.main.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main.compilation.model.Compilation;
import ru.practicum.ewm.main.compilation.repository.CompilationRepository;
import ru.practicum.ewm.main.event.dto.EventMapper;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventContainer;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.event.service.EventServiceImpl;
import ru.practicum.ewm.main.event.service.EventSpecifications;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.main.compilation.service.CompilationSpecifications.isPinned;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventServiceImpl eventService;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Set<Event> eventsSet = new HashSet<>();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            eventsSet = new HashSet<>(getListEvents(newCompilationDto.getEvents()));
        }
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto, eventsSet);
        Compilation savedCompilation = compilationRepository.save(compilation);
        List<EventShortDto> eventsShortDto = getShortEventsDto(newCompilationDto.getEvents());
        return compilationMapper.toDto(savedCompilation, eventsShortDto);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(UpdateCompilationRequest updateRequestDto, Long compilationId) {
        findCompilationById(compilationId);
        List<Event> eventList = new ArrayList<>();
        if (Objects.nonNull(updateRequestDto.getEvents()) && !updateRequestDto.getEvents().isEmpty()) {
            eventList = eventRepository.findAllById(updateRequestDto.getEvents());
        }
        Compilation compilationToUpdate = createCompilationUpdate(updateRequestDto, compilationId, eventList);
        Compilation updatedCompilation = compilationRepository.save(compilationToUpdate);
        List<EventShortDto> eventShortDto = getShortEventsDto(updateRequestDto.getEvents());
        return compilationMapper.toDto(updatedCompilation, eventShortDto);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compId) {
        findCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = findCompilationById(compId);
        List<EventShortDto> eventShortDto = getShortEventsDto(compilation.getEvents().stream()
                .map(Event::getId)
                .collect(Collectors.toSet()));
        return compilationMapper.toDto(compilation, eventShortDto);
    }

    @Override
    public List<CompilationDto> getCompilationsByPinned(Boolean pinned, Pageable pageable) {
        List<Compilation> compilationList = getCompilations(pinned, pageable);
        return compilationList.stream()
                .map(compilation -> compilationMapper.toDto(compilation,
                        getShortEventsDto(compilation.getEvents().stream()
                                .map(Event::getId).collect(Collectors.toSet()))))
                .collect(Collectors.toList());
    }

    private List<Compilation> getCompilations(Boolean pinned, Pageable pageable) {
        Page<Compilation> page = compilationRepository.findAll(isPinned(pinned), pageable);
        return page.getContent();
    }

    private List<Event> getListEvents(Set<Long> events) {
        return eventRepository.findAllById(events);
    }

    private Compilation createCompilationUpdate(UpdateCompilationRequest updateCompilationRequestDto, Long compId,
                                                List<Event> events) {
        Compilation compilation = findCompilationById(compId);
        Set<Event> eventSet = new HashSet<>(events);
        Optional.ofNullable(updateCompilationRequestDto.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateCompilationRequestDto.getPinned()).ifPresent(compilation::setPinned);
        compilation.setEvents(eventSet);
        return compilation;
    }

    private Compilation findCompilationById(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("CompilationId=%d was not found", id)));
    }

    private List<EventShortDto> getShortEventsDto(Set<Long> events) {
        if (events == null || events.isEmpty()) {
            return new ArrayList<>();
        }
        Specification<Event> spec = Specification
                .where(EventSpecifications.hasIdIn(events));
        EventContainer data = eventService.getEventData(spec, Pageable.unpaged());
        return data.getEvents()
                .stream()
                .map(event -> eventMapper.toEventShortDto(event,
                        data.getEventsViews().getOrDefault(event.getId(), 0L),
                        data.getConfirmedRequests().getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }
}