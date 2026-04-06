package com.adopt.apigw.dialShreeModule;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Access(AccessType.FIELD)
@Table(name = "tblmcalllogs")
public class CustCallLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(name = "uniqueid")
    private String uniqueId;

    @Column(name = "call_start_time")
    private LocalDateTime callStartTime;

    @Column(name = "call_end_time")
    private LocalDateTime callEndTime;

    @Column(name = "call_duration")
    private Long callDuration;

    @Column(name = "recording_url")
    private String recordingUrl;

    @Column(name = "lead_id")
    private String leadId;

    @Column(name = "entry_date")
    private LocalDateTime entryDate;

    @Column(name = "modify_date")
    private LocalDateTime modifyDate;

    @Column(name = "status")
    private String status;

    @Column(name = "user")
    private String user;

    @Column(name = "vendor_lead_code")
    private String vendorLeadCode;

    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "list_id")
    private Long listId;

    @Column(name = "gmt_offset_now")
    private String gmtOffsetNow;

    @Column(name = "called_since_last_reset")
    private String calledSinceLastReset;

    @Column(name = "phone_code")
    private String phoneCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "title")
    private String title;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_initial")
    private String middleInitial;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "address3")
    private String address3;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "province")
    private String province;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "gender")
    private String gender;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "alt_phone")
    private String altPhone;

    @Column(name = "email")
    private String email;

    @Column(name = "security_phrase")
    private String securityPhrase;

    @Column(name = "comments")
    private String comments;

    @Column(name = "call_notes")
    private String callNotes;

    @Column(name = "called_count")
    private Long calledCount;

    @Column(name = "last_local_call_time")
    private LocalDateTime lastLocalCallTime;

    @Column(name = "ranking")
    private Long rank;

    @Column(name = "owner")
    private String owner;

    @Column(name = "entry_list_id")
    private Long entryListId;

    @Column(name = "callback_datetime")
    private String callbackDatetime;

    @Column(name = "disposition_description")
    private String dispositionDescription;

    @Column(name = "agent_full_name")
    private String agentFullName;

    @Column(name = "campaign")
    private String campaign;

    @Column(name = "call_type")
    private String callType;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "callLogs")
    @JsonManagedReference
    @ToString.Exclude
    private DynamicData dynamicData;


}
