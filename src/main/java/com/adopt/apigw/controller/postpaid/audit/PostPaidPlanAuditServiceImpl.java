package com.adopt.apigw.controller.postpaid.audit;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.pojo.PlanAudit;
import com.adopt.apigw.repository.PlanAuditRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

import static com.adopt.apigw.utils.UpdateDiffFinder.isExcludedProperty;

@Service
public class PostPaidPlanAuditServiceImpl implements PostPaidPlanAuditService {

    private static final Logger log = LoggerFactory.getLogger(PostPaidPlanAuditServiceImpl.class);
    @Autowired
    private PlanAuditRepository planRepo;

    public PageableResponse getPlanAudit(Integer loggedInMvno, PaginationDTO paginationDTO) {
        try {
            Page<PlanAudit> planManagementPage = null;

            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "id"));
            planManagementPage = planRepo.findAll(pageable);
            PageableResponse<PlanAudit> pageableResponse = new PageableResponse<>();
            return pageableResponse.convert(new PageImpl<PlanAudit>(planManagementPage.getContent(), pageable, planManagementPage.getTotalElements()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public boolean updatePostpaidPlan(PostpaidPlan existingPlan, PostpaidPlan updatedPlan, Integer staffId, String username) {
        // Compare and create an audit record if there are changes
        if (!existingPlan.equals(updatedPlan)) {
            String details = compareObjects(existingPlan, updatedPlan);
            System.out.println(details);
            PlanAudit planAudit = new PlanAudit(
                    existingPlan,
                    username,
                    "UPDATE",
                    staffId,
                    details,
                    "N/A"
            );

            // Save the audit record
//            planRepo.save(planAudit);
            return true;
        }
        return false;
    }

    public static String getUpdatedDiff(Object oldObj, Object newObj) {
        StringBuilder updated = new StringBuilder();

        try {
            Class<?> clazz = oldObj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            updated.append(" For: ").append(clazz.getSimpleName()).append(", ");

            int fieldCount = fields.length;

            int changesCount = 0;

            for (int i = 0; i < fieldCount; i++) {
                Field field = fields[i];
                field.setAccessible(true);

                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);

                // TODO: Customize the exclusion logic based on your requirements
                if ((oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue))) {
                    if (!isExcludedProperty(field.getName()) && !areEqual(oldValue, newValue)) {
                        updated.append(field.getName())
                                .append(" changes from ").append(oldValue)
                                .append(" to ").append(newValue);

                        changesCount++;

                        // Append " , and " if there are more changes
                        if (changesCount < countUpdatedFields(oldObj, newObj)) {
                            updated.append(" , and ");
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO: Handle exceptions more gracefully, log, or rethrow if needed
            return null;
        }

        // //		System.out.println("Custom changes updated >>>>>>>>>>>>>>>>>> " + updated.toString());
        return updated.toString();
    }

    private static boolean areEqual(Object obj1, Object obj2) {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }

    private static int countUpdatedFields(Object oldObj, Object newObj) throws IllegalAccessException {
        Class<?> clazz = oldObj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        int count = 0;

        for (Field field : fields) {
            field.setAccessible(true);

            Object oldValue = field.get(oldObj);
            Object newValue = field.get(newObj);

            if ((oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue))) {
                if (!isExcludedProperty(field.getName()) && !areEqual(oldValue, newValue)) {
                    count++;
                }
            }
        }

        return count;
    }


    public static String compareObjects(Object oldObj, Object newObj) {
        StringBuilder differences = new StringBuilder();
        int updatedFieldsCount = 0; // Counter for updated fields
        if (oldObj == null || newObj == null || !oldObj.getClass().equals(newObj.getClass())) {
            return "Objects are not comparable.";
        }

        Class<?> clazz = oldObj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // Access private fields
            try {
                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);

                if (!Objects.equals(oldValue, newValue)) {
                    // Handle collections or arrays separately
                    updatedFieldsCount++; // Increment the counter for each change
                    if (oldValue instanceof Collection<?> || newValue instanceof Collection<?>) {
                        differences.append(compareCollections(field.getName(), (Collection<?>) oldValue, (Collection<?>) newValue));
                    } else {
                        differences.append(String.format("Field '%s' changed: Old Value = %s, New Value = %s%n",
                                field.getName(), oldValue, newValue));
                    }
                }
            } catch (IllegalAccessException e) {
                differences.append(String.format("Could not access field '%s'.%n", field.getName()));
            }
        }
        differences.append(String.format("Total fields updated: %d%n", updatedFieldsCount)); // Append the count at the end
        return differences.toString();
    }

    private static String compareCollections(String fieldName, Collection<?> oldCollection, Collection<?> newCollection) {
        StringBuilder diff = new StringBuilder();
        diff.append(String.format("Field '%s' (Collection) changed:%n", fieldName));

        if (oldCollection == null) oldCollection = Collections.emptyList();
        if (newCollection == null) newCollection = Collections.emptyList();

        Set<Object> removed = new HashSet<>(oldCollection);
        removed.removeAll(newCollection);

        Set<Object> added = new HashSet<>(newCollection);
        added.removeAll(oldCollection);

        if (!removed.isEmpty()) {
            diff.append(String.format("  Removed: %s%n", removed));
        }
        if (!added.isEmpty()) {
            diff.append(String.format("  Added: %s%n", added));
        }

        return diff.toString();
    }
}
