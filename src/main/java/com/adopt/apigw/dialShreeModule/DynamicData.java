package com.adopt.apigw.dialShreeModule;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmdynamicdata")
public class DynamicData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "log_id", referencedColumnName = "log_id")
    @JsonBackReference
    @ToString.Exclude
    private CustCallLogs callLogs;

    @Column(name = "lead_id")
    private String leadId;

    @Column(name = "disposition")
    private String disposition;

    @Column(name = "callback_date")
    private String callbackDate;

    @Column(name = "callback_datetime")
    private String callbackDatetime;

    @Column(name = "my_callback")
    private String myCallback;

    @Column(name = "select_country")
    private String selectCountry;

    @Column(name = "select_state")
    private String selectState;

    @Column(name = "select_city")
    private String selectCity;

    @Column(name = "dial_button_field")
    private String dialButtonField;


}
