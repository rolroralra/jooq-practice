package com.example.jooqpractice.film;

import com.example.jooqpractice.tables.JActor;
import com.example.jooqpractice.tables.JFilm;
import com.example.jooqpractice.tables.JFilmActor;
import com.example.jooqpractice.tables.pojos.Film;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FilmRepository {

    private static final JFilm FILM = JFilm.FILM;

    private static final JFilmActor FILM_ACTOR = JFilmActor.FILM_ACTOR;

    private static final JActor ACTOR = JActor.ACTOR;

    private final DSLContext dslContext;

    public Film findById(Long id) {
        return dslContext.select(FILM.fields())
            .from(FILM)
            .where(FILM.FILM_ID.eq(id))
            .fetchOneInto(Film.class);
    }

    public SimpleFilmInfo findSimpleInfoById(Long id) {
        return dslContext.select(
                FILM.FILM_ID,
                FILM.TITLE,
                FILM.DESCRIPTION)
            .from(FILM)
            .where(FILM.FILM_ID.eq(id))
            .fetchOneInto(SimpleFilmInfo.class);
    }

    public List<FilmWithActor> findFilmWithActorList(Pageable pageable) {
        return dslContext.select(
                DSL.row(FILM.fields()),
                DSL.row(FILM_ACTOR.fields()),
                DSL.row(ACTOR.fields())
            ).from(FILM)
            .leftJoin(FILM_ACTOR).on(FILM.FILM_ID.eq(FILM_ACTOR.FILM_ID))
            .leftJoin(ACTOR).on(ACTOR.ACTOR_ID.eq(FILM_ACTOR.ACTOR_ID))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchInto(FilmWithActor.class);
    }
}
