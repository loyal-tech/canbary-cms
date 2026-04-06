package com.adopt.apigw.pojo.api;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class DbcdrProcessingPojo extends ParentPojo {

    private Integer id;

    private String username;

    private String userpassword;

    private String chappassword;

    private String nasipaddress;

    private String nasport;

    private String servicetype;

    private String framedprotocol;

    private String framedipaddress;

    private String framedipnetmask;

    private String framedrouting;

    private String filterid;

    private String framedmtu;

    private String framedcompression;

    private String loginiphost;

    private String loginservice;

    private String logintcpport;

    private String replymessage;

    private String callbacknumber;

    private String callbackid;

    private String framedroute;

    private String framedipxnetwork;

    private String state;

    private String strclass;

    private String vendorspecific;

    private String sessiontimeout;

    private String idletimeout;

    private String terminationaction;

    private String calledstationid;

    private String callingstationid;

    private String nasidentifier;

    private String proxystate;

    private String loginlatservice;

    private String loginlatnode;

    private String loginlatgroup;

    private String framedappletalklink;

    private String framedappletalknetwork;

    private String framedappletalkzone;

    private String acctstatustype;

    private String acctdelaytime;

    private String acctinputoctets;

    private String acctoutputoctets;

    private String acctsessionid;

    private String acctauthentic;

    private String acctsessiontime;

    private String acctinputpackets;

    private String acctoutputpackets;

    private String acctterminatecause;

    private String acctmultisessionid;

    private String acctlinkcount;

    private String acctinputgigawords;

    private String acctoutputgigawords;

    private String eventtimestamp;

    private String chapchallenge;

    private String nasporttype;

    private String portlimit;

    private String loginlatport;

    private String accttunnelconnection;

    private String arappassword;

    private String arapfeatures;

    private String arapzoneaccess;

    private String arapsecurity;

    private String arapsecuritydata;

    private String passwordretry;

    private String prompt;

    private String connectinfo;

    private String configurationtoken;

    private String eapmessage;

    private String messageauthenticator;

    private String arapchallengeresponse;

    private String acctinteriminterval;

    private String nasportid;

    private String framedpool;

    private String nasipv6address;

    private String framedinterfaceid;

    private String framedipv6prefix;

    private String loginipv6host;

    private String framedipv6route;

    private String framedipv6pool;

    private String digestresponse;

    private String digestattributes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime CREATE_DATE;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime END_DATE;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endSessionTime;

    private Integer custId;

    private String usage_data;

    private String usage_time;

    public String getUsage_data() {
        return usage_data;
    }

    public void setUsage_data(String usage_data) {
        this.usage_data = usage_data;
    }

    public String getUsage_time() {
        return usage_time;
    }

    public void setUsage_time(String usage_time) {
        this.usage_time = usage_time;
    }

    public Integer getCustId() {
        return custId;
    }

    public void setCustId(Integer custId) {
        this.custId = custId;
    }

    public LocalDateTime getEndSessionTime() {
        return endSessionTime;
    }

    public void setEndSessionTime(LocalDateTime endSessionTime) {
        this.endSessionTime = endSessionTime;
    }

    public LocalDateTime getCREATE_DATE() {
        return CREATE_DATE;
    }

    public void setCREATE_DATE(LocalDateTime CREATE_DATE) {
        this.CREATE_DATE = CREATE_DATE;
    }

    public LocalDateTime getEND_DATE() {
        return END_DATE;
    }

    public void setEND_DATE(LocalDateTime END_DATE) {
        this.END_DATE = END_DATE;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getChappassword() {
        return chappassword;
    }

    public void setChappassword(String chappassword) {
        this.chappassword = chappassword;
    }

    public String getNasipaddress() {
        return nasipaddress;
    }

    public void setNasipaddress(String nasipaddress) {
        this.nasipaddress = nasipaddress;
    }

    public String getNasport() {
        return nasport;
    }

    public void setNasport(String nasport) {
        this.nasport = nasport;
    }

    public String getServicetype() {
        return servicetype;
    }

    public void setServicetype(String servicetype) {
        this.servicetype = servicetype;
    }

    public String getFramedprotocol() {
        return framedprotocol;
    }

    public void setFramedprotocol(String framedprotocol) {
        this.framedprotocol = framedprotocol;
    }

    public String getFramedipaddress() {
        return framedipaddress;
    }

    public void setFramedipaddress(String framedipaddress) {
        this.framedipaddress = framedipaddress;
    }

    public String getFramedipnetmask() {
        return framedipnetmask;
    }

    public void setFramedipnetmask(String framedipnetmask) {
        this.framedipnetmask = framedipnetmask;
    }

    public String getFramedrouting() {
        return framedrouting;
    }

    public void setFramedrouting(String framedrouting) {
        this.framedrouting = framedrouting;
    }

    public String getFilterid() {
        return filterid;
    }

    public void setFilterid(String filterid) {
        this.filterid = filterid;
    }

    public String getFramedmtu() {
        return framedmtu;
    }

    public void setFramedmtu(String framedmtu) {
        this.framedmtu = framedmtu;
    }

    public String getFramedcompression() {
        return framedcompression;
    }

    public void setFramedcompression(String framedcompression) {
        this.framedcompression = framedcompression;
    }

    public String getLoginiphost() {
        return loginiphost;
    }

    public void setLoginiphost(String loginiphost) {
        this.loginiphost = loginiphost;
    }

    public String getLoginservice() {
        return loginservice;
    }

    public void setLoginservice(String loginservice) {
        this.loginservice = loginservice;
    }

    public String getLogintcpport() {
        return logintcpport;
    }

    public void setLogintcpport(String logintcpport) {
        this.logintcpport = logintcpport;
    }

    public String getReplymessage() {
        return replymessage;
    }

    public void setReplymessage(String replymessage) {
        this.replymessage = replymessage;
    }

    public String getCallbacknumber() {
        return callbacknumber;
    }

    public void setCallbacknumber(String callbacknumber) {
        this.callbacknumber = callbacknumber;
    }

    public String getCallbackid() {
        return callbackid;
    }

    public void setCallbackid(String callbackid) {
        this.callbackid = callbackid;
    }

    public String getFramedroute() {
        return framedroute;
    }

    public void setFramedroute(String framedroute) {
        this.framedroute = framedroute;
    }

    public String getFramedipxnetwork() {
        return framedipxnetwork;
    }

    public void setFramedipxnetwork(String framedipxnetwork) {
        this.framedipxnetwork = framedipxnetwork;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStrclass() {
        return strclass;
    }

    public void setStrclass(String strclass) {
        this.strclass = strclass;
    }

    public String getVendorspecific() {
        return vendorspecific;
    }

    public void setVendorspecific(String vendorspecific) {
        this.vendorspecific = vendorspecific;
    }

    public String getSessiontimeout() {
        return sessiontimeout;
    }

    public void setSessiontimeout(String sessiontimeout) {
        this.sessiontimeout = sessiontimeout;
    }

    public String getIdletimeout() {
        return idletimeout;
    }

    public void setIdletimeout(String idletimeout) {
        this.idletimeout = idletimeout;
    }

    public String getTerminationaction() {
        return terminationaction;
    }

    public void setTerminationaction(String terminationaction) {
        this.terminationaction = terminationaction;
    }

    public String getCalledstationid() {
        return calledstationid;
    }

    public void setCalledstationid(String calledstationid) {
        this.calledstationid = calledstationid;
    }

    public String getCallingstationid() {
        return callingstationid;
    }

    public void setCallingstationid(String callingstationid) {
        this.callingstationid = callingstationid;
    }

    public String getNasidentifier() {
        return nasidentifier;
    }

    public void setNasidentifier(String nasidentifier) {
        this.nasidentifier = nasidentifier;
    }

    public String getProxystate() {
        return proxystate;
    }

    public void setProxystate(String proxystate) {
        this.proxystate = proxystate;
    }

    public String getLoginlatservice() {
        return loginlatservice;
    }

    public void setLoginlatservice(String loginlatservice) {
        this.loginlatservice = loginlatservice;
    }

    public String getLoginlatnode() {
        return loginlatnode;
    }

    public void setLoginlatnode(String loginlatnode) {
        this.loginlatnode = loginlatnode;
    }

    public String getLoginlatgroup() {
        return loginlatgroup;
    }

    public void setLoginlatgroup(String loginlatgroup) {
        this.loginlatgroup = loginlatgroup;
    }

    public String getFramedappletalklink() {
        return framedappletalklink;
    }

    public void setFramedappletalklink(String framedappletalklink) {
        this.framedappletalklink = framedappletalklink;
    }

    public String getFramedappletalknetwork() {
        return framedappletalknetwork;
    }

    public void setFramedappletalknetwork(String framedappletalknetwork) {
        this.framedappletalknetwork = framedappletalknetwork;
    }

    public String getFramedappletalkzone() {
        return framedappletalkzone;
    }

    public void setFramedappletalkzone(String framedappletalkzone) {
        this.framedappletalkzone = framedappletalkzone;
    }

    public String getAcctstatustype() {
        return acctstatustype;
    }

    public void setAcctstatustype(String acctstatustype) {
        this.acctstatustype = acctstatustype;
    }

    public String getAcctdelaytime() {
        return acctdelaytime;
    }

    public void setAcctdelaytime(String acctdelaytime) {
        this.acctdelaytime = acctdelaytime;
    }

    public String getAcctinputoctets() {
        return acctinputoctets;
    }

    public void setAcctinputoctets(String acctinputoctets) {
        this.acctinputoctets = acctinputoctets;
    }

    public String getAcctoutputoctets() {
        return acctoutputoctets;
    }

    public void setAcctoutputoctets(String acctoutputoctets) {
        this.acctoutputoctets = acctoutputoctets;
    }

    public String getAcctsessionid() {
        return acctsessionid;
    }

    public void setAcctsessionid(String acctsessionid) {
        this.acctsessionid = acctsessionid;
    }

    public String getAcctauthentic() {
        return acctauthentic;
    }

    public void setAcctauthentic(String acctauthentic) {
        this.acctauthentic = acctauthentic;
    }

    public String getAcctsessiontime() {
        return acctsessiontime;
    }

    public void setAcctsessiontime(String acctsessiontime) {
        this.acctsessiontime = acctsessiontime;
    }

    public String getAcctinputpackets() {
        return acctinputpackets;
    }

    public void setAcctinputpackets(String acctinputpackets) {
        this.acctinputpackets = acctinputpackets;
    }

    public String getAcctoutputpackets() {
        return acctoutputpackets;
    }

    public void setAcctoutputpackets(String acctoutputpackets) {
        this.acctoutputpackets = acctoutputpackets;
    }

    public String getAcctterminatecause() {
        return acctterminatecause;
    }

    public void setAcctterminatecause(String acctterminatecause) {
        this.acctterminatecause = acctterminatecause;
    }

    public String getAcctmultisessionid() {
        return acctmultisessionid;
    }

    public void setAcctmultisessionid(String acctmultisessionid) {
        this.acctmultisessionid = acctmultisessionid;
    }

    public String getAcctlinkcount() {
        return acctlinkcount;
    }

    public void setAcctlinkcount(String acctlinkcount) {
        this.acctlinkcount = acctlinkcount;
    }

    public String getAcctinputgigawords() {
        return acctinputgigawords;
    }

    public void setAcctinputgigawords(String acctinputgigawords) {
        this.acctinputgigawords = acctinputgigawords;
    }

    public String getAcctoutputgigawords() {
        return acctoutputgigawords;
    }

    public void setAcctoutputgigawords(String acctoutputgigawords) {
        this.acctoutputgigawords = acctoutputgigawords;
    }

    public String getEventtimestamp() {
        return eventtimestamp;
    }

    public void setEventtimestamp(String eventtimestamp) {
        this.eventtimestamp = eventtimestamp;
    }

    public String getChapchallenge() {
        return chapchallenge;
    }

    public void setChapchallenge(String chapchallenge) {
        this.chapchallenge = chapchallenge;
    }

    public String getNasporttype() {
        return nasporttype;
    }

    public void setNasporttype(String nasporttype) {
        this.nasporttype = nasporttype;
    }

    public String getPortlimit() {
        return portlimit;
    }

    public void setPortlimit(String portlimit) {
        this.portlimit = portlimit;
    }

    public String getLoginlatport() {
        return loginlatport;
    }

    public void setLoginlatport(String loginlatport) {
        this.loginlatport = loginlatport;
    }

    public String getAccttunnelconnection() {
        return accttunnelconnection;
    }

    public void setAccttunnelconnection(String accttunnelconnection) {
        this.accttunnelconnection = accttunnelconnection;
    }

    public String getArappassword() {
        return arappassword;
    }

    public void setArappassword(String arappassword) {
        this.arappassword = arappassword;
    }

    public String getArapfeatures() {
        return arapfeatures;
    }

    public void setArapfeatures(String arapfeatures) {
        this.arapfeatures = arapfeatures;
    }

    public String getArapzoneaccess() {
        return arapzoneaccess;
    }

    public void setArapzoneaccess(String arapzoneaccess) {
        this.arapzoneaccess = arapzoneaccess;
    }

    public String getArapsecurity() {
        return arapsecurity;
    }

    public void setArapsecurity(String arapsecurity) {
        this.arapsecurity = arapsecurity;
    }

    public String getArapsecuritydata() {
        return arapsecuritydata;
    }

    public void setArapsecuritydata(String arapsecuritydata) {
        this.arapsecuritydata = arapsecuritydata;
    }

    public String getPasswordretry() {
        return passwordretry;
    }

    public void setPasswordretry(String passwordretry) {
        this.passwordretry = passwordretry;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getConnectinfo() {
        return connectinfo;
    }

    public void setConnectinfo(String connectinfo) {
        this.connectinfo = connectinfo;
    }

    public String getConfigurationtoken() {
        return configurationtoken;
    }

    public void setConfigurationtoken(String configurationtoken) {
        this.configurationtoken = configurationtoken;
    }

    public String getEapmessage() {
        return eapmessage;
    }

    public void setEapmessage(String eapmessage) {
        this.eapmessage = eapmessage;
    }

    public String getMessageauthenticator() {
        return messageauthenticator;
    }

    public void setMessageauthenticator(String messageauthenticator) {
        this.messageauthenticator = messageauthenticator;
    }

    public String getArapchallengeresponse() {
        return arapchallengeresponse;
    }

    public void setArapchallengeresponse(String arapchallengeresponse) {
        this.arapchallengeresponse = arapchallengeresponse;
    }

    public String getAcctinteriminterval() {
        return acctinteriminterval;
    }

    public void setAcctinteriminterval(String acctinteriminterval) {
        this.acctinteriminterval = acctinteriminterval;
    }

    public String getNasportid() {
        return nasportid;
    }

    public void setNasportid(String nasportid) {
        this.nasportid = nasportid;
    }

    public String getFramedpool() {
        return framedpool;
    }

    public void setFramedpool(String framedpool) {
        this.framedpool = framedpool;
    }

    public String getNasipv6address() {
        return nasipv6address;
    }

    public void setNasipv6address(String nasipv6address) {
        this.nasipv6address = nasipv6address;
    }

    public String getFramedinterfaceid() {
        return framedinterfaceid;
    }

    public void setFramedinterfaceid(String framedinterfaceid) {
        this.framedinterfaceid = framedinterfaceid;
    }

    public String getFramedipv6prefix() {
        return framedipv6prefix;
    }

    public void setFramedipv6prefix(String framedipv6prefix) {
        this.framedipv6prefix = framedipv6prefix;
    }

    public String getLoginipv6host() {
        return loginipv6host;
    }

    public void setLoginipv6host(String loginipv6host) {
        this.loginipv6host = loginipv6host;
    }

    public String getFramedipv6route() {
        return framedipv6route;
    }

    public void setFramedipv6route(String framedipv6route) {
        this.framedipv6route = framedipv6route;
    }

    public String getFramedipv6pool() {
        return framedipv6pool;
    }

    public void setFramedipv6pool(String framedipv6pool) {
        this.framedipv6pool = framedipv6pool;
    }

    public String getDigestresponse() {
        return digestresponse;
    }

    public void setDigestresponse(String digestresponse) {
        this.digestresponse = digestresponse;
    }

    public String getDigestattributes() {
        return digestattributes;
    }

    public void setDigestattributes(String digestattributes) {
        this.digestattributes = digestattributes;
    }
}
