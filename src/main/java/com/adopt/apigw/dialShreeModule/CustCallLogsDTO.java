package com.adopt.apigw.dialShreeModule;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustCallLogsDTO {
    @JsonProperty("uniqueid")
    private String uniqueId;

    @JsonProperty("call_start_time")
    private LocalDateTime callStartTime;

    @JsonProperty("call_end_time")
    private LocalDateTime callEndTime;

    @JsonProperty("call_duration")
    private Long callDuration;

    @JsonProperty("recording_url")
    private String recordingUrl;

    @JsonProperty("lead_id")
    private Long leadId;

    @JsonProperty("entry_date")
    private LocalDateTime entryDate;

    @JsonProperty("modify_date")
    private LocalDateTime modifyDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("user")
    private String user;

    @JsonProperty("vendor_lead_code")
    private String vendorLeadCode;

    @JsonProperty("source_id")
    private String sourceId;

    @JsonProperty("list_id")
    private Long listId;

    @JsonProperty("gmt_offset_now")
    private String gmtOffsetNow;

    @JsonProperty("called_since_last_reset")
    private String calledSinceLastReset;

    @JsonProperty("phone_code")
    private String phoneCode;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("title")
    private String title;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("middle_initial")
    private String middleInitial;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("address1")
    private String address1;

    @JsonProperty("address2")
    private String address2;

    @JsonProperty("address3")
    private String address3;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("province")
    private String province;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("date_of_birth")
    private String dateOfBirth;

    @JsonProperty("alt_phone")
    private String altPhone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("security_phrase")
    private String securityPhrase;

    @JsonProperty("comments")
    private String comments;

    @JsonProperty("call_notes")
    private String callNotes;

    @JsonProperty("called_count")
    private Long calledCount;

    @JsonProperty("last_local_call_time")
    private LocalDateTime lastLocalCallTime;

    @JsonProperty("rank")
    private Long rank;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("entry_list_id")
    private Long entryListId;

    @JsonProperty("callback_datetime")
    private String callbackDatetime;

    @JsonProperty("disposition_description")
    private String dispositionDescription;

    @JsonProperty("agent_full_name")
    private String agentFullName;

    @JsonProperty("campaign")
    private String campaign;

    @JsonProperty("call_type")
    private String callType;

    @JsonProperty("dynamic_data")
    private DynamicDataDTO dynamicDataDTO;

}
