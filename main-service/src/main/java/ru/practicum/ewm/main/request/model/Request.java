package ru.practicum.ewm.main.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.user.model.User;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "request")
@Builder
@Getter
@Setter
@EqualsAndHashCode(exclude = "createdOn")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "requester_id")
    User requester;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    RequestStatus requestStatus;

    @Column(name = "request_created_on")
    LocalDateTime createdOn;
}