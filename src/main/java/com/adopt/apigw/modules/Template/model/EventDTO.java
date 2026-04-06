package com.adopt.apigw.modules.Template.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    @ApiModelProperty(notes = "This is event name",required = true)
    private String eventName;
    @ApiModelProperty(notes = "This is event type",allowableValues = "Schedule,Trigger",  value = "This field accept value only : Schedule or Trigger",required = true)
    private String eventType;
    @ApiModelProperty(notes = "This is event description",required = false)
    private String description;
    @ApiModelProperty(notes = "Status of the template",allowableValues = "Active,Inactive",  value = "This field accept value only : Active or Inactive",required = true)
    private String status;
}
