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

@XmlRootElement(name = "psExamVersion")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="schdl_exam_snpsh")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsVersion implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public PsVersion() {
	}
	
	private int versionId;
	private int staffId;		
	private Date effDate;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "psVersionSeqSequence")
	@SequenceGenerator(name = "psVersionSeqSequence", sequenceName = "schdl_exam_snpsh_id_seq", allocationSize=1)
	@Column(name="SCHDL_EXAM_SNPSH_ID")
	@XmlElement(name = "versionId")
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
	
	@Column(name="STAFF_ID")
	@XmlElement(name = "staffId", required = true)
	public int getStaffId() {
		return staffId;
	}
	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}
	
	@Column(name="VRSN_CRTD_DT")
	@XmlElement(name = "effDate", required = true)
	public Date getEffDate() {
		return effDate;
	}
	public void setEffDate(Date effDate) {
		this.effDate = effDate;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsVersion) {
			PsVersion o = (PsVersion)obj;
            return versionId ==o.versionId;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int)( 31 *versionId);
	}
}
