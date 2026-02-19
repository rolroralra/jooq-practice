package com.example.jooqpractice.utils.jooq;

import java.util.List;
import lombok.NoArgsConstructor;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class JooqListConditionUtil {
    public static <T> Condition inIfNotEmpty(Field<T> actorId, List<T> actorIds) {
        if (CollectionUtils.isEmpty(actorIds)) {
            return DSL.noCondition();
        }

        return actorId.in(actorIds);
    }
}
