package com.adopt.apigw.modules.LocationMaster.service;


import com.adopt.apigw.modules.LocationMaster.domain.LocationMaster;
import com.adopt.apigw.modules.LocationMaster.domain.LocationMasterMapping;
import com.adopt.apigw.modules.LocationMaster.module.LocationMasterDto;
import com.adopt.apigw.modules.LocationMaster.module.LocationMasterMappingDto;
import com.adopt.apigw.modules.LocationMaster.module.UpdateLocationMasterDto;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.HashMap;
import java.util.List;

public interface LocationMasterService {
	LocationMaster saveLocationMaster(LocationMasterDto locationmasterDto, Long mvnoId);

	PageableResponse<LocationMaster> findAllLocationMaster(Long mvnoId, String name, PaginationDTO paginationDTO);

	LocationMaster findlocationMasterById(Long locationMasterId, Long mvnoId);

	LocationMaster updateLocation(UpdateLocationMasterDto locationDto, Long mvnoId);

	void deleteLocationById(Long locationMasterId, Long mvnoId);

	List<LocationMaster> findLocation(String name, Long mvnoId);

	String updateLocationStatus(String name, String status, Long mvnoId);

	List<LocationMasterMappingDto> getAllMacFromLocations(@Param("locationIds") List<Long> locationIds, Boolean isParentLocation);

	List<LocationMaster> getLocationFromMac(@Param("mac") String mac);

	List<LocationMasterMapping> saveLocationMasterMapping(List<LocationMasterMappingDto> locationMasterMappingDtos, LocationMaster locationMaster);

	//List<LocationMaster> findLocationByPlan(Long planId, Long mvnoId);
}
