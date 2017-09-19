package org.finra.esched.service.star.impl;

import org.finra.esched.service.star.PsStarManager;
import org.springframework.stereotype.Service;

@Service("psStarService")
public class PsStarManagerImpl implements PsStarManager {
/*
    public static final SimpleDateFormat CORRELATION_ID_DATE_FORMAT = new SimpleDateFormat("yyMMddHHmmss");
    public static final String NEW_STATUS = "NEW";
    public static final String PUBLISHED_STATUS = "PUBLISHED";
    public static final String ERROR_STATUS = "ERROR";
    private static final Logger log = LoggerFactory.getLogger(PsStarManagerImpl.class);
    public static final String CREATE_MATTER_PREFIX = "EW_CM_";
    public static final String UPDATE_MATTER_PREFIX = "EW_UM_";
    public static final int BRANCH_CAUSE_MATTER = 25;
    public static final int MATTER_MANAGEMENT = 2;
    public static final int EXAM_ANNOUNCMENT_DATE = 83;
    public static final int ON_SITE_START_DATE = 4;
    public static final int PROJECTED_TYPE = 1;
    public static final int NON_SPECIFIC_PRODUCT_ID= 20;
    public static final String NON_SPECIFIC_PRODUCT_NAME= "Non-Specific";
    public static final int BORAM_ORIGIN_ID= 524;
    public static final int BRANCH_COMMENT_ID= 1;
    public static final String BRANCH_COMMENT_TEXT= "Please refer to the branch office control sheet for further details.";
    public static final int BRANCH_CAUSE_CODE_ID= 389;
    
    private MatterServiceClient matterServiceClient;


	@Value("${dispositionPublishURI}")
	private String dispositionPublishURI;


	@Autowired
	public void setMatterServiceClient(
			MatterServiceClient matterServiceClient) {
		this.matterServiceClient = matterServiceClient;
	}



    @Override
    public void linkMatters(PsFirmStaffDistrict firmStaff, PsOutput fnExam, PsOutput spExam, int versionId) throws ExamOrMatterLinkingException {
        String publishingUser = firmStaff.getFinopSupervisorId();
        Integer firmId = fnExam.getSession().getFirm().getId().getFirmId();
        User currentUser = UserContext.getUser();
        PsStarSyncLog starSyncLog = createPsStarSyncLog(
                (long) fnExam.getOutputId(),
                generateLinkMatterPublishType(firmId, fnExam.getMatterId(), spExam.getMatterId(), publishingUser),
                currentUser.getStaffId());
        CreateMatterBuilder builder = new CreateMatterBuilder();
        try {
            String correlationId = generateLinkMatterCorrelationId(firmId, fnExam.getMatterId(), spExam.getMatterId());
            UpdateMatterCommand updateMatterCommand = builder.composeUpdateMatterCommand(fnExam, spExam, correlationId);
            MatterServiceInputType matterServiceInput = new MatterServiceInputType();
            matterServiceInput.setMessageRequestID(correlationId);
            matterServiceInput.setUpdateMatter(updateMatterCommand);
            MatterServiceRequest req = new MatterServiceRequest();
            req.setMatterServiceInput(matterServiceInput);
            String reqXml = matterServiceClient.reqToXml(req);
            starSyncLog.setRequest(reqXml);
            // invoke ws
            log.info("..before processRequest Link Matter");
            MatterServiceResponse value = matterServiceClient.processRequest(req, publishingUser);
            String respXml;
            if (null != value) {
                respXml = matterServiceClient.respToXml(value);
                starSyncLog.setResponse(respXml);
                String error = readErrors(value);
                if (0 < error.length()) {
                    throw new Exception(error);
                }
                starSyncLog.setStatus(PUBLISHED_STATUS);
            } else {
                throw new Exception("No response received from STAR");
            }
        } catch (Exception e) {
            log.error("Failed to link matters", e);
            starSyncLog.setStatus(ERROR_STATUS);
            throw new ExamOrMatterLinkingException(e.getMessage());
        } finally {
            saveStarSyncLog(starSyncLog);
        }

    }

    private String readErrors(MatterServiceResponse value) {
        List<ErrorType> errors = value.getMatterServiceOutput().getResult().getErrors();
        StringBuilder msgBuilder = new StringBuilder();

        for (ErrorType error : errors) {
            msgBuilder.append(error.getMessage()).append(" ");
        }
        return msgBuilder.toString();
    }

    private MatterServiceRequest composeMatterServiceRequest(PsOutput psOutput,
                                                             PsFirmStaffDistrict firmStaff,
                                                             int versionId,
                                                             Integer firmId,
                                                             int matterType,
                                                             int matterSubType,
                                                             String marketTypeCode,
                                                             PsMarketMatterMetaData marketMatterMetaData) {
    	List<PsFirmBillableEntity> beList = null;
        Integer supervisor;
        Integer coordinator;
        CreateMatterBuilder builder = new CreateMatterBuilder();
        String correlationId = generateCreateMatterCorrelationId(matterType, firmId +"");
        if (psOutput.isFinOpMatter()) {
            coordinator = firmStaff.getFinopCoordinator();
            supervisor = firmStaff.getFinopSupervisor();
        } else {
            coordinator = firmStaff.getSpCoordinator();
            supervisor = firmStaff.getSpSupervisor();
        }
        
        if (marketTypeCode == null) {
        	//this means matter is for Member Reg
	        String spRequirement = psExamDao.getExamComponentFromMatterType("SALES_PRACTICE", matterType);
	        if (!spRequirement.equals("Y")) spRequirement = psExamDao.getExamComponentFromMatterSubType("SALES_PRACTICE", matterSubType);
	
	        String finopRequirement = psExamDao.getExamComponentFromMatterType("FINOP", matterType);
	        if (!finopRequirement.equals("Y")) finopRequirement = psExamDao.getExamComponentFromMatterSubType("FINOP", matterSubType);
	        if (!finopRequirement.equals("Y")) finopRequirement = psExamDao.getExamComponentFromMatterType("FIRST_FINOP", matterType);
	        if (!finopRequirement.equals("Y")) finopRequirement = psExamDao.getExamComponentFromMatterSubType("FIRST_FINOP", matterSubType);
	        //use BEs using Member Reg logic
	        beList = psExamDao.getFirmBillableEntity(firmId, versionId, spRequirement, finopRequirement);
        } else {
        	//this means matter is for Market Reg
        	//use BEs that was imported from PRET
        	List<PsFirmMarketBillableEntity> marketBEList = psExamDao.getFirmMarketBillableEntity(firmId, marketTypeCode);
        	if (marketBEList != null && marketBEList.size() > 0) {
	        	beList = new ArrayList<PsFirmBillableEntity>();
	        	int id = 0;
	        	for (PsFirmMarketBillableEntity bean:marketBEList) {
	        		PsFirmBillableEntity marketBE = new PsFirmBillableEntity();
	        		marketBE.setId(id);
	        		marketBE.setBillableEntity(bean.getBillableEntityId());
	        		marketBE.setCurrentFlag("Y");
	        		beList.add(marketBE);
	        		id++;
	        	}
        	}
        }
        	
        CreateMatterCommand cmt = builder.composeCreateMatterCommand(correlationId,
                firmId, firmStaff.getMainBranchId(), matterType, matterSubType,
                coordinator, supervisor, psOutput.getPrjEwsd(), psOutput.getPrjFwsd(),
                beList, psOutput.getSpDistrict(), psOutput.getFnDistrict(), marketMatterMetaData);
        MatterServiceInputType matterServiceInput = new MatterServiceInputType();
        matterServiceInput.setMessageRequestID(correlationId);
        matterServiceInput.setCreateMatter(cmt);
        MatterServiceRequest req = new MatterServiceRequest();
        req.setMatterServiceInput(matterServiceInput);
        return req;
    }


    private MatterServiceRequest composeMatterServiceRequest(PsMatter matter) {
        CreateMatterBuilder builder = new CreateMatterBuilder();
        String correlationId = generateCreateMatterCorrelationId(matter.getMatterTypeID(), generateContactId(matter.getContactList()));
        
        CreateMatterCommand cmt = builder.composeCreateMatterCommand(correlationId, matter);
        MatterServiceInputType matterServiceInput = new MatterServiceInputType();
        matterServiceInput.setMessageRequestID(correlationId);
        matterServiceInput.setCreateMatter(cmt);
        MatterServiceRequest req = new MatterServiceRequest();
        req.setMatterServiceInput(matterServiceInput);
        return req;
    }

    
    @Override
    public void saveStarSyncLog(PsStarSyncLog bean){
		psExamDao.saveStarSyncLog(bean);
	}

    private PsStarSyncLog createPsStarSyncLog(Long outputId, String publishType, Long userId) {
        PsStarSyncLog bean = new PsStarSyncLog();
		bean.setOutputId(outputId);
		bean.setPublishDate(new Date());
        bean.setPublishType(publishType);
        bean.setStatus(NEW_STATUS);
        bean.setUserId(userId);
		return bean;
	}
 

    private static String generateCreateMatterCorrelationId(int matterType, String firmId) {
        return CREATE_MATTER_PREFIX + CORRELATION_ID_DATE_FORMAT.format(new Date()) + "_"
                + firmId + "_"
                + matterType;
    }

    private static String generateLinkMatterCorrelationId(int firmId, String fnMatter, String spMatter) {
        return UPDATE_MATTER_PREFIX + CORRELATION_ID_DATE_FORMAT.format(new Date()) + "_"
                + firmId + "_"
                + fnMatter + "_"
                + spMatter;
    }


    private static String generateLinkMatterPublishType(Integer firmId, String fnMatter, String spMatter,
                                                        String publishUser) {
        return UPDATE_MATTER_PREFIX + firmId + "_" + fnMatter + "_" + spMatter + "_" + publishUser;
    }
    
    private static String generateContactId(List<CreateExamContact> contactList) {
    	String retVal = "";
    	
    	if (contactList.get(0) != null) retVal = contactList.get(0).getCrdNumber() +"";
    	return retVal;
	}    
*/
}
