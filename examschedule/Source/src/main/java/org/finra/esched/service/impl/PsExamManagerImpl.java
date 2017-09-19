package org.finra.esched.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.finra.esched.dao.PsExamDao;
import org.finra.esched.domain.*;
import org.finra.esched.domain.PsFirm.NMA_TYPE;
import org.finra.esched.domain.PsSessionStatus.PS_STATUS_TYPE;
import org.finra.esched.domain.ui.*;
import org.finra.esched.exception.*;
import org.finra.esched.service.PsExamManager;
import org.finra.esched.service.rest.ui.*;
import org.finra.exam.common.domain.ui.ErrorInfoJson;
import org.finra.exam.common.domain.ui.MaintainExamResponse;
import org.finra.exam.common.domain.ui.ReturnCode;
import org.finra.exam.common.domain.ui.ReturnCodeJson;
import org.finra.exam.common.domain.ui.examcreate.*;
import org.finra.exam.common.grid.GridRequest;
import org.finra.exam.common.security.SecurityConstants;
import org.finra.exam.common.security.domain.User;
import org.finra.exam.common.security.util.UserContext;
import org.finra.exam.common.service.ExamManager;
import org.finra.exam.common.util.EwServiceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;


@Service("psExamManager")
public class PsExamManagerImpl implements PsExamManager {

    public static final String NEW_STATUS = "NEW";
    public static final String PUBLISHED_STATUS = "PUBLISHED";
    public static final String ERROR_STATUS = "ERROR";
    public static final Long SUPERVISOR_APPLICATION_ROLE_ID = 4L;
    public static final Long COORDINATOR_APPLICATION_ROLE_ID = 6L;
    private PsExamDao psExamDao;
    private static final Logger log = LoggerFactory.getLogger(PsExamManagerImpl.class);
    public static final long TIMEOUT = 15000;
    public static final long SCHEDULING_ROLEID = 13;

    @Autowired
    private ExamManager examManager;

    @Autowired
    @Override
    public void setPsExamDao(PsExamDao psExamDao) {
        this.psExamDao = psExamDao;
    }

    @Value("${examEmailSupportUserNm}")
    private String examEmailSupportUserNm;

    @Value("${examEmailSupportAddr}")
    private String examEmailSupportAddr;

    @Value("${examCreationURI}")
    private String examCreationURI;

    @Value("${createStaffURI}")
    private String createStaffURI;

    @Value("${createMilestonesURI}")
    private String createMilestonesURI;

    @Value("${examLinkingURI}")
    private String examLinkingURI;

    @Value("${examEmailURI}")
    private String examEmailURI;

    @Value("${examEmailSubject}")
    private String examEmailSubject;

    @Value("${examEmailBody}")
    private String examEmailBody;

    @Value("${examEmailUserNm}")
    private String examEmailUserNm;

    @Value("${examEmailAddr}")
    private String examEmailAddr;

    @Value("${examEmailEnv}")
    private String examEmailEnv;

    @Value("${getContactURI}")
    private String getContactURI;

    @Value("${createExamWebService}")
    private String createExamWebService;

    @Override
    public void processPS() {

        User user = UserContext.getUser();

        int staffId = user.getStaffId().intValue();
        int versionId = psExamDao.createVersion(staffId);
        if (psExamDao.loadInputData(versionId) == 1)
            psExamDao.processVersion(versionId);
    }


    @Override
    public void processNMA() {

        User user = UserContext.getUser();

        int versionId = psExamDao.getCurrentVersion();
        if (psExamDao.loadRSANMAData(versionId) == 1) {

            List<Integer> nmaFirmIds = psExamDao.processNMAVersion(versionId);

            if (nmaFirmIds != null && nmaFirmIds.size() > 0) {

                for (Integer firmId : nmaFirmIds) {
                    PsFirm firm = psExamDao.getPsFirmById(firmId);

                    // Get PsFirmStaffDistrict to get recipients data
                    PsFirmStaffDistrict firmStaff = psExamDao.getFirmStaff(firmId);

                    if (firmStaff == null) {
                        log.error("Application Error: Email Notifications after processing RSA/NMA. Unable to get Staff for Firm " + firmId);
                        continue;
                    }

                    List<EmailRecipient> toList = new ArrayList<EmailRecipient>();

                    toList.add(new EmailRecipient(firmStaff.getSpCoordinatorId(), firmStaff.getSpCoordinatorEmail()));
                    toList.add(new EmailRecipient(firmStaff.getFinopCoordinatorId(), firmStaff.getFinopCoordinatorEmail()));
                    toList.add(new EmailRecipient(firmStaff.getSpSupervisorId(), firmStaff.getSpSupervisorEmail()));
                    toList.add(new EmailRecipient(firmStaff.getFinopSupervisorId(), firmStaff.getFinopSupervisorEmail()));

                    // TODO: Add for testing
                    //toList.add(new EmailRecipient("ruzhav", "vasily.ruzha@finra.org"));
                    //toList.add(new EmailRecipient("arellanr", null));
                    try {

                        // VRuzha: as per discussion, we have to send separate emails for NMA & RSANMA cases.
                        PsSession sssn = firm.getCurrentSession();
                        if (sssn != null && (sssn.hasNma(NMA_TYPE.NMA) || sssn.hasNma(NMA_TYPE.RSANMA))) {
                            String body = examEmailBody.replace("{Firm_ID}", "" + firm.getId().getFirmId()).replace("{Firm_Name}", firm.getFirmName());

                            // TODO: Add for testing
                            // body=body+"<br/><br/> This email will be send to: "+firmStaff.getSpCoordinatorEmail()+"("+firmStaff.getSpCoordinatorId()+")"+", "+firmStaff.getFinopCoordinatorEmail()+"("+firmStaff.getFinopCoordinatorId()+")"+", "+firmStaff.getSpSupervisorEmail()+"("+firmStaff.getSpSupervisorId()+")"+", "+firmStaff.getFinopSupervisorEmail()+"("+firmStaff.getFinopSupervisorId()+")";

                            String typeCd = "" + (sssn.hasNma(NMA_TYPE.NMA) ? NMA_TYPE.NMA.name() : "") + (sssn.hasNma(NMA_TYPE.RSANMA) ? (sssn.hasNma(NMA_TYPE.NMA) ? "/" + NMA_TYPE.RSANMA.name() : NMA_TYPE.RSANMA.name()) : "");

                            String emailId = sendEmail(firm, typeCd, new EmailRecipient(examEmailUserNm, examEmailAddr), toList, null, body);
                            log.debug("NMA/RSANMA Email notification sent:" + emailId);
                        }

                    } catch (Exception ex) {
                        log.error("Application Error: Email Notifications after processing RSA/NMA for Firm Id " + firmId, ex);
                    }
                }
            }
        }
    }


