package com.adopt.apigw.pojo;

import java.util.List;

import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendLeadDocConvertPojo {

	private List<CustomerDocDetailsDTO> customerDocDetailsDTOList;
}
