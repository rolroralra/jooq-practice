package com.example.jooqpractice.actor;

import static com.example.jooqpractice.utils.jooq.JooqListConditionUtil.inIfNotEmpty;

import com.example.jooqpractice.tables.JActor;
import com.example.jooqpractice.tables.JFilm;
import com.example.jooqpractice.tables.JFilmActor;
import com.example.jooqpractice.tables.daos.ActorDao;
import com.example.jooqpractice.tables.pojos.Actor;
import com.example.jooqpractice.tables.pojos.Film;
import com.example.jooqpractice.tables.records.ActorRecord;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Row2;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class ActorRepository {
    private static final JFilmActor FILM_ACTOR = JFilmActor.FILM_ACTOR;
    private static final JFilm FILM = JFilm.FILM;
    private static final JActor ACTOR = JActor.ACTOR;

    private final DSLContext dslContext;

    @SuppressWarnings("unused")
    private final ActorDao actorDao;

    public ActorRepository(DSLContext dslContext, Configuration configuration) {
        this.dslContext = dslContext;
        this.actorDao = new ActorDao(configuration);
    }

    public List<Actor> findBytFirstNameAndLastName(String firstName, String lastName) {
        return dslContext.selectFrom(JActor.ACTOR)
            .where(
//                JActor.ACTOR.FIRST_NAME.eq(firstName).and(JActor.ACTOR.LAST_NAME.eq(lastName))
                JActor.ACTOR.FIRST_NAME.eq(firstName),
                JActor.ACTOR.LAST_NAME.eq(lastName)
            ).fetchInto(Actor.class);
    }

    public List<Actor> findBytFirstNameOrLastName(String firstName, String lastName) {
        return dslContext.selectFrom(JActor.ACTOR)
            .where(
                JActor.ACTOR.FIRST_NAME.eq(firstName)
                    .or(JActor.ACTOR.LAST_NAME.eq(lastName))
            ).fetchInto(Actor.class);
    }

    public List<Actor> findByActorIdIn(List<Long> actorIds) {
        return dslContext.selectFrom(JActor.ACTOR)
            .where(inIfNotEmpty(JActor.ACTOR.ACTOR_ID, actorIds))
            .fetchInto(Actor.class);
    }

    public List<ActorFilmography> findActorFilmography(ActorFilmographySearchOption searchOption) {
        Map<Actor, List<Film>> actorListMap = dslContext.select(
                DSL.row(ACTOR.fields()).as("actor"),
                DSL.row(FILM.fields()).as("film")
            ).from(ACTOR)
            .leftJoin(FILM_ACTOR).on(FILM_ACTOR.ACTOR_ID.eq(ACTOR.ACTOR_ID))
            .leftJoin(FILM).on(FILM.FILM_ID.eq(FILM_ACTOR.FILM_ID))
            .where(
                containsIfNotBlank(ACTOR.FIRST_NAME.concat(" ").concat(ACTOR.LAST_NAME), searchOption.actorName()),
                containsIfNotBlank(FILM.TITLE, searchOption.filmTitle())
            ).fetchGroups(it -> it.get("actor", Actor.class),
                it -> it.get("film", Film.class));

        return actorListMap.entrySet().stream()
            .map(entry -> new ActorFilmography(entry.getKey(), entry.getValue()))
            .toList();
    }

    private Condition containsIfNotBlank(Field<String> field, String inputValue) {
        if (inputValue == null || inputValue.isBlank()) {
            return DSL.noCondition();
        }

        return field.like("%" + inputValue + "%");
    }

    public Long saveByDao(Actor actor) {
        actorDao.insert(actor);
        return dslContext.lastID().longValue();
    }

    public Optional<Actor> findById(Long actorId) {
        return Optional.ofNullable(actorDao.findById(actorId));
    }

    public ActorRecord saveByActiveRecord(Actor actor) {
        ActorRecord actorRecord = dslContext.newRecord(ACTOR, actor);
        actorRecord.insert();
        return actorRecord;
    }

    public Long saveWithReturningPkOnly(Actor actor) {
        return dslContext.insertInto(ACTOR, ACTOR.FIRST_NAME, ACTOR.LAST_NAME)
            .values(actor.getFirstName(), actor.getLastName())
            .returningResult(ACTOR.ACTOR_ID)
            .fetchOneInto(Long.class);
    }

    public Actor saveWithReturning(Actor actor) {
        return dslContext.insertInto(ACTOR, ACTOR.FIRST_NAME, ACTOR.LAST_NAME)
            .values(actor.getFirstName(), actor.getLastName())
            .returning(ACTOR.fields())
            .fetchOneInto(Actor.class);
    }

    public void bulkInsert(List<Actor> actors) {
        List<Row2<String, String>> rows = getActorRows(actors);

        // Bulk Insert 수행
        dslContext.insertInto(ACTOR, ACTOR.FIRST_NAME, ACTOR.LAST_NAME)
            .valuesOfRows(rows)
            .execute();
    }

    public List<Long> bulkInsertAndReturnPks(List<Actor> actors) {
        List<Row2<String, String>> rows = getActorRows(actors);

        // Bulk Insert 후, PK 반환
        return dslContext.insertInto(ACTOR, ACTOR.FIRST_NAME, ACTOR.LAST_NAME)
            .valuesOfRows(rows)
            .returning(ACTOR.ACTOR_ID)
            .fetch()
            .getValues(ACTOR.ACTOR_ID);
    }

    public List<Actor> bulkInsertAndReturnPojos(List<Actor> actors) {
        List<Row2<String, String>> rows = getActorRows(actors);

        // Bulk Insert 후, 전체 컬럼 반환
        return dslContext.insertInto(ACTOR, ACTOR.FIRST_NAME, ACTOR.LAST_NAME)
            .valuesOfRows(rows)
            .returning(ACTOR.fields())
            .fetchInto(Actor.class);
    }

    public void update(Actor actor) {
        actorDao.update(actor);
    }

    public void updateWithDto(Long id, ActorUpdateRequest request) {
        var firstName = StringUtils.hasText(request.firstName()) ? DSL.val(request.firstName()) : DSL.noField(ACTOR.FIRST_NAME);
        var lastName = StringUtils.hasText(request.lastName()) ? DSL.val(request.lastName()) : DSL.noField(ACTOR.LAST_NAME);

        dslContext.update(ACTOR)
            .set(ACTOR.FIRST_NAME, firstName)
            .set(ACTOR.LAST_NAME, lastName)
            .where(ACTOR.ACTOR_ID.eq(id))
            .execute();
    }

    public void updateWithRecord(Long id, ActorUpdateRequest request) {
        ActorRecord actorRecord = dslContext.selectFrom(ACTOR)
            .where(ACTOR.ACTOR_ID.eq(id))
            .fetchOne();

        if (actorRecord == null) {
            return;
        }

        if (StringUtils.hasText(request.firstName())) {
            actorRecord.setFirstName(request.firstName());
        }

        if (StringUtils.hasText(request.lastName())) {
            actorRecord.setLastName(request.lastName());
        }

        dslContext.update(ACTOR)
            .set(actorRecord)
            .where(ACTOR.ACTOR_ID.eq(id))
            .execute();
    }

    public void updateWithActiveRecord(Long id, ActorUpdateRequest request) {
        ActorRecord actorRecord = dslContext.selectFrom(ACTOR)
            .where(ACTOR.ACTOR_ID.eq(id))
            .fetchOne();

        if (actorRecord == null) {
            return;
        }

        if (StringUtils.hasText(request.firstName())) {
            actorRecord.setFirstName(request.firstName());
        }

        if (StringUtils.hasText(request.lastName())) {
            actorRecord.setLastName(request.lastName());
        }

        // actorRecord.store();  // Upsert
        // actorRecord.insert(); // Insert Only
        actorRecord.update();    // Update Only
    }

    public void deleteByIdWithDao(Long id) {
        actorDao.deleteById(id);
    }

    public void deleteByIdWithDslContext(Long id) {
        dslContext.deleteFrom(ACTOR)
            .where(ACTOR.ACTOR_ID.eq(id))
            .execute();
    }

    public void deleteByIdWithActiveRecord(Long id) {
        ActorRecord actorRecord = dslContext.selectFrom(ACTOR)
            .where(ACTOR.ACTOR_ID.eq(id))
            .fetchOne();

        if (actorRecord == null) {
            return;
        }

        actorRecord.delete();
    }

    private static @NonNull List<Row2<String, String>> getActorRows(List<Actor> actors) {
        return actors.stream()
            .map(actor -> DSL.row(
                actor.getFirstName(),
                actor.getLastName()))
            .toList();
    }

    public ActorRecord findRecordByActorId(Long actorId) {
        return dslContext.fetchOne(ACTOR, ACTOR.ACTOR_ID.eq(actorId));
    }
}
