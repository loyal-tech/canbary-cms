package com.adopt.apigw.pojo;

import com.adopt.apigw.model.common.CustomerNotes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerNotesDto {

    private Long id;

    private String notes;

    private Integer custId;
    private LocalDateTime createdOn;
    private String createdBy;
    private String createdByName;

    public CustomerNotesDto(CustomerNotes customerNotes) {
        this.id = customerNotes.getId();
        this.notes = customerNotes.getNotes();
        if(customerNotes.getCustomers() != null)
            this.custId = customerNotes.getCustomers().getId();
        this.createdOn = customerNotes.getCreatedOn();
        this.createdBy = customerNotes.getCreatedBy();
        this.createdByName = customerNotes.getCreatedByName();
    }

}