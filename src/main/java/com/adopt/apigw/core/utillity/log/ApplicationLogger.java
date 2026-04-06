package com.adopt.apigw.core.utillity.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public class ApplicationLogger {
    public static final Logger logger = LoggerFactory.getLogger(ApplicationLogger.class);
    public static final String START = " [START] ";
    public static final String END = " [END] ";
}
