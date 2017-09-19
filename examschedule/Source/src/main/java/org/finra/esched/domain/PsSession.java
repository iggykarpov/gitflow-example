package org.finra.esched.domain;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.finra.esched.domain.PsFirm.NMA_TYPE;
import org.finra.esched.domain.PsSessionStatus.PS_STATUS_TYPE;
import org.finra.exam.common.security.domain.User;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

@XmlRootElement(name = "psSession")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Table(name="schdl_firm_sssn")
@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
public class PsSession implements java.io.Serializable, Comparable<PsSession> {
	
	private int id;
	private Date sessionDate;
	
	private PsFirm firm;
	
	private PsSessionStatus status;

	private boolean spApprovedFl;
	private boolean fnApprovedFl;
	
	private User spApproveUser;
	private Date spApproveDt;
	
	private User fnApproveUser;
	private Date fnApproveDt;
	
	
	private List<PsApplicableCmp> aCmps;

	private List<PsOutput> exams;
	
	private Date prjFwsd;
	private Date prjEwsd;
	
	private boolean mttrLinkedFl;
	private boolean examLinkedFl;

	private String flDistrictCd;
	private String flDistrictTypeCode;

	private boolean flApprovedFl;
	private User flApproveUser;
	private Date flApproveDt;


	
	
