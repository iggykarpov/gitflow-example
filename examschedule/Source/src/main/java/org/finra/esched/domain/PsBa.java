package org.finra.esched.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)*/
@XmlRootElement(name = "psFirmBusinessActivity")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="SCHDL_FIRM_BUS_ACTVY")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsBa implements java.io.Serializable {

	private final Logger log = LoggerFactory.getLogger(PsBa.class);
	
	// , 
	public static final int ALTERNATE_NET_CAPITAL_ID=1;
	public static final int FLOOR_BROKER_WITHOUT_DIRECT_ACCESS_ID=2;
	public static final int FLOOR_BROKER_WITH_DIRECT_ACCESS_ID=3;
	public static final int NYSE_DMM_ID=5;
	
	// Constructors

	/** default constructor */
	public PsBa() {
	}
	
	private int baId;
	private PsFirm firm;
	
	private int activityId;
	private String activityDesc;
	private int versionId;
	
	@Id
	@GeneratedValue
	@Column(name="SCHDL_FIRM_BUS_ACTVY_ID")
	@XmlElement(name = "baId", required = true)
	public int getBaId() {
		return baId;
	}
	public void setBaId(int baId) {
		this.baId = baId;
	}
	
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="FIRM_ID"),
		@JoinColumn(name="schdl_exam_snpsh_id")
	})
	@Cascade(CascadeType.PERSIST)
	@XmlElement(name = "firm", required = true)
	public PsFirm getFirm() {
		return firm;
	}
	public void setFirm(PsFirm firm) {
		this.firm = firm;
	}
	
	@Column(name="SCHDL_EXAM_SNPSH_ID", insertable=false, updatable=false)
	@XmlElement(name = "versionId")	
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
	
	@Column(name="BUS_ACTVY_DS")
	@XmlElement(name = "activityDesc")	
	public String getActivityDesc() {
		return activityDesc;
	}
	public void setActivityDesc(String activityDesc) {
		this.activityDesc = activityDesc;
	}
	
	@Column(name="BUS_ACTVY_ID")
	@XmlElement(name = "activityId")	
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsBa) {
			PsBa o = (PsBa)obj;
            return baId == o.baId && versionId == o.versionId;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(baId + versionId);
	}
	
}
