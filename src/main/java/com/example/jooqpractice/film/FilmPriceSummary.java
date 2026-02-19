package com.example.jooqpractice.film;

public record FilmPriceSummary(
    Long filmId,
    String filmTitle,
    Double rentalRate,
    String priceCategory,
    Long inventoryCount
) {

}