	public PsSession() {
		this.setSessionDate(new Date());
		this.setSpApprovedFl(false);
		this.setFnApprovedFl(false);
		this.setMttrLinkedFl(false);
		this.setExamLinkedFl(false);
		this.setFlApprovedFl(false);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "psSessionSeqSequence")
	@SequenceGenerator(name = "psSessionSeqSequence", sequenceName = "SCHDL_FIRM_SSSN_ID_SEQ", allocationSize=1)
	@Column(name="FIRM_SSSN_ID")
	@XmlElement(name = "sessionId", required = true)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="CRTD_DT")
	public Date getSessionDate() {
		return sessionDate;
	}
	public void setSessionDate(Date sessionDate) {
		this.sessionDate = sessionDate;
	}
	
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="FIRM_ID"),
		@JoinColumn(name="SCHDL_EXAM_SNPSH_ID")
	})
	@XmlElement(name = "firm")
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	public PsFirm getFirm() {
		return firm;
	}
	public void setFirm(PsFirm firm) {
		this.firm = firm;
	}
	
	@ManyToOne
	@JoinColumn(name="SCHDL_SSSN_STTS_CD")
	public PsSessionStatus getStatus() {
		return status;
	}
	public void setStatus(PsSessionStatus status) {
		this.status = status;
	}
	
	@Column(name="SP_APPRV_FL")
	public boolean isSpApprovedFl() {
		return spApprovedFl;
	}
	public void setSpApprovedFl(boolean spApprovedFl) {
		this.spApprovedFl = spApprovedFl;
	}
	
	@ManyToOne
	@JoinColumn(name="sp_apprv_user_id")
	public User getSpApproveUser() {
		return spApproveUser;
	}
	public void setSpApproveUser(User spApproveUser) {
		this.spApproveUser = spApproveUser;
	}
	
	@Column(name="sp_apprv_updt_ts")
	public Date getSpApproveDt() {
		return spApproveDt;
	}
	public void setSpApproveDt(Date spApproveDt) {
		this.spApproveDt = spApproveDt;
	}
	
	@Column(name="FINOP_APPRV_FL")
	public boolean isFnApprovedFl() {
		return fnApprovedFl;
	}
	public void setFnApprovedFl(boolean fnApprovedFl) {
		this.fnApprovedFl = fnApprovedFl;
	}
	
	@ManyToOne
	@JoinColumn(name="finop_apprv_user_id")
	public User getFnApproveUser() {
		return fnApproveUser;
	}
	public void setFnApproveUser(User fnApproveUser) {
		this.fnApproveUser = fnApproveUser;
	}
	
	@Column(name="finop_apprv_updt_ts")
	public Date getFnApproveDt() {
		return fnApproveDt;
	}
	public void setFnApproveDt(Date fnApproveDt) {
		this.fnApproveDt = fnApproveDt;
	}
	
	@Transient
	public boolean hasExamStatus(PS_STATUS_TYPE type){
		if(this.status==null || type==null) return false;
		return (type.toString().equalsIgnoreCase(this.status.getId().toUpperCase()));
	}
	
	@Transient
	public boolean hasNma(NMA_TYPE type){
		if(this.aCmps==null || (this.aCmps!=null && this.aCmps.size()==0)) return false;
		
		Iterator<PsApplicableCmp> aCmpIt=this.aCmps.iterator();
		
		while(aCmpIt.hasNext()){
			
			PsApplicableCmp c=aCmpIt.next();
			boolean isNmaCmpnt=c.isNmaFl();
			boolean isRsaNmaCmpnt=c.isRsaNmaFl();
			
			// If type provided - check that at least one Component is an NMA of provided type
			if(type!=null){
				
				if(type==NMA_TYPE.NMA && isNmaCmpnt){
					return true;
				}else if(type==NMA_TYPE.RSANMA && isRsaNmaCmpnt){
					return true;
				}
				return false;

			}else{
				// If type is not provided - check that at least one Component is an NMA of two supported types
				return (isNmaCmpnt || isRsaNmaCmpnt);
			}
		}
		
		return false;
	}
	
	@Transient
	public PsApplicableCmp getCmpntByType(PsCmp.CMPNT_TYPE type){
		if(getaCmps()==null) return null;
		
		Iterator<PsApplicableCmp> aCmpntsIt=getaCmps().iterator();
		while(aCmpntsIt.hasNext()){
			PsApplicableCmp aCmpnt=aCmpntsIt.next();
			if(aCmpnt.getCmp().getType()==type) return aCmpnt;
		}
		return null;	
	}
	
	@Transient
	public String getNmaCodes(){
		StringBuffer result=new StringBuffer();
		
		if(hasNma(NMA_TYPE.NMA))
			result.append(NMA_TYPE.NMA.name());
	
		if(hasNma(NMA_TYPE.RSANMA)){
			if(result.length()>0) result.append("/");
			result.append(NMA_TYPE.RSANMA.name());
		}
		
		return result.length()>0 ? result.toString() : null;
	}


	@OneToMany(fetch = FetchType.LAZY, mappedBy="session")
	@NotAudited
	public List<PsApplicableCmp> getaCmps() {
		return aCmps;
	}
	public void setaCmps(List<PsApplicableCmp> aCmps) {
		this.aCmps = aCmps;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy="session")
	@NotAudited
	public List<PsOutput> getExams() {
		return exams;
	}
	public void setExams(List<PsOutput> exams) {
		this.exams = exams;
	}
	
	
	
	@Column(name="FLD_WRK_START_DT")
	public Date getPrjFwsd() {
		return prjFwsd;
	}
	public void setPrjFwsd(Date prjFwsd) {
		this.prjFwsd = prjFwsd;
	}
	
	@Column(name="EXAM_WRK_START_DT")
	public Date getPrjEwsd() {
		return prjEwsd;
	}
	public void setPrjEwsd(Date prjEwsd) {
		this.prjEwsd = prjEwsd;
	}
	
	@Column(name="MTTR_LNKD_FL")
	public boolean isMttrLinkedFl() {
		return mttrLinkedFl;
	}
	public void setMttrLinkedFl(boolean mttrLinkedFl) {
		this.mttrLinkedFl = mttrLinkedFl;
	}

	@Column(name="EXAM_LNKD_FL")
	public boolean isExamLinkedFl() {
		return examLinkedFl;
	}
	public void setExamLinkedFl(boolean examLinkedFl) {
		this.examLinkedFl = examLinkedFl;
	}

	@Column(name="FLR_DSTRT_CD")
	public String getFlDistrictCd() {
		return flDistrictCd;
	}

	public void setFlDistrictCd(String flDistrictCd) {
		this.flDistrictCd = flDistrictCd;
	}

	@Column(name="FLR_DSTRT_TYPE_CD")
	public String getFlDistrictTypeCode() {
		return flDistrictTypeCode;
	}

	public void setFlDistrictTypeCode(String flDistrictTypeCode) {
		this.flDistrictTypeCode = flDistrictTypeCode;
	}




	@Column(name="FLR_APPRV_FL")
	public boolean isFlApprovedFl() {
		return flApprovedFl;
	}

	public void setFlApprovedFl(boolean flApprovedFl) {
		this.flApprovedFl = flApprovedFl;
	}

	@ManyToOne
	@JoinColumn(name="FLR_APPRV_USER_ID")
	public User getFlApproveUser() {
		return flApproveUser;
	}

	public void setFlApproveUser(User flApproveUser) {
		this.flApproveUser = flApproveUser;
	}

	@Column(name="FLR_APPRV_UPDT_TS")
	public Date getFlApproveDt() {
		return flApproveDt;
	}

	public void setFlApproveDt(Date flApproveDt) {
		this.flApproveDt = flApproveDt;
	}

	@Override
	public int compareTo(PsSession o) {
		final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;
	    
	    if (this.id < o.id) return BEFORE;
	    if (this.id > o.id) return AFTER;
	   
	    return EQUAL;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsSession) {
			PsSession o = (PsSession)obj;
            return id==o.id;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(id);
	}
}
