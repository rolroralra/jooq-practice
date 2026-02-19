package com.example.jooqpractice.actor;

import lombok.Builder;

@Builder
public record ActorUpdateRequest(
    String firstName,
    String lastName
) {

}
