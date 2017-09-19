package org.finra.esched.service.rest.ui;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.finra.esched.domain.PsSessionView;
import org.finra.exam.common.domain.ui.ReturnCode;
import org.finra.exam.common.domain.ui.ReturnCodeJson;

/**
 * @author RuzhaV
 */
public class PrescheduleResponse extends ReturnCodeJson {

    private List<PsSessionView> psExams;
    
    private int total;

    private String annualPlanningPhase;
    
    public PrescheduleResponse() {
		this.setStatus(ReturnCode.STATUS_OK);
    }

	public List<PsSessionView> getPsExams() {
		return psExams;
	}

	public void setPsExams(List<PsSessionView> psExams) {
		this.psExams = psExams;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getAnnualPlanningPhase() {
		return annualPlanningPhase;
	}

	public void setAnnualPlanningPhase(String annualPlanningPhase) {
		this.annualPlanningPhase = annualPlanningPhase;
	}
}
