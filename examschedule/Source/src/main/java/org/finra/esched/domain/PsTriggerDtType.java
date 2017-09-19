package org.finra.esched.domain;

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
@Table(name="schdl_trggr_type_lk")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsTriggerDtType implements java.io.Serializable {
	
	private final Logger log = LoggerFactory.getLogger(PsTriggerDtType.class);
	
	public enum TRIGGER_TYPE {
		LAST_EXAM, NPOB, MSRB_EFCTV_DT, MMBRSHP_DT
	};
	
	private int id;
	private String code;
	private String desc;
	
	@Id
	@GeneratedValue
	@Column(name="SCHDL_TRGGR_TYPE_LK_ID")
	@XmlElement(name = "id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@Column(name="TRGGR_TYPE_CD")
	@XmlElement(name = "code", required = true)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	@Column(name="TRGGR_TYPE_DS")
	@XmlElement(name = "desc", required = true)
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Transient
	public TRIGGER_TYPE getType(){
		try{
			return TRIGGER_TYPE.valueOf(this.code);
		}catch(Exception ex){
			log.error("Unsopported component type found:"+this.code);
			return null;
		}
	}
	
	@Transient
	public static PsTriggerDtType findByType(List<PsTriggerDtType> tl,TRIGGER_TYPE type){
		if(tl==null || type==null) return null;
		Iterator tIt=tl.iterator();
		while(tIt.hasNext()){
			PsTriggerDtType tt=(PsTriggerDtType) tIt.next();
			if(tt.getType()==type) return tt;
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsTriggerDtType) {
			PsTriggerDtType o = (PsTriggerDtType)obj;
            return id ==o.id;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int)( 31 *id);
	}
}
