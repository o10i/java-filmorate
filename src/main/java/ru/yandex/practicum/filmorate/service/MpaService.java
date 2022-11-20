package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    public Mpa findMpaById(Long id) {
        return mpaDbStorage.findMpaById(id);
    }

    public List<Mpa> findAllMpa() {
        return mpaDbStorage.findAllMpa();
    }
}
