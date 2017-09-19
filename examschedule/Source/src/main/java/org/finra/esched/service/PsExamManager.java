package org.finra.esched.service;

import org.finra.esched.dao.PsExamDao;
import org.finra.esched.domain.*;
import org.finra.esched.domain.ui.*;
import org.finra.esched.exception.*;
import org.finra.esched.service.impl.PsExamManagerImpl.EmailRecipient;
import org.finra.esched.service.rest.ui.*;
import org.finra.exam.common.domain.ui.ReturnCode;
import org.finra.exam.common.domain.ui.ReturnCodeJson;
import org.finra.exam.common.grid.GridRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface PsExamManager{

	void setPsExamDao(PsExamDao psExamDao);
	void processPS();
	void processNMA();
	void verifyPS();
	
	int getCurrentPsVersion();

	List<PsSessionView> getPsExams(GridRequest rq);
	int getPsExamsCount(GridRequest rq);
	PsSessionView getPsExamsById(Long id)throws Exception;
	
	List<PsOvrdReason> getMnReasons();
	List<PsOvrdReason> getMrReasons();
	List<PsDistrictView> getDistricts();
	PsDistrictView getUserDistrict();
	List<PsSessionStatus> getStatuses();

	PsSchedSessionResponse schedPsSessions(PsSchedSessionRequest rq) throws Exception;
	List<PsSession> getListForReprocessing(int versionId, String status);
	PsFirmStaffDistrict getFirmStaff(int firmId);



	void setSessionStatus(Integer sssnId, String status);
	PsSession getPsSessionById(Integer sssnId);
	
	String sendEmail(PsFirm firm, String type, EmailRecipient from,
			List<EmailRecipient> to, String subject, String body)
			throws EmailNotificationException;
	
	boolean isRoleAccess(long roleId) throws Exception;
	List<PsSession> getMarketListForReprocessing(int versionId);
	PsFirmStaffMarket getFirmStaffMarket(int firmId, String marketTypeCode);
	PsMarketMatterMetaData getMarketMatterMetaData(String marketTypeCode);
    List<PsExamCategoryType> getExamCategoryTypes();
    List<PsExamTypeType> getExamTypes();
    List<PsExamSubTypeType> getExamSubTypes();
    List<PsRegulatorySignificance> getRegulatorySignificance();
    String getAnnualPlanningPhase();

	public PsSessionView saveFirmSession(SaveFirmSessionRequest saveFirmSessionRequest) throws Exception;
	public PsSchedSessionResponse createExamsMatters(PsSchedSessionResponse schedPsSessions, String tfceExamType, String marketTypeCode) throws Exception;
	public PsSchedSessionResponse reprocessExamsMatters(String marketType) throws Exception;
	public PsSchedSessionResponse processMarketExams(int versionId, String marketTypeCode) throws Exception;
	public List<PsTypeSubTypeComponentMapView> getPsTypeSubTypeComponentMapView()throws Exception;
	public List <PsTypeSubTypeComponentMapView> saveOrUpdateTypeSubTypeMappings(PsTypeSubTypeComponentMapView[] psTypeSubTypeComponentMapViewArray) throws Exception;


	}
