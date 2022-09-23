package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class FilmControllerMockMvcUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private FilmController fc;

    @Test
    public void givenFilm_whenCreate_thenStatus200andFilmReturned() throws Exception {
        Film film = Film.builder()
                .name("Test")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        Mockito.when(fc.create(Mockito.any())).thenReturn(film);
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    @Test
    public void givenFilmWithFailedName_whenCreate_thenStatus400() throws Exception {
        Film film = Film.builder()
                .name("")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenFilmWithFailedDescription_whenCreate_thenStatus400() throws Exception {
        Film film = Film.builder()
                .name("Test")
                .description("123456789012345678901234567890123456789012345678901234567890" +
                        "1234567890123456789012345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890123456789012345678901")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenFilmWithFailedReleaseDate_whenCreate_thenStatus400() throws Exception {
        Film film = Film.builder()
                .name("Test")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(1)
                .build();
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenFilmWithFailedDuration_whenCreate_thenStatus400() throws Exception {
        Film film = Film.builder()
                .name("Test")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(0)
                .build();
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenFilms_whenGetAll_thenStatus200() throws Exception {
        Film film1 = Film.builder()
                .name("Test1")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        Film film2 = Film.builder()
                .name("Test2")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        Mockito.when(fc.getAll()).thenReturn(Arrays.asList(film1, film2));
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(film1, film2))));
    }
}
