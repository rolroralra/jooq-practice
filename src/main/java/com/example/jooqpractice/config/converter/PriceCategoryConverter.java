package com.example.jooqpractice.config.converter;

import com.example.jooqpractice.film.FilmPriceSummary.PriceCategory;
import org.jooq.impl.EnumConverter;

public class PriceCategoryConverter extends EnumConverter<String, PriceCategory> {

    public PriceCategoryConverter() {
        super(String.class, PriceCategory.class, PriceCategory::getCode);
    }
}
