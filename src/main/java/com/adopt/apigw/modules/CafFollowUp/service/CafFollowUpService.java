package com.adopt.apigw.modules.CafFollowUp.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.CafFollowUp.domain.CafFollowUp;
import com.adopt.apigw.modules.CafFollowUp.domain.CafFollowUpAudit;
import com.adopt.apigw.modules.CafFollowUp.mapper.CafFollowUpMapper;
import com.adopt.apigw.modules.CafFollowUp.model.CafFollowUpDTO;
import com.adopt.apigw.modules.CafFollowUp.repository.CafFollowUpAuditRepository;
import com.adopt.apigw.modules.CafFollowUp.repository.CafFollowUpRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;

@Service
public class CafFollowUpService extends ExBaseAbstractService<CafFollowUpDTO, CafFollowUp, Long> {

	public CafFollowUpService(CafFollowUpRepository repository, CafFollowUpMapper mapper) {
		super(repository, mapper);
	}

	DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");

	@Autowired
	private CafFollowUpMapper cafFollowUpMapper;

	@Autowired
	private CafFollowUpRepository cafFollowUpRepository;

	@Autowired
	private CafFollowUpAuditRepository cafFollowUpAuditRepository;

	@Autowired
	private StaffUserRepository staffUserRepository;

	@Autowired
	private CustomersRepository customerRepository;
	
	@Override
	public String getModuleNameForLog() {
		return "[CafFollowUpService]";
	}

