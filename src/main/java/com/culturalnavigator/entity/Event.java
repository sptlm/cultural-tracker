package com.culturalnavigator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event extends BaseEntity {

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Column(nullable = false)
    private BigDecimal price;

    private String imageUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @ManyToMany
    @JoinTable(name = "event_categories",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @Transient
    public String getStartLabel() {
        return startAt == null ? "" : startAt.format(DISPLAY_FORMATTER);
    }

    @Transient
    public String getTimeLabel() {
        if (startAt == null) {
            return "";
        }
        if (endAt == null) {
            return getStartLabel();
        }
        if (startAt.toLocalDate().equals(endAt.toLocalDate())) {
            return startAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + " - " + endAt.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return getStartLabel() + " - " + endAt.format(DISPLAY_FORMATTER);
    }
}
