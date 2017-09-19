package org.finra.esched.domain;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name="schdl_bllbl_entty_firm_map_vw")
public class PsFirmBillableEntity implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public PsFirmBillableEntity() {
	}
	
	private Integer id;
	private Integer firmId;
	private int billableEntity;		
	private String currentFlag;
	private int versionId;
	private String spRequirement;
	private String finopRequirement;
	
	@Id
	@GeneratedValue
	@Column(name="SCHDL_BLLBL_ENTTY_FIRM_MAP_ID")
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="FIRM_ID")
	public Integer getFirmId() {
		return firmId;
	}

	public void setFirmId(Integer firmId) {
		this.firmId = firmId;
	}
	@Column(name="STAR_MTTR_BLLBL_ENTTY_ID")
	public int getBillableEntity() {
		return billableEntity;
	}

	public void setBillableEntity(int billableEntity) {
		this.billableEntity = billableEntity;
	}
	@Column(name="CRRNT_FL")
	public String getCurrentFlag() {
		return currentFlag;
	}

	public void setCurrentFlag(String currentFlag) {
		this.currentFlag = currentFlag;
	}
	@Column(name="SCHDL_EXAM_SNPSH_ID")
	public int getVersionId() {
		return versionId;
	}

	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
	@Column(name="SP_RQRD_FL")
	public String getSpRequirement() {
		return spRequirement;
	}

	public void setSpRequirement(String spRequirement) {
		this.spRequirement = spRequirement;
	}
	@Column(name="FN_RQRD_FL")
	public String getFinopRequirement() {
		return finopRequirement;
	}

	public void setFinopRequirement(String finopRequirement) {
		this.finopRequirement = finopRequirement;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsFirmBillableEntity) {
			PsFirmBillableEntity o = (PsFirmBillableEntity)obj;
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
