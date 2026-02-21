package com.example.jooqpractice;

import com.example.jooqpractice.tables.JActor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("ExecutionListener를 통해 Slow Query를 탐지할 수 있다.")
    void testSlowQuery() {
        dslContext
            .select(DSL.field("SLEEP(4)"))
            .from(DSL.dual())
            .execute();
    }
}
