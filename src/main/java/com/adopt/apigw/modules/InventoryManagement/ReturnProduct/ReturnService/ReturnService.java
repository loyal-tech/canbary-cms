package com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnService;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnDomain.ReturnDto;
import com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnModel.Return;
import com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnRepository.ReturnRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReturnService extends ExBaseAbstractService<ReturnDto, Return, Long> {

    public ReturnService(JpaRepository<Return, Long> repository, IBaseMapper<ReturnDto, Return> mapper) {
        super(repository, mapper);
    }

    @Autowired
    private ReturnRepo returnRepo;

    public ReturnDto saveReturn(ReturnDto returnDto) throws Exception{

        Return aReturn = new Return();
        //aReturn.setId(returnDto.getId());
        aReturn.setProduct_name(returnDto.getProduct_name());
        aReturn.setMac_name(returnDto.getMac_name());
        aReturn.setSerial_no(returnDto.getSerial_no());
        aReturn.setItem_condition(returnDto.getItem_condition());
        aReturn.setProduct_id(returnDto.getProduct_id());
        aReturn.setCurrent_inward_id(returnDto.getCurrent_inward_id());
        aReturn.setCurrent_inward_type(returnDto.getCurrent_inward_type());
        aReturn.setItem_status(returnDto.getItem_status());
        aReturn.setCust_id(returnDto.getCust_id());
        returnRepo.save(aReturn);

        return returnDto;
    }

    public List<Return> getreturnforcustomer(Long id){
        List<Return> returns = new ArrayList<>();
        returns = returnRepo.getallforCustomer(id);
        return returns;
    }

    @Override
    public String getModuleNameForLog() {
        return "[ReturnService]";
    }
}
