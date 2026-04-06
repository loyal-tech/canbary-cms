package com.adopt.apigw.modules.InventoryManagement.ItemGroup;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import com.adopt.apigw.modules.InventoryManagement.item.ItemRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ItemAssemblyServiceImp extends ExBaseAbstractService<ItemAssemblyDto, ItemAssembly,Long> {

    @Autowired
    ItemAssemblyRepo itemAssemblyRepo;

    @Autowired
    ItemAssemblyMapper itemAssemblyMapper;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemAssemblyProductMappingRepo itemAssemblyProductMappingRepo;



    public ItemAssemblyServiceImp(ItemAssemblyRepo itemAssemblyRepo, IBaseMapper<ItemAssemblyDto, ItemAssembly> mapper) {
        super(itemAssemblyRepo, mapper);
    }


    @Override
    @Transactional
    public ItemAssemblyDto saveEntity(ItemAssemblyDto groupDto) {
        try {
            boolean flag = duplicateVerifyAtSave(groupDto.getItemAssemblyName());
            if (flag) {
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    groupDto.setMvnoId(getMvnoIdFromCurrentStaff(null));
                }
                ItemAssemblyDto itemAssemblyDto = super.saveEntity(groupDto);
                List<ItemAssemblyProductMapping> itemAssemblyProductMappings = itemAssemblyProductMappingRepo.findAllByItemAssemblyId(itemAssemblyDto.getId());
                itemAssemblyProductMappings.stream().forEach(r -> {
                    Item item = itemRepository.findById(r.getItemId()).get();
                    r.setProductId(item.getProductId());
                    itemAssemblyProductMappingRepo.save(r);
                });
                return itemAssemblyDto;

            } else {
                throw new RuntimeException(MessageConstants.ITEM_GROUP);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

        public GenericDataDTO getAllItemProduct(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            QItemAssembly  qItemAssembly = QItemAssembly.itemAssembly;
            BooleanExpression booleanExpression = qItemAssembly.isNotNull();
            booleanExpression = booleanExpression.and(qItemAssembly.isDeleted.eq(true).or(qItemAssembly.isDeleted.eq(false).or(qItemAssembly.status.eq("Approve").or(qItemAssembly.status.eq("Rejected").or(qItemAssembly.status.eq("Pending"))))));
            Page<ItemAssembly> page1 = itemAssemblyRepo.findAll(booleanExpression, pageRequest);
            page1.forEach(p -> {
                List<ItemAssemblyProductMapping> itemAssemblyProductMappingsList = itemAssemblyProductMappingRepo.findAllByItemAssemblyId(p.getId());
                List<String> setItemNameList = new ArrayList<>();
                itemAssemblyProductMappingsList.stream().forEach(r -> {
                    String itemName = itemRepository.findById(r.getItemId()).get().getMacAddress();
                    setItemNameList.add(itemName);
                });
                p.setItemNameList(setItemNameList);
            });

            if (null != page && 0 < page1.getSize()) {
                makeGenericResponse(genericDataDTO, page1);
            }

            return genericDataDTO;
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }

    }


    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [ search()] ";
        try {
            PageRequest pageRequest1 = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim() != null) {
                        return getItemGroupByName(searchModel.getFilterValue(), pageRequest1);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }


    public GenericDataDTO getItemGroupByName(String s1, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getItemGroupByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QItemAssembly qItemAssembly = QItemAssembly.itemAssembly;
            BooleanExpression booleanExpression = qItemAssembly.isNotNull()
                    .and(qItemAssembly.isDeleted.eq(false))
                    .and(qItemAssembly.itemAssemblyName.likeIgnoreCase("%" + s1 + "%"));

            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                booleanExpression = booleanExpression
                        .and(qItemAssembly.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));  // TODO: pass mvnoID manually 6/5/2025
            }
            Page<ItemAssembly> itemGroups = itemAssemblyRepo.findAll(booleanExpression, pageRequest);
            if (null != itemGroups && 0 < itemGroups.getSize()) {
                makeGenericResponse(genericDataDTO, itemGroups);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }


    @Override
    public  boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) {
                count = itemAssemblyRepo.duplicateVerifyAtSave(name);
            } else {
                count = itemAssemblyRepo.duplicateVerifyAtSave(name, mvnoIds);
            }

            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }



    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = itemAssemblyRepo.deleteVerify(id);
        if (count == 1) {
            flag = true;
        }
        return flag;
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }
}
