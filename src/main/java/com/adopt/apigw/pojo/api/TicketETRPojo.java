package com.adopt.apigw.pojo.api;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;

@Data
@NoArgsConstructor
@Getter
@Setter
public class TicketETRPojo {

    private String templateContent;

    private HashMap<String,Boolean> selectedNotificationType = new HashMap<>();

    private LocalTime notificationTime;

    private LocalDate notificationDate;

    private Integer staffId;

    private Integer custId;

    private Integer ticketId;

    private String ticketNumber;

    private String customerMobileNo;

    private String customerEmailId;

    private Integer mvnoId;

    private String  status;

    private String  sender;

    private Boolean  isTemplateDynamic;

    private String remark;


}
