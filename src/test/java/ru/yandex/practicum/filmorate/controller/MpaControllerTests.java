package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaControllerTests {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void testFindMpaById() {
        assertEquals(Mpa.builder().id(1L).name("G").build(), mpaDbStorage.findMpaById(1L));
    }

    @Test
    void testFindUnknownMpa() {
        assertThrows(ObjectNotFoundException.class, () -> mpaDbStorage.findMpaById(-1L), "Mpa с id " + -1 + " не найден.");
    }

    @Test
    void testFindAllMpa() {
        assertEquals(Mpa.builder().id(1L).name("G").build(), mpaDbStorage.findAllMpa().get(0));
        assertEquals(5, mpaDbStorage.findAllMpa().size());
    }
}
