package com.example.jooqpractice.film;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.jooqpractice.web.FilmWithActorPagedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Test
    void getFilmActorPageResponse() {
        FilmWithActorPagedResponse filmActorPageResponse = filmService.getFilmActorPageResponse(
            PageRequest.of(0, 20));

        System.out.println(filmActorPageResponse);

        assertThat(filmActorPageResponse)
            .isNotNull();

        assertThat(filmActorPageResponse.filmActorList()).hasSize(20);
    }
}