package com.example.jooqpractice.web;

import com.example.jooqpractice.film.FilmWithActor;
import java.util.List;
import org.springframework.data.domain.Pageable;

public record FilmWithActorPagedResponse(
    PagedResponse page,
    List<FilmActorResponse> filmActorList
) {

    public static FilmWithActorPagedResponse of(Pageable pageable, List<FilmWithActor> filmWithActorList) {
        return new FilmWithActorPagedResponse(
            PagedResponse.of(pageable),
            filmWithActorList.stream()
                .map(FilmActorResponse::from)
                .toList()
        );
    }

    public record FilmActorResponse(
        String filmTitle,
        String actorFullName,
        Long filmId
    ) {

        public static FilmActorResponse from(FilmWithActor filmWithActor) {
            return new FilmActorResponse(
                filmWithActor.getFilmTitle(),
                filmWithActor.getActorFullName(),
                filmWithActor.getFilmId()
            );
        }
    }
}
