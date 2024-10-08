package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    private final String url = "/films";
    private Film film1;
    private Film film2;
    private Film film3;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void beforeEach() throws Exception {
        film1 = new Film(null, "Film1", "desc1", LocalDate.of(1991, 1, 1), 110L, null, 0);
        mvc.perform(post(url).content(mapper.writeValueAsString(film1)).contentType(MediaType.APPLICATION_JSON));
        film2 = new Film(null, "Film2", "desc2", LocalDate.of(1992, 1, 1), 110L, null, 0);
        mvc.perform(post(url).content(mapper.writeValueAsString(film2)).contentType(MediaType.APPLICATION_JSON));
        film3 = new Film(null, "Film3", "desc3", LocalDate.of(1993, 1, 1), 110L, null, 0);
        mvc.perform(post(url).content(mapper.writeValueAsString(film3)).contentType(MediaType.APPLICATION_JSON));
        film1.setId(1L);
        film2.setId(2L);
        film3.setId(3L);
    }

    @AfterEach
    public void afterEach() throws Exception {
        mvc.perform(delete(url));
    }

    @Test
    public void findAllShouldBeReturnAllFilms() throws Exception {
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(film1, film2, film3))));
    }

    @Test
    public void findAllShouldBeReturnEmptyListWhenNotFilms() throws Exception {
        mvc.perform(delete(url));

        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void createValidFilmShouldBeReturnFilmAndValidFilmId() throws Exception {
        afterEach();
        Film newFilm = new Film();
        newFilm.setName("Film1");
        newFilm.setDescription("desc1");
        newFilm.setReleaseDate(LocalDate.of(1994, 4, 4));
        newFilm.setDuration(140L);

        mvc.perform(post(url)
                        .content(mapper.writeValueAsString(newFilm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Film1"))
                .andExpect(jsonPath("$.description").value("desc1"))
                .andExpect(jsonPath("$.releaseDate").value("1994-04-04"))
                .andExpect(jsonPath("$.duration").value(140L));

        newFilm.setId(1L);
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(newFilm))));
    }

    @Test
    public void updateFilmShouldBeReturnFilm() throws Exception {
        Film filmToUpdate = new Film(2L, "Film2 updated", "desc4 updated", LocalDate.of(2000, 1, 1), 30L, null, 0);

        mvc.perform(put(url)
                        .content(mapper.writeValueAsString(filmToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(filmToUpdate)));
    }

    @Test
    public void updateFilmWithOnlyIdShouldBeReturnOldFilm() throws Exception {
        Film filmToUpdate = new Film();
        filmToUpdate.setId(2L);
        filmToUpdate.setName(null);
        filmToUpdate.setDescription(null);
        filmToUpdate.setReleaseDate(null);
        filmToUpdate.setDuration(null);

        mvc.perform(put(url)
                        .content(mapper.writeValueAsString(filmToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(film2)));
    }

    @Test
    public void updateFilmWithOnlyNameShouldBeReturnFilmWithNewName() throws Exception {
        Film filmToUpdate = new Film();
        filmToUpdate.setId(2L);
        filmToUpdate.setName("film2 updated");
        filmToUpdate.setDescription(null);
        filmToUpdate.setReleaseDate(null);
        filmToUpdate.setDuration(null);

        film2.setName("film2 updated");
        mvc.perform(put(url)
                        .content(mapper.writeValueAsString(filmToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(film2)));
    }
}
