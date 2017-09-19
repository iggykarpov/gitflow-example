package org.finra.esched.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

@XmlRootElement(name = "starOutput")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="SCHED")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class StarOutput {
	
	private int schedId;
	private int firmId;
	private String mttrType;
	private String mttrSubType;
	private String requiredFl;
	
	public StarOutput() {

	}

	@Id
	@GeneratedValue
	@Column(name="sched_id")
	@XmlElement(name = "schedId", required = true)
	public int getSchedId() {
		return schedId;
	}
	public void setSchedId(int outputId) {
		this.schedId = outputId;
	}
	
	@Column(name="FIRM_ID")
	@XmlElement(name = "firmId")
	public int getFirmId() {
		return firmId;
	}
	public void setFirmId(int firmId) {
		this.firmId = firmId;
	}

	@Column(name="MTTR_TYPE_DS")
	@XmlElement(name = "mttrTypeName")	
	public String getMttrType() {
		return mttrType;
	}
	public void setMttrType(String mttrType) {
		this.mttrType = mttrType;
	}

	@Column(name="MTTR_SUB_TYPE_DS")
	@XmlElement(name = "mttrSubTypeName")
	public String getMttrSubType() {
		return mttrSubType;
	}
	public void setMttrSubType(String mttrSubType) {
		this.mttrSubType = mttrSubType;
	}

	@Column(name="RQRD_IND")
	@XmlElement(name = "reFlag")	
	public String getRequiredFl() {
		return requiredFl;
	}
	public void setRequiredFl(String requiredFl) {
		this.requiredFl = requiredFl;
	}
	
	@Transient
	public boolean isRequired(){
		if(requiredFl!=null && (requiredFl.equalsIgnoreCase("R"))){ //|| requiredFl.equalsIgnoreCase("S")
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof StarOutput) {
			StarOutput o = (StarOutput)obj;
            return schedId==o.schedId;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(schedId);
	}
	
}
