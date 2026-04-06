package com.adopt.apigw.modules.Template.domain;

import com.adopt.apigw.modules.Template.model.EventDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBLMNOTIFICATIONEVENT")
@ApiModel(value = "Event Entity",description = "This is Event entity which is used to add event data")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated event id")
    @Column(name="eventid", nullable = false)
    private Long eventId;

    @ApiModelProperty(notes = "This is event name",required = true)
    @Column (name="eventname", length = 250, nullable = false)
    private String eventName;

    @ApiModelProperty(notes = "This is event type",allowableValues = "Schedule,Trigger",  value = "This field accept value only : Schedule or Trigger",required = true)
    @Column (name="eventtype", length = 20, nullable = false)
    private String eventType;

    @ApiModelProperty(notes = "This is event description",required = false)
    @Column (name="description", length = 700, nullable = true)
    private String description;

    @ApiModelProperty(notes = "Status of the template",allowableValues = "Active,Inactive",  value = "This field accept value only : Active or Inactive",required = true)
    @Column (name="status", nullable = false , length = 250)
    private String status;

    @ApiModelProperty(hidden = true)
    @Column (name="createdate")
    private Timestamp createDate;

    @ApiModelProperty(hidden = true)
    @Column (name="lastmodificationdate")
    private Timestamp lastModificationDate;

    @JsonManagedReference
    @OneToOne(mappedBy = "event", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private TemplateNotification template;

    @ApiModelProperty(notes = "This is mvno id", required = true)
    @Column (name="mvnoid", nullable = false)
    private Long mvnoId;

//	@JsonManagedReference
//	@OneToOne(mappedBy = "event", fetch = FetchType.EAGER,
//            cascade = CascadeType.ALL)
//    private EmailTemplate emailTemplate;

    public Event(EventDTO eventDto)
    {
        this.eventName = eventDto.getEventName();
        this.eventType = eventDto.getEventType();
        this.description = eventDto.getDescription();
        this.status=eventDto.getStatus();
        //this.mvnoId=mvnoId;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                '}';
    }
}
