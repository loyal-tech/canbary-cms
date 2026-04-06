package com.adopt.apigw.dialShreeModule;

import lombok.Data;
import java.time.LocalDateTime;


@Data
public class CustCallDTO {
        private String user;
        private String phoneCode;
        private String phoneNumber;
        private LocalDateTime entryDate;
        private LocalDateTime callStartTime;
        private LocalDateTime callEndTime;
        private String uniqueId;

        public CustCallDTO(String user, String phoneCode, String phoneNumber,
                           LocalDateTime entryDate, LocalDateTime callStartTime,
                           LocalDateTime callEndTime, String uniqueId) {
            this.user = user;
            this.phoneCode = phoneCode;
            this.phoneNumber = phoneNumber;
            this.entryDate = entryDate;
            this.callStartTime = callStartTime;
            this.callEndTime = callEndTime;
            this.uniqueId = uniqueId;
        }
}


