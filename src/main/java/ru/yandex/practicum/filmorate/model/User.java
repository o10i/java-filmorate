package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class User {
    private Long id;
    @NotBlank(message = "Логин не может быть пустым.")
    @Pattern(regexp = "^\\S*$", message = "Логин не может содержать пробелы.")
    private String login;
    private String name;
    @Email(message = "Электронная почта не может быть пустой и должна содержать символ '@'.")
    private String email;
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
    private List<Long> friends;
}
