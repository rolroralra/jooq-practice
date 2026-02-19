package com.example.jooqpractice.film;

import lombok.Getter;

public record FilmPriceSummary(
    Long filmId,
    String filmTitle,
    Double rentalRate,
    PriceCategory priceCategory,
    Long inventoryCount
) {
    @Getter
    public enum PriceCategory {
        CHEAP("Cheap"),
        NORMAL("Normal"),
        EXPENSIVE("Expensive");

        private final String code;

        PriceCategory(String code) {
            this.code = code;
        }

        public static PriceCategory of(String code) {
            for (PriceCategory category : values()) {
                if (category.code.equalsIgnoreCase(code)) {
                    return category;
                }
            }

            throw new IllegalArgumentException("Unknown price category: " + code);
        }
    }
}
