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
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)*/
@XmlRootElement(name = "psStatus")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="schdl_sssn_stts_lk")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsSessionStatus implements java.io.Serializable{
	
	private final Logger log = LoggerFactory.getLogger(PsSessionStatus.class);
	
	public enum PS_STATUS_TYPE {
		NEW, REVIEW, PENDING, SCHED, ERROR
	};

	public PsSessionStatus() {
	}

	
	public PsSessionStatus(String id) {
		super();
		this.id = id;
	}

	private String id;
	private String desc;
	private int priorityId;
	
	@Id
	@GeneratedValue
	@Column(name="SCHDL_SSSN_STTS_CD")
	@XmlElement(name = "id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@Column(name="SCHDL_SSSN_STTS_DS")
	@XmlElement(name = "desc", required = true)
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Transient
	public PS_STATUS_TYPE getType(){
		try{
			return PS_STATUS_TYPE.valueOf(this.id);
		}catch(Exception ex){
			log.error("Unsopported status type found:"+this.id);
			return null;
		}
	}
	
	@Column(name="DSPLY_PRCDN_NB")
	@XmlElement(name = "priorityId")
	public int getPriorityId() {
		return priorityId;
	}
	public void setPriorityId(int priorityId) {
		this.priorityId = priorityId;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsSessionStatus) {
			PsSessionStatus o = (PsSessionStatus)obj;
            return id == o.id;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(id.hashCode());
	}
		
}
