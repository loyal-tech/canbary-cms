package com.adopt.apigw.AirtelAppToCRM.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStatusDTO {

    @JsonProperty("AccountNo")
    private String AccountNo;

    @JsonProperty("PhoneNumber")
    private String PhoneNumber;

    @JsonProperty("Name")
    private String Name;

    @JsonProperty("Email")
    private String Email;

    public TransactionStatusDTO(String AccountNo, String PhoneNumber, String Name, String Email) {
        this.AccountNo = AccountNo;
        this.PhoneNumber = PhoneNumber;
        this.Name = Name;
        this.Email = Email;
    }
}