    @Override
    public int getCurrentPsVersion() {
        return psExamDao.getCurrentVersion();
    }


    @Override
    public List<PsSessionView> getPsExams(GridRequest rq) {
        return psExamDao.getPsExams(rq);
    }

    @Override
    public int getPsExamsCount(GridRequest rq) {
        return psExamDao.getPsExamsCount(rq);
    }

    @Override
    public PsSessionView getPsExamsById(Long id)throws Exception {
        return psExamDao.getPsExamsById(id);
    }

    @Override
    public List<PsOvrdReason> getMnReasons() {
        return psExamDao.getOvrdReasons(false);
    }

    @Override
    public List<PsOvrdReason> getMrReasons() {
        return psExamDao.getOvrdReasons(true);
    }

    @Override
    public List<PsDistrictView> getDistricts() {
        return psExamDao.getDistricts();
    }

    @Override
    public PsDistrictView getUserDistrict() {
        User schedUser = UserContext.getUser();
        return psExamDao.getUserDistrict(schedUser);
    }

    @Override
    public List<PsSessionStatus> getStatuses() {
        return psExamDao.getStatuses();
    }

    @Override
    public PsSchedSessionResponse schedPsSessions(PsSchedSessionRequest rq) throws Exception{
        User user = UserContext.getUser();
        PsSchedSessionResponse response = psExamDao.schedPsSessions(rq, user);
        return response;
    }

    @Override
    public List<PsSession> getListForReprocessing(int versionId, String status) {
        return psExamDao.getListForReprocessing(versionId, status);
    }

    @Override
    public PsFirmStaffDistrict getFirmStaff(int firmId) {
        return psExamDao.getFirmStaff(firmId);
    }

    private PsExamWorkspaceSyncLog createPsExamWorkspaceSyncLog(int sessionId, String publishType, Integer userId) {
        PsExamWorkspaceSyncLog bean = new PsExamWorkspaceSyncLog();
        bean.setOutputId(Long.valueOf(sessionId));
        bean.setPublishDate(new Date());
        bean.setPublishType(publishType);
        bean.setStatus(NEW_STATUS);
        bean.setUserId(userId);
        return bean;
    }

    private static String generateCreateExamPublishType(int sessionId) {
        return "EW_CREXAM_SESSIONID_" + sessionId;
    }

    @Override
    public void setSessionStatus(Integer sssnId, String status) {
        // Save approval
        psExamDao.setSessionStatus(sssnId, status);
    }

    private WebClient createClient(String address, String requestMediaType, String responseMediaType, PrintWriter outWriter, PrintWriter inWriter) {
        List<Object> providers = new ArrayList<>();
        providers.add(new JacksonJaxbJsonProvider());
        JAXRSClientFactoryBean factoryBean = new JAXRSClientFactoryBean();
        factoryBean.setAddress(address);
        factoryBean.setProviders(providers);
        LoggingOutInterceptor outInterceptor = new LoggingOutInterceptor(outWriter);
        LoggingInInterceptor inInterceptor = new LoggingInInterceptor(inWriter);
        factoryBean.getOutInterceptors().add(outInterceptor);
        factoryBean.getOutFaultInterceptors().add(outInterceptor);
        factoryBean.getInInterceptors().add(inInterceptor);
        factoryBean.getInFaultInterceptors().add(inInterceptor);

        WebClient webClient = factoryBean.createWebClient();
        if (requestMediaType != null) webClient.type(requestMediaType);
        webClient.accept(responseMediaType);

        WebClient.getConfig(webClient).setSynchronousTimeout(TIMEOUT);
        HTTPConduit conduit = WebClient.getConfig(webClient).getHttpConduit();
        conduit.getClient().setReceiveTimeout(TIMEOUT);
        conduit.getClient().setConnectionTimeout(TIMEOUT);
        return webClient;
    }

    @Override
    public PsSession getPsSessionById(Integer sssnId) {
        return this.psExamDao.getPsSessionById(sssnId);
    }

