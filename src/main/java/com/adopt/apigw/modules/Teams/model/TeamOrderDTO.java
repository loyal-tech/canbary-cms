package com.adopt.apigw.modules.Teams.model;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamOrderDTO implements IBaseDto {



    private  Long id;

    private Integer orederId;

    private Long teamId;

    private Integer mvnoId;

    private Integer nextTeamId;




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
