import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

class request {

    public static String requestToMTN(String amount , String transactionId , String currency, String fri){
        StringBuilder response = new StringBuilder();
        Integer exitCode = -1;
        try {
            String url = "curl --tlsv1.2 -H 'Content-Type: application/xml' -v -s -k -u wifi.admin:Wifiadmin@12345 --cacert /home/adoptminss/mTLS_Certs/RootCA.cer --cert /home/adoptminss/mTLS_Certs/mtnsshotspot.cer  --key /home/adoptminss/mTLS_Certs/adopt_server.key -H 'X-lwac-execute-as: ID:bill.mtn/USER' -d";

            String soapRequest = "'<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:debitrequest xmlns:ns2=\"http://www.ericsson.com/em/emm/financial/v1_1\"><fromfri>FRI:"+fri+"/MSISDN</fromfri><tofri>FRI:bill.mtn/USER</tofri><amount><amount>"+amount+"</amount><currency>"+currency+"</currency></amount><externaltransactionid>"+transactionId+"</externaltransactionid></ns2:debitrequest>' 'https://10.250.151.39:8014/pg/debit'";
            String curlCommand = url.concat(soapRequest);
            System.out.println(curlCommand);
            ProcessBuilder processBuilder = new ProcessBuilder(curlCommand.split("\\s+"));

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                response.append(line);
            }
            exitCode = process.waitFor();

            // Print the response

            System.out.println(processBuilder);
            System.out.println("Response: " + response.toString());

            // Print the exit code
            System.out.println("Exit Code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();


        }
        String  mokeresponse = "<ns8:debitresponse xmlns:ns6=\"http://www.ericsson.com/em/emm/financial/v1_1\" \n" +
                "xmlns:fic=\"http://www.ericsson.com/em/emm/financial/v1_0/common\"\n" +
                " xmlns:ns4=\"http://www.ericsson.com/em/emm/coupons/v1_0/common\"\n" +
                " xmlns:op=\"http://www.ericsson.com/em/emm/v1_0/common\" \n" +
                " xmlns:xs=\"http://www.w3.org/2001/xmlschema\"> \n" +
                " <transactionid>2661050</transactionid> \n" +
                "<status>pending</status> \n" +
                "</ns8:debitresponse>";
        return mokeresponse;
    }

    public static String requestToGetBalance(String fri , String transactionId , String currency , String test){
        StringBuilder response = new StringBuilder();
        String mokeresponse = new String();
        Integer exitCode = -1;
        String setfri = new String();

        String trimmedfri = fri.replace("\"","");
        if(trimmedfri.length() > 0){
             setfri = fri;
        }
        else{
            setfri = "FRI:490001/ID";
        }
        try {
            String url = "curl --tlsv1.2 -H 'Content-Type: application/xml' -v -s -k -u wifi.admin:Wifiadmin@12345 -H --cacert /home/adoptminss/mTLS_Certs/RootCA.cer --cert /home/adoptminss/mTLS_Certs/mtnsshotspot.cer  --key /home/adoptminss/mTLS_Certs/adopt_server.key -d '";

            String soapRequest = "'<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<ns1:getbalancerequest xmlns:ns0=\"http://www.ericsson.com/em/emm/financial/v1_2\">" +
                    "<fri>FRI:"+fri+"/MSISDN</fri>" +
                    "</ns1:getbalancerequest>''https://10.250.151.39:8014/pg/getbalance'";

            String curlCommand = url.concat(soapRequest);
            System.out.println(curlCommand);
            ProcessBuilder processBuilder = new ProcessBuilder(curlCommand.split("\\s+"));

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                response.append(line);
            }
            exitCode = process.waitFor();

            // Print the response
             mokeresponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<ns8:getbalanceresponse xmlns:ns8=\"http://www.ericsson.com/em/emm/financial/v1_2\" xmlns:fic=\"http://www.ericsson.com/em/emm/financial/v1_2/common\" xmlns:ns10=\"http://www.ericsson.com/em/emm/financial/v1_1\" xmlns:ns4=\"http://www.ericsson.com/em/emm/financial/v1_0/common\" xmlns:ns5=\"http://www.ericsson.com/em/emm/v2_1/common\" xmlns:ns6=\"http://www.ericsson.com/em/emm/financial/v1_1/common\" xmlns:ns9=\"http://www.ericsson.com/em/emm/financial/v1_2\" xmlns:op=\"http://www.ericsson.com/em/emm/v1_0/common\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
                    "<balance>\n" +
                    "<amount>1000000</amount>\n" +
                    "<currency>SPP</currency>\n" +
                    "</balance>\n" +
                    "</ns8:getbalanceresponse>";
            System.out.println(processBuilder);
            System.out.println("Response: " + response.toString());
            System.out.println("Response: " + response.toString());

            // Print the exit code
            System.out.println("Exit Code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();


        }
        return mokeresponse;
    }

    public static String requestToMTNUSSDPAY(String mobileNumber, String offeringId ,  String transactionId, String test) {
        HashMap<String, String> responseMap = new HashMap<>();
        String endpoint = "http://172.29.7.3:9010/offersubscription/v1/vas/offer";
        String soapXml = "\n" +
                "{\n" +
                "    \"commonInformation\": {\n" +
                "        \"transactionId\": \"" + transactionId + "\",\n" +
                "        \"senderId\": \"TT\",\n" +
                "        \"typeCode\": \"addvas\"\n" +
                "    },\n" +
                "    \"dataArea\": {\n" +
                "        \"productIDChngFlag\": 1,\n" +
                "        \"posAcctCredit\": 1,\n" +
                "        \"paidMode\": 1,\n" +
                "        \"modvasRequired\": 1,\n" +
                "        \"primaryIdentity\": " + mobileNumber + ",\n" +
                "        \"provisionBody\": {\n" +
                "            \"bizCode\": \"MODVAS\",\n" +
                "            \"paraList\": {\n" +
                "                \"para\": [\n" +
                "                    {\n" +
                "                        \"name\": \"MSISDN\",\n" +
                "                        \"value\": " + mobileNumber + "\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"name\": \"RBTFLAG\",\n" +
                "                        \"value\": \"0\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"name\": \"IMSI\",\n" +
                "                        \"value\": \"659020009109828\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        },\n" +
                "        \"supplementaryOfferDetail\": {\n" +
                "            \"offeringId\": " + offeringId + ",\n" +
                "            \"effectiveTimeMode\": \"I\"\n" +
                "        },\n" +
                "        \"acctCreditLimitDetail\": {\n" +
                "            \"payType\": \"234\",\n" +
                "            \"newLimitAmount\": \"200\",\n" +
                "            \"effectiveTimeMode\": \"I\",\n" +
                "            \"creditLimitType\": \"C_INITIAL_CREDIT_LIMIT\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        StringBuilder sb = new StringBuilder();
        sb.append(endpoint);
        sb.append("||");
        sb.append(soapXml);
        return sb.toString();
    }
}