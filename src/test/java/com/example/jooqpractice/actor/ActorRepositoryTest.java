package com.example.jooqpractice.actor;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.jooqpractice.tables.pojos.Actor;
import com.example.jooqpractice.tables.records.ActorRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
            .allMatch(
                it -> it.getFirstName().equals(firstName) || it.getLastName().equals(lastName));
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

    @Test
    @DisplayName("JOOQ에서 제공하는 DAO를 사용하여 Actor를 저장한다.")
    @Transactional
    void insert_by_dao() {
        // Given
        Actor actor = new Actor(null, "John", "Doe", LocalDateTime.now());

        // When
        Long generatedId = actorRepository.saveByDao(actor);

        // Then
        Optional<Actor> result = actorRepository.findById(generatedId);

        assertThat(result)
            .isPresent()
            .get()
            .hasNoNullFieldsOrProperties()
            .extracting(Actor::getFirstName, Actor::getLastName)
            .containsExactly(actor.getFirstName(), actor.getLastName());
    }

    @Test
    @DisplayName("JOOQ에서 제공하는 Active Record 패턴을 사용하여 Actor를 저장한다.")
    @Transactional
    void insert_by_active_record() {
        // Given
        Actor actor = new Actor(null, "John", "Doe", null);

        // When
        ActorRecord actorRecord = actorRepository.saveByActiveRecord(actor);

        // Then
        assertThat(actorRecord)
            .isNotNull()
            .extracting(ActorRecord::getActorId)
            .isNotNull();

        assertThat(actor.getActorId())
            .isNull();

        Optional<Actor> result = actorRepository.findById(actorRecord.getActorId());

        assertThat(result)
            .isPresent()
            .get()
            .hasNoNullFieldsOrProperties()
            .extracting(Actor::getFirstName, Actor::getLastName)
            .containsExactly(actor.getFirstName(), actor.getLastName());

        System.out.println(actorRecord);
    }

    @Test
    @DisplayName("JOOQ의 Insert 구문에서 RETURNING 절을 사용하여 PK만 반환받는다.")
    @Transactional
    void insert_with_returning_pk() {
        // Given
        Actor actor = new Actor(null, "John", "Doe", null);

        // When
        Long pk = actorRepository.saveWithReturningPkOnly(actor);

        // Then
        Optional<Actor> result = actorRepository.findById(pk);

        assertThat(result)
            .isPresent()
            .get()
            .hasNoNullFieldsOrProperties()
            .extracting(Actor::getFirstName, Actor::getLastName)
            .containsExactly(actor.getFirstName(), actor.getLastName());
    }

    @Test
    @DisplayName("JOOQ의 Insert 구문에서 RETURNING 절을 사용하여 저장된 레코드 전체를 반환받는다.")
    @Transactional
    void insert_with_returning() {
        // Given
        Actor actor = new Actor(null, "John", "Doe", null);

        // When
        Actor result = actorRepository.saveWithReturning(actor);

        // Then
        assertThat(result)
            .hasNoNullFieldsOrProperties()
            .extracting(Actor::getFirstName, Actor::getLastName)
            .containsExactly(actor.getFirstName(), actor.getLastName());
    }

    @Test
    @DisplayName("JOOQ의 Bulk Insert 기능을 사용하여 여러 Actor를 저장한다.")
    @Transactional
    void bulk_insert() {
        // Given
        Actor actor1 = new Actor(null, "John", "Doe", null);
        Actor actor2 = new Actor(null, "John2", "Doe2", null);

        List<Actor> actors = List.of(actor1, actor2);

        // When
        actorRepository.bulkInsert(actors);
    }

    @Test
    @DisplayName("JOOQ의 Bulk Insert 기능을 사용하여 여러 Actor를 저장하고, RETURNING 절을 사용하여 저장된 레코드의 PK 리스트를 반환받는다.")
    @Transactional
    void bulk_insert_and_return_pk() {
        // Given
        Actor actor1 = new Actor(null, "John", "Doe", null);
        Actor actor2 = new Actor(null, "John2", "Doe2", null);

        List<Actor> actors = List.of(actor1, actor2);

        // When
        List<Long> pks = actorRepository.bulkInsertAndReturnPks(actors);

        // Then
        assertThat(pks).hasSameSizeAs(actors);

        List<Actor> result = actorRepository.findByActorIdIn(pks);

        assertThat(result)
            .hasSameSizeAs(actors)
            .allSatisfy(actor -> assertThat(actor)
                .hasNoNullFieldsOrProperties()
                .extracting(Actor::getFirstName, Actor::getLastName)
                .containsExactly(actor.getFirstName(), actor.getLastName()));
    }

    @Test
    @DisplayName("JOOQ의 Bulk Insert 기능을 사용하여 여러 Actor를 저장하고, RETURNING 절을 사용하여 저장된 레코드 전체를 반환받는다.")
    @Transactional
    void bulk_insert_and_return_pojo() {
        // Given
        Actor actor1 = new Actor(null, "John", "Doe", null);
        Actor actor2 = new Actor(null, "John2", "Doe2", null);

        List<Actor> actors = List.of(actor1, actor2);

        // When
        List<Actor> result = actorRepository.bulkInsertAndReturnPojos(actors);

        // Then
        assertThat(result)
            .hasSameSizeAs(actors)
            .allSatisfy(actor -> assertThat(actor)
                .hasNoNullFieldsOrProperties()
                .extracting(Actor::getFirstName, Actor::getLastName)
                .containsExactly(actor.getFirstName(), actor.getLastName()));
    }
}