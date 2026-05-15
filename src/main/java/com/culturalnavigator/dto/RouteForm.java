package com.culturalnavigator.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RouteForm {
    @NotBlank(message = "Введите название маршрута")
    @Size(max = 255, message = "Название должно быть короче 255 символов")
    private String title;

    @NotBlank(message = "Добавьте описание маршрута")
    @Size(max = 2000, message = "Описание должно быть короче 2000 символов")
    private String description;

    @NotNull(message = "Укажите длительность")
    @Min(value = 15, message = "Маршрут должен длиться хотя бы 15 минут")
    private Integer durationMinutes = 90;

    @NotNull(message = "Укажите бюджет")
    @DecimalMin(value = "0", message = "Бюджет не может быть отрицательным")
    @DecimalMax(value = "1000000", message = "Бюджет должен быть не больше 1 000 000")
    private BigDecimal budget = BigDecimal.ZERO;

    private Boolean isPublic = true;

    @NotEmpty(message = "Добавьте хотя бы одно событие")
    private List<Long> eventIds = new ArrayList<>();
}
