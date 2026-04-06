package com.adopt.apigw.modules.CafFollowUp.mapper;

import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.CafFollowUp.domain.CafFollowUp;
import com.adopt.apigw.modules.CafFollowUp.model.CafFollowUpDTO;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.StaffUserService;

@Mapper
public abstract class CafFollowUpMapper implements IBaseMapper<CafFollowUpDTO, CafFollowUp> {

	String MODULE = " [CafFollowUpMapper] ";

	@Autowired
	private StaffUserService staffUserService;
	@Autowired
	StaffUserRepository staffUserRepository;
	@Autowired
	private CustomersRepository customersRepository;
	
	@Autowired
	private CustomersService customersService;

	@Mapping(source = "customers", target = "customersId")
	@Mapping(source = "customers", target = "customersName")
	@Mapping(source = "staffUser", target = "staffUserId")
	@Mapping(source = "staffUser", target = "staffUserName")
	@Override
	public abstract CafFollowUpDTO domainToDTO(CafFollowUp data, @Context CycleAvoidingMappingContext context);

	@Mapping(source = "customersId", target = "customers")
	@Mapping(source = "staffUserId", target = "staffUser")
	@Override
	public abstract CafFollowUp dtoToDomain(CafFollowUpDTO dtoData, @Context CycleAvoidingMappingContext context);

	Integer fromStaffUserToStaffUserId(StaffUser entity) {
		return entity == null ? null : entity.getId();
	}
	
	String fromStaffUserToStaffUserName(StaffUser entity) {
		return entity == null ? null : entity.getFirstname()+" "+entity.getLastname();
	}

	StaffUser fromStaffUserIdToStaffUser(Integer entityId) {
		if (entityId == null) {
			return null;
		}
		StaffUser entity;
		try {
			entity = staffUserRepository.findById(entityId).get();
		} catch (Exception e) {
			e.printStackTrace();
			entity = null;
		}
		return entity;
	}

	Integer fromCustomersToCustomersId(Customers entity) {
		return entity == null ? null : entity.getId();
	}
	
	String fromCustomersToCustomersName(Customers entity) {
		return entity == null ? null : entity.getFirstname()+" "+entity.getLastname();
	}

	Customers fromCustomersIdToCustomers(Integer entityId) {
		if (entityId == null) {
			return null;
		}
		Customers entity;
		try {
			entity =  customersRepository.findById(entityId).get();
		} catch (Exception e) {
			e.printStackTrace();
			entity = null;
		}
		return entity;
	}
	
	@AfterMapping
	void afterMapping(@MappingTarget CafFollowUpDTO cafFollowUpDTO, CafFollowUp cafFollowUp) {
		try {
			if (cafFollowUp != null) {
				if (cafFollowUp.getCustomers() != null) {
					cafFollowUpDTO.setCustomersId(cafFollowUp.getCustomers().getId());
					cafFollowUpDTO.setCustomersName(
							cafFollowUp.getCustomers().getFirstname() + " " + cafFollowUp.getCustomers().getLastname());
				}
				if (cafFollowUp.getStaffUser() != null) {
					cafFollowUpDTO.setStaffUserId(cafFollowUp.getStaffUser().getId());
					cafFollowUpDTO.setStaffUserName(
							cafFollowUp.getStaffUser().getFirstname() + " " + cafFollowUp.getStaffUser().getLastname());
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
			ex.printStackTrace();
		}
	}
}
