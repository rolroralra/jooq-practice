package com.example.jooqpractice.film;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.jooqpractice.tables.pojos.Film;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class FilmRepositoryTest {

    @Autowired
    private FilmRepository filmRepository;

    @Test
    @DisplayName("id로 Film을 조회한다.")
    void findById() {
        Film result = filmRepository.findById(1L);

        assertThat(result)
            .isNotNull();
    }

    @Test
    @DisplayName("id로 간단한 Film 정보를 조회한다.")
    void findSimpleInfoById() {
        SimpleFilmInfo result = filmRepository.findSimpleInfoById(1L);

        assertThat(result)
            .isNotNull()
            .extracting(SimpleFilmInfo::filmId, SimpleFilmInfo::title, SimpleFilmInfo::description)
            .doesNotContainNull();
    }

    @Test
    @DisplayName("영화와 영화에 출연한 배우 정보를 페이징하여 조회한다.")
    void findFilmWithActorsByPage() {
        List<FilmWithActor> filmWithActorList = filmRepository.findFilmWithActorList(
            PageRequest.of(1, 5));

        assertThat(filmWithActorList)
            .isNotEmpty()
            .allSatisfy(it -> assertThat(it).isNotNull());
    }

    @Test
    @DisplayName("")
    void findFilmWithActorsByPage2() {
        List<FilmWithActor> filmWithActorList = filmRepository.findFilmWithActorList(
            PageRequest.of(0, 5));

        assertThat(filmWithActorList)
            .isNotEmpty()
            .allSatisfy(it -> assertThat(it).isNotNull());
    }
}