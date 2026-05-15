package com.culturalnavigator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VenueForm {
    @NotBlank(message = "Введите название площадки")
    @Size(max = 255, message = "Название должно быть короче 255 символов")
    private String name;

    @NotBlank(message = "Добавьте описание площадки")
    @Size(max = 2000, message = "Описание должно быть короче 2000 символов")
    private String description;

    @NotBlank(message = "Введите адрес")
    @Size(max = 500, message = "Адрес должен быть короче 500 символов")
    private String address;

    private Long cityDistrictId;
}
