package org.finra.esched.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PsFirmPK implements Serializable {

	private int firmId;
	private int versionId;
	
	public PsFirmPK() {
	}
	
	public PsFirmPK(int firmId, int versionId){
		this.firmId=firmId;
		this.versionId=versionId;
	}
	
	@Column(name="FIRM_ID")
	public int getFirmId() {
		return firmId;
	}
	public void setFirmId(int firmId) {
		this.firmId = firmId;
	}
	
	@Column(name="SCHDL_EXAM_SNPSH_ID")
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PsFirmPK) {
			PsFirmPK pk = (PsFirmPK)obj;
            return firmId == pk.firmId && versionId == pk.versionId;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(firmId + versionId);
	}
	
}
