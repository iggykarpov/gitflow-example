package org.finra.esched.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.finra.exam.common.security.domain.User;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "psPastExamCmp")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Table(name="schdl_aplbl_cmpnt")
@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
public class PsApplicableCmp implements java.io.Serializable, Comparable<PsApplicableCmp> {
	
	private final Logger log = LoggerFactory.getLogger(PsApplicableCmp.class);
	
	public enum RESP_DISTR_TYPE {
		SP, FN, FL
	};
	
	private Integer id;
	
	private PsSession session;
	private PsCmp cmp;
	
	
	private boolean required;
	
	private Boolean requiredOvrd;
	private PsOvrdReason ovrdReason;
	private User ovrdUser;
	private Date ovrdDate;
	
	private Boolean nmaFl;
	private Boolean rsaNmaFl;
	
	private int examFreq;
	private Date triggerDt;
	private PsTriggerDtType triggerDtType;
	private String businessReviewText;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "psApplicableCmpSeqSequence")
	@SequenceGenerator(name = "psApplicableCmpSeqSequence", sequenceName = "schdl_aplbl_cmpnt_id_seq", allocationSize=1)
	@Column(name="SCHDL_APLBL_CMPNT_ID")
	@XmlElement(name = "id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="FIRM_SSSN_ID")
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	public PsSession getSession() {
		return session;
	}
	public void setSession(PsSession session) {
		this.session = session;
	}
	
	@ManyToOne
	@JoinColumn(name="SCHDL_CMPNT_CD")
	@XmlElement(name = "component", required = true)
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	public PsCmp getCmp() {
		return cmp;
	}
	public void setCmp(PsCmp cmp) {
		this.cmp = cmp;
	}
	
	@Column(name="RQRD_FL")
	@XmlElement(name = "isRequired", required = true)
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	@Column(name="OVRD_RQRD_FL")
	@XmlElement(name = "isOvrdRequired", required = true)	
	public Boolean isRequiredOvrd() {
		return requiredOvrd;
	}
	public void setRequiredOvrd(Boolean ovrdRequired) {
		this.requiredOvrd = ovrdRequired;
	}
	
	@ManyToOne
	@JoinColumn(name="OVRD_RSN_ID")
	@XmlElement(name = "ovrdReason", required = true)	
	public PsOvrdReason getOvrdReason() {
		return ovrdReason;
	}
	public void setOvrdReason(PsOvrdReason ovrdReason) {
		this.ovrdReason = ovrdReason;
	}
	
	@ManyToOne
	@JoinColumn(name="LAST_OVRD_USER_ID")
	@XmlElement(name = "ovrdUser", required = true)	
	public User getOvrdUser() {
		return ovrdUser;
	}
	public void setOvrdUser(User ovrdUser) {
		this.ovrdUser = ovrdUser;
	}
	
	@Column(name="LAST_OVRD_UPDT_TS")
	@XmlElement(name = "ovrdDate", required = true)	
	public Date getOvrdDate() {
		return ovrdDate;
	}
	public void setOvrdDate(Date ovrdDate) {
		this.ovrdDate = ovrdDate;
	}
	
	@Column(name="TRGGR_DT")
	@XmlElement(name = "triggerDt", required = true)	
	public Date getTriggerDt() {
		return triggerDt;
	}
	public void setTriggerDt(Date triggerDt) {
		this.triggerDt = triggerDt;
	}
	
	@ManyToOne
	@JoinColumn(name="TRGGR_TYPE_CD")
	@XmlElement(name = "triggerType", required = true)
	public PsTriggerDtType getTriggerDtType() {
		return triggerDtType;
	}
	public void setTriggerDtType(PsTriggerDtType triggerType) {
		this.triggerDtType = triggerType;
	}
	
	
	@Column(name="EXAM_FREQ_CT")
	@XmlElement(name = "examFreq")	
	public int getExamFreq() {
		return examFreq;
	}
	public void setExamFreq(int examFreq) {
		this.examFreq = examFreq;
	}
	
	
	
	@Column(name="NMA_FL")
	@XmlElement(name = "isNmaFl", required = true)	
	public Boolean isNmaFl() {
		return nmaFl;
	}
	public void setNmaFl(Boolean nmaFl) {
		this.nmaFl = nmaFl;
	}

	@Column(name="RSA_NMA_FL")
	@XmlElement(name = "isRsaNmaFl", required = true)	
	public Boolean isRsaNmaFl() {
		return rsaNmaFl;
	}
	public void setRsaNmaFl(Boolean rsaNmaFl) {
		this.rsaNmaFl = rsaNmaFl;
	}

	@Column(name="BUS_RVW_TX")
	public String getBusinessReviewText() {
		return businessReviewText;
	}

	public void setBusinessReviewText(String businessReviewText) {
		this.businessReviewText = businessReviewText;
	}


	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsApplicableCmp) {
			PsApplicableCmp o = (PsApplicableCmp)obj;
            return id == o.id;// && versionId == o.versionId;
        } else {
            return false;
        }
	}


	@Override
	public int hashCode() {
		return (int) 31 *(id);// + versionId);
	}
	
	@Override
	public String toString() {
		ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE);
		return sb.toString();
	}
	
	// Used to sort collections by Cmpnt priority
	@Override
	public int compareTo(PsApplicableCmp o) {
		return this.cmp.compareTo(o.cmp);
	}
}
