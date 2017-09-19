package org.finra.esched.domain;

import java.util.Date;

import org.finra.esched.domain.PsTriggerDtType.TRIGGER_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsTriggerDt {
	
	private final Logger log = LoggerFactory.getLogger(PsTriggerDt.class);
	
	private Date trggrDt;
	private TRIGGER_TYPE trggrDtType;
	
	public PsTriggerDt(Date trggrDt, TRIGGER_TYPE trggrDtType) {
		this.trggrDt=trggrDt;
		this.trggrDtType=trggrDtType;
	}
	
	public Date getTrggrDt() {
		return trggrDt;
	}
	public void setTrggrDt(Date trggrDt) {
		this.trggrDt = trggrDt;
	}

	public TRIGGER_TYPE getTrggrDtType() {
		return trggrDtType;
	}

	public void setTrggrDtType(TRIGGER_TYPE trggrDtType) {
		this.trggrDtType = trggrDtType;
	}
	

}
