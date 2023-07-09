package ru.practicum.ewm.main.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;
    Long commentatorId;
    LocalDateTime createdOn;

    @Size(min = 2, max = 1024)
    @NotBlank
    String text;

    @NotNull
    Long eventId;
}