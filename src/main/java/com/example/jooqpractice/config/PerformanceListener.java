package com.example.jooqpractice.config;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Query;
import org.jooq.tools.StopWatch;

@Slf4j
public class PerformanceListener implements ExecuteListener {
    private transient StopWatch stopWatch;

    private static final Duration SLOW_QUERY_LIMIT = Duration.ofSeconds(3L);

    @Override
    public void executeStart(ExecuteContext ctx) {
        stopWatch = new StopWatch();
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {
        long queryTimeNanoSeconds = stopWatch.split();

        if (queryTimeNanoSeconds > SLOW_QUERY_LIMIT.getNano()) {
            Query query = ctx.query();

            Duration executionTime = Duration.ofNanos(queryTimeNanoSeconds);

            log.warn(
                """
                ### Slow SQL 탐지 >> 경고: jooq로 실행된 쿼리 중 {}초 이상 실행된 쿼리가 있습니다.
                실행시간: {}초
                실행쿼리:
                {}
                """, SLOW_QUERY_LIMIT.getSeconds(), executionTime.getSeconds(), query
            );
        }
    }
}
