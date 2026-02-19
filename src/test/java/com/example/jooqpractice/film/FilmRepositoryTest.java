package com.example.jooqpractice.film;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.jooqpractice.tables.pojos.Film;
import java.util.Comparator;
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
    @DisplayName("영화 제목을 포함하는 영화의 가격 요약 정보를 조회한다.")
    void findFilmPriceSummaryByFilmTitle() {
        // Given
        String givenFilmTitle = "EGG";

        // When
        List<FilmPriceSummary> result = filmRepository.findFilmPriceSummaryByFilmTitle(
            givenFilmTitle);

        // Then
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("영화 제목을 포함하는 영화의 대여 기간 순으로 영화 대여 요약 정보를 조회한다.")
    void findFilmRentalSummaryByFilmTitleOrderByRentalDuration() {
        // Given
        String filmTitle = "EGG";

        // When
        List<FilmRentalSummary> result = filmRepository.findFilmRentalSummaryByFilmTitleOrderByRentalDuration(filmTitle);

        // Then
        assertThat(result)
            .allMatch(it -> it.filmTitle().contains(filmTitle))
            .allMatch(it -> it.rentalDuration() > 0)
            .isSortedAccordingTo(Comparator.comparing(FilmRentalSummary::rentalDuration).reversed())
            .isNotEmpty();
    }
}