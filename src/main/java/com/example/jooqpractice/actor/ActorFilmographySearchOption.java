package com.example.jooqpractice.actor;

import lombok.Builder;

@Builder
public record ActorFilmographySearchOption(
    String actorName,
    String filmTitle
) {

}
