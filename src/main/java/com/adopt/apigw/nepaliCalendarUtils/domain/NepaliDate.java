package com.adopt.apigw.nepaliCalendarUtils.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "tblmnepalidate")
@ApiModel(value = "Customer Entity", description = "This is Nepali date entity which is used to fetch nepali data")
public class NepaliDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
	private Long id;
	
    @Column(nullable = false, length = 40)
	private String year;
	
    @Column(nullable = false, length = 40)
	private String days;
}
