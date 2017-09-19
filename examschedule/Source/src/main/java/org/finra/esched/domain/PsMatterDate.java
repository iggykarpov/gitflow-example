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
@XmlRootElement(name = "psMatterDate")
@XmlAccessorType(XmlAccessType.NONE) 
@Immutable
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsMatterDate implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public PsMatterDate() {
	}
	@Id
	@Column(name = "ID")
	private Long id;
	@Column(name = "star_date_name_id")
	private Integer matterDateNameId;
	@Column(name = "star_date_type_id")
	private Integer matterDateTypeId;
	@Column(name = "event_date")
	private Date matterDate;
	
	
	
	public Integer getMatterDateNameId() {
		return matterDateNameId;
	}

	public void setMatterDateNameId(Integer matterDateNameId) {
		this.matterDateNameId = matterDateNameId;
	}

	public Integer getMatterDateTypeId() {
		return matterDateTypeId;
	}

	public void setMatterDateTypeId(Integer matterDateTypeId) {
		this.matterDateTypeId = matterDateTypeId;
	}

	public Date getMatterDate() {
		return matterDate;
	}

	public void setMatterDate(Date matterDate) {
		this.matterDate = matterDate;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsMatterDate) {
			PsMatterDate o = (PsMatterDate)obj;
            return Integer.valueOf(matterDateNameId + "" + matterDateTypeId + "") == Integer.valueOf(o.matterDateNameId + "" + o.matterDateTypeId + "");
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int)( 31 * Integer.valueOf(matterDateNameId + "" + matterDateTypeId + ""));
	}
}
