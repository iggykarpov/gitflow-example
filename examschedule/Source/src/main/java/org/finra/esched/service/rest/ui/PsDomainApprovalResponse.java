package org.finra.esched.service.rest.ui;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.finra.exam.common.domain.ui.ReturnCode;
import org.finra.exam.common.security.domain.User;

/**
 * @author RuzhaV
 */
@XmlRootElement(name="response")
@XmlAccessorType(XmlAccessType.NONE)
public class PsDomainApprovalResponse extends ReturnCode {
    @XmlAttribute(name="spApproved")
    private boolean spApproved;
    
    @XmlAttribute(name="spApproveUserNm")
    private String spApproveUserNm;
    
    @XmlAttribute(name="spApproveDt")
    private Date spApproveDate;
    
    @XmlAttribute(name="fnApproved")
    private boolean fnApproved;
    
    @XmlAttribute(name="fnApproveUserNm")
    private String fnApproveUserNm;
    
    @XmlAttribute(name="fnApproveDt")
    private Date fnApproveDate;
    
    
    @XmlAttribute(name="statusTx")
    private String statusTx;
    
    
    public PsDomainApprovalResponse(){
    	super();
    }
    
    public PsDomainApprovalResponse(boolean spApproved, User spApproveUser, Date spApproveDt, boolean fnApproved, User fnApproveUser, Date fnApproveDt, String statusTx) {
    	this();
    	this.spApproved=spApproved;
    	this.spApproveUserNm=(spApproveUser!=null ? spApproveUser.getFirstName()+" "+spApproveUser.getLastName() : "");
    	this.spApproveDate=spApproveDt;
    	
    	this.fnApproved=fnApproved;
    	this.fnApproveUserNm=(fnApproveUser!=null ? fnApproveUser.getFirstName()+" "+fnApproveUser.getLastName() : "");
    	this.fnApproveDate=fnApproveDt;
    	
    	this.statusTx=statusTx;
    }

    public boolean isSpApproved() {
		return spApproved;
	}

	public void setSpApproved(boolean spApproved) {
		this.spApproved = spApproved;
	}

	public boolean isFnApproved() {
		return fnApproved;
	}

	public void setFnApproved(boolean fnApproved) {
		this.fnApproved = fnApproved;
	}
	
	public String getStatusTx() {
		return statusTx;
	}

	public void setStatusTx(String statusTx) {
		this.statusTx = statusTx;
	}

	
	
	public String getSpApproveUserNm() {
		return spApproveUserNm;
	}

	public void setSpApproveUserNm(String spApproveUserNm) {
		this.spApproveUserNm = spApproveUserNm;
	}

	public Date getSpApproveDate() {
		return spApproveDate;
	}

	public void setSpApproveDate(Date spApproveDate) {
		this.spApproveDate = spApproveDate;
	}

	public String getFnApproveUserNm() {
		return fnApproveUserNm;
	}

	public void setFnApproveUserNm(String fnApproveUserNm) {
		this.fnApproveUserNm = fnApproveUserNm;
	}

	public Date getFnApproveDate() {
		return fnApproveDate;
	}

	public void setFnApproveDate(Date fnApproveDate) {
		this.fnApproveDate = fnApproveDate;
	}

	@Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
