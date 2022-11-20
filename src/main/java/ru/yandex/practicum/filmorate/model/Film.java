package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Long id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    String name;
    @FilmReleaseDateConstraint
    LocalDate releaseDate;
    @Size(max = 200, message = "Максимальная длина описания фильма — 200 символов.")
    String description;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    int duration;
    Integer rate;
    @NotNull
    Mpa mpa;
    List<Genre> genres;
    List<Long> likes;
}