    @Override
    public String sendEmail(PsFirm firm, String type, EmailRecipient from,
                            List<EmailRecipient> to, String subject, String body)
            throws EmailNotificationException {

        PrintWriter outWriter = null;
        StringWriter outString = null;
        PrintWriter inWriter = null;
        StringWriter inString = null;

        PsEmailNotificationsLog ewLog = createPsEmailNotificationsLog(firm, type);

        try {

            outString = new StringWriter();
            outWriter = new PrintWriter(outString);
            inString = new StringWriter();
            inWriter = new PrintWriter(inString);
            WebClient client = createClient(examEmailURI, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, outWriter, inWriter);
            client.type(MediaType.APPLICATION_JSON);
            client.accept(MediaType.APPLICATION_JSON);

            WebClient.getConfig(client).setSynchronousTimeout(TIMEOUT);
            HTTPConduit conduit = WebClient.getConfig(client).getHttpConduit();
            conduit.getClient().setReceiveTimeout(TIMEOUT);
            conduit.getClient().setConnectionTimeout(TIMEOUT);

            EmailNotificationRequest emailRequest = new EmailNotificationRequest(
                    (from != null ? from : new EmailRecipient(examEmailUserNm, examEmailAddr)),
                    to,
                    (subject != null ? subject.replace("{Env}", examEmailEnv) : examEmailSubject.replace("{Env}", examEmailEnv)),
                    (body != null ? body : examEmailBody),
                    examEmailEnv
            );

            EmailNotificationResponse response = client
                    .post(emailRequest, EmailNotificationResponse.class);

            if (response.isSuccess()) {
                ewLog.setSttsCd(EmailNotificationResponse.SUCCESS);
                return response.getEmailId();
            } else {
                log.error("Failed to create exam: " + response.getMessage());
                ewLog.setSttsCd(EmailNotificationResponse.FAILURE);
                throw new EmailNotificationException(response.getMessage());
            }
        } catch (Exception e) {
            log.error("Application Error: Email Notification", e);
            ewLog.setSttsCd(EmailNotificationResponse.FAILURE);
            throw new EmailNotificationException(e.getMessage());
        } finally {
            closeStreamsAndWriteEmailResult(ewLog, outWriter, outString, inWriter, inString);
        }

        //return null;
    }


    private PsEmailNotificationsLog createPsEmailNotificationsLog(PsFirm firm, String emailType) {
        PsEmailNotificationsLog bean = new PsEmailNotificationsLog();
        bean.setFirmId(firm != null ? firm.getId().getFirmId() : null);
        bean.setTypeCd(emailType != null ? emailType : "RE-PROC");
        bean.setSentDate(new Date());

        return bean;
    }

    private void closeStreamsAndWriteEmailResult(PsEmailNotificationsLog enLog,
                                                 PrintWriter outWriter, StringWriter outString,
                                                 PrintWriter inWriter, StringWriter inString) {
        String response = null;
        String request = null;
        if (null != inString) {
            try {
                response = inString.toString();
                log.debug("Response: " + response);
                inString.close();
            } catch (IOException e) {
                log.error("Failed to close in stream", e);
            }
        }
        if (null != inWriter) {
            inWriter.close();
        }
        if (null != outString) {
            try {
                request = outString.toString();
                log.debug("Request: " + request);
                outString.close();
            } catch (IOException e) {
                log.error("Failed to close out stream", e);
            }
        }
        if (null != outWriter) {
            outWriter.close();
        }
        if (StringUtils.isNotBlank(request)) {
            enLog.setRequest(request);
        }
        if (StringUtils.isNotBlank(response)) {
            enLog.setResponse(response);
        }
        psExamDao.saveEmailNotificationsLog(enLog);
    }

    public static class EmailNotificationRequest implements Serializable {

        private EmailContext context;

        private EmailRecipient from;
        private List<EmailRecipient> to;

        private String subject;
        private String body;

        public EmailNotificationRequest(EmailRecipient from,
                                        List<EmailRecipient> to, String subject, String body, String examEmailEnv) {

            this.context = new EmailContext(examEmailEnv);
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.body = body;
        }

        @Override
        public String toString() {
            return "EmailNotificationRequest : {"
                    + "context : " + this.context.toString()
                    + ", from : " + this.from.toString()
                    + ", to : " + this.to.toString()
                    + ", subject : " + this.subject
                    + ", body : " + this.body
                    + '}';
        }


        public EmailContext getContext() {
            return context;
        }

        public void setContext(EmailContext context) {
            this.context = context;
        }

        public EmailRecipient getFrom() {
            return from;
        }

        public void setFrom(EmailRecipient from) {
            this.from = from;
        }

        public List<EmailRecipient> getTo() {
            return to;
        }

        public void setTo(List<EmailRecipient> to) {
            this.to = to;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }


    }

    public static class EmailContext implements Serializable {
        private String source = "PRESCHEDULE";
        private String processType = "GENERIC";
        private String environment = "DEV";
        private String contentType = "text/html; charset=us-ascii";

        public EmailContext(String env) {
            if (env != null && !env.trim().equalsIgnoreCase("")) {
                this.environment = env;
            }
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getProcessType() {
            return processType;
        }

        public void setProcessType(String processType) {
            this.processType = processType;
        }

        public String getEnvironment() {
            return environment;
        }

        public void setEnvironment(String environment) {
            this.environment = environment;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }

    public static class EmailNotificationResponse implements Serializable {

        public static final String SUCCESS = "SUCCESS";
        public static final String FAILURE = "FAILURE";

        private String emailResponseStatus;
        private String emailId;
        private String message;

        public EmailNotificationResponse() {

        }

        public String getEmailResponseStatus() {
            return emailResponseStatus;
        }

        public void setEmailResponseStatus(String emailResponseStatus) {
            this.emailResponseStatus = emailResponseStatus;
        }

        public String getEmailId() {
            return emailId;
        }


        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }


        public String getMessage() {
            return message;
        }


        public void setMessage(String message) {
            this.message = message;
        }

        private boolean isSuccess() {
            return SUCCESS.equalsIgnoreCase(getEmailResponseStatus());
        }
    }

    public static class EmailRecipient implements Serializable {
        private String username;
        private String overrideEmail;

        public EmailRecipient(String userNm, String userEmailAddr) {
            this.username = userNm;
            this.overrideEmail = userEmailAddr;
        }

        @Override
        public String toString() {
            return "EmailRecipient{" + "username : '" + this.username + '\''
                    + ", overrideEmail : '" + this.overrideEmail + '\'' + '}';
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getOverrideEmail() {
            return overrideEmail;
        }

        public void setOverrideEmail(String overrideEmail) {
            this.overrideEmail = overrideEmail;
        }
    }

