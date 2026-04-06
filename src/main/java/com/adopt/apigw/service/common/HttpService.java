package com.adopt.apigw.service.common;

public class HttpService {

    public void post() {
		/*
		 *    	
	    	StringBuffer sb = new StringBuffer(jsonBody);
	    	sb=sb
	    	.append(route)
	    	.append(individual)
	    	.append(timeValidaties)
	    	.append(zoneCondition)
	    	.append(anyUEID)
	    	.append(end);
	    	
	    	jsonBody=sb.toString();
	    	
	    	System.out.print("Submit PFD Service: payload:" + jsonBody);
	    	//String apiURL="https://localhost:8443/transactions";
	    	Request request = null;
	    	RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);	    		
    		request = new Request.Builder()
    				.url(SUBMIT_SUBCRIPTION_URL)
    				.post(body)
    				.build();
    		
    		Response response = httpClient.newCall(request).execute();
    		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

    		// Get response headers
    		Headers responseHeaders = response.headers();
    		logger.info("Response Protocol:"+response.protocol());
    		logger.info("Response Headers:");
    		for (int i = 0; i < responseHeaders.size(); i++) {
    			logger.info(responseHeaders.name(i) + ": " + responseHeaders.value(i));
    		}

    		// Get response body
    		String answer=response.body().string();
		 */
    }
}
