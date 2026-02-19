package com.example.jooqpractice.film;

import com.example.jooqpractice.tables.pojos.Actor;
import com.example.jooqpractice.tables.pojos.Film;
import com.example.jooqpractice.tables.pojos.FilmActor;

public record FilmWithActor(
    Film film,
    FilmActor filmActor,
    Actor actor
) {

    public String getFilmTitle() {
        return film.getTitle();
    }

    public Long getFilmId() {
        return film.getFilmId();
    }

    public String getActorFullName() {
        return actor.getFirstName() + " " + actor.getLastName();
    }
}
