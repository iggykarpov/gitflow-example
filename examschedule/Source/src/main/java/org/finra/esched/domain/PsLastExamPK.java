package org.finra.esched.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;

@Embeddable
public class PsLastExamPK implements Serializable {

	private int firmId;
	private String cmpntId;
	private int versionId;
	
	public PsLastExamPK() {
	}
	
	public PsLastExamPK(int firmId, String cmptnId, int versionId){
		this.firmId=firmId;
		this.cmpntId=cmptnId;
		this.versionId=versionId;
	}
	
	@Column(name="FIRM_ID")
	public int getFirmId() {
		return firmId;
	}
	public void setFirmId(int firmId) {
		this.firmId = firmId;
	}
	
	@Column(name="SCHDL_CMPNT_CD")
	public String getCmpntId() {
		return cmpntId;
	}

	public void setCmpntId(String cmpntId) {
		this.cmpntId = cmpntId;
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
		if (obj instanceof PsLastExamPK) {
			PsLastExamPK pk = (PsLastExamPK)obj;
            return firmId == pk.firmId && cmpntId.equalsIgnoreCase(pk.cmpntId) && pk. versionId == pk.versionId;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(firmId+ cmpntId.hashCode() + versionId);
	}
	
}
