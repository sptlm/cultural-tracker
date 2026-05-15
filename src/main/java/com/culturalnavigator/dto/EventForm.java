package com.culturalnavigator.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EventForm {

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @NotBlank(message = "Название обязательно")
    @Size(max = 255, message = "Название слишком длинное")
    private String title;

    @NotBlank(message = "Описание обязательно")
    @Size(max = 2000, message = "Описание слишком длинное")
    private String description;

    @NotNull(message = "Укажите начало")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endAt;

    @NotNull(message = "Укажите стоимость")
    @DecimalMin(value = "0.0", message = "Стоимость не может быть отрицательной")
    @DecimalMax(value = "1000000", message = "Стоимость должна быть не больше 1 000 000")
    private BigDecimal price = BigDecimal.ZERO;

    private String imageUrl;

    @NotNull(message = "Выберите площадку")
    private Long venueId;

    private List<Long> categoryIds = new ArrayList<>();

    public String getStartInputValue() {
        return startAt == null ? "" : startAt.format(INPUT_FORMATTER);
    }

    public String getEndInputValue() {
        return endAt == null ? "" : endAt.format(INPUT_FORMATTER);
    }
}
