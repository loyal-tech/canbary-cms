package com.adopt.apigw.model.radius;

import lombok.Data;

@Data
public class DbCDRProcessingQueryScript {

    public static final String SELECT_QUERY = " select t.CDRID 'id' from tblacctcdr t ";
    public static final String WHERE = " WHERE ";
    public static final String USERNAME = " t.UserName = '";
    public static final String ACCT_STATUS_TYPE = " t.AcctStatusType = '";
    public static final String CREATE_DATE_BETWEEN = " t.CREATE_DATE between ";
    
    public static final String AND = " AND ";

}
