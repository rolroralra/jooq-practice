package com.example.jooqpractice.film;

import com.example.jooqpractice.web.FilmWithActorPagedResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;

    public FilmWithActorPagedResponse getFilmActorPageResponse(Pageable pageable) {
        List<FilmWithActor> filmWithActorList = filmRepository.findFilmWithActorList(pageable);

        return FilmWithActorPagedResponse.of(pageable, filmWithActorList);
    }
}
