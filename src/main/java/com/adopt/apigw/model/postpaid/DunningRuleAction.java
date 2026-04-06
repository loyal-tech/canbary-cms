package com.adopt.apigw.model.postpaid;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tbldunruleaction")
public class DunningRuleAction {
	
	
	/*
create table tbldunruleaction
(
	actionid serial primary key,
	druleid BIGINT UNSIGNED NOT NULL,
	days numeric(4),
	emailsub varchar(200),
	daction varchar(50),
	foreign key (druleid) references tbldunningrules(druleid)
); 
	 */

    public DunningRuleAction() {
        super();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actionid", nullable = false, length = 40)
    private Integer id;


    @Column(name = "days", nullable = false, length = 40)
    private Integer days;

    @Column(name = "daction", nullable = false, length = 40)
    private String action;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "druleid")
    private DunningRule drule;

}