    @Override
    public void verifyPS() {
        // Run component verification logic, require SCHED table to be populated
        psExamDao.verifyComponents();
    }

    @Override
    public boolean isRoleAccess(long roleId) throws Exception{
        return psExamDao.isRoleAccess(UserContext.getUser().getStaffId(), roleId);
    }

    @Override
    public List<PsSession> getMarketListForReprocessing(int versionId) {
        return psExamDao.getMarketListForReprocessing(versionId);
    }

    @Override
    public PsFirmStaffMarket getFirmStaffMarket(int firmId, String marketTypeCode) {
        return psExamDao.getFirmStaffMarket(firmId, marketTypeCode);

    }

    @Override
    public PsMarketMatterMetaData getMarketMatterMetaData(String marketTypeCode) {
        return psExamDao.getMarketMatterMetaData(marketTypeCode);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = org.finra.exam.common.Constants.LOOKUP_CACHE, key = "new String(\"examCategoryType\")")
    public List<PsExamCategoryType> getExamCategoryTypes() {
        return psExamDao.getExamCategories();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = org.finra.exam.common.Constants.LOOKUP_CACHE, key = "new String(\"examType\")")
    public List<PsExamTypeType> getExamTypes() {
        return psExamDao.getExamTypes();
    }

    @Override
    public List<PsExamSubTypeType> getExamSubTypes() {
        return psExamDao.getExamSubTypes();
    }

    @Override
    public List<PsRegulatorySignificance> getRegulatorySignificance() {
        return psExamDao.getRegulatorySignificance();
    }

    @Override
    public String getAnnualPlanningPhase() {
        return psExamDao.getAnnualPlanningPhase();
    }

    @Override
    public PsSessionView saveFirmSession(SaveFirmSessionRequest saveFirmSessionRequest) throws Exception{
        return psExamDao.saveFirmSession(saveFirmSessionRequest);
    }

    @Override
    public List <PsTypeSubTypeComponentMapView> saveOrUpdateTypeSubTypeMappings(PsTypeSubTypeComponentMapView[] psTypeSubTypeComponentMapViewArray) throws Exception {
        List<PsTypeSubTypeComponentMapView> result = psExamDao.saveOrUpdateTypeSubTypeMappings(psTypeSubTypeComponentMapViewArray);
        psExamDao.schdlLoadComponentMapping();
        return result;
    }


    @Override
    public PsSchedSessionResponse createExamsMatters(PsSchedSessionResponse schedPsSessions, String tfceExamType, String marketTypeCode) throws Exception {

        //checking if any sessions to process
        if (schedPsSessions == null || (schedPsSessions.getSessionResponses() == null) || (schedPsSessions.getSessionResponses().isEmpty())) {
            log.error("No sessions found in the request to session.");
            throw new Exception("No sessions found in the request to session.");
        }

        List<ScheduleSessionResponse> sssnList = schedPsSessions.getSessionResponses();
        PsMarketMatterMetaData marketMatterMetaData = getMarketMatterMetaData(marketTypeCode);

        PsSession sssn = null;
        // Process each group, link colab exams
        for (ScheduleSessionResponse sssnV : sssnList) {
            if(sssnV.getStatus() != ReturnCode.STATUS_OK)
                continue;
            try{
                sssn = getPsSessionById(sssnV.getSssnId());

                //EXAM-10465: if session is already in SCHEDULED or ERROR state - skip it.
                if(sssn.getStatus()!=null && (sssn.getStatus().getType().equals(PS_STATUS_TYPE.ERROR) || sssn.getStatus().getType().equals(PS_STATUS_TYPE.SCHED)))
                    continue;

                PublishBean publishBean;
                try {

                    if(marketTypeCode != null)
                        publishBean = new PublishBean(sssn, tfceExamType, marketTypeCode);
                    else
                        publishBean = new PublishBean(sssn, tfceExamType, null);

                } catch (ValidationException e) {
                    setSessionStatus(sssn.getId(), PsSessionStatus.PS_STATUS_TYPE.ERROR.name());
                    sssnV.setStatus(ReturnCode.STATUS_ERROR);
                    sssnV.getErrorInfoJsonList().add(new ErrorInfoJson(ReturnCode.STATUS_ERROR.toString(), "Validation error. Session Id:" + (sssn!=null ? sssn.getId() : null)+e.getMessage()));
                    continue;
                }

                createMatterAndUpdateOutput(publishBean, marketMatterMetaData);

                setSessionStatus(sssn.getId(), PsSessionStatus.PS_STATUS_TYPE.SCHED.name());
                sssnV.setStatus(ReturnCode.STATUS_OK);
            }catch (Exception e){
                setSessionStatus(sssn.getId(), PsSessionStatus.PS_STATUS_TYPE.ERROR.name());
                if(e instanceof MatterExamExistsException)
                    sssnV.setStatus(ReturnCode.STATUS_MATTER_EXAM_EXISTS);
                else
                    sssnV.setStatus(ReturnCode.STATUS_ERROR);
            }
        }
        schedPsSessions.setSessionResponses(sssnList);
        return schedPsSessions;
    }

