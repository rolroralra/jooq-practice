package com.example.jooqpractice.actor;

import com.example.jooqpractice.tables.pojos.Actor;
import com.example.jooqpractice.tables.pojos.Film;
import java.util.List;

public record ActorFilmography(
    Actor actor,
    List<Film> filmList
) {

}
