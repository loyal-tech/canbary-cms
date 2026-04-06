package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.postpaid.Location;
import com.adopt.apigw.pojo.api.LocationPojo;
import com.adopt.apigw.repository.postpaid.LocationRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UpdateDiffFinder;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class LocationService extends AbstractService<Location, LocationPojo, Integer> {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository<Location, Integer> getRepository() {
        return locationRepository;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Location', '1')")
    public Page<Location> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return locationRepository.searchEntity(searchText, pageRequest);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Location', '1')")
    public List<Location> getAllActiveEntities() {
        return locationRepository.findByStatus("Y");
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Location', '1')")
    public List<Location> getAllEntities() {
        return locationRepository.findAll();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Location', '2')")
    public Location getLocationForAdd() {
        return new Location();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Location', '2')")
    public Location getLocationForEdit(Integer id) {
        return locationRepository.getOne(id);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Location', '4')")
    public void deleteLocation(Integer id) throws Exception {
        locationRepository.deleteById(id);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Location', '2')")
    public Location saveLocation(Location location) {
        Location save = locationRepository.save(location);
        return save;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Location', '2')")
    public LocationPojo save(LocationPojo pojo) throws Exception {
        Location oldObj = null;
        if (pojo.getId() != null) {
            oldObj = locationRepository.findById(pojo.getId()).get();
        }
        Location obj = convertLocationPojoToLocationModel(pojo);
        if(oldObj!=null) {
            log.info("Location update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
        }
        obj = saveLocation(obj);
        pojo = convertLocationModelToLocationPojo(obj);
        return pojo;
    }

    public LocationPojo convertLocationModelToLocationPojo(Location location) throws Exception {
        LocationPojo pojo = null;
        if (location != null) {
            pojo = new LocationPojo();
            pojo.setId(location.getId());
            pojo.setName(location.getName());
            pojo.setStatus(location.getStatus());
            pojo.setIsDelete(location.getIsDelete());
        }
        return pojo;
    }

    public Location convertLocationPojoToLocationModel(LocationPojo pojo) throws Exception {
        Location location = null;
        if (pojo != null) {
            location = new Location();
            if (pojo.getId() != null) {
                location.setId(pojo.getId());
            }
            location.setName(pojo.getName());
            location.setStatus(pojo.getStatus());
        }
        return location;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Location', '1')")
    public List<LocationPojo> convertResponseModelIntoPojo(List<Location> locationList) throws Exception {
        List<LocationPojo> pojoListRes = new ArrayList<LocationPojo>();
        if (locationList != null && locationList.size() > 0) {
            for (Location location : locationList) {
                pojoListRes.add(convertLocationModelToLocationPojo(location));
            }
        }
        return pojoListRes;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Location', '1')")
    public List<LocationPojo> convertResponseModelIntoPojo(Set<Location> locationList) throws Exception {
        List<LocationPojo> pojoListRes = new ArrayList<LocationPojo>();
        if (locationList != null && locationList.size() > 0) {
            for (Location location : locationList) {
                pojoListRes.add(convertLocationModelToLocationPojo(location));
            }
        }
        return pojoListRes;
    }


    public void validateRequest(LocationPojo pojo, Integer operation) {
        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation == CommonConstants.OPERATION_ADD) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
        if (!(pojo.getStatus().equalsIgnoreCase("Y") || pojo.getStatus().equalsIgnoreCase("N"))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_DELETE) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Location");
        List<LocationPojo> locationPojoList =  convertResponseModelIntoPojo(locationRepository.findAll());
        createExcel(workbook, sheet, LocationPojo.class, locationPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<LocationPojo> locationPojoList =  convertResponseModelIntoPojo(locationRepository.findAll());
        createPDF(doc, LocationPojo.class, locationPojoList, null);
    }
}
