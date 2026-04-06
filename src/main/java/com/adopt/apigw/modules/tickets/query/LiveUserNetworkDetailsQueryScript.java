package com.adopt.apigw.modules.tickets.query;

import lombok.Data;

@Data
public class LiveUserNetworkDetailsQueryScript {

    public static String activeNetworkUserDetailsByCustomer(Integer custId){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select serviceAreaId\n" +
                ", count(serviceAreaId) 'serviceAreaCount'\n" +
                ", oltId \n" +
                ", count(oltId) 'oltCount'\n" +
                ", slotId\n" +
                ", count(slotId) 'slotCount'\n" +
                "from vwlivsersnetwork  t\n" +
                "where  exists \n" +
                "(select * from tblcustomers cust where cust.custid = ").append(custId)
                .append("\nand t.serviceAreaId = cust.servicearea_id " +
                "and t.slotId = cust.oltslotid)\n group by serviceAreaId,oltId,slotId;");
        return stringBuilder.toString();
    }
}
