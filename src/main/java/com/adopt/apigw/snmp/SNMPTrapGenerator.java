package com.adopt.apigw.snmp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;

import java.util.Date;


public class SNMPTrapGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SNMPTrapGenerator.class);

    public static void main(String[] args) {
        SNMPTrapGenerator trapV2 = new SNMPTrapGenerator();
        trapV2.sendTrap_Version2("public", "127.0.0.1", 162, "Trap", ".1.3.6.1.2.1.1.8");
        trapV2.clearTrap_Version2("public", "127.0.0.1", 162, "Clear", ".1.3.6.1.2.1.1.9");

    }

    /**
     * This methods sends the V1 trap to the Localhost in port 162
     */
    public void sendTrap_Version2(String strCommunity, String strIpaddress, int intPort, String strMsg, String strOid) {
        try {
            // Create Transport Mapping
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Create Target
            CommunityTarget cTarget = new CommunityTarget();
            cTarget.setCommunity(new OctetString(strCommunity));
            cTarget.setVersion(SnmpConstants.version2c);
            cTarget.setAddress(new UdpAddress(strIpaddress + "/" + intPort));
            cTarget.setRetries(2);
            cTarget.setTimeout(5000);

            // Create PDU for V2
            PDU pdu = new PDU();

            // need to specify the system up time
            pdu.add(new VariableBinding(SnmpConstants.sysUpTime,
                    new OctetString(new Date().toString())));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(
                    strOid)));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress,
                    new IpAddress(strIpaddress)));

            pdu.add(new VariableBinding(new OID(strOid), new OctetString(
                    strMsg)));
            pdu.setType(PDU.TRAP);

            // Send the PDU
            Snmp snmp = new Snmp(transport);
            ApplicationLogger.logger.info("Sending V2 Trap... Check Whether NMS is Listening or not? ");
            snmp.send(pdu, cTarget);
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTrap_Version2(String strCommunity, String strIpaddress, int intPort, String strMsg, String strOid) {
        try {
            // Create Transport Mapping
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Create Target
            CommunityTarget cTarget = new CommunityTarget();
            cTarget.setCommunity(new OctetString(strCommunity));
            cTarget.setVersion(SnmpConstants.version2c);
            cTarget.setAddress(new UdpAddress(strIpaddress + "/" + intPort));
            cTarget.setRetries(2);
            cTarget.setTimeout(5000);

            // Create PDU for V2
            PDU pdu = new PDU();

            // need to specify the system up time
            pdu.add(new VariableBinding(SnmpConstants.sysUpTime,
                    new OctetString(new Date().toString())));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(
                    strOid)));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress,
                    new IpAddress(strIpaddress)));

            pdu.add(new VariableBinding(new OID(strOid), new OctetString(
                    strMsg)));
            pdu.setType(PDU.TRAP);

            // Send the PDU
            Snmp snmp = new Snmp(transport);
            snmp.send(pdu, cTarget);
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}