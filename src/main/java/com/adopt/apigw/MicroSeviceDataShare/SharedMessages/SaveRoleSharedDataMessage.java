package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.modules.acl.domain.CustomACLEntry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SaveRoleSharedDataMessage {
    private Long id;
    private String rolename;
    private String status;
    private Boolean sysRole = false;
    private List<CustomACLEntry> aclEntry;
    private Boolean isDelete;
    private Integer mvnoId;
    private Integer lcoId;
    private Integer createdById;
    private Integer lastModifiedById;
}
