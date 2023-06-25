package ru.practicum.ewm.main.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    @NotBlank
    @Size(max = 50)
    String title;
    Set<Long> events;
    Boolean pinned;
}