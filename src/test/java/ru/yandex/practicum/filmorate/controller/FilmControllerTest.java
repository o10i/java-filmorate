package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        fc.findAllFilms().clear();
    }

    private Film getFilm() {
        return Film.builder()
                .name("Test")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
    }

/*    @Test
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
    }*/

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

/*    @Test
    public void givenFilm_whenUpdatedWithUnknownId_thenThrowsValidationException() {
        Film film = getFilm();
        fc.saveFilm(film);
        Film updatedFilm = getFilm();
        updatedFilm.setId(0L);
        assertThrows(ObjectNotFoundException.class, () -> fc.updateFilm(updatedFilm), "Фильма с id " + updatedFilm.getId() + " не существует.");
    }*/
}
