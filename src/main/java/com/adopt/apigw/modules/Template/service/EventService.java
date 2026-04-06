package com.adopt.apigw.modules.Template.service;

import com.adopt.apigw.constants.NotificationConstants;
import com.adopt.apigw.modules.Template.domain.Event;
import com.adopt.apigw.modules.Template.domain.QEvent;
import com.adopt.apigw.modules.Template.model.EventDTO;
import com.adopt.apigw.modules.Template.repository.EventRepository;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private static final String SCHEDULE = "Schedule";
    private static final String TRIGGER = "Trigger";
    @Autowired
    EventRepository eventRepository;
    @Autowired
    NotificationTemplateRepository templateRepository;


    public Event findEventById(Long id) {
        try {
            Optional<Event> event = eventRepository.findById(id);
            if (event.isPresent()) {
                return event.get();
            } else {
                throw new IllegalArgumentException(
                        "No record found with event id " + id + " . Please enter valid event id.");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public List<Event> findAllEvents(Long mvnoId)
    {
        try
        {
            if(!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId))
            {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            }
            else
            {
                if(mvnoId == 1)
                {
                    return eventRepository.findAll();
                }
                else
                {
                    QEvent qEvent = QEvent.event;
                    BooleanExpression boolExp = qEvent.isNotNull();
                    boolExp = boolExp.and(qEvent.mvnoId.eq(mvnoId).or(qEvent.mvnoId.eq(1L)));
                    return  (List<Event>) eventRepository.findAll(boolExp);
                }
            }
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }


    public Event saveEvent(EventDTO eventDto)
    {
        try
        {
            Event event = validateEventData(eventDto);
            event.setCreateDate(new Timestamp(new Date().getTime()));
            event.setLastModificationDate(new Timestamp(new Date().getTime()));
            return eventRepository.save(event);
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Event validateEventData(EventDTO eventDto)
    {
        try
        {
            if(!ValidateCrudTransactionData.validateStringTypeFieldValue(eventDto.getEventName()))
            {
                throw new RuntimeException(NotificationConstants.BASIC_STRING_MSG+"Event name is mandatory. Please enter valid event name.");
            }
            else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(eventDto.getEventType()))
            {
                throw new RuntimeException(NotificationConstants.BASIC_STRING_MSG+"Event type is mandatory. Please enter valid event type.");
            }
            else if(!eventDto.getEventType().equals(SCHEDULE) && !eventDto.getEventType().equals(TRIGGER))
            {
                throw new RuntimeException("Please enter valid event type. It should be "+SCHEDULE+" OR "+TRIGGER+".");
            }
            else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(eventDto.getStatus()))
            {
                throw new RuntimeException(NotificationConstants.BASIC_STRING_MSG+"Event status is mandatory. Please enter valid event status.");
            }
            else if(!eventDto.getStatus().equals(NotificationConstants.ACTIVE) && !eventDto.getStatus().equals(NotificationConstants.IN_ACTIVE))
            {
                throw new RuntimeException("Please enter valid event status. It should be "+NotificationConstants.ACTIVE+" OR "+NotificationConstants.IN_ACTIVE+".");
            }
            Event event = new Event(eventDto);
            return event;
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }


    public Event udpateEvent(EventDTO eventDto)
    {
        try
        {
            Event event = validateEventData(eventDto);
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventDto.getEventName());
            if(!optionalEvent.isPresent())
            {
                throw new RuntimeException("No record found with event name '"+eventDto.getEventName()+"', Please enter valid event name to update the event record.");
            }
            else
            {
                event.setEventId(optionalEvent.get().getEventId());
                event.setCreateDate(optionalEvent.get().getCreateDate());
                event.setLastModificationDate(new Timestamp(new Date().getTime()));
                return eventRepository.save(event);
            }
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }


    public void deleteEvent(Long eventId)
    {
        try
        {
            eventRepository.deleteById(eventId);
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }


    public List<Event> findByName(String name)
    {
        try
        {
            if(StringUtils.isBlank(name) || name.equalsIgnoreCase("null"))
            {
                return eventRepository.findAll();
            }
            else
            {
                return eventRepository.findByEventNameContaining(name);
            }
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }
}
