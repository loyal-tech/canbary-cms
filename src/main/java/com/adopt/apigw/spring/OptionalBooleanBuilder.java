package com.adopt.apigw.spring;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Function;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;

public class OptionalBooleanBuilder {

    private BooleanExpression predicate;

    public OptionalBooleanBuilder(BooleanExpression predicate) {
        this.predicate = predicate;
    }

    public <T> OptionalBooleanBuilder notNullAnd(Function<T, BooleanExpression> expressionFunction, T value) {
        if (value != null && !"null".equals(value)) {
            return new OptionalBooleanBuilder(predicate.and(expressionFunction.apply(value)));
        }
        return this;
    }

    public OptionalBooleanBuilder notEmptyAnd(Function<String, BooleanExpression> expressionFunction, String value) {
        if (!StringUtils.isEmpty(value) && !"null".equals(value)) {
            return new OptionalBooleanBuilder(predicate.and(expressionFunction.apply(value)));
        }
        return this;
    }
    public OptionalBooleanBuilder notEmptyAnd(Function<Integer, BooleanExpression> expressionFunction, Integer value) {
        if (value !=null && !"null".equals(value)) {
            return new OptionalBooleanBuilder(predicate.and(expressionFunction.apply(value)));
        }
        return this;
    }
    public OptionalBooleanBuilder notEmptyAnd(Function<LocalDateTime, BooleanExpression> expressionFunction, LocalDateTime value) {
        if (value !=null && !"null".equals(value)) {
            return new OptionalBooleanBuilder(predicate.and(expressionFunction.apply(value)));
        }
        return this;
    }
    public OptionalBooleanBuilder notEmptyAnd(Function<Date, BooleanExpression> expressionFunction, Date value) {
        if (value !=null && !"null".equals(value)) {
            return new OptionalBooleanBuilder(predicate.and(expressionFunction.apply(value)));
        }
        return this;
    }

    public OptionalBooleanBuilder notEmptyAnd(Function<Time, BooleanExpression> expressionFunction, Time value) {
        if (value !=null && !"null".equals(value)) {
            return new OptionalBooleanBuilder(predicate.and(expressionFunction.apply(value)));
        }
        return this;
    }
    public OptionalBooleanBuilder notEmptyAnd(Function<Double, BooleanExpression> expressionFunction, Double value) {
        if (value !=null && !"null".equals(value)) {
            return new OptionalBooleanBuilder(predicate.and(expressionFunction.apply(value)));
        }
        return this;
    }

    public <T> OptionalBooleanBuilder notEmptyAnd(Function<Collection<T>, BooleanExpression> expressionFunction, Collection<T> collection) {
        if (!CollectionUtils.isEmpty(collection)) {
            return new OptionalBooleanBuilder(predicate.and(expressionFunction.apply(collection)));
        }
        return this;
    }

    public BooleanExpression build() {
        return predicate;
    }
}