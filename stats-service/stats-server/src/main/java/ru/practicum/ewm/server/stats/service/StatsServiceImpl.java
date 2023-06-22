package ru.practicum.ewm.server.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.stats.HitDto;
import ru.practicum.ewm.dto.stats.ViewDto;
import ru.practicum.ewm.server.stats.mapper.StatsMapper;
import ru.practicum.ewm.server.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Transactional
    public void saveHit(HitDto dto) {
        statsRepository.save(statsMapper.toHit(dto));
    }

    public List<ViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewDto> result = unique
                ? statsRepository.getStatsWithUniqUris(start, end)
                : statsRepository.getStats(start, end);

        return (uris == null || uris.isEmpty())
                ? result
                : result.stream()
                .filter(viewDto -> uris.contains(viewDto.getUri()))
                .collect(Collectors.toUnmodifiableList());
    }
}