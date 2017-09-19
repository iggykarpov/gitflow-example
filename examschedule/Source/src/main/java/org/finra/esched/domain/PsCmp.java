package org.finra.esched.domain;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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

import org.finra.esched.domain.PsApplicableCmp.RESP_DISTR_TYPE;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)*/
@XmlRootElement(name = "psComponent")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="schdl_cmpnt_lk")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsCmp implements java.io.Serializable, Comparable<PsCmp>{
	
	private final Logger log = LoggerFactory.getLogger(PsCmp.class);
	
	public enum CMPNT_TYPE {
		SALES_PRACTICE, FIRST_FINOP, FINOP, ANC, MUNICIPAL, FLOOR_REVIEW, OPTIONS, ROUTINE, RSA_FINOP, RSA_SALES_PRACTICE, SDF, MUNICIPAL_ADVISOR
	};
	
	
	private String id;
	private String desc;
	private int priorityId;
	private String respDistrictType;
	
	@Id
	@GeneratedValue
	@Column(name="SCHDL_CMPNT_CD")
	@XmlElement(name = "id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@Column(name="SCHDL_CMPNT_DS")
	@XmlElement(name = "desc", required = true)
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Transient
	public CMPNT_TYPE getType(){
		try{
			return CMPNT_TYPE.valueOf(this.id);
		}catch(Exception ex){
			log.error("Unsopported component type found:"+this.id+" This component will not be processed");
			return null;
		}
	}
	
	@Column(name="PRRTY_ID")
	@XmlElement(name = "priorityId")
	public int getPriorityId() {
		return priorityId;
	}
	public void setPriorityId(int priorityId) {
		this.priorityId = priorityId;
	}
	
	@Column(name="RSPNB_DSTRT_CD")
	@XmlElement(name = "respDistrCode")	
	public String getRespDistrictType() {
		return respDistrictType;
	}
	public void setRespDistrictType(String respDistrictType) {
		this.respDistrictType = respDistrictType;
	}
	@Transient
	public RESP_DISTR_TYPE getRespDistrType(){
		try{
			return RESP_DISTR_TYPE.valueOf(this.respDistrictType);
		}catch(Exception ex){
			log.error("Unsopported responsible district type found:"+this.respDistrictType+" for Applicable Cmpnt:"+this.id);
			return null;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsCmp) {
			PsCmp o = (PsCmp)obj;
            return id == o.id;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(id.hashCode());
	}
	@Override
	public int compareTo(PsCmp o) {
		final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;
	    
	    if (this.priorityId< o.priorityId) return BEFORE;
	    if (this.priorityId> o.priorityId) return AFTER;
	   
	    return EQUAL;
	}
	
	

	
	
}