    public void createMatterAndUpdateOutput(PublishBean publishBean, PsMarketMatterMetaData marketMatterMetaData) throws Exception {
        //creating SP & FINOP

        MaintainExamResponse examCreateResponse = null;
        if(publishBean.isLinkFnAndSp()) {
            examCreateResponse = callExamCreateWebService(publishBean, "SPFINOP", marketMatterMetaData);
            saveExamAndMatterinOutput(publishBean.getFnExam(),  getLongExamIdFromExamCreateResponse(examCreateResponse), examCreateResponse.getMatterId());
            saveExamAndMatterinOutput(publishBean.getSpExam(), examCreateResponse.getOtherDomainInfoList().get(0).getExamId().longValue(), examCreateResponse.getOtherDomainInfoList().get(0).getModelId());
        }else{
            if(publishBean.getSpExam() != null){
                examCreateResponse = callExamCreateWebService(publishBean, "SP", marketMatterMetaData);
                saveExamAndMatterinOutput(publishBean.getSpExam(), getLongExamIdFromExamCreateResponse(examCreateResponse), examCreateResponse.getMatterId());
            }
            if(publishBean.getFnExam() != null){
                examCreateResponse = callExamCreateWebService(publishBean, "FINOP", marketMatterMetaData);
                saveExamAndMatterinOutput(publishBean.getFnExam(), getLongExamIdFromExamCreateResponse(examCreateResponse), examCreateResponse.getMatterId());
            }
        }

        if(publishBean.getAncExam() != null){
            examCreateResponse = callExamCreateWebService(publishBean, "ANC", marketMatterMetaData);
            saveExamAndMatterinOutput(publishBean.getAncExam(), getLongExamIdFromExamCreateResponse(examCreateResponse), examCreateResponse.getMatterId());
        }

        if(publishBean.getFloorExam() != null) {
            examCreateResponse = callExamCreateWebService(publishBean, "FLOOR", marketMatterMetaData);
            saveExamAndMatterinOutput(publishBean.getFloorExam(), getLongExamIdFromExamCreateResponse(examCreateResponse), examCreateResponse.getMatterId());
        }

        if(publishBean.getTfceExam() != null && publishBean.getMarketTypeCode() != null) {
            examCreateResponse = callExamCreateWebService(publishBean, "TFCE", marketMatterMetaData);
            saveExamAndMatterinOutput(publishBean.getTfceExam(), getLongExamIdFromExamCreateResponse(examCreateResponse), examCreateResponse.getMatterId());
        }

        if(!examCreateResponse.getStatus().equals(ReturnCodeJson.STATUS_OK)) {
            throw new Exception(examCreateResponse.getMessage() + ": " + examCreateResponse.getErrorDetails());
        }

    }

    Long getLongExamIdFromExamCreateResponse(MaintainExamResponse examCreateResponse) {
        if(examCreateResponse.getExamId() == null) {
            return null;
        }
        else {
            return examCreateResponse.getExamId().longValue();
        }

    }
    public void saveExamAndMatterinOutput(PsOutput psOutput, Long examId, String matterId) throws Exception{
        psOutput.setExamId(examId);
        psOutput.setMatterId(matterId);
        psExamDao.saveOutput(psOutput);
    }

    public PsFirmStaffDistrict getFirmStaffBasedOnType(int firmId, String marketTypeCode) throws Exception {
        PsFirmStaffDistrict firmStaff = null;
        if(marketTypeCode == null)
            firmStaff = getFirmStaff(firmId);
        else {
            switch(marketTypeCode){
                case    "TMMS":
                    PsMarketMatterMetaData marketMatterMetaData = getMarketMatterMetaData(marketTypeCode);
                    firmStaff = new PsFirmStaffDistrict();
                    User staffUser = examManager.getUser(marketMatterMetaData.getManagerUserId().longValue());
                    firmStaff.setFinopSupervisor(staffUser.getStaffId().intValue());
                    firmStaff.setSpSupervisor(staffUser.getStaffId().intValue());
                    firmStaff.setFinopSupervisorId(staffUser.getUserId());
                    firmStaff.setSpSupervisorId(staffUser.getUserId());
                    break;
                default:
                    PsFirmStaffMarket firmStaffMarket = getFirmStaffMarket(firmId, marketTypeCode);
                    firmStaff = new PsFirmStaffDistrict();
                    firmStaff.setFirmId(firmStaffMarket.getFirmId());
                    firmStaff.setMainBranchId(firmStaffMarket.getMainBranchId());
                    firmStaff.setFinopSupervisor(firmStaffMarket.getManagerStaffId());
                    firmStaff.setSpSupervisor(firmStaffMarket.getManagerStaffId());
                    firmStaff.setFinopSupervisorId(firmStaffMarket.getManagerUserId());
                    firmStaff.setSpSupervisorId(firmStaffMarket.getManagerUserId());
                    break;
            }
        }
        return firmStaff;
    }

