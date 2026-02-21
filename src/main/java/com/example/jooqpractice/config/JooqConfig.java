package com.example.jooqpractice.config;

import org.jooq.conf.ExecuteWithoutWhere;
import org.jooq.conf.RenderImplicitJoinType;
import org.springframework.boot.jooq.autoconfigure.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {
    @Bean
    public DefaultConfigurationCustomizer jooqDefaultConfigurationCustomizer() {
        return c -> {
            c.set(PerformanceListener::new);
            c.settings()
                .withExecuteDeleteWithoutWhere(ExecuteWithoutWhere.THROW)
                .withExecuteUpdateWithoutWhere(ExecuteWithoutWhere.THROW)
                .withRenderImplicitJoinToManyType(RenderImplicitJoinType.LEFT_JOIN)
//                .withRenderImplicitJoinType(RenderImplicitJoinType.LEFT_JOIN)
                .withRenderSchema(false);
        };
    }
}
