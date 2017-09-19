package org.finra.esched.domain.ui;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.finra.exam.common.domain.ui.ReturnCode;


import javax.xml.bind.annotation.*;


@XmlRootElement(name="userpermissions")
@XmlAccessorType(XmlAccessType.FIELD) 
public class CheckPermissionsResponse extends ReturnCode{

	public CheckPermissionsResponse() {
		super();
		this.setAnnualPlanning("N");
		this.setIndividual("N");
		this.setComponentMapping("N");
	}

	@XmlElement(name="annualplanning")
    private String annualPlanning;
	
	@XmlElement(name="individual")
    private String individual;

	@XmlElement(name="componentmapping")
	private String componentMapping;

	public String getAnnualPlanning() {
		return annualPlanning;
	}
	public void setAnnualPlanning(String annualPlanning) {
		this.annualPlanning = annualPlanning;
	}

	public String getIndividual() {
		return individual;
	}
	public void setIndividual(String individual) {
		this.individual = individual;
	}

	public String getComponentMapping() {return componentMapping; }
	public void setComponentMapping(String componentMapping) {this.componentMapping = componentMapping; }

	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this,  ToStringStyle.MULTI_LINE_STYLE );
	}
}