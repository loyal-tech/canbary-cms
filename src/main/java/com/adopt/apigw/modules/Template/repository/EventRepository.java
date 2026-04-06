package com.adopt.apigw.modules.Template.repository;

import com.adopt.apigw.modules.Template.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Optional<Event> findByEventName(String eventName);
    List<Event> findByEventNameContaining(String eventName);
}
