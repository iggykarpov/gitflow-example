package org.finra.esched.service.rest.impl;

import io.swagger.annotations.Api;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.finra.esched.domain.PsSessionStatus;
import org.finra.esched.domain.PsSessionView;
import org.finra.esched.domain.PsTypeSubTypeComponentMap;
import org.finra.esched.domain.PsTypeSubTypeComponentMapView;
import org.finra.esched.domain.ui.CheckPermissionsResponse;
import org.finra.esched.domain.ui.PsLookUpResponse;
import org.finra.esched.domain.ui.SaveFirmSessionRequest;
import org.finra.esched.exception.PsSessionStatusException;
import org.finra.esched.service.PsExamManager;
import org.finra.esched.service.rest.PsExamWebService;
import org.finra.esched.service.rest.ui.*;
import org.finra.exam.common.domain.ui.ReturnCode;
import org.finra.exam.common.domain.ui.ReturnCodeJson;
import org.finra.exam.common.excel.exception.ExcelException;
import org.finra.exam.common.excel.model.Document;
import org.finra.exam.common.excel.processor.DefaultExcelExportFilterProcessor;
import org.finra.exam.common.excel.util.ExcelControllerUtil;
import org.finra.exam.common.excel.view.ExtendedAnnotationExcelGridView;
import org.finra.exam.common.grid.GridObjectMapper;
import org.finra.exam.common.grid.GridRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Api("Exam-scheduling related services")
@Service("psExamWebService")
public class PsExamWebServiceImpl implements PsExamWebService {

	public static final long ANNUAL_PLANNING_ROLE_ID = 7;
	public static final long INDIVIDUAL_ROLE_ID = 21;
	public static final long SCHEDULING_ROLE_ID = 13;
	public static final long COMPONENT_MAPPING_ROLE_ID = 23;

	public static final int STATUS_SCHEDULING_ERROR = -8;

	private static final Logger log = LoggerFactory.getLogger(PsExamWebServiceImpl.class);

	@Autowired
	private PsExamManager psExamManager;


	public ReturnCodeJson getStandardError(Exception ex, Object object) {
		ReturnCodeJson returnCodeJson = new ReturnCodeJson();
		if(object instanceof ReturnCodeJson)
			returnCodeJson = (ReturnCodeJson) object;

		returnCodeJson.setStatus(ReturnCode.STATUS_ERROR);
		returnCodeJson.setMessage("There was an error processing your request.");
		//returnCodeJson.setErrorDetails(ex);
		log.error("Processing request error", ex);
		return returnCodeJson;
	}

	public ReturnCode getStandardErrorXml(Exception ex, Object object) {
		ReturnCode returnCode = new ReturnCode();
		if(object instanceof ReturnCode)
			returnCode = (ReturnCode) object;

		returnCode.setStatus(ReturnCode.STATUS_ERROR);
		returnCode.setMessage("There was an error processing your request.");
		log.error("Processing request error", ex);
		return returnCode;
	}

	public CheckPermissionsResponse checkUserPermissions() {
		CheckPermissionsResponse response = new CheckPermissionsResponse();
		try {

			response.setAnnualPlanning(psExamManager.isRoleAccess(ANNUAL_PLANNING_ROLE_ID) ? "Y" : "N");
			response.setComponentMapping(psExamManager.isRoleAccess(COMPONENT_MAPPING_ROLE_ID) ? "Y" : "N");
			response.setIndividual(psExamManager.isRoleAccess(INDIVIDUAL_ROLE_ID) ? "Y" : "N");

		} catch (Exception ex) {
			return (CheckPermissionsResponse)getStandardErrorXml(ex, response);
		}
		return response;
	}

	@Override
	public ReturnCode keepAlive() {
		ReturnCode response = new ReturnCode();
		try {
			response.setMessage(new Date().toString());
		} catch (Exception ex) {
			return getStandardErrorXml(ex, response);		}
		return response;
	}

	public PrescheduleResponse getPsExams(GridRequest rq) {
		PrescheduleResponse response = new PrescheduleResponse();

		try {
			String annualPlanningPhase = psExamManager.getAnnualPlanningPhase();

			response.setAnnualPlanningPhase(annualPlanningPhase);
			List<PsSessionView> firms = psExamManager.getPsExams(rq);
			response.setPsExams(firms);

			int total = psExamManager.getPsExamsCount(rq);
			response.setTotal(total);

		} catch (Exception ex) {
			response.setStatus(-1);
			response.setMessage("There was an error processing your request.");
			//response.setErrorDetails(ex);
			log.error("Processing request error", ex);
		}
		return response;
	}

