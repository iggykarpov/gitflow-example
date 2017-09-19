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
@XmlRootElement(name = "psMatterStarData")
@XmlAccessorType(XmlAccessType.NONE) 
@Immutable
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsMatterStarData implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public PsMatterStarData() {
	}
	@Id
	@Column(name = "MTTR_ID")
	private String matterId;
	@Column(name = "RGLTY_SGNFC_ID")
	private Integer regulatorySignificanceId;
	@Column(name = "RCVD_DT")
	private Date receivedDate;

	

	public String getMatterId() {
		return matterId;
	}

	public void setMatterId(String matterId) {
		this.matterId = matterId;
	}

	public Integer getRegulatorySignificanceId() {
		return regulatorySignificanceId;
	}

	public void setRegulatorySignificanceId(Integer regulatorySignificanceId) {
		this.regulatorySignificanceId = regulatorySignificanceId;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsMatterStarData) {
			PsMatterStarData o = (PsMatterStarData)obj;
            return Integer.valueOf(matterId) == Integer.valueOf(o.matterId);
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int)( 31 * Integer.valueOf(matterId));
	}
}
