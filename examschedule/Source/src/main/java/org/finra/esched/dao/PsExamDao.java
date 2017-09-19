package org.finra.esched.dao;

import java.math.BigDecimal;
import java.util.List;

import org.finra.esched.domain.*;
import org.finra.esched.domain.ui.*;
import org.finra.esched.service.rest.ui.PsSchedSessionRequest;
import org.finra.esched.service.rest.ui.PsSchedSessionResponse;
import org.finra.esched.service.rest.ui.ScheduleSessionResponse;
import org.finra.exam.common.grid.GridRequest;
import org.finra.exam.common.security.domain.User;
import org.springframework.stereotype.Service;

@Service
public interface PsExamDao extends IBaseHibernateDAO {
	

	int createVersion(int staffId);
	int getCurrentVersion();
	
	void processVersion(int versionId);
	List<Integer> processNMAVersion(int versionId);
	

	PsSession getPsSessionById(Integer sssnId);
	


	
	void verifyComponents();
	

	
	List<PsSessionView> getPsExams(GridRequest rq);

	PsSessionView getPsExamsById(Long id)throws Exception;

	int getPsExamsCount(GridRequest rq);
	
	List<PsOvrdReason> getOvrdReasons(boolean isMr);

	PsSchedSessionResponse schedPsSessions(PsSchedSessionRequest rq, User schedUser) throws Exception;
	
	public void scheduleSession(PsSchedSessionView examRq, User schedUser) throws Exception;
	
	public List<PsDistrictView> getDistricts();
	public PsDistrictView getUserDistrict(User schedUser);
	
	public List<PsSessionStatus> getStatuses();
	
	List<PsSession> getListForReprocessing(int version, String status);
	PsFirmStaffDistrict getFirmStaff(int firmId);

	void saveExamWorkspaceSyncLog(PsExamWorkspaceSyncLog bean);


	int loadInputData(int versionId);
	int loadRSANMAData(int versionId);
	void setSessionStatus(Integer sssnId, String status);
	List<PsFirmBillableEntity> getFirmBillableEntity(int firmId, int versionId, String spRequirement, String finopRequirement);
	

	PsFirm getPsFirmById(Integer firmId);
	
	void saveEmailNotificationsLog(PsEmailNotificationsLog bean);
	
	String getExamComponentFromMatterType(String component, int matterType);
	String getExamComponentFromMatterSubType(String component, int matterSubType);
	boolean isRoleAccess(long appUserId, long roleId) throws Exception;
	
	List<PsMatterDate> getBranchCauseMatterDate(Integer spExamId);
	List<PsMatterStaff> getBranchCauseMatterStaff(Integer spExamId);
	PsMatterStarData getBranchCauseMatterStarData(Integer spExamId);
	PsMarketMatterMetaData getMarketMatterMetaData(String marketTypeCode);
	public void resetMarketSessions(String marketTypeCode) throws Exception;
	List<PsSession> getMarketListForReprocessing(int version);
	PsFirmStaffMarket getFirmStaffMarket(int firmId, String marketTypeCode);
	List<PsFirmMarketBillableEntity> getFirmMarketBillableEntity(int firmId, String marketTypeCode);
	List<PsExamCategoryType> getExamCategories();
	List<PsExamTypeType> getExamTypes();
	List<PsExamSubTypeType> getExamSubTypes();	
	List<PsRegulatorySignificance> getRegulatorySignificance();
	Integer getMatterTypeId(String examTypeCd);
	Integer getMatterSubTypeId(String examSubTypeCd);
	String getDistrictCode(Integer districtId);
	BigDecimal getExamDomainId(Integer examId);
	String getUserId(Integer applicationUserId);
	Long getSchdlOtptSeq();
	String getAnnualPlanningPhase();

	public PsSessionView saveFirmSession(SaveFirmSessionRequest saveFirmSessionRequest) throws Exception;
	public ScheduleSessionResponse createExamsMatters(PsSchedSessionRequest psSchedSessionRequest) throws Exception;
	public void saveOutput(PsOutput bean) throws Exception;
	public List<PsTypeSubTypeComponentMapView> getPsTypeSubTypeComponentMapView()throws Exception;

    public List<PsTypeSubTypeComponentMapView> saveOrUpdateTypeSubTypeMappings(PsTypeSubTypeComponentMapView[] psTypeSubTypeComponentMapViewArray);
	public void schdlLoadComponentMapping() throws Exception;
	public void saveBean(Object o) throws Exception;
	public void saveBeanInTransaction(Object o) throws Exception;
}