	@Override
	public PrescheduleResponse getPsExamsById(Long id){
		PrescheduleResponse response = new PrescheduleResponse();
		try {
			response.setPsExams(new ArrayList<PsSessionView>());
			response.getPsExams().add(psExamManager.getPsExamsById(id));
			response.setTotal(1);
			return response;
		} catch (Exception ex) {
			response.setStatus(-1);
			response.setMessage("There was an error processing your request.");
			//response.setErrorDetails(ex);
			log.error("Processing request error", ex);
		}
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public PsLookUpResponse getLookUpLists() {
		PsLookUpResponse retVal = new PsLookUpResponse();
		try {
			log.debug("::enter getLookUpLists");

			retVal.setExamcategorytypes(psExamManager.getExamCategoryTypes());
			retVal.setExamTypeTypes(psExamManager.getExamTypes());
			retVal.setExamSubTypeTypes(psExamManager.getExamSubTypes());
			retVal.setRegulatorySignifiance(psExamManager.getRegulatorySignificance());

			retVal.setCmpntOvrdRqReasons(psExamManager.getMrReasons());
			retVal.setCmpntOvrdNonRqReasons(psExamManager.getMnReasons());
			retVal.setCmpntDistricts(psExamManager.getDistricts());
			retVal.setStatuses(psExamManager.getStatuses());

			retVal.setPsSnapshotId(psExamManager.getCurrentPsVersion());
			retVal.setPsUserDistrictCd((psExamManager.getUserDistrict()!=null ? psExamManager.getUserDistrict().getCode() : null));
			retVal.setSchedulingAccess(psExamManager.isRoleAccess(SCHEDULING_ROLE_ID));

			retVal.setImpactsFlags(new String[]{"Low","Med-Low","Med-High","High"});
			retVal.setRiskFlags(new String[]{"Low","Med-Low","Med-High","High"});

			return retVal;
		} catch (Exception e){
			return (PsLookUpResponse)getStandardError(e, retVal);
		}

	}

	@Override
	public PrescheduleResponse saveFirmSession(Long id, SaveFirmSessionRequest saveFirmSessionRequest){
		PrescheduleResponse response = new PrescheduleResponse();
		try{
			if(id == null)
				throw new Exception("id is required to modify component");
			saveFirmSessionRequest.setId(id);
			PsSessionView view = psExamManager.saveFirmSession(saveFirmSessionRequest);
			response.setPsExams(new ArrayList<PsSessionView>());
			response.getPsExams().add(view);
			response.setTotal(1);
		} catch (PsSessionStatusException statEx) {
			response.setStatus(STATUS_SCHEDULING_ERROR);
			response.setMessage(statEx.getMessage());
		} catch (Exception ex) {
			response = (PrescheduleResponse)getStandardError(ex, response);
		}
		return  response;
	}

	public PsSchedSessionResponse schedPsSessions(PsSchedSessionRequest psSchedSessionRequest) throws Exception{
		PsSchedSessionResponse response = new PsSchedSessionResponse();

		try {
			response = psExamManager.schedPsSessions(psSchedSessionRequest);
				response = psExamManager.createExamsMatters(response, "NonTFCEEXAM", null);

		} catch (Exception ex) {
			log.error("Processing request error", ex);
			throw ex;
		}
		return response;
	}

	public PsSchedSessionResponse reProcessErrors(String marketType) throws Exception{

		PsSchedSessionResponse response = new PsSchedSessionResponse();

		try {

			response = psExamManager.reprocessExamsMatters(marketType);

		} catch (Exception ex) {
			throw ex;
		}
		return response;
	}

	/**********************************************************************************************************************************
	Scenarios:
 	 					PrimaryMatterCreation	PrimaryExamCreation		SecondaryMatterCreation	SecondaryExamCreation	Link	Action
Non-Collab	Use Case 1	FAIL	 										N/A						N/A						N/A		Reprocess Firm Session
			Use Case 2	SUCCESS					FAIL					N/A						N/A						N/A		Reprocess Exam Creation
Collab		Use Case 3	FAIL	 	 	 	 																					Reprocess Firm Session
			Use Case 4	SUCCESS					FAIL	 	 	 																Reprocess Primary Exam Creation; Create Secondary Matter and Exam; Manually Link Primary and Secondary
			Use Case 5	SUCCESS					SUCCESS					FAIL	 	 											Create Secondary Matter and Exam; Manually Link Primary and Secondary
			Use Case 6	SUCCESS					SUCCESS					SUCCESS					FAIL	 						Create Secondary Exam; Manually Link Primary and Secondary
			Use Case 7	SUCCESS					SUCCESS					SUCCESS					SUCCESS					FAIL	Manually Link Primary and Secondary

	 **********************************************************************************************************************************/

	public PsSchedSessionResponse reProcessPrimaryMatterCreationFailedErrors(String sessionId) throws Exception{ //Use Cases 1 and 3

		PsSchedSessionResponse psSchedSessionResponse = new PsSchedSessionResponse();
		ScheduleSessionResponse input = new ScheduleSessionResponse();
		input.setSssnId(Integer.parseInt(sessionId));
		input.setStatus(ReturnCode.STATUS_OK);
		List<ScheduleSessionResponse> sssnList = new ArrayList<>();
		sssnList.add(input);
		psSchedSessionResponse.setSessionResponses(sssnList);

		try {

			psSchedSessionResponse = psExamManager.createExamsMatters(psSchedSessionResponse, "NonTFCEEXAM", null);

		} catch (Exception ex) {
			throw ex;
		}
		return psSchedSessionResponse;
	}

	@Override
	public PsSchedSessionResponse processMarketExams(String marketType) throws Exception {
		log.debug("PsExamWebServiceImpl.processMarketExams()");
		PsSchedSessionResponse response = new PsSchedSessionResponse();
		try {
			response =  psExamManager.processMarketExams(psExamManager.getCurrentPsVersion(), marketType);
		} catch (Exception ex) {
			throw ex;
		}
		return response;
	}

	@Override
	public ReturnCode processPS() {
		log.debug("PsExamWebServiceImpl.processPS()");
		ReturnCode response = new ReturnCode();
		try {
			String annualPlanningPhase = psExamManager.getAnnualPlanningPhase();
			if(annualPlanningPhase != null && annualPlanningPhase.equalsIgnoreCase("PRE_SCHEDULE"))
				psExamManager.processPS();
			else{
				response.setStatus(ReturnCode.STATUS_ERROR);
				response.setMessage("Scheduling is not in valid state for batch job. Status is :"+annualPlanningPhase);
			}

		} catch (Exception ex) {
			return getStandardErrorXml(ex, response);
		}
		return response;
	}



	@Override
	public ReturnCode processNMA() {
		log.debug("PsExamWebServiceImpl.processNMA()");
		ReturnCode response = new ReturnCode();
		try {
			psExamManager.processNMA();
		} catch (Exception ex) {
			return getStandardErrorXml(ex, response);
		}
		return response;
	}

	@Override
	public ReturnCode verifyPS() {
		log.debug("PsExamWebServiceImpl.verifyPS()");
		ReturnCode response = new ReturnCode();
		try {
			psExamManager.verifyPS();
		} catch (Exception ex) {
			return getStandardErrorXml(ex, response);
		}
		return response;
	}

	public Response exportPsExams(String encodedGridRequest) {
		GridRequest rq = GridObjectMapper.map(encodedGridRequest);

		// VRuzha: to EXPORT all records,set pageNum && pageSize to -1, DAO will
		// handle that
		rq.setPageNum(-1);
		rq.setPageSize(-1);

		Response response = null;

		try {
			List<PsSessionView> firms = psExamManager.getPsExams(rq);
			response = export(firms);

		} catch (Exception ex) {
			log.error("Processing request error", ex);
		}
		return response;
	}

	private Response export(List<PsSessionView> data) throws ExcelException {
		Map<Integer, List> model = new HashMap<>();
		model.put(0, data);
		Document document = ExcelControllerUtil.unmarshalXml("excelTemplates/firmExport.xml");
		DefaultExcelExportFilterProcessor.process(document, null);
		ExtendedAnnotationExcelGridView excelView = new ExtendedAnnotationExcelGridView(document, PsSessionView.class);
		final HSSFWorkbook workbook = excelView.buildExcelDocument(model);
		Response.ResponseBuilder ok = Response.ok(new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				workbook.write(output);
				output.flush();
			}
		});
		ok.header("Content-Disposition", "attachment; filename=" + document.getFilename() + ".xls");
		return ok.build();
	}

	public TypeSubTypeMappingViewResponse getTypeSubTypeMappings() {

		TypeSubTypeMappingViewResponse response = new TypeSubTypeMappingViewResponse();

		try {

            List<PsTypeSubTypeComponentMapView> views= psExamManager.getPsTypeSubTypeComponentMapView();
			response.setMapping(views);
			response.setTotal(views.size());

		} catch (Exception ex) {
			log.error("Processing request error", ex);
		}
		return response;
	}


	@Override
	public TypeSubTypeMappingResponse saveOrUpdateTypeSubTypeMappings(PsTypeSubTypeComponentMapView[] psTypeSubTypeComponentMapViewArray) {
		TypeSubTypeMappingResponse response = new TypeSubTypeMappingResponse();
		try{
			List <PsTypeSubTypeComponentMapView> psTypeSubTypeComponentMapList = psExamManager.saveOrUpdateTypeSubTypeMappings(psTypeSubTypeComponentMapViewArray);
			response.setMapping(psTypeSubTypeComponentMapList);
			response.setTotal(psTypeSubTypeComponentMapList.size());
		} catch (PsSessionStatusException statEx) {
			response.setStatus(STATUS_SCHEDULING_ERROR);
			response.setMessage(statEx.getMessage());
		} catch (Exception ex) {
			response = (TypeSubTypeMappingResponse)getStandardError(ex, response);
		}
		return response;
	}
}

