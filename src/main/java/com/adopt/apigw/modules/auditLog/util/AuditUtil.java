package com.adopt.apigw.modules.auditLog.util;

import org.springframework.data.repository.CrudRepository;

import java.lang.reflect.Field;

public class AuditUtil {

    // Inject your generic repository through constructor or @Autowired
    private final CrudRepository<Object, Long> genericRepository;

    // Constructor to inject the generic repository
    public AuditUtil(CrudRepository<Object, Long> genericRepository) {
        this.genericRepository = genericRepository;
    }

    public <T> T findEntityById(String className, Long entityId) throws ClassNotFoundException {
        try {
            // Dynamically create an instance of the entity class
            Class<?> entityClass = Class.forName(className);

            // Find the entity by ID using the generic repository
            Field idField = findIdField(entityClass);
            if (idField != null) {
                idField.setAccessible(true);
                return (T) genericRepository.findById(entityId).orElse(null);
            } else {
                // Handle entities without a valid ID field
                return null;
            }
        } catch (Exception e) {
            // Handle reflection or repository exceptions
            e.printStackTrace();
            return null;
        }
    }

    private Field findIdField(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(javax.persistence.Id.class)) {
                return field;
            } else if (field.getName().equalsIgnoreCase("id")) {
                return field;
            }
        }
        return null;
    }
}
