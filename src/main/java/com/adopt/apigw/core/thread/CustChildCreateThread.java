package com.adopt.apigw.core.thread;

import com.adopt.apigw.modules.childcustomer.dto.ChildCustPojo;
import com.adopt.apigw.modules.childcustomer.implemetation.ChildCustomerImpl;
import com.adopt.apigw.spring.LoggedInUser;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@NoArgsConstructor
@Service
public class CustChildCreateThread implements Runnable {

    private static final Log logger = LogFactory.getLog(CustChildCreateThread.class);
    private ChildCustomerImpl childCustomerimpl;
    private ChildCustPojo childCustPojo;
    private HttpServletRequest req;
    private SecurityContext securityContext;

    public CustChildCreateThread(ChildCustomerImpl childCustomerimpl, ChildCustPojo childCustPojo, HttpServletRequest req, SecurityContext securityContext) {
        this.childCustomerimpl = childCustomerimpl;
        this.childCustPojo = childCustPojo;
        this.req = req;
        this.securityContext = securityContext;
    }

    @Override
    public void run() {
        logger.info(":::::::::::::::::::::: Child Create Thread Started  :::::::::::::::::::::: ");
        try {
            long startTime = System.currentTimeMillis();
            SecurityContextHolder.setContext(securityContext);
            childCustomerimpl.create(childCustPojo,req);
            logger.warn(":::::::::::::::::::::: Child Create Thread Completed  :::::::::::::::::::::: "+ (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
