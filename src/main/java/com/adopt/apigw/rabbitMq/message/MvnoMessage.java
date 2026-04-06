package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MvnoMessage {

    private Long id;
	
    private String name;

	private String username;

	private String password;
	
    private String suffix;

    private String description;
	
    private String email;
	
    private String phone;
    
	private String status;
    

	
	private String logfile;
	
	private String mvnoHeader;
	
	private String mvnoFooter;
	
    private Boolean isDelete;
    private byte[] profileImage;
    private String logo_file_name;
}
