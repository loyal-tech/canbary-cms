package com.adopt.apigw.modules.Template.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Template Dto",description = "This is template dto which is used to add and update template data")
public class TemplateNotificationDTO {

    @ApiModelProperty(notes = "This is event id",required = true)
    private Long eventId;
    @ApiModelProperty(notes = "This is template name",required = true)
    private String templateName;
    @ApiModelProperty(notes = "This is sms template data",required = true)
    private String smsTemplateData;
    @ApiModelProperty(notes = "This is SMS Gateway Template ID",required = true)
    private String appendUrl;
    @ApiModelProperty(notes = "This is flag to check whether event is configured for sms or not",required = true)
    private boolean smsEventConfigured;
    @ApiModelProperty(notes = "This is email template data",required = true)
    private String emailTemplateData;
    @ApiModelProperty(notes = "This is flag to check whether event is configured for email or not",required = true)
    private boolean emailEventConfigured;
    @ApiModelProperty(notes = "Status of the template",allowableValues = "Active,Inactive",  value = "This field accept value only : Active or Inactive",required = true)
    private String status;
    private Integer mvnoId;
}
