package org.finra.esched.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Embeddable
public class PsLastExamProjectedPK implements java.io.Serializable{
	
	private int firmId;
	private String componentType;
	private int versionId;
	
	public PsLastExamProjectedPK() {
	}
	
	public PsLastExamProjectedPK(int firmId, String componentType, int versionId){
		this.firmId=firmId;
		this.componentType=componentType;
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
	public String getComponentType() {
		return componentType;
	}
	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}
	@Column(name="schdl_exam_snpsh_id")
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsLastExamProjectedPK) {
			PsLastExamProjectedPK pk = (PsLastExamProjectedPK)obj;
			return firmId == pk.firmId && versionId == pk.versionId && componentType.equals(pk.componentType) ;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(firmId + versionId + componentType.hashCode());
	}
	
	
}
