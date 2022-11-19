package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Genre {
    private Long id;
    @NotBlank(message = "Название жанра не может быть пустым.")
    private String name;
}
