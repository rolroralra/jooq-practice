package com.example.jooqpractice.actor;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.jooqpractice.tables.JActor;
import com.example.jooqpractice.tables.records.ActorRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ActorRecordTest {

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    DSLContext dslContext;

    @Autowired
    Configuration configuration;

    @Test
    @DisplayName("DSLContext를 이용하여 ActiveRecord를 조회한다. (recommended)")
    void findActiveRecordByDSLContext() {
        // Given
        Long actorId = 1L;

        // When
        ActorRecord actorRecord = actorRepository.findRecordByActorId(actorId);

        // Then
        assertThat(actorRecord)
            .isNotNull();

        assertThat(actorRecord.getActorId()).isNotNull();
        assertThat(actorRecord.getFirstName()).isNotBlank();
        assertThat(actorRecord.getLastName()).isNotBlank();
        assertThat(actorRecord.getLastUpdate()).isNotNull();
    }

    @Test
    @DisplayName("DSL.using(configuration) 이용하여 ActiveRecord를 조회한다.")
    void findActiveRecordByConfiguration() {
        // Given
        Long actorId = 1L;

        // When
        ActorRecord actorRecord = DSL.using(configuration)
            .selectFrom(JActor.ACTOR)
            .where(JActor.ACTOR.ACTOR_ID.eq(actorId))
            .fetchOne();

        // Then
        assertThat(actorRecord)
            .isNotNull();

        assertThat(actorRecord.getActorId()).isNotNull();
        assertThat(actorRecord.getFirstName()).isNotBlank();
        assertThat(actorRecord.getLastName()).isNotBlank();
        assertThat(actorRecord.getLastUpdate()).isNotNull();
    }

    @Test
    @DisplayName("ActiveRecord의 refresh() 메서드를 이용하여 데이터베이스의 최신 상태로 갱신한다.")
    void refreshByActiveRecord() {
        // Given
        Long actorId = 1L;
        ActorRecord actorRecord = actorRepository.findRecordByActorId(actorId);
        actorRecord.setFirstName(null);

        // When
        assertThat(actorRecord.getFirstName()).isNull();
//        actorRecord.refresh();        // 전체 칼럼 refresh
        actorRecord.refresh(JActor.ACTOR.FIRST_NAME);

        // Then
        assertThat(actorRecord)
            .isNotNull();

        assertThat(actorRecord.getActorId()).isNotNull();
        assertThat(actorRecord.getFirstName()).isNotBlank();
        assertThat(actorRecord.getLastName()).isNotBlank();
        assertThat(actorRecord.getLastUpdate()).isNotNull();
    }

    @Test
    @DisplayName("ActiveRecord의 store() 메서드를 이용하여 데이터를 저장한다. (upsert)")
    void storeByActiveRecord() {
        // Given
        ActorRecord actorRecord = dslContext.newRecord(JActor.ACTOR);

        // When
        actorRecord.setFirstName("John");
        actorRecord.setLastName("Doe");
        actorRecord.store();            // store() 메서드는 insert 또는 update를 자동으로 판단하여 실행한다.
        actorRecord.refresh();

        // Then
        assertThat(actorRecord)
            .isNotNull();

        assertThat(actorRecord.getActorId()).isNotNull();
        assertThat(actorRecord.getFirstName()).isNotBlank();
        assertThat(actorRecord.getLastName()).isNotBlank();
        assertThat(actorRecord.getLastUpdate()).isNotNull();
    }

    @Test
    @DisplayName("ActiveRecord의 insert() 메서드를 이용하여 데이터를 저장한다.")
    void insertByActiveRecord() {
        // Given
        ActorRecord actorRecord = dslContext.newRecord(JActor.ACTOR);

        // When
        actorRecord.setFirstName("John");
        actorRecord.setLastName("Doe");
        actorRecord.insert();
        actorRecord.refresh();

        // Then
        assertThat(actorRecord)
            .isNotNull();

        assertThat(actorRecord.getActorId()).isNotNull();
        assertThat(actorRecord.getFirstName()).isNotBlank();
        assertThat(actorRecord.getLastName()).isNotBlank();
        assertThat(actorRecord.getLastUpdate()).isNotNull();
    }

    @Test
    @DisplayName("ActiveRecord의 update() 메서드를 이용하여 데이터를 수정한다.")
    void updateByActiveRecord() {
        // Given
        Long actorId = 1L;
        String givenModifiedFirstName = "Emily";
        ActorRecord actorRecord = actorRepository.findRecordByActorId(actorId);

        // When
        actorRecord.setFirstName(givenModifiedFirstName);
        actorRecord.update();   // actorRecord.store();

        // Then
        assertThat(actorRecord)
            .isNotNull();

        assertThat(actorRecord.getActorId()).isNotNull();
        assertThat(actorRecord.getFirstName()).isNotBlank().isEqualTo(givenModifiedFirstName);
        assertThat(actorRecord.getLastName()).isNotBlank();
        assertThat(actorRecord.getLastUpdate()).isNotNull();

    }

    @Test
    @DisplayName("ActiveRecord의 delete() 메서드를 이용하여 데이터를 삭제한다.")
    void deleteByActiveRecord() {
        // Given
        ActorRecord actorRecord = dslContext.newRecord(JActor.ACTOR);
        actorRecord.setFirstName("John");
        actorRecord.setLastName("Doe");
        actorRecord.insert();

        // When
        int result = actorRecord.delete();

        // Then
        assertThat(result).isEqualTo(1);
    }
}
