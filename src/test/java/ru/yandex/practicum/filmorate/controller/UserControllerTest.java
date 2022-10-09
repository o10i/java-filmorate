package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserController uc;
    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        uc.getAll().clear();
        //uc.userService. = 1;
    }

    private User getUser() {
        return User.builder()
                .login("Test")
                .name("name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
    }

    @Test
    public void givenUser_whenCreate_thenStatus200andUserReturned() throws Exception {
        User user = getUser();
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("Test"))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(jsonPath("$.birthday").value("2022-01-01"));
    }

    @Test
    public void givenUserWithEmptyName_whenCreate_thenNameAsLoginAndStatus200andUserReturned() throws Exception {
        User user = getUser();
        user.setName("");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("Test"))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(jsonPath("$.birthday").value("2022-01-01"));
    }

    @Test
    public void givenUserWithFailedLogin_whenCreate_thenStatus400() throws Exception {
        User user = getUser();
        user.setLogin("Test failed");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void givenUserWithFailedEmail_whenCreate_thenStatus400() throws Exception {
        User user = getUser();
        user.setEmail("mail.ru");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenUserWithFailedBirthday_whenCreate_thenStatus400() throws Exception {
        User user = getUser();
        user.setBirthday(LocalDate.of(2030, 1, 1));
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenUser_whenUpdated_thenStatus200andUpdatedUserReturned() throws Exception {
        User user = getUser();
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        User updatedUser = getUser();
        updatedUser.setName("UpdatedUser");
        updatedUser.setId(1L);
        mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(uc.getAll().get(1))));
    }

    @Test
    public void givenUser_whenUpdatedWithUnknownId_thenThrowsValidationException() {
        User user = getUser();
        uc.create(user);
        User updatedUser = getUser();
        updatedUser.setId(0L);
        assertThrows(UserNotFoundException.class, () -> uc.update(updatedUser), "Пользователя с id " + updatedUser.getId() + " не существует.");
    }

    @Test
    public void givenUsers_whenGetAll_thenStatus200() throws Exception {
        User user1 = getUser();
        user1.setName("Test1");
        user1.setId(1L);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        User user2 = getUser();
        user2.setName("Test2");
        user2.setId(2L);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(user1, user2))));
    }
}