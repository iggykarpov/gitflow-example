package org.finra.esched.service.rest;

import io.swagger.annotations.Api;
import org.finra.esched.domain.PsTypeSubTypeComponentMap;
import org.finra.esched.domain.PsTypeSubTypeComponentMapView;
import org.finra.esched.domain.ui.CheckPermissionsResponse;
import org.finra.esched.domain.ui.PsLookUpResponse;
import org.finra.esched.domain.ui.SaveFirmSessionRequest;
import org.finra.esched.service.rest.ui.*;
import org.finra.exam.common.domain.ui.ReturnCode;
import org.finra.exam.common.grid.GridRequest;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api("Exam-scheduling related services")
@Component
@WebService
@Path("/PS/")
@Produces("application/xml")
public interface PsExamWebService  {

	@POST
	@Path("/checkUserPermissions")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	CheckPermissionsResponse checkUserPermissions();

	@GET
	@Path("/keepAlive")
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ReturnCode keepAlive();

	@POST
	@Path("/getPsCmpnts")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public PrescheduleResponse getPsExams(GridRequest rq);

	@POST
	@Path("/getPsCmpnts/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public PrescheduleResponse getPsExamsById(@PathParam("id") Long id);

	@POST
	@Path("/getLookUpLists")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	PsLookUpResponse getLookUpLists();


	@POST
	@Path("/saveFirmSession/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	PrescheduleResponse saveFirmSession(@PathParam("id") Long id, SaveFirmSessionRequest saveFirmSessionRequest);

	@POST
	@Path("/schedPS")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PsSchedSessionResponse schedPsSessions(PsSchedSessionRequest psSchedSessionRequest) throws Exception;

	@POST
	@Path("/reProcessErrors")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public PsSchedSessionResponse reProcessErrors(@FormParam("marketType") String marketType) throws Exception;

	@POST
	@Path("/reProcessPrimaryMatterCreationFailedErrors")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public PsSchedSessionResponse reProcessPrimaryMatterCreationFailedErrors(@FormParam("sessionId") String sessionId) throws Exception;


	@POST
	@Path("/processMarketExams")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public PsSchedSessionResponse processMarketExams(@FormParam("marketType") String marketType) throws Exception;

	@POST
    @Path("/processPS")
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ReturnCode processPS();
	
	@GET
    @Path("/processNMA")
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ReturnCode processNMA();
	
	
	@GET
    @Path("/verifyPS")
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ReturnCode verifyPS();
	
	//@WebService
	@GET
	@Path("/exportExcel")
	@Produces("application/vnd.ms-excel")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response exportPsExams(@QueryParam("gridrequest") String encodedGridRequest);

	@GET
	@Path("/getTypeSubTypeMappings")
	@Produces(MediaType.APPLICATION_JSON)
	public TypeSubTypeMappingViewResponse getTypeSubTypeMappings();

	@POST
	@Path("/saveOrUpdateTypeSubTypeMappings")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TypeSubTypeMappingResponse saveOrUpdateTypeSubTypeMappings(PsTypeSubTypeComponentMapView[] psTypeSubTypeComponentMapViewArray);

}