	@Transactional
	public GenericDataDTO save(CafFollowUpDTO cafFollowUpDTO) {
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			CafFollowUp cafFollowUp = this.cafFollowUpMapper.dtoToDomain(cafFollowUpDTO,
					new CycleAvoidingMappingContext());
			cafFollowUp.setStatus("Pending");
			CafFollowUp savedCafFollowUp = this.cafFollowUpRepository.save(cafFollowUp);
			// add schedule followup audit
			String name = savedCafFollowUp.getStaffUser().getFirstname() + " did " + savedCafFollowUp.getFollowUpName()
					+ " for customer on " + dateFormat.format(savedCafFollowUp.getCreatedOn());
			addAudit(savedCafFollowUp, savedCafFollowUp.getStaffUser(), name,
					savedCafFollowUp.getFollowUpName() + " has been Created");
			genericDataDTO.setResponseCode(HttpStatus.OK.value());
			genericDataDTO.setResponseMessage("Caf FollowUp has been schedule successfully");
			genericDataDTO
					.setData(this.cafFollowUpMapper.domainToDTO(savedCafFollowUp, new CycleAvoidingMappingContext()));
		} catch (Exception e) {
			ApplicationLogger.logger.error("[CafFollowUpService]" + e.getMessage(), e);
			e.printStackTrace();
			return genericDataDTO;
		}
		return genericDataDTO;
	}

	@Transactional
	public GenericDataDTO reSchedule(CafFollowUpDTO cafFollowUpDTO, Long followUpId, String remarks,
			Integer staffUserId) {
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			// close exsting followUp
			CafFollowUp exstingCafFollowUp = this.cafFollowUpRepository.findById(followUpId).get();
			exstingCafFollowUp.setStatus("Closed");
			exstingCafFollowUp.setRemarks(remarks);
			this.cafFollowUpRepository.save(exstingCafFollowUp);
			GenericDataDTO savedGenericDataDTO = save(cafFollowUpDTO);
			CafFollowUpDTO savedCafFollowUpDTO = (CafFollowUpDTO) savedGenericDataDTO.getData();
			genericDataDTO.setResponseCode(HttpStatus.OK.value());
			genericDataDTO.setResponseMessage("Caf FollowUp has been reschedule successfully");
			genericDataDTO.setData(savedCafFollowUpDTO);
			// add close and reSchedule followup audit
			Optional<StaffUser> optionalStaffUser = this.staffUserRepository.findById(staffUserId);
			if (optionalStaffUser.isPresent()) {
				StaffUser staffUser = optionalStaffUser.get();
				String closeAuditName = staffUser.getFirstname() + " closed  " + exstingCafFollowUp.getFollowUpName()
						+ " for customer on " + dateFormat.format(LocalDateTime.now());
				addAudit(exstingCafFollowUp, staffUser, closeAuditName,
						exstingCafFollowUp.getFollowUpName() + " has been Closed");
				String reScheduleAuditName = staffUser.getFirstname() + " reschedule  "
						+ exstingCafFollowUp.getFollowUpName() + " for customer on "
						+ dateFormat.format(LocalDateTime.now());
				addAudit(exstingCafFollowUp, staffUser, reScheduleAuditName,
						exstingCafFollowUp.getFollowUpName() + " has been Reschedule");
			}
		} catch (Exception e) {
			ApplicationLogger.logger.error("[CafFollowUpService]" + e.getMessage(), e);
			e.printStackTrace();
			return genericDataDTO;
		}
		return genericDataDTO;
	}

	@Transactional
	public GenericDataDTO closefollowup(Long followUpId, String remarks, Integer staffUserId) {
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			// close exsting followUp
			CafFollowUp exstingCafFollowUp = this.cafFollowUpRepository.findById(followUpId).get();
			exstingCafFollowUp.setStatus("Closed");
			exstingCafFollowUp.setRemarks(remarks);
			this.cafFollowUpRepository.save(exstingCafFollowUp);
			// add close followup audit
			Optional<StaffUser> optionalStaffUser = this.staffUserRepository.findById(staffUserId);
			if (optionalStaffUser.isPresent()) {
				StaffUser staffUser = optionalStaffUser.get();
				String closeAuditName = staffUser.getFirstname() + " closed  " + exstingCafFollowUp.getFollowUpName()
						+ " for customer on " + dateFormat.format(LocalDateTime.now());
				addAudit(exstingCafFollowUp, staffUser, closeAuditName,
						exstingCafFollowUp.getFollowUpName() + " has been Closed");
			}
			genericDataDTO.setResponseCode(HttpStatus.OK.value());
			genericDataDTO.setResponseMessage("Caf FollowUp has been closed successfully");
		} catch (Exception e) {
			ApplicationLogger.logger.error("[CafFollowUpService]" + e.getMessage(), e);
			e.printStackTrace();
			return genericDataDTO;
		}
		return genericDataDTO;
	}

	public GenericDataDTO getAllByCustomerId(Integer customerId, PaginationRequestDTO paginationRequestDTO) {
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			PageRequest pageRequest = generatePageRequest(paginationRequestDTO.getPage(),
					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
					paginationRequestDTO.getSortOrder());
			Page<CafFollowUp> cafFollowUpPage = this.cafFollowUpRepository.findByCustomersId(customerId, pageRequest);
			Page<CafFollowUpDTO> cafFollowUpDTOPage = cafFollowUpPage
					.map(data -> this.cafFollowUpMapper.domainToDTO(data, new CycleAvoidingMappingContext()));
			genericDataDTO.setDataList(cafFollowUpDTOPage.getContent());
			genericDataDTO.setCurrentPageNumber(paginationRequestDTO.getPage());
			genericDataDTO.setTotalPages(cafFollowUpDTOPage.getTotalPages());
			genericDataDTO.setTotalRecords(cafFollowUpDTOPage.getTotalElements());
			genericDataDTO.setResponseCode(HttpStatus.OK.value());
			genericDataDTO.setResponseMessage("Fetching All CafFollowUp With id " + customerId);
		} catch (Exception e) {
			ApplicationLogger.logger.error("[CafFollowUpService]" + e.getMessage(), e);
			e.printStackTrace();
			return genericDataDTO;
		}
		return genericDataDTO;
	}

	public void addAudit(CafFollowUp cafFollowUp, StaffUser staffUser, String name, String auditName) {
		CafFollowUpAudit cafFollowUpAudit = new CafFollowUpAudit();
		cafFollowUpAudit.setName(name);
		cafFollowUpAudit.setAuditName(auditName);
		cafFollowUpAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
		cafFollowUpAudit.setCustomerId(cafFollowUp.getCustomers().getId());
		cafFollowUpAuditRepository.save(cafFollowUpAudit);
	}

	public CafFollowUp get(Long id) {
		return this.cafFollowUpRepository.findById(id).orElse(null);
	}
	
	public Page<CafFollowUp> findByIsMissedAndIsSendAndStatus(Pageable pageable) {
		return this.cafFollowUpRepository.findByIsMissedAndIsSendAndStatus(false,false,"Pending", pageable);
	}
	
	public GenericDataDTO generateNameOfTheFollowUp(Integer customerId) {
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			String generatedNameOfTheCafFollowUp = "";
			Optional<Customers> customers = this.customerRepository.findById(customerId);
			if (customers.isPresent()) {
				CafFollowUp cafFollowUp = this.cafFollowUpRepository.findTopByOrderByIdDesc();
				if (cafFollowUp != null) {
					int num = cafFollowUp.getId().intValue() + 1;
					generatedNameOfTheCafFollowUp = customers.get().getFirstname() + "_CAFFollowup" + num;
				}else {
					generatedNameOfTheCafFollowUp = customers.get().getFirstname() + "_CAFFollowup" + 1;
				}
				genericDataDTO.setData(generatedNameOfTheCafFollowUp);
				genericDataDTO.setResponseCode(HttpStatus.OK.value());
				genericDataDTO.setResponseMessage("Successfully");
			}else {
				genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				genericDataDTO.setResponseMessage("Customer not found for ID : "+customerId);
			}
			return genericDataDTO;
		} catch (Exception e) {
			ApplicationLogger.logger.error("[CafFollowUpService]" + e.getMessage(), e);
		}
		return null;
	}
}
