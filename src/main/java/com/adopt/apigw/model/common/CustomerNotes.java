package com.adopt.apigw.model.common;

import com.adopt.apigw.pojo.CustomerNotesDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTCUSTOMERNOTES")
public class CustomerNotes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_notes_id", nullable = false)
    private Long id;

    @Column(name = "notes", nullable = false)
    private String notes;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custid")
    private Customers customers;

    @CreationTimestamp
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_by_name")
    private String createdByName;


    public CustomerNotes(CustomerNotesDto customerNotesDto, Long staffId){
        this.id = customerNotesDto.getId();
        this.notes = customerNotesDto.getNotes();
        if(customerNotesDto.getCustId() != null)
            this.customers = new Customers(customerNotesDto.getCustId());
        this.createdOn = customerNotesDto.getCreatedOn();
        this.createdBy = String.valueOf(staffId);
        this.createdByName = customerNotesDto.getCreatedByName();
    }
}
