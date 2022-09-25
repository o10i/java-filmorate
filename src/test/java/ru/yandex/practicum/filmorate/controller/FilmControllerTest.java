package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FilmController fc;
    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        fc.films.clear();
        fc.id = 1;
    }

    private Film getFilm() {
        return Film.builder()
                .name("Test")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
    }

    @Test
    public void givenFilm_whenCreate_thenStatus200andFilmReturned() throws Exception {
        Film film = getFilm();
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.releaseDate").value("1895-12-28"))
                .andExpect(jsonPath("$.duration").value(1));
    }

    @Test
    public void givenFilmWithFailedName_whenCreate_thenStatus400() throws Exception {
        Film film = getFilm();
        film.setName("");
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenFilmWithFailedDescription_whenCreate_thenStatus400() throws Exception {
        Film film = getFilm();
        film.setDescription("123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890123456789012345678901");
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenFilmWithFailedReleaseDate_whenCreate_thenStatus400() throws Exception {
        Film film = getFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenFilmWithFailedDuration_whenCreate_thenStatus400() throws Exception {
        Film film = getFilm();
        film.setDuration(0);
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenFilm_whenUpdated_thenStatus200andUpdatedFilmReturned() throws Exception {
        Film film = getFilm();
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Film updatedFilm = getFilm();
        updatedFilm.setName("UpdatedFilm");
        updatedFilm.setId(1);
        mockMvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(fc.films.get(1))));
    }

    @Test
    public void givenFilm_whenUpdatedWithUnknownId_thenThrowsValidationException() {
        Film film = getFilm();
        fc.create(film);
        Film updatedFilm = getFilm();
        updatedFilm.setId(0);
        assertThrows(ValidationException.class, () -> fc.update(updatedFilm), "Фильма с id " + updatedFilm.getId() + " не существует.");
    }

    @Test
    public void givenFilms_whenGetAll_thenStatus200() throws Exception {
        Film film1 = getFilm();
        film1.setName("Test1");
        film1.setId(1);
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Film film2 = getFilm();
        film2.setName("Test2");
        film2.setId(2);
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(film1, film2))));
    }
}
