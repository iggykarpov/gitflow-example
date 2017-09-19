package org.finra.esched.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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
@XmlRootElement(name = "psOverrideReasonType")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="schdl_ovrd_rsn_lk")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsOvrdReason implements java.io.Serializable {
	
	private final Logger log = LoggerFactory.getLogger(PsOvrdReason.class);
	
	private int id;
	private String code;
	private String desc;
	private String type;
	private String businessReview;
	
	@Id
	@GeneratedValue
	@Column(name="ovrd_rsn_id")
	@XmlElement(name = "id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@Column(name="ovrd_rsn_cd")
	@XmlElement(name = "code", required = true)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name="ovrd_rsn_ds")
	@XmlElement(name = "desc", required = true)
	public String getDesc() {
		return desc.replaceAll("\"", "\'");
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Column(name="ovrd_rsn_type_cd")
	@XmlElement(name = "type", required = true)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Column(name="OVRD_BUS_RVW_FL")
	@XmlElement(name = "businessReview")
	public String getBusinessReview() {
		return businessReview;
	}
	public void setBusinessReview(String businessReview) {
		this.businessReview = businessReview;
	}
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsOvrdReason) {
			PsOvrdReason o = (PsOvrdReason)obj;
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
