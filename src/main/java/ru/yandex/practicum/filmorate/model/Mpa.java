package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Mpa {
    private Long id;
    @NotBlank(message = "Название MPA не может быть пустым.")
    private String name;
}