    public MaintainExamResponse callExamCreateWebService(PublishBean publishBean, String examMatterType, PsMarketMatterMetaData marketMatterMetaData) throws Exception {
        User user = UserContext.getUser();
        PsSession session = publishBean.getSession();
        PsExamWorkspaceSyncLog ewLog = createPsExamWorkspaceSyncLog(session.getId(),
                generateCreateExamPublishType(session.getId()),
                user.getStaffId().intValue());
        MaintainExamResponse examCreateResponse = null;
        try{
            CreateExamRequest examRequest = new CreateExamRequest();
            examRequest.setCreatedByUserId(user.getUserId());

            CreateExamSource createExamSource = new CreateExamSource();
            createExamSource.setSourceType(CreateExamSource.SourceType.PSCHD);
            createExamSource.setSourceId(String.valueOf(session.getId()));
            examRequest.setSource(createExamSource);

            CreateExamDestination createExamDestination = new CreateExamDestination();
            createExamDestination.setDestinationType("STAR");
            examRequest.setDestination(createExamDestination);

            PsFirmStaffDistrict firmStaff = getFirmStaffBasedOnType(session.getFirm().getId().getFirmId(), publishBean.getMarketTypeCode());

            //setting the primary Domain if it is a non collaborative exam
            switch (examMatterType){
                case "SPFINOP":
/*                    if(publishBean.getFnExam().getMatterId() != null || publishBean.getFnExam().getExamId() != null)
                        throw new MatterExamExistsException("Matter OR exam already Exists for FINOP EXAM in Collaborative");
                    if(publishBean.getSpExam().getMatterId() != null || publishBean.getSpExam().getExamId() != null)
                        throw new MatterExamExistsException("Matter OR exam already Exists for SP EXAM in Collaborative");*/

                    examRequest.setOtherDomains(new ArrayList<CreateExamDomain>());
                    examRequest.setPrimaryDomain(getDomainFromPsOutput(publishBean.getVersion(), publishBean.getFnExam(), firmStaff, marketMatterMetaData));
                    examRequest.getOtherDomains().add(getDomainFromPsOutput(publishBean.getVersion(), publishBean.getSpExam(), firmStaff, marketMatterMetaData));
                    break;
                case "SP":
/*                    if(publishBean.getSpExam().getMatterId() != null || publishBean.getSpExam().getExamId() != null)
                        throw new MatterExamExistsException("Matter already Exists for SP EXAM");*/
                    examRequest.setPrimaryDomain(getDomainFromPsOutput(publishBean.getVersion(), publishBean.getSpExam(), firmStaff, marketMatterMetaData));
                    break;
                case "FINOP":
/*                    if(publishBean.getFnExam().getMatterId() != null || publishBean.getFnExam().getExamId() != null)
                        throw new MatterExamExistsException("Matter already Exists for FINOP EXAM");*/
                    examRequest.setPrimaryDomain(getDomainFromPsOutput(publishBean.getVersion(), publishBean.getFnExam(), firmStaff, marketMatterMetaData));
                    break;
                case "ANC":
/*                    if(publishBean.getAncExam().getMatterId() != null || publishBean.getAncExam().getExamId() != null)
                        throw new MatterExamExistsException("Matter already Exists ANC SP EXAM");*/
                    examRequest.setPrimaryDomain(getDomainFromPsOutput(publishBean.getVersion(), publishBean.getAncExam(), firmStaff, marketMatterMetaData));
                    break;
                case "FLOOR":
/*                    if(publishBean.getFloorExam().getMatterId() != null || publishBean.getFloorExam().getExamId() != null)
                        throw new MatterExamExistsException("Matter already Exists for FLOOR EXAM");*/
                    PsOutput floorExam = publishBean.getFloorExam();
                    if(floorExam != null && floorExam.getFloorDistrict().equalsIgnoreCase("TF"))
                        firmStaff = getFirmStaffBasedOnType(session.getFirm().getId().getFirmId(), "TMMS");
                    examRequest.setPrimaryDomain(getDomainFromPsOutput(publishBean.getVersion(), publishBean.getFloorExam(), firmStaff, marketMatterMetaData));
                    break;
                case "TFCE":
/*                    if(publishBean.getTfceExam().getMatterId() != null || publishBean.getTfceExam().getExamId() != null)
                        throw new MatterExamExistsException("Matter already Exists for TFCE EXAM");*/
                    examRequest.setPrimaryDomain(getDomainFromPsOutput(publishBean.getVersion(), publishBean.getTfceExam(), firmStaff, marketMatterMetaData));
                    break;
                default:
/*                    if(publishBean.getFnExam().getMatterId() != null || publishBean.getFnExam().getExamId() != null)
                        throw new MatterExamExistsException("Matter already Exists for FINOP EXAM");*/
                    examRequest.setPrimaryDomain(getDomainFromPsOutput(publishBean.getVersion(), publishBean.getFnExam(), firmStaff, marketMatterMetaData));
            }

            EwServiceBean servBean = new EwServiceBean();
            servBean.setEntity(String.valueOf(session.getId()));
            servBean.setEntityType("SESSION_ID");
            servBean.setPublishType("Create Exam");
            servBean.setStaffId(user.getStaffId());
            servBean.setRequestURI(createExamWebService);
            servBean.setResponseClass(MaintainExamResponse.class.getName());
            servBean.setRequestObject(examRequest);

            Map headers = new HashMap();
            headers.put(SecurityConstants.USER_PRINCIPAL, user.getUserId() + "@");
            servBean.setHeaders(headers);

            ObjectMapper mapper = new ObjectMapper();
            ewLog.setRequest(mapper.writeValueAsString(examRequest));
            servBean = examManager.sendService(servBean);

            if(servBean.getResponseObject() != null)
                ewLog.setResponse(mapper.writeValueAsString(servBean.getResponseObject()));



            if(servBean != null) {
                examCreateResponse = (MaintainExamResponse)servBean.getResponseObject();
            }

            if(servBean != null && servBean.getSttsCd().equalsIgnoreCase("SUCCESS") &&
                    (servBean.getResponseObject() instanceof MaintainExamResponse) &&
                    ((MaintainExamResponse)servBean.getResponseObject()).getStatus() != null &&
                    ((MaintainExamResponse)servBean.getResponseObject()).getStatus().equals(ReturnCode.STATUS_OK)){
                ewLog.setStatus(PUBLISHED_STATUS);
                return examCreateResponse;
            }else {
                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append(System.lineSeparator()).append("Exception in creating the exam");
                msgBuilder.append(System.lineSeparator()).append("User Id:------> "+user.getUserId());
                msgBuilder.append(System.lineSeparator()).append("Session Id:------> "+session.getId());
                sendSupportEmail(msgBuilder.toString());
                throw new Exception("Exception in creating the exam");
            }

        } catch (Exception e) {
            ewLog.setStatus(ERROR_STATUS);
            //ewLog.setResponse(e.getMessage());
            return examCreateResponse;
        } finally {
            try{
                psExamDao.saveExamWorkspaceSyncLog(ewLog);
            }catch(Exception ex) {
                log.error("Failed to save the ExamWorkspaceSyncLog", ex);
            }
        }
    }

