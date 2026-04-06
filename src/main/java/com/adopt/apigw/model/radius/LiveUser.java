package com.adopt.apigw.model.radius;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "TBLTLIVEUSER")
@JsonInclude(Include.NON_NULL)
public class LiveUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CDRID", nullable = false)
	private Long cdrID;

	@Column(name = "USERNAME", length = 250)
	private String userName;

	@Column(name = "USERPASSWORD", length = 250)
	private String userPassword;


	@Column(name = "CHAPPASSWORD", length = 250)
	private String chapPassword;

	@Column(name = "NASIPADDRESS", length = 250)
	private String nasIpAddress;

	@Column(name = "NASPORT", length = 100)
	private String nasPort;

	@Column(name = "SERVICETYPE", length = 100)
	private String serviceType;

	@Column(name = "FRAMEDPROTOCOL", length = 100)
	private String framedProtocol;

	@Column(name = "FRAMEDIPADDRESS", length = 100)
	private String framedIpAddress;

	@Column(name = "FRAMEDROUTING", length = 100)
	private String framedRouting;

	@Column(name = "FRAMEDIPNETMASK", length = 100)
	private String framedNetwork;

	@Column(name = "FILTERID", length = 100)
	private String filterId;

	@Column(name = "FRAMEDMTU", length = 100)
	private String frmaedMTU;

	@Column(name = "FRAMEDCOMPRESSION", length = 100)
	private String framedCompression;

	@Column(name = "LOGINIPHOST", length = 100)
	private String loginIPHost;

	@Column(name = "LOGINSERVICE", length = 100)
	private String loginService;

	@Column(name = "LOGINTCPPORT", length = 100)
	private String loginTCPPort;

	@Column(name = "REPLYMESSAGE", length = 100)
	private String replyMessage;

	@Column(name = "CALLBACKNUMBER", length = 100)
	private String callbackNumber;

	@Column(name = "CALLBACKID", length = 100)
	private String callbackId;

	@Column(name = "FRAMEDROUTE", length = 100)
	private String framedRoute;

	@Column(name = "FRAMEDIPXNETWORK", length = 100)
	private String framedIPXNetwork;

	@Column(name = "STATE", length = 250)
	private String state;

	@Column(name = "CLASS", length = 250)
	private String lClass;

	@Column(name = "VENDORSPECIFIC", length = 250)
	private String vendorSpecific;

	@Column(name = "SESSIONTIMEOUT", length = 100)
	private String sessionTimeout;

	@Column(name = "IDLETIMEOUT", length = 100)
	private String idleTimeout;

	@Column(name = "TERMINATIONACTION", length = 100)
	private String terminationAction;

	@Column(name = "CALLEDSTATIONID", length = 100)
	private String calledStationId;

	@Column(name = "CALLINGSTATIONID", length = 100)
	private String callingStationId;

	@Column(name = "PROXYSTATE", length = 100)
	private String proxyState;

	@Column(name = "LOGINLATSERVICE", length = 250)
	private String loginLATService;

	@Column(name = "LOGINLATNODE", length = 100)
	private String loginLATNode;

	@Column(name = "LOGINLATGROUP", length = 100)
	private String loginLATGroup;

	@Column(name = "FRAMEDAPPLETALKLINK", length = 100)
	private String framedAppleTalkLink;

	@Column(name = "FRAMEDAPPLETALKNETWORK", length = 100)
	private String framedAppleTalkNetwork;

	@Column(name = "FRAMEDAPPLETALKZONE", length = 250)
	private String framedAppleTalkZone;

	@Column(name = "ACCTSTATUSTYPE", length = 100)
	private String acctStatusType;

	@Column(name = "ACCTDELAYTIME", length = 100)
	private String acctDelayTime;

	@Column(name = "ACCTINPUTOCTETS", length = 100)
	private String acctInputOctets;

	@Column(name = "ACCTOUTPUTOCTETS", length = 100)
	private String acctOutputOctets;

	@Column(name = "ACCTSESSIONID", length = 250)
	private String acctSessionId;

	@Column(name = "ACCTAUTHENTIC", length = 250)
	private String acctAuthentic;

	@Column(name = "ACCTSESSIONTIME", length = 100)
	private String acctSessionTime;

	@Column(name = "ACCTINPUTPACKETS", length = 100)
	private String acctInputPackets;

	@Column(name = "ACCTOUTPUTPACKETS", length = 100)
	private String acctOutputPackets;

	@Column(name = "ACCTTERMINATECAUSE", length = 250)
	private String acctTerminateCause;

	@Column(name = "ACCTMULTISESSIONID", length = 250)
	private String acctMultiSessionId;

	@Column(name = "ACCTLINKCOUNT", length = 250)
	private String acctLinkCount;

	@Column(name = "ACCTINPUTGIGAWORDS", length = 100)
	private String acctInputGigawords;

	@Column(name = "ACCTOUTPUTGIGAWORDS", length = 100)
	private String acctOutputGigawords;

	@Column(name = "EVENTTIMESTAMP", length = 250)
	private String eventTimestamp;

	@Column(name = "CHAPCHALLENGE", length = 250)
	private String chapChallenge;

	@Column(name = "NASPORTTYPE", length = 100)
	private String nasPortType;

	@Column(name = "PORTLIMIT", length = 100)
	private String portLimit;

	@Column(name = "LOGINLATPORT", length = 250)
	private String loginLATPort;

	@Column(name = "ACCTTUNNELCONNECTION", length = 250)
	private String acctTunnelConnection;

	@Column(name = "ARAPPASSWORD", length = 250)
	private String arapPassword;

	@Column(name = "ARAPFEATURES", length = 250)
	private String arapFeatures;

	@Column(name = "ARAPZONEACCESS", length = 250)
	private String arapZoneAccess;

	@Column(name = "ARAPSECURITY", length = 250)
	private String arapSecurity;

	@Column(name = "ARAPSECURITYDATA", length = 250)
	private String arapSecurityData;

	@Column(name = "PASSWORDRETRY", length = 250)
	private String passwordRetry;

	@Column(name = "PROMPT", length = 250)
	private String prompt;

	@Column(name = "CONNECTINFO", length = 250)
	private String connectInfo;

	@Column(name = "CONFIGURATIONTOKEN", length = 250)
	private String configurationToken;

	@Column(name = "EAPMESSAGE", length = 250)
	private String eapMessage;

	@Column(name = "MESSAGEAUTHENTICATOR", length = 250)
	private String messageAuthenticator;

	@Column(name = "ARAPCHALLENGERESPONSE", length = 250)
	private String arapChallengeResponse;

	@Column(name = "ACCTINTERIMINTERVAL", length = 250)
	private String acctInterimInterval;

	@Column(name = "NASPORTID", length = 100)
	private String nasPortId;

	@Column(name = "FRAMEDPOOL", length = 100)
	private String framedPool;

	@Column(name = "NASIPV6ADDRESS", length = 100)
	private String nasIPv6Address;

	@Column(name = "FRAMEDINTERFACEID", length = 100)
	private String framedInterfaceId;

	@Column(name = "FRAMEDIPV6PREFIX", length = 100)
	private String framedIPv6Prefix;

	@Column(name = "LOGINIPV6HOST", length = 100)
	private String loginIPv6Host;

	@Column(name = "FRAMEDIPV6ROUTE", length = 100)
	private String framedIPv6Route;

	@Column(name = "FRAMEDIPV6POOL", length = 100)
	private String framedIPv6Pool;

	@Column(name = "DIGESTRESPONSE", length = 100)
	private String digestResponse;

	@Column(name = "DIGESTATTRIBUTES", length = 100)
	private String digestAttributes;

	@Column(name = "FRAMEDIPV6ADDRESS", length = 100)
	private String framedipv6address;

	@Column(name = "CREATEDATE", length = 100)
	@JsonProperty("createDate")
	private Timestamp createdDate;

	@Column(name = "LASTMODIFICATIONDATE", length = 100)
	@JsonProperty("lastModificationDate")
	private Timestamp lastmodifiedDate;

	@ApiModelProperty(hidden = true)
	@Column (name="mvnoid", nullable = false)
	private Integer mvnoId;

	@ApiModelProperty(notes = "This is location id")
	@Column (name="location_id", nullable = false)
	private Long locationId;

	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}

	public Long getCdrID() {
		return cdrID;
	}

	public void setCdrID(Long cdrID) {
		this.cdrID = cdrID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getChapPassword() {
		return chapPassword;
	}

	public void setChapPassword(String chapPassword) {
		this.chapPassword = chapPassword;
	}

	public String getNasIpAddress() {
		return nasIpAddress;
	}

	public void setNasIpAddress(String nasIpAddress) {
		this.nasIpAddress = nasIpAddress;
	}

	public String getNasPort() {
		return nasPort;
	}

	public void setNasPort(String nasPort) {
		this.nasPort = nasPort;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getFramedProtocol() {
		return framedProtocol;
	}

	public void setFramedProtocol(String framedProtocol) {
		this.framedProtocol = framedProtocol;
	}

	public String getFramedIpAddress() {
		return framedIpAddress;
	}

	public void setFramedIpAddress(String framedIpAddress) {
		this.framedIpAddress = framedIpAddress;
	}

	public String getFramedRouting() {
		return framedRouting;
	}

	public void setFramedRouting(String framedRouting) {
		this.framedRouting = framedRouting;
	}

	public String getFramedNetwork() {
		return framedNetwork;
	}

	public void setFramedNetwork(String framedNetwork) {
		this.framedNetwork = framedNetwork;
	}

	public String getFilterId() {
		return filterId;
	}

	public void setFilterId(String filterId) {
		this.filterId = filterId;
	}

	public String getFrmaedMTU() {
		return frmaedMTU;
	}

	public void setFrmaedMTU(String frmaedMTU) {
		this.frmaedMTU = frmaedMTU;
	}

	public String getFramedCompression() {
		return framedCompression;
	}

	public void setFramedCompression(String framedCompression) {
		this.framedCompression = framedCompression;
	}

	public String getLoginIPHost() {
		return loginIPHost;
	}

	public void setLoginIPHost(String loginIPHost) {
		this.loginIPHost = loginIPHost;
	}

	public String getLoginService() {
		return loginService;
	}

	public void setLoginService(String loginService) {
		this.loginService = loginService;
	}

	public String getLoginTCPPort() {
		return loginTCPPort;
	}

	public void setLoginTCPPort(String loginTCPPort) {
		this.loginTCPPort = loginTCPPort;
	}

	public String getReplyMessage() {
		return replyMessage;
	}

	public void setReplyMessage(String replyMessage) {
		this.replyMessage = replyMessage;
	}

	public String getCallbackNumber() {
		return callbackNumber;
	}

	public void setCallbackNumber(String callbackNumber) {
		this.callbackNumber = callbackNumber;
	}

	public String getCallbackId() {
		return callbackId;
	}

	public void setCallbackId(String callbackId) {
		this.callbackId = callbackId;
	}

	public String getFramedRoute() {
		return framedRoute;
	}

	public void setFramedRoute(String framedRoute) {
		this.framedRoute = framedRoute;
	}

	public String getFramedIPXNetwork() {
		return framedIPXNetwork;
	}

	public void setFramedIPXNetwork(String framedIPXNetwork) {
		this.framedIPXNetwork = framedIPXNetwork;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getlClass() {
		return lClass;
	}

	public void setlClass(String lClass) {
		this.lClass = lClass;
	}

	public String getVendorSpecific() {
		return vendorSpecific;
	}

	public void setVendorSpecific(String vendorSpecific) {
		this.vendorSpecific = vendorSpecific;
	}

	public String getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(String sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public String getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(String idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public String getTerminationAction() {
		return terminationAction;
	}

	public void setTerminationAction(String terminationAction) {
		this.terminationAction = terminationAction;
	}

	public String getCalledStationId() {
		return calledStationId;
	}

	public void setCalledStationId(String calledStationId) {
		this.calledStationId = calledStationId;
	}

	public String getCallingStationId() {
		return callingStationId;
	}

	public void setCallingStationId(String callingStationId) {
		this.callingStationId = callingStationId;
	}

	public String getProxyState() {
		return proxyState;
	}

	public void setProxyState(String proxyState) {
		this.proxyState = proxyState;
	}

	public String getLoginLATService() {
		return loginLATService;
	}

	public void setLoginLATService(String loginLATService) {
		this.loginLATService = loginLATService;
	}

	public String getLoginLATNode() {
		return loginLATNode;
	}

	public void setLoginLATNode(String loginLATNode) {
		this.loginLATNode = loginLATNode;
	}

	public String getLoginLATGroup() {
		return loginLATGroup;
	}

	public void setLoginLATGroup(String loginLATGroup) {
		this.loginLATGroup = loginLATGroup;
	}

	public String getFramedAppleTalkLink() {
		return framedAppleTalkLink;
	}

	public void setFramedAppleTalkLink(String framedAppleTalkLink) {
		this.framedAppleTalkLink = framedAppleTalkLink;
	}

	public String getFramedAppleTalkNetwork() {
		return framedAppleTalkNetwork;
	}

	public void setFramedAppleTalkNetwork(String framedAppleTalkNetwork) {
		this.framedAppleTalkNetwork = framedAppleTalkNetwork;
	}

	public String getFramedAppleTalkZone() {
		return framedAppleTalkZone;
	}

	public void setFramedAppleTalkZone(String framedAppleTalkZone) {
		this.framedAppleTalkZone = framedAppleTalkZone;
	}

	public String getAcctStatusType() {
		return acctStatusType;
	}

	public void setAcctStatusType(String acctStatusType) {
		this.acctStatusType = acctStatusType;
	}

	public String getAcctDelayTime() {
		return acctDelayTime;
	}

	public void setAcctDelayTime(String acctDelayTime) {
		this.acctDelayTime = acctDelayTime;
	}

	public String getAcctInputOctets() {
		return acctInputOctets;
	}

	public void setAcctInputOctets(String acctInputOctets) {
		this.acctInputOctets = acctInputOctets;
	}

	public String getAcctOutputOctets() {
		return acctOutputOctets;
	}

	public void setAcctOutputOctets(String acctOutputOctets) {
		this.acctOutputOctets = acctOutputOctets;
	}

	public String getAcctSessionId() {
		return acctSessionId;
	}

	public void setAcctSessionId(String acctSessionId) {
		this.acctSessionId = acctSessionId;
	}

	public String getAcctAuthentic() {
		return acctAuthentic;
	}

	public void setAcctAuthentic(String acctAuthentic) {
		this.acctAuthentic = acctAuthentic;
	}

	public String getAcctSessionTime() {
		return acctSessionTime;
	}

	public void setAcctSessionTime(String acctSessionTime) {
		this.acctSessionTime = acctSessionTime;
	}

	public String getAcctInputPackets() {
		return acctInputPackets;
	}

	public void setAcctInputPackets(String acctInputPackets) {
		this.acctInputPackets = acctInputPackets;
	}

	public String getAcctOutputPackets() {
		return acctOutputPackets;
	}

	public void setAcctOutputPackets(String acctOutputPackets) {
		this.acctOutputPackets = acctOutputPackets;
	}

	public String getAcctTerminateCause() {
		return acctTerminateCause;
	}

	public void setAcctTerminateCause(String acctTerminateCause) {
		this.acctTerminateCause = acctTerminateCause;
	}

	public String getAcctMultiSessionId() {
		return acctMultiSessionId;
	}

	public void setAcctMultiSessionId(String acctMultiSessionId) {
		this.acctMultiSessionId = acctMultiSessionId;
	}

	public String getAcctLinkCount() {
		return acctLinkCount;
	}

	public void setAcctLinkCount(String acctLinkCount) {
		this.acctLinkCount = acctLinkCount;
	}

	public String getAcctInputGigawords() {
		return acctInputGigawords;
	}

	public void setAcctInputGigawords(String acctInputGigawords) {
		this.acctInputGigawords = acctInputGigawords;
	}

	public String getAcctOutputGigawords() {
		return acctOutputGigawords;
	}

	public void setAcctOutputGigawords(String acctOutputGigawords) {
		this.acctOutputGigawords = acctOutputGigawords;
	}

	public String getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(String eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	public String getChapChallenge() {
		return chapChallenge;
	}

	public void setChapChallenge(String chapChallenge) {
		this.chapChallenge = chapChallenge;
	}

	public String getNasPortType() {
		return nasPortType;
	}

	public void setNasPortType(String nasPortType) {
		this.nasPortType = nasPortType;
	}

	public String getPortLimit() {
		return portLimit;
	}

	public void setPortLimit(String portLimit) {
		this.portLimit = portLimit;
	}

	public String getLoginLATPort() {
		return loginLATPort;
	}

	public void setLoginLATPort(String loginLATPort) {
		this.loginLATPort = loginLATPort;
	}

	public String getAcctTunnelConnection() {
		return acctTunnelConnection;
	}

	public void setAcctTunnelConnection(String acctTunnelConnection) {
		this.acctTunnelConnection = acctTunnelConnection;
	}

	public String getArapPassword() {
		return arapPassword;
	}

	public void setArapPassword(String arapPassword) {
		this.arapPassword = arapPassword;
	}

	public String getArapFeatures() {
		return arapFeatures;
	}

	public void setArapFeatures(String arapFeatures) {
		this.arapFeatures = arapFeatures;
	}

	public String getArapZoneAccess() {
		return arapZoneAccess;
	}

	public void setArapZoneAccess(String arapZoneAccess) {
		this.arapZoneAccess = arapZoneAccess;
	}

	public String getArapSecurity() {
		return arapSecurity;
	}

	public void setArapSecurity(String arapSecurity) {
		this.arapSecurity = arapSecurity;
	}

	public String getArapSecurityData() {
		return arapSecurityData;
	}

	public void setArapSecurityData(String arapSecurityData) {
		this.arapSecurityData = arapSecurityData;
	}

	public String getPasswordRetry() {
		return passwordRetry;
	}

	public void setPasswordRetry(String passwordRetry) {
		this.passwordRetry = passwordRetry;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getConnectInfo() {
		return connectInfo;
	}

	public void setConnectInfo(String connectInfo) {
		this.connectInfo = connectInfo;
	}

	public String getConfigurationToken() {
		return configurationToken;
	}

	public void setConfigurationToken(String configurationToken) {
		this.configurationToken = configurationToken;
	}

	public String getEapMessage() {
		return eapMessage;
	}

	public void setEapMessage(String eapMessage) {
		this.eapMessage = eapMessage;
	}

	public String getMessageAuthenticator() {
		return messageAuthenticator;
	}

	public void setMessageAuthenticator(String messageAuthenticator) {
		this.messageAuthenticator = messageAuthenticator;
	}

	public String getArapChallengeResponse() {
		return arapChallengeResponse;
	}

	public void setArapChallengeResponse(String arapChallengeResponse) {
		this.arapChallengeResponse = arapChallengeResponse;
	}

	public String getAcctInterimInterval() {
		return acctInterimInterval;
	}

	public void setAcctInterimInterval(String acctInterimInterval) {
		this.acctInterimInterval = acctInterimInterval;
	}

	public String getNasPortId() {
		return nasPortId;
	}

	public void setNasPortId(String nasPortId) {
		this.nasPortId = nasPortId;
	}

	public String getFramedPool() {
		return framedPool;
	}

	public void setFramedPool(String framedPool) {
		this.framedPool = framedPool;
	}

	public String getNasIPv6Address() {
		return nasIPv6Address;
	}

	public void setNasIPv6Address(String nasIPv6Address) {
		this.nasIPv6Address = nasIPv6Address;
	}

	public String getFramedInterfaceId() {
		return framedInterfaceId;
	}

	public void setFramedInterfaceId(String framedInterfaceId) {
		this.framedInterfaceId = framedInterfaceId;
	}

	public String getFramedIPv6Prefix() {
		return framedIPv6Prefix;
	}

	public void setFramedIPv6Prefix(String framedIPv6Prefix) {
		this.framedIPv6Prefix = framedIPv6Prefix;
	}

	public String getLoginIPv6Host() {
		return loginIPv6Host;
	}

	public void setLoginIPv6Host(String loginIPv6Host) {
		this.loginIPv6Host = loginIPv6Host;
	}

	public String getFramedIPv6Route() {
		return framedIPv6Route;
	}

	public void setFramedIPv6Route(String framedIPv6Route) {
		this.framedIPv6Route = framedIPv6Route;
	}

	public String getFramedIPv6Pool() {
		return framedIPv6Pool;
	}

	public void setFramedIPv6Pool(String framedIPv6Pool) {
		this.framedIPv6Pool = framedIPv6Pool;
	}

	public String getDigestResponse() {
		return digestResponse;
	}

	public void setDigestResponse(String digestResponse) {
		this.digestResponse = digestResponse;
	}

	public String getDigestAttributes() {
		return digestAttributes;
	}

	public void setDigestAttributes(String digestAttributes) {
		this.digestAttributes = digestAttributes;
	}

	public String getFramedipv6address() {
		return framedipv6address;
	}

	public void setFramedipv6address(String framedipv6address) {
		this.framedipv6address = framedipv6address;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getLastmodifiedDate() {
		return lastmodifiedDate;
	}

	public void setLastmodifiedDate(Timestamp lastmodifiedDate) {
		this.lastmodifiedDate = lastmodifiedDate;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public LiveUser(Long cdrID, String userName, String userPassword, String chapPassword, String nasIpAddress,
                    String nasPort, String serviceType, String framedProtocol, String framedIpAddress, String framedRouting,
                    String framedNetwork, String filterId, String frmaedMTU, String framedCompression, String loginIPHost,
                    String loginService, String loginTCPPort, String replyMessage, String callbackNumber, String callbackId,
                    String framedRoute, String framedIPXNetwork, String state, String lClass, String vendorSpecific,
                    String sessionTimeout, String idleTimeout, String terminationAction, String calledStationId,
                    String callingStationId, String proxyState, String loginLATService, String loginLATNode,
                    String loginLATGroup, String framedAppleTalkLink, String framedAppleTalkNetwork, String framedAppleTalkZone,
                    String acctStatusType, String acctDelayTime, String acctInputOctets, String acctOutputOctets,
                    String acctSessionId, String acctAuthentic, String acctSessionTime, String acctInputPackets,
                    String acctOutputPackets, String acctTerminateCause, String acctMultiSessionId, String acctLinkCount,
                    String acctInputGigawords, String acctOutputGigawords, String eventTimestamp, String chapChallenge,
                    String nasPortType, String portLimit, String loginLATPort, String acctTunnelConnection, String arapPassword,
                    String arapFeatures, String arapZoneAccess, String arapSecurity, String arapSecurityData,
                    String passwordRetry, String prompt, String connectInfo, String configurationToken, String eapMessage,
                    String messageAuthenticator, String arapChallengeResponse, String acctInterimInterval, String nasPortId,
                    String framedPool, String nasIPv6Address, String framedInterfaceId, String framedIPv6Prefix,
                    String loginIPv6Host, String framedIPv6Route, String framedIPv6Pool, String digestResponse,
                    String digestAttributes, String framedipv6address, Timestamp createdDate, Timestamp lastmodifiedDate) {
		super();
		this.cdrID = cdrID;
		this.userName = userName;
		this.userPassword = userPassword;
		this.chapPassword = chapPassword;
		this.nasIpAddress = nasIpAddress;
		this.nasPort = nasPort;
		this.serviceType = serviceType;
		this.framedProtocol = framedProtocol;
		this.framedIpAddress = framedIpAddress;
		this.framedRouting = framedRouting;
		this.framedNetwork = framedNetwork;
		this.filterId = filterId;
		this.frmaedMTU = frmaedMTU;
		this.framedCompression = framedCompression;
		this.loginIPHost = loginIPHost;
		this.loginService = loginService;
		this.loginTCPPort = loginTCPPort;
		this.replyMessage = replyMessage;
		this.callbackNumber = callbackNumber;
		this.callbackId = callbackId;
		this.framedRoute = framedRoute;
		this.framedIPXNetwork = framedIPXNetwork;
		this.state = state;
		this.lClass = lClass;
		this.vendorSpecific = vendorSpecific;
		this.sessionTimeout = sessionTimeout;
		this.idleTimeout = idleTimeout;
		this.terminationAction = terminationAction;
		this.calledStationId = calledStationId;
		this.callingStationId = callingStationId;
		this.proxyState = proxyState;
		this.loginLATService = loginLATService;
		this.loginLATNode = loginLATNode;
		this.loginLATGroup = loginLATGroup;
		this.framedAppleTalkLink = framedAppleTalkLink;
		this.framedAppleTalkNetwork = framedAppleTalkNetwork;
		this.framedAppleTalkZone = framedAppleTalkZone;
		this.acctStatusType = acctStatusType;
		this.acctDelayTime = acctDelayTime;
		this.acctInputOctets = acctInputOctets;
		this.acctOutputOctets = acctOutputOctets;
		this.acctSessionId = acctSessionId;
		this.acctAuthentic = acctAuthentic;
		this.acctSessionTime = acctSessionTime;
		this.acctInputPackets = acctInputPackets;
		this.acctOutputPackets = acctOutputPackets;
		this.acctTerminateCause = acctTerminateCause;
		this.acctMultiSessionId = acctMultiSessionId;
		this.acctLinkCount = acctLinkCount;
		this.acctInputGigawords = acctInputGigawords;
		this.acctOutputGigawords = acctOutputGigawords;
		this.eventTimestamp = eventTimestamp;
		this.chapChallenge = chapChallenge;
		this.nasPortType = nasPortType;
		this.portLimit = portLimit;
		this.loginLATPort = loginLATPort;
		this.acctTunnelConnection = acctTunnelConnection;
		this.arapPassword = arapPassword;
		this.arapFeatures = arapFeatures;
		this.arapZoneAccess = arapZoneAccess;
		this.arapSecurity = arapSecurity;
		this.arapSecurityData = arapSecurityData;
		this.passwordRetry = passwordRetry;
		this.prompt = prompt;
		this.connectInfo = connectInfo;
		this.configurationToken = configurationToken;
		this.eapMessage = eapMessage;
		this.messageAuthenticator = messageAuthenticator;
		this.arapChallengeResponse = arapChallengeResponse;
		this.acctInterimInterval = acctInterimInterval;
		this.nasPortId = nasPortId;
		this.framedPool = framedPool;
		this.nasIPv6Address = nasIPv6Address;
		this.framedInterfaceId = framedInterfaceId;
		this.framedIPv6Prefix = framedIPv6Prefix;
		this.loginIPv6Host = loginIPv6Host;
		this.framedIPv6Route = framedIPv6Route;
		this.framedIPv6Pool = framedIPv6Pool;
		this.digestResponse = digestResponse;
		this.digestAttributes = digestAttributes;
		this.framedipv6address = framedipv6address;
		this.createdDate = createdDate;
		this.lastmodifiedDate = lastmodifiedDate;
	}

	
	public LiveUser() {}

	
	
}
