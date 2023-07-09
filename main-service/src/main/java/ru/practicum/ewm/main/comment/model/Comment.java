package ru.practicum.ewm.main.comment.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@EqualsAndHashCode(exclude = {"createdOn"})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "comment_text")
    String text;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_commentator_id")
    User commentator;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_event_id")
    Event event;

    @Column(name = "comment_created_on")
    LocalDateTime createdOn = LocalDateTime.now();
}