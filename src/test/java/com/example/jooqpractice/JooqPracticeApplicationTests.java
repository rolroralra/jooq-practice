package com.example.jooqpractice;

import com.example.jooqpractice.tables.JActor;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JooqPracticeApplicationTests {
    @Autowired
    DSLContext dslContext;

    @Test
    void contextLoads() {
        dslContext.selectFrom(JActor.ACTOR)
            .limit(10)
            .fetch();

    }

}
