package com.adopt.apigw.modules.CafFollowUp.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.CafFollowUp.domain.CafFollowUpAudit;
import com.adopt.apigw.modules.CafFollowUp.domain.CafFollowUpRemark;
import com.adopt.apigw.modules.CafFollowUp.mapper.CafFollowUpRemarkMapper;
import com.adopt.apigw.modules.CafFollowUp.model.CafFollowUpRemarkDTO;
import com.adopt.apigw.modules.CafFollowUp.repository.CafFollowUpAuditRepository;
import com.adopt.apigw.modules.CafFollowUp.repository.CafFollowUpRemarkRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;

@Service
public class CafFollowUpRemarkService extends ExBaseAbstractService<CafFollowUpRemarkDTO, CafFollowUpRemark, Long> {

	public CafFollowUpRemarkService(CafFollowUpRemarkRepository repository, CafFollowUpRemarkMapper mapper) {
		super(repository, mapper);
	}

	@Override
	public String getModuleNameForLog() {
		return "[CafFollowUpRemarkService]";
	}

	@Autowired
	private CafFollowUpRemarkRepository cafFollowUpRemarkRepository;

	@Autowired
	private CafFollowUpRemarkMapper cafFollowUpRemarkMapper;

	@Autowired
	private StaffUserRepository staffUserRepository;

	@Autowired
	private CafFollowUpAuditRepository cafFollowUpAuditRepository;

	@Transactional
	public GenericDataDTO save(CafFollowUpRemarkDTO cafFollowUpRemarkDTO, Integer staffUserId) {
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			CafFollowUpRemark cafFollowUpRemark = this.cafFollowUpRemarkMapper.dtoToDomain(cafFollowUpRemarkDTO,
					new CycleAvoidingMappingContext());
			CafFollowUpRemark savedCafFollowUpRemark = this.cafFollowUpRemarkRepository.save(cafFollowUpRemark);
			// add followup remark audit
			Optional<StaffUser> optionalStaffUser = this.staffUserRepository.findById(staffUserId);
			if (optionalStaffUser.isPresent()) {
				StaffUser staffUser = optionalStaffUser.get();
				String name = staffUser.getFirstname() + " added follow up remark in "
						+ savedCafFollowUpRemark.getCafFollowUp().getFollowUpName() + ".Remark: "
						+ savedCafFollowUpRemark.getRemark() + ".";
				addCafFollowUpRemarkAudit(savedCafFollowUpRemark.getCafFollowUp().getCustomers().getId(), staffUser,
						name, "Followup Remark Added");
			}
			genericDataDTO.setResponseCode(HttpStatus.OK.value());
			genericDataDTO.setResponseMessage("CafFollowUp Remark has been created successfully");
			genericDataDTO.setData(this.cafFollowUpRemarkMapper.domainToDTO(savedCafFollowUpRemark,
					new CycleAvoidingMappingContext()));
		} catch (Exception e) {
			ApplicationLogger.logger.error("[CafFollowUpRemarkService]" + e.getMessage(), e);
			e.printStackTrace();
			return genericDataDTO;
		}
		return genericDataDTO;
	}

	public void addCafFollowUpRemarkAudit(Integer customerId, StaffUser staffUser, String name, String auditName) {
		CafFollowUpAudit cafFollowUpAudit = new CafFollowUpAudit();
		cafFollowUpAudit.setName(name);
		cafFollowUpAudit.setAuditName(auditName);
		cafFollowUpAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
		cafFollowUpAudit.setCustomerId(customerId);
		this.cafFollowUpAuditRepository.save(cafFollowUpAudit);
	}

	public GenericDataDTO getAllByCafFollowUpId(Long cafFollowUpId) {
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			List<CafFollowUpRemark> cafFollowUpRemarkList = this.cafFollowUpRemarkRepository.findByCafFollowUpId(cafFollowUpId);
			genericDataDTO.setDataList(this.cafFollowUpRemarkMapper.domainToDTO(cafFollowUpRemarkList, new CycleAvoidingMappingContext()));
			genericDataDTO.setResponseCode(HttpStatus.OK.value());
			genericDataDTO.setResponseMessage("Fetching All CafFollowUpRemark With cafFollowUpId " + cafFollowUpId);
		} catch (Exception e) {
			ApplicationLogger.logger.error("[CafFollowUpRemarkService]" + e.getMessage(), e);
			e.printStackTrace();
			return genericDataDTO;
		}
		return genericDataDTO;
	}

}
