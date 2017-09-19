package org.finra.esched.domain.ui;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.finra.exam.common.domain.ui.ReturnCode;
import org.finra.esched.domain.ui.PsExamCategoryType;
import org.finra.esched.domain.ui.PsExamSubTypeType;
import org.finra.esched.domain.ui.PsExamTypeType;


import javax.xml.bind.annotation.*;

import java.util.List;


@XmlRootElement(name="examcreate")
@XmlAccessorType(XmlAccessType.FIELD) 
public class PsCreateExamResponse extends ReturnCode{
	
	public PsCreateExamResponse() {
		super();
	}

	@XmlElement(name="examid")
    private Integer examId;
	
	@XmlElement(name="matterid")
    private String matterId;

	
	public Integer getExamId() {
		return examId;
	}


	public void setExamId(Integer examId) {
		this.examId = examId;
	}


	public String getMatterId() {
		return matterId;
	}


	public void setMatterId(String matterId) {
		this.matterId = matterId;
	}


	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this,  ToStringStyle.MULTI_LINE_STYLE );
	}
}