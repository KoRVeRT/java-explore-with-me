package ru.practicum.ewm.main.event.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.category.model.Category;
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
@Table(name = "events")
@Getter
@Setter
@EqualsAndHashCode(exclude = {"eventDate", "createdOn", "publishedOn"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    Long id;

    @Column(name = "event_annotation")
    String annotation;

    @Column(name = "event_title")
    String title;

    @Column(name = "event_description")
    String description;

    @Column(name = "event_location_lat")
    Float lat;

    @Column(name = "event_location_lon")
    Float lon;

    @Column(name = "event_paid")
    Boolean paid = Boolean.FALSE;

    @Column(name = "event_request_moderation")
    Boolean requestModeration;

    @Column
    @Enumerated(EnumType.STRING)
    EventState eventState;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_initiator_id")
    User initiator;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_category_id")
    Category category;

    @Column(name = "event_participant_limit")
    Integer participantLimit;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @Column(name = "event_created_on")
    LocalDateTime createdOn;

    @Column(name = "event_published_on")
    LocalDateTime publishedOn;
}
