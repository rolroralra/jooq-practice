package com.example.jooqpractice.web;

import org.springframework.data.domain.Pageable;

public record PagedResponse(
    long page,
    long pageSize
) {
    public static PagedResponse of(Pageable pageable) {
        return new PagedResponse(
            pageable.getPageNumber(),
            pageable.getPageSize()
        );
    }
}
