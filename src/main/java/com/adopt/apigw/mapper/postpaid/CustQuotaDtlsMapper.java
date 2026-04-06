package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.CustQuotaDetails;
import com.adopt.apigw.pojo.api.CustQuotaDtlsPojo;

@Mapper
public abstract class CustQuotaDtlsMapper implements IBaseMapper<CustQuotaDtlsPojo, CustQuotaDetails> {

    /*@Autowired
    private PostpaidPlanService postpaidPlanService;

    @Override
    @Mapping(target = "postpaidPlan", source = "planId")
    @Mapping(target = "custPlanMappping", source = "planId")
    public abstract CustQuotaDetails dtoToDomain(CustQuotaDtlsPojo pojo, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(target = "planId", source = "postpaidPlan")
    public abstract CustQuotaDtlsPojo domainToDTO(CustQuotaDetails domain, @Context CycleAvoidingMappingContext context);

    Integer fromPlanToId(PostpaidPlan entity) {
        return entity == null ? null : entity.getId();
    }

    PostpaidPlan fromIdToPlan(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        PostpaidPlan entity;
        try {
            entity = postpaidPlanService.get(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
*/
}
