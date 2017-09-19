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
public class PsCmpntOvrrdResponse extends ReturnCode {
    @XmlAttribute(name="spApproved")
    private boolean spApproved;
    
    @XmlAttribute(name="fnApproved")
    private boolean fnApproved;
    
    
    @XmlAttribute(name="isNonColabSssn")
    private boolean nonColabSssn;
    
    @XmlAttribute(name="statusTx")
    private String statusTx;
    
    
    public PsCmpntOvrrdResponse(){
    	super();
    }
    
    public PsCmpntOvrrdResponse(boolean spApproved, boolean fnApproved, boolean isNonColabSssn, String statusTx) {
    	this();
    	this.spApproved=spApproved;
    	this.fnApproved=fnApproved;
    	this.nonColabSssn=isNonColabSssn;
    	
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
	
	public boolean isNonColabSssn() {
		return nonColabSssn;
	}

	public void setNonColabSssn(boolean isNonColabSssn) {
		this.nonColabSssn = isNonColabSssn;
	}
	
	public String getStatusTx() {
		return statusTx;
	}

	public void setStatusTx(String statusTx) {
		this.statusTx = statusTx;
	}

	@Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
