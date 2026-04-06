package com.adopt.apigw.modules.Template.domain;

import com.adopt.apigw.modules.Template.model.TemplateNotificationDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLNOTIFICATIONEMPLATE")
@ApiModel(value = "Template Entity",description = "This is template entity")
public class TemplateNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated template id")
    @Column(name="templateid", nullable = false)
    private Long templateId;

    @JsonBackReference
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "eventid", nullable = false)
    private Event event;

    @ApiModelProperty(notes = "This is template name",required = true)
    @Column (name="templatename", length = 250, nullable = false)
    private String templateName;

    @ApiModelProperty(notes = "This is sms template data",required = true)
    @Column (name="smstemplatedata",nullable = false)
    private String smsTemplateData;

    @ApiModelProperty(notes = "This is flag to check whether event is configured for sms or not",required = true)
    @Column (name="smseventconfigured",nullable = false)
    private boolean smsEventConfigured;

    @ApiModelProperty(notes = "This is email template data",required = true)
    @Column (name="emailtemplatedata",nullable = false)
    private String emailTemplateData;

    @ApiModelProperty(notes = "This is flag to check whether event is configured for email or not",required = true)
    @Column (name="emaileventconfigured",nullable = false)
    private boolean emailEventConfigured;

    @ApiModelProperty(notes = "Status of the template",allowableValues = "Active,Inactive",  value = "This field accept value only : Active or Inactive",required = true)
    @Column (name="status", nullable = false , length = 10)
    private String status;

    @ApiModelProperty(hidden = true)
    @Column (name="createdate")
    private Timestamp createDate;

    @ApiModelProperty(hidden = true)
    @Column (name="lastmodificationdate")
    private Timestamp lastModificationDate;

    @ApiModelProperty(notes = "This is Gateway Template ID",required = false)
    @Column (name="appendurl")
    private String appendUrl;

    @Column (name="mvnoid", updatable = false)
    private Integer mvnoId;

    public TemplateNotification(TemplateNotificationDTO templateDto, Event event)
    {
        this.emailEventConfigured=templateDto.isEmailEventConfigured();
        this.smsEventConfigured=templateDto.isSmsEventConfigured();
        this.appendUrl=templateDto.getAppendUrl();
        this.status=templateDto.getStatus();
        this.smsTemplateData=templateDto.getSmsTemplateData();
        this.emailTemplateData=templateDto.getEmailTemplateData();
        this.templateName=templateDto.getTemplateName();
        this.event = event;

    }

    @Override
    public String toString() {
        return "TemplateNotification{" +
                "templateId=" + templateId +
                '}';
    }
}
