package com.example.jooqpractice.film;

import com.example.jooqpractice.config.converter.PriceCategoryConverter;
import com.example.jooqpractice.tables.JActor;
import com.example.jooqpractice.tables.JFilm;
import com.example.jooqpractice.tables.JFilmActor;
import com.example.jooqpractice.tables.JInventory;
import com.example.jooqpractice.tables.JRental;
import com.example.jooqpractice.tables.pojos.Film;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FilmRepository {

    private static final JFilm FILM = JFilm.FILM;

    private static final JFilmActor FILM_ACTOR = JFilmActor.FILM_ACTOR;

    private static final JActor ACTOR = JActor.ACTOR;

    private static final JInventory INVENTORY = JInventory.INVENTORY;

    private static final JRental RENTAL = JRental.RENTAL;

    private final DSLContext dslContext;

    public Film findById(Long id) {
        return dslContext
            .select(FILM.fields())
            .from(FILM)
            .where(FILM.FILM_ID.eq(id))
            .fetchOneInto(Film.class);
    }

    public SimpleFilmInfo findSimpleInfoById(Long id) {
        return dslContext
            .select(
                FILM.FILM_ID,
                FILM.TITLE,
                FILM.DESCRIPTION)
            .from(FILM)
            .where(FILM.FILM_ID.eq(id))
            .fetchOneInto(SimpleFilmInfo.class);
    }

    public List<FilmWithActor> findFilmWithActorList(Pageable pageable) {
        return dslContext
            .select(
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

    public List<FilmPriceSummary> findFilmPriceSummaryByFilmTitle(String filmTitle) {
        return dslContext
            .select(
                FILM.FILM_ID,
                FILM.TITLE,
                FILM.RENTAL_RATE,
                DSL.case_()
                    .when(FILM.RENTAL_RATE.le(BigDecimal.valueOf(1.0)), "Cheap")
                    .when(FILM.RENTAL_RATE.le(BigDecimal.valueOf(3.0)), "Normal")
                    .otherwise("Expensive")
                    .as("priceCategory").convert(new PriceCategoryConverter()),
                DSL.selectCount()
                    .from(INVENTORY.where(INVENTORY.FILM_ID.eq(FILM.FILM_ID)))
                    .asField("totalInventory")
            ).from(FILM)
            .where(FILM.TITLE.contains(filmTitle))
            .fetchInto(FilmPriceSummary.class);
    }

    public List<FilmRentalSummary> findFilmRentalSummaryByFilmTitleOrderByRentalDuration(
        String filmTitle) {
        var averageRentalDurationAlias = "averageRentalDuration";
        var rentalDurationInfoSubquery = DSL
            .select(
                INVENTORY.FILM_ID,
                DSL.avg(DSL.localDateTimeDiff(DatePart.DAY, RENTAL.RENTAL_DATE, RENTAL.RETURN_DATE))
                    .as(averageRentalDurationAlias))
            .from(RENTAL)
            .leftJoin(INVENTORY).on(RENTAL.INVENTORY_ID.eq(INVENTORY.INVENTORY_ID))
            .where(RENTAL.RETURN_DATE.isNotNull())
            .groupBy(INVENTORY.FILM_ID)
            .asTable("rentalDurationInfo");

        return dslContext
            .select(
                FILM.FILM_ID,
                FILM.TITLE,
                rentalDurationInfoSubquery.field(averageRentalDurationAlias)
            ).from(FILM)
            .leftJoin(rentalDurationInfoSubquery)
            .on(FILM.FILM_ID.eq(rentalDurationInfoSubquery.field(INVENTORY.FILM_ID)))
            .where(FILM.TITLE.contains(filmTitle))
            .orderBy(
                rentalDurationInfoSubquery.field(averageRentalDurationAlias).desc().nullsLast())
            .fetchInto(FilmRentalSummary.class);
    }

    public List<Film> findRentedFilmsByFilmTitle(String filmTitle) {
        return dslContext
            .select(FILM.fields())
            .from(FILM)
            .where(FILM.TITLE.contains(filmTitle))
            .andExists(DSL.selectOne()
                .from(INVENTORY)
                .leftJoin(RENTAL).on(RENTAL.INVENTORY_ID.eq(INVENTORY.INVENTORY_ID))
                .where(INVENTORY.FILM_ID.eq(FILM.FILM_ID))
                .and(RENTAL.RENTAL_DATE.isNotNull()))
            .fetchInto(Film.class);
    }
}
