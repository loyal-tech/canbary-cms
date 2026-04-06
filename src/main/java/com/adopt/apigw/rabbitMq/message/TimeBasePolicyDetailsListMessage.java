package com.adopt.apigw.rabbitMq.message;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = TimeBasePolicyDetailsListMessage.class)
public class TimeBasePolicyDetailsListMessage {

    List<TimeBasePolicyDetailsMessage> timeBasePolicyDetailsMessageList;



}
