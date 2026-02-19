package com.example.jooqpractice.film;

public record FilmRentalSummary(
    Long filmId,
    String filmTitle,
    Double rentalDuration
) {

}
