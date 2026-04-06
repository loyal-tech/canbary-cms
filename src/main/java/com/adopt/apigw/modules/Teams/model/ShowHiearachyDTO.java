package com.adopt.apigw.modules.Teams.model;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShowHiearachyDTO implements IBaseDto  {

    private Long id;

    private String hierarchyName;

    private String eventName;

    private Map<String, String> teamsSet;

    private Integer mvnoId;

    private Long buId;



    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }
}