    private CreateExamDomain getDomainFromPsOutput(int versionId, PsOutput output, PsFirmStaffDistrict firmStaff, PsMarketMatterMetaData marketMatterMetaData) throws Exception {
        CreateExamDomain domainRequest = new CreateExamDomain();
        domainRequest.setFinraReceivedDate(new Date());
        domainRequest.setInitialDepartmentReceivedDate(new Date());
        domainRequest.setTypeCode(output.getExamTypeCd());
        domainRequest.setSubTypeCode(output.getExamSubTypeCd());
        domainRequest.setMatterId(output.getMatterId());

        if(output.getExamId() != null)
            domainRequest.setExamId(output.getExamId().intValue());
        domainRequest.setCategoryCode("CYCLE");

        if(output.getMemberMarketCode() != null ){
            if(output.getMemberMarketCode().equalsIgnoreCase("EQTY"))
                domainRequest.setProductId(marketMatterMetaData.getProductId());
            domainRequest.setRegulatorySignificanceId(marketMatterMetaData.getRegulatorSignificanceId());
            domainRequest.setMatterDescription(marketMatterMetaData.getMatterDescription());
        }

        List<CreateExamDomainStaff> examStaffList = createExamStaffList(firmStaff, output);
        domainRequest.setStaffs(examStaffList);

        List<CreateExamContact> contactsList = createExamcontactList(output);
        domainRequest.setContacts(contactsList);

        //setting the Milestones
        List<CreateExamDomainMilestones> milestoneList = createMilestoneList(output);
        if(milestoneList != null && !milestoneList.isEmpty())
            domainRequest.setMilestones(milestoneList);

        //setting the Billable Entities
        domainRequest.setBillableEntities(getBillableEntities(output, versionId));

        return domainRequest;
    }

    /**Composes the milestone requests
     *
     * @return
     * @throws Exception
     */
    public static final Integer EW_START_DATE_ID = 1;
    public static final Integer FW_START_DATE_ID = 4;

    private List<CreateExamDomainMilestones> createMilestoneList(PsOutput domainInfo) throws Exception {
        List<CreateExamDomainMilestones> milestoneList = new ArrayList<CreateExamDomainMilestones>();
        if(null != domainInfo.getPrjEwsd())
            milestoneList.add(new CreateExamDomainMilestones(EW_START_DATE_ID, domainInfo.getPrjEwsd()));
        if(null != domainInfo.getPrjFwsd())
            milestoneList.add(new CreateExamDomainMilestones(FW_START_DATE_ID, domainInfo.getPrjFwsd()));
        return milestoneList;
    }

    private List<CreateExamDomainStaff> createExamStaffList(PsFirmStaffDistrict firmStaff, PsOutput output) throws Exception {
        List<CreateExamDomainStaff> examStaffList = new ArrayList<>();

        CreateExamDomainStaff examStaff = new CreateExamDomainStaff();
        examStaff.setPrimaryFlag(true);
        Long staffId = output.isFinOpMatter() ? firmStaff.getFinopSupervisor().longValue() : firmStaff.getSpSupervisor().longValue();
        examStaff.setApplicationUserId(staffId.intValue());
        User staffUser = examManager.getUser(staffId);
        if(staffUser.getRoleId() != null && (staffUser.getRoleId()== CreateExamDomainStaff.RoleId.MANAGER.getId()))
            examStaff.setRoleId(CreateExamDomainStaff.RoleId.MANAGER);
        else if(staffUser.getRoleId() != null && (staffUser.getRoleId()== CreateExamDomainStaff.RoleId.SUPERVISOR.getId()))
            examStaff.setRoleId(CreateExamDomainStaff.RoleId.SUPERVISOR);

        examStaffList.add(examStaff);

        return examStaffList;
    }

    private List<CreateExamContact> createExamcontactList(PsOutput bean) throws Exception{
        List<CreateExamContact> contactsList = new ArrayList<>();
        CreateExamContact contact = new CreateExamContact();
        contact.setBranchCrdNumber(bean.getSession().getFirm().getMainBranchId());
        contact.setCrdNumber(bean.getSession().getFirm().getId().getFirmId());
        contact.setPrimaryFlag(true);
        contact.setPotentialRespondentFlag(true);
        contact.setCrdType(CreateExamContact.CrdType.FIRM);

        contactsList.add(contact);
        return contactsList;
    }

    private List<CreateExamDomainBillableEntities> getBillableEntities(PsOutput psOutput, int versionId) throws Exception{

        List<CreateExamDomainBillableEntities> createExamDomainBillableEntitiesList = new ArrayList<CreateExamDomainBillableEntities>();
        Integer firmId = psOutput.getSession().getFirm().getId().getFirmId();
        int matterType = psOutput.getMttrTypeId().intValue();
        int matterSubType = null != psOutput.getMttrSubTypeId() ? psOutput.getMttrSubTypeId().intValue() : 0;
        String marketTypeCode = psOutput.getMemberMarketCode();

        if (marketTypeCode == null) {
            //this means matter is for Member Reg
            String spRequirement = psExamDao.getExamComponentFromMatterType("RSA_SALES_PRACTICE", matterType);
            if (!spRequirement.equals("Y"))
                spRequirement = psExamDao.getExamComponentFromMatterSubType("RSA_SALES_PRACTICE", matterSubType);

            String finopRequirement = psExamDao.getExamComponentFromMatterType("RSA_FINOP", matterType);
            if (!finopRequirement.equals("Y"))
                finopRequirement = psExamDao.getExamComponentFromMatterSubType("RSA_FINOP", matterSubType);

            //use BEs using Member Reg logic
            List<PsFirmBillableEntity> beList = psExamDao.getFirmBillableEntity(firmId, versionId, spRequirement, finopRequirement);
            if(beList != null && !beList.isEmpty()){
                for(PsFirmBillableEntity billableEntity : beList){
                    CreateExamDomainBillableEntities createExamDomainBillableEntities = new CreateExamDomainBillableEntities();
                    createExamDomainBillableEntities.setBillableEntityId(billableEntity.getBillableEntity());
                    if(billableEntity.getCurrentFlag() != null && billableEntity.getCurrentFlag().equalsIgnoreCase("Y"))
                        createExamDomainBillableEntities.setCurrentFlag(true);
                    else
                        createExamDomainBillableEntities.setCurrentFlag(false);
                    createExamDomainBillableEntities.setTimeTrackingFlag(true);
                    createExamDomainBillableEntitiesList.add(createExamDomainBillableEntities);
                }
            }
        }else{
            List<PsFirmMarketBillableEntity> marketBEList = psExamDao.getFirmMarketBillableEntity(firmId, marketTypeCode);
            if (marketBEList != null && marketBEList.size() > 0) {
                for (PsFirmMarketBillableEntity bean:marketBEList) {
                    CreateExamDomainBillableEntities createExamDomainBillableEntities = new CreateExamDomainBillableEntities();
                    createExamDomainBillableEntities.setBillableEntityId(bean.getBillableEntityId());
                    createExamDomainBillableEntities.setCurrentFlag(true);
                    createExamDomainBillableEntities.setTimeTrackingFlag(true);
                    createExamDomainBillableEntitiesList.add(createExamDomainBillableEntities);
                }
            }
        }

        return createExamDomainBillableEntitiesList;
    }

