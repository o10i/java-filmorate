package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Mpa findMpaById(Long id) {
        Mpa mpa = mpaDbStorage.findMpaById(id);
        log.info("Найден MPA c id = {}", id);
        return mpa;
    }

    public List<Mpa> findAllMpa() {
        List<Mpa> mpa = mpaDbStorage.findAllMpa();
        log.info("Все MPA найдены.");
        return mpa;
    }
}
