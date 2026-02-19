package com.example.jooqpractice.actor;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.jooqpractice.tables.pojos.Actor;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ActorRepositoryTest {

    @Autowired
    private ActorRepository actorRepository;

    @Test
    @DisplayName("firstName과 lastName이 모두 일치하는 배우를 조회한다.")
    void findBytFirstNameAndLastName() {
        // Given
        String firstName = "ED";
        String lastName = "CHASE";

        // When
        List<Actor> actors = actorRepository.findBytFirstNameAndLastName(firstName, lastName);

        // Then
        assertThat(actors)
            .isNotEmpty()
            .hasSize(1)
            .allSatisfy(actor -> assertThat(actor.getFirstName()).isEqualTo(firstName))
            .allSatisfy(actor -> assertThat(actor.getLastName()).isEqualTo(lastName));
    }

    @Test
    @DisplayName("firstName과 lastName 중 하나라도 일치하는 배우를 조회한다.")
    void findBytFirstNameOrLastName() {
        // Given
        String firstName = "ED";
        String lastName = "CHASE";

        // When
        List<Actor> actors = actorRepository.findBytFirstNameOrLastName(firstName, lastName);

        // Then
        assertThat(actors)
            .isNotEmpty()
            .allMatch(it -> it.getFirstName().equals(firstName) || it.getLastName().equals(lastName));
    }

    @ParameterizedTest
    @DisplayName("actorId 리스트에 해당하는 배우를 조회한다. 리스트가 null이거나 비어있으면 모든 배우를 조회한다.")
    @NullAndEmptySource
    void findByActorIdIn_WhenInputIsNullOrEmpty(List<Long> givenActorIds) {
        // When
        List<Actor> actors = actorRepository.findByActorIdIn(givenActorIds);

        // Then
        assertThat(actors)
            .isNotEmpty();
    }

    @Test
    @DisplayName("actorId 리스트에 해당하는 배우를 조회한다.")
    void findByActorIdIn() {
        // Given
        List<Long> givenActorIds = List.of(1L, 2L, 3L);

        // When
        List<Actor> actors = actorRepository.findByActorIdIn(givenActorIds);

        // Then
        assertThat(actors)
            .hasSize(3);
    }

    @Test
    @DisplayName("배우 이름으로 배우의 필모그래피를 조회한다.")
    void findActorFilmographyByActorName() {
        // Given
        var searchOption = ActorFilmographySearchOption.builder()
            .actorName("LOLLOBRIGIDA")
            .build();

        // When
        List<ActorFilmography> result = actorRepository.findActorFilmography(
            searchOption);

        // Then
        assertThat(result)
            .hasSize(1);

        assertThat(result.getFirst().filmList())
            .isNotEmpty();
    }

    @Test
    @DisplayName("배우 이름과 영화 제목으로 배우의 필모그래피를 조회한다.")
    void findActorFilmographyByFilmTitle() {
        // Given
        var searchOption = ActorFilmographySearchOption.builder()
            .actorName("LOLLOBRIGIDA")
            .filmTitle("COMMANDMENTS EXPRESS")
            .build();

        // When
        List<ActorFilmography> result = actorRepository.findActorFilmography(searchOption);

        // Then
        assertThat(result)
            .hasSize(1);

        assertThat(result.getFirst().filmList())
            .hasSize(1);
    }
}