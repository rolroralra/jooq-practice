package com.example.jooqpractice.film;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class FilmRepositoryJooqJoinTest {
    @Autowired
    private FilmRepository filmRepository;

    @Test
    @DisplayName("영화와 영화에 출연한 배우 정보를 페이징하여 조회한다. (Implicit Path Join)")
    void findFilmWithActorListByImplicitPathJoin() {
        PageRequest pageRequest = PageRequest.of(1, 5);
        List<FilmWithActor> filmWithActorList = filmRepository.findFilmWithActorListByImplicitPathJoin(
            pageRequest);

        List<FilmWithActor> expectedResult = filmRepository.findFilmWithActorList(pageRequest);

        assertThat(filmWithActorList)
            .isNotEmpty()
            .containsExactlyElementsOf(expectedResult)
            .allSatisfy(it -> assertThat(it).isNotNull());
    }

    @Test
    @DisplayName("영화와 영화에 출연한 배우 정보를 페이징하여 조회한다. (Explicit Path Join)")
    void findFilmWithActorListByExplicitPathJoin() {
        PageRequest pageRequest = PageRequest.of(1, 5);
        List<FilmWithActor> filmWithActorList = filmRepository.findFilmWithActorListByExplicitPathJoin(
            pageRequest);
        List<FilmWithActor> expectedResult = filmRepository.findFilmWithActorList(pageRequest);

        assertThat(filmWithActorList)
            .isNotEmpty()
            .containsExactlyElementsOf(expectedResult)
            .allSatisfy(it -> assertThat(it).isNotNull());
    }
}