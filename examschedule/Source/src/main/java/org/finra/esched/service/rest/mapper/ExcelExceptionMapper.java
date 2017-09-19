package org.finra.esched.service.rest.mapper;

import org.finra.exam.common.domain.ui.ReturnCode;
import org.finra.exam.common.excel.exception.ExcelException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author atsirel
 * @since 2/24/2015
 */
public class ExcelExceptionMapper implements ExceptionMapper<ExcelException> {
    @Override
    public Response toResponse(ExcelException exception) {
        ReturnCode returnCode = new ReturnCode();
        returnCode.setCode(ReturnCode.STATUS_ERROR);
        returnCode.setMessage(ReturnCode.MSG_ERROR);
        returnCode.setErrorDetails(exception);
        ResponseBuilder rBuild = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        return rBuild.type(MediaType.APPLICATION_XML)
                .entity(returnCode)
                .build();
    }
}
