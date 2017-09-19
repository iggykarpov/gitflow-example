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
@XmlRootElement(name = "psFirmSRO")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="schdl_firm_sro_mbrsp")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsSro implements java.io.Serializable {

	private final Logger log = LoggerFactory.getLogger(PsSro.class);
	
	public enum SRO_TYPE {
		NYSE, NYSEAMER
	};
	
	// Constructors

	/** default constructor */
	public PsSro() {
	}
	
	private int sroId;
	private PsFirm firm;
	
	private String exchangeName;
	private int versionId;
	
	@Id
	@GeneratedValue
	@Column(name="SCHDL_FIRM_SRO_MBRSP_ID")
	@XmlElement(name = "sroId", required = true)
	public int getSroId() {
		return sroId;
	}
	public void setSroId(int sroId) {
		this.sroId = sroId;
	}
	
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="FIRM_ID"),
		@JoinColumn(name="SCHDL_EXAM_SNPSH_ID")
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
	
	@Column(name="XCHNG_NM")
	@XmlElement(name = "exchangeName")	
	public String getExchangeName() {
		return exchangeName;
	}
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	
	@Transient
	public SRO_TYPE getType(){
		try{
			return SRO_TYPE.valueOf(this.getExchangeName().trim().toUpperCase().replace("-", "").replace(" ", "_"));
		}catch(Exception ex){
			log.info("The only SRO types used in calculations are 'NYSE' and 'NYSEMKT'. Unsopported SRO type found:"+this.getExchangeName()+". Not a big deal, but just in case...");
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
		
		if (obj instanceof PsSro) {
			PsSro o = (PsSro)obj;
            return sroId ==o.sroId && versionId == o.versionId;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(sroId + versionId);
	}
	
	
}
