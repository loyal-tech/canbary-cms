package com.adopt.apigw.service.tacacs.Impl;

import com.adopt.apigw.converter.DtoConverter;
import com.adopt.apigw.exception.AccessLevelGroupNotFound;
import com.adopt.apigw.model.tacacs.AccessLevelGroupTacacs;
import com.adopt.apigw.rabbitMq.message.ReceiveAccessLevelGroupTacacsMessage;
import com.adopt.apigw.repository.tacacs.AccessLevelGroupTacacsRepository;
import com.adopt.apigw.repository.tacacs.TacacsSQLQueries;
import com.adopt.apigw.service.tacacs.AccessLevelGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
public class AccessLevelGroupTacacsServiceImpl implements AccessLevelGroupService {

    @Autowired
    private AccessLevelGroupTacacsRepository accessLevelGroupTacacsRepository;

    @Autowired
    DtoConverter<ReceiveAccessLevelGroupTacacsMessage, AccessLevelGroupTacacs> receiveTacacsMessageAccessLevelGroupTacacsDtoConverter;

    @Autowired
    TacacsSQLQueries sqlQueries;

    @Override
    public Page<AccessLevelGroupTacacs> getAllAccessLevelGroup(int page, int pageSize) throws AccessLevelGroupNotFound {
        long noOfAccessGroup = accessLevelGroupTacacsRepository.count();

        if (noOfAccessGroup > 0) {
            Pageable pageable = PageRequest.of(page, pageSize);
            return accessLevelGroupTacacsRepository.findAll(pageable);
        } else {
            log.error("There is no Access Group fields found in database. Please check.");
            throw new AccessLevelGroupNotFound("No Access Group found in database");
        }
    }

    @Override
    public AccessLevelGroupTacacs addAccessLevelGroup(ReceiveAccessLevelGroupTacacsMessage receiveAccessLevelGroupTacacsMessage) {
        AccessLevelGroupTacacs accessLevelGroupTacacs = null;
        try {
            if (receiveAccessLevelGroupTacacsMessage != null) {
                accessLevelGroupTacacs = receiveTacacsMessageAccessLevelGroupTacacsDtoConverter.convert(receiveAccessLevelGroupTacacsMessage, AccessLevelGroupTacacs.class);
                log.info("Creating tacacs level group.....");
                accessLevelGroupTacacsRepository.save(accessLevelGroupTacacs);
                log.info("Access level group staff user created successfully!");
            }
        } catch (RuntimeException e) {
            log.error("Error occurred while creating access level group, Reason is: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        log.info("Access Control Group Message {}", accessLevelGroupTacacs);
        return accessLevelGroupTacacs;
    }

    @Override
    public Map<String, Boolean> deleteAccessLevelGroupById(Long accessLvlId) throws AccessLevelGroupNotFound {
        Optional<AccessLevelGroupTacacs> accessLvlGrpExists = accessLevelGroupTacacsRepository.findById(accessLvlId);

        if (!accessLvlGrpExists.isPresent()) {
            throw new AccessLevelGroupNotFound("No Access Level Group found in the database");
        }
        Map<String, Boolean> response = new HashMap<>();
        sqlQueries.deleteData(accessLvlGrpExists.get().getAccessLevelGroupName());
        accessLevelGroupTacacsRepository.deleteById(accessLvlId);
        log.info("AccessLvlGrp of Id: {} is deleted successfully!", accessLvlId);
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    @Override
    public AccessLevelGroupTacacs updateAccessLevelGroup(Long accessLvlId, ReceiveAccessLevelGroupTacacsMessage receiveAccessLevelGroupTacacsMessage) throws AccessLevelGroupNotFound {
        Optional<AccessLevelGroupTacacs> accessLvlGrpExists = accessLevelGroupTacacsRepository.findById(accessLvlId);

        if (!accessLvlGrpExists.isPresent()) {
            throw new AccessLevelGroupNotFound("No Access Level Group found in the database");
        }

        // Update a new AccessLevelGroupMessage object
        AccessLevelGroupTacacs accessLevelGroup = accessLvlGrpExists.get();
        String existingAccessLevelGroupName = accessLvlGrpExists.get().getAccessLevelGroupName();

        //Check condition for access level group name and access level group id
        if (!receiveAccessLevelGroupTacacsMessage.getAccessLevelGroupName().equals(accessLevelGroup.getAccessLevelGroupName())) {
            accessLevelGroup.setAccessLevelGroupName(receiveAccessLevelGroupTacacsMessage.getAccessLevelGroupName());
        }

        if (!receiveAccessLevelGroupTacacsMessage.getAccessLevelGroupId().equals(accessLevelGroup.getAccessLevelGroupId())) {
            accessLevelGroup.setAccessLevelGroupId(receiveAccessLevelGroupTacacsMessage.getAccessLevelGroupId());
        }

        accessLevelGroupTacacsRepository.save(accessLevelGroup);
        accessLevelGroupTacacsRepository.flush();

        sqlQueries.updateData(accessLevelGroup.getAccessLevelGroupName(), existingAccessLevelGroupName);

        log.info("Updated access level group-------{}",accessLevelGroup);
        return accessLevelGroup;
    }
}