    @Override
    public PsSchedSessionResponse reprocessExamsMatters(String marketType) throws Exception{
        PsSchedSessionResponse retVal = new PsSchedSessionResponse();
        retVal.setSessionResponses(new ArrayList<ScheduleSessionResponse>());
        List<PsSession> sessions = getListForReprocessing(getCurrentPsVersion(), PsSessionStatus.PS_STATUS_TYPE.ERROR.name());
        if(sessions != null && !sessions.isEmpty()) {
            for (PsSession session : sessions) {
                ScheduleSessionResponse psSchedSessionResponse = new ScheduleSessionResponse();
                try {
                    setSessionStatus(session.getId(), PsSessionStatus.PS_STATUS_TYPE.REVIEW.name());
                    psSchedSessionResponse.setSssnId(session.getId());
                    psSchedSessionResponse.setErrorInfoJsonList(new ArrayList<ErrorInfoJson>());
                    psSchedSessionResponse.setStatus(ReturnCode.STATUS_OK);
                    retVal.getSessionResponses().add(psSchedSessionResponse);
                } catch (Exception e) {
                    setSessionStatus(session.getId(), PsSessionStatus.PS_STATUS_TYPE.ERROR.name());
                }
            }

            if (marketType == null)
                retVal = createExamsMatters(retVal, "NonTFCEEXAM", null);
            else
                retVal = createExamsMatters(retVal, "TFCEEXAM", marketType);

        }
        return retVal;
    }

    @Override
    public PsSchedSessionResponse processMarketExams(int versionId, String marketTypeCode) throws Exception{
        PsSchedSessionResponse retVal = new PsSchedSessionResponse();
        retVal.setSessionResponses(new ArrayList<ScheduleSessionResponse>());

        psExamDao.resetMarketSessions(marketTypeCode);

        StringBuilder msgBuilder = new StringBuilder();

        List<PsSession> sessions = getMarketListForReprocessing(versionId);
        if(sessions != null && !sessions.isEmpty()) {
            for (PsSession session : sessions) {
                ScheduleSessionResponse scheduleSessionResponse = new ScheduleSessionResponse();
                scheduleSessionResponse.setSssnId(session.getId());
                scheduleSessionResponse.setErrorInfoJsonList(new ArrayList<ErrorInfoJson>());
                scheduleSessionResponse.setStatus(ReturnCode.STATUS_OK);
                retVal.getSessionResponses().add(scheduleSessionResponse);
            }
            retVal = createExamsMatters(retVal, "TFCEEXAM", marketTypeCode);
        }

        if(retVal.getSessionResponses() != null && !retVal.getSessionResponses().isEmpty()){
            for (ScheduleSessionResponse session : retVal.getSessionResponses()) {
                msgBuilder.append(System.lineSeparator()).append("Session Id:------> "+session.getSssnId());
                msgBuilder.append(System.lineSeparator()).append("Status :------> "+session.getStatus());
                if(session.getErrorInfoJsonList() != null && !session.getErrorInfoJsonList().isEmpty()){
                    msgBuilder.append(System.lineSeparator()).append("Error List found Size:------> "+session.getErrorInfoJsonList().size());
                    for (ErrorInfoJson errorInfoJson : session.getErrorInfoJsonList()) {
                        msgBuilder.append(System.lineSeparator()).append("Error code:--->"+errorInfoJson.getErrorCode());
                        msgBuilder.append(System.lineSeparator()).append("Error Message:--->"+errorInfoJson.getErrorMessage());
                    }
                }
            }
        }else
            msgBuilder.append("No Sessions processed for this market code.");

        sendSupportEmail(msgBuilder.toString());
        return retVal;

    }

    public void sendSupportEmail(String message){
        // send email notification to ourselves...
        List<EmailRecipient> to = new ArrayList<>();
        to.add(new EmailRecipient(examEmailSupportUserNm, examEmailSupportAddr));
        try {
            if (examEmailSupportUserNm != null && !examEmailSupportUserNm.trim().equalsIgnoreCase(""))
                sendEmail(null, null, null, to, "[{Env}] -  Unable to re-process scheduling errors",
                        "We were unable to re-process following entities: <br/><br/>" + message);
        } catch (EmailNotificationException e) {
            log.error("Unable to send email notification about errors during re-processing scheduling errors. "
                    + e.getMessage());
        }
    }

    public List<PsTypeSubTypeComponentMapView> getPsTypeSubTypeComponentMapView()throws Exception {
        List<PsTypeSubTypeComponentMapView> views = psExamDao.getPsTypeSubTypeComponentMapView();
        return views;
    }
}
