package org.finra.esched.domain;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.finra.esched.domain.PsCmp.CMPNT_TYPE;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/*@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)*/
@XmlRootElement(name = "psFirmPastExam")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="schdl_firm_last_exams")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsLastExam implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public PsLastExam() {
	}
	
	private PsLastExamPK id;
	
	private String mttrId;
	
	private PsFirm firm;		
		
	private PsCmp cmp;
	
	/*private Boolean tgtSpFl;
	private Boolean tgtFnFl;*/
	
	private Date fldwrkStartDate;
	private Date fldwrkStartPrjDate;
	
	private PsLastExamProjected lastExamProjectedDate;
	
	//private int versionId;
	
	@EmbeddedId
	public PsLastExamPK getId() {
		return id;
	}
	public void setId(PsLastExamPK id) {
		this.id = id;
	}
	
	@Column(name="MTTR_ID")
	@XmlElement(name = "mttrId")	
	public String getMttrId() {
		return mttrId;
	}
	public void setMttrId(String mttrId) {
		this.mttrId = mttrId;
	}
	
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="FIRM_ID", insertable=false, updatable=false),
		@JoinColumn(name="SCHDL_EXAM_SNPSH_ID", insertable=false, updatable=false)
	})
	@Cascade(CascadeType.PERSIST)
	@XmlElement(name = "firm", required = true)
	public PsFirm getFirm() {
		return firm;
	}
	public void setFirm(PsFirm firm) {
		this.firm = firm;
	}
	
	
	@ManyToOne
	@JoinColumn(name="SCHDL_CMPNT_CD", insertable=false, updatable=false)
	@XmlElement(name = "component", required = true)
	public PsCmp getCmp() {
		return cmp;
	}
	public void setCmp(PsCmp cmp) {
		this.cmp = cmp;
	}
	
	
	/*@Column(name="schdl_exam_snpsh_id")
	@XmlElement(name = "versionId")	
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}*/
	
	
/*	@Column(name="trgtd_sp_ds")
	@XmlElement(name = "tgtSpFl")	
	public Boolean getTgtSpFl() {
		return tgtSpFl;
	}
	public void setTgtSpFl(Boolean tgtSpFl) {
		this.tgtSpFl = tgtSpFl;
	}
	
	@Column(name="trgtd_fn_ds")
	@XmlElement(name = "tgtFnFl")	
	public Boolean getTgtFnDesc() {
		return tgtFnFl;
	}
	public void setTgtFnDesc(Boolean tgtFnFl) {
		this.tgtFnFl = tgtFnFl;
	}
*/	
	@Column(name="FLD_WRK_START_ACTL_DT")
	@XmlElement(name = "fldWrkStartDt")	
	public Date getFldwrkStartDate() {
		return fldwrkStartDate;
	}
	public void setFldwrkStartDate(Date fldwrkStartDate) {
		this.fldwrkStartDate = fldwrkStartDate;
	}
	
	
	@Column(name="FLD_WRK_START_PRJTD_DT")
	@XmlElement(name = "fldWrkStartDt")	
	public Date getFldwrkStartPrjDate() {
		return fldwrkStartPrjDate;
	}
	public void setFldwrkStartPrjDate(Date fldwrkStartPrjDate) {
		this.fldwrkStartPrjDate = fldwrkStartPrjDate;
	}
	
//	@OneToOne(mappedBy="lastexam")
//	public PsLastExamProjected getLastExamProjectedDate() {
//		return lastExamProjectedDate;
//	}
//	public void setLastExamProjectedDate(PsLastExamProjected lastExamProjectedDate) {
//		this.lastExamProjectedDate = lastExamProjectedDate;
//	}
	
	@Transient
	public static PsLastExam findCmpByType(List<PsLastExam> lel, CMPNT_TYPE type){
		
		if(lel!=null){
			Iterator<PsLastExam> it=lel.iterator();
			while(it.hasNext()){
				PsLastExam le=it.next();
				if(le.getCmp().getType()==type) return le;
			}
			
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
		
		if (obj instanceof PsLastExam) {
			PsLastExam o = (PsLastExam)obj;
            return id.getCmpntId()==o.id.getCmpntId() && id.getFirmId()==o.id.getFirmId() && id.getVersionId()==o.id.getVersionId();
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(id.getCmpntId().hashCode() + id.getFirmId() + id.getVersionId());
	}
}
