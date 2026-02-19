package com.example.jooqpractice.actor;

import static com.example.jooqpractice.utils.jooq.JooqListConditionUtil.inIfNotEmpty;

import com.example.jooqpractice.tables.JActor;
import com.example.jooqpractice.tables.JFilm;
import com.example.jooqpractice.tables.JFilmActor;
import com.example.jooqpractice.tables.daos.ActorDao;
import com.example.jooqpractice.tables.pojos.Actor;
import com.example.jooqpractice.tables.pojos.Film;
import java.util.List;
import java.util.Map;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

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
}
