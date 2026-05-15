package com.culturalnavigator.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProfileForm {

    @NotBlank(message = "Email обязателен")
    @Email(message = "Введите корректный email")
    private String email;

    @DecimalMin(value = "0", message = "Бюджет не может быть отрицательным")
    @DecimalMax(value = "1000000", message = "Бюджет должен быть не больше 1 000 000")
    private BigDecimal preferredBudget;

    private String preferredAddress;

    private List<Long> favoriteCategoryIds = new ArrayList<>();

    private List<String> favoriteCategoryNames = new ArrayList<>();
}
