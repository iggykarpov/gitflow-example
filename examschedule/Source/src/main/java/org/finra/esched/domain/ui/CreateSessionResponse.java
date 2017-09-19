package org.finra.esched.domain.ui;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.finra.exam.common.domain.ui.ReturnCode;
import org.finra.esched.domain.ui.PsExamCategoryType;
import org.finra.esched.domain.ui.PsExamSubTypeType;
import org.finra.esched.domain.ui.PsExamTypeType;


import javax.xml.bind.annotation.*;

import java.util.List;


@XmlRootElement(name="sessioncreate")
@XmlAccessorType(XmlAccessType.FIELD) 
public class CreateSessionResponse extends ReturnCode{
	
	public CreateSessionResponse() {
		super();
	}

	@XmlElement(name="sessionid")
    private String sessionId;
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this,  ToStringStyle.MULTI_LINE_STYLE );
	}
}