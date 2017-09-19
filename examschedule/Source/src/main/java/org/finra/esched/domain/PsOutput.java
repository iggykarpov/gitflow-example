package org.finra.esched.domain;

import org.finra.exam.common.security.domain.User;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/*@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)*/
@XmlRootElement(name = "psOutput")
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "schdl_otpt")
public class PsOutput {

    public static final String SALES_PRACTICE_TYPE = "NYSP";
    public static final String FINOP_TYPE = "NYFINOP";
    public static final String FIRST_FINOP_TYPE = "FO";
    public static final String OPTIONS_TYPE = "OP";
    public static final String MUNICIPAL_TYPE = "OM";
    public static final String FLOOR_TYPE = "NYFLOOR";
    public static final String ANC_TYPE = "CSE";
    private int outputId;
    /*@NotAudited*/
    private PsSession session;

    //	private String mttrTypeNm;
//	private String mttrSubTypeNms;
    private Long mttrTypeId;
    private Long mttrSubTypeId;
    private String examTypeCd;
    private String examSubTypeCd;

    private boolean requiredFl;

    //	private String spImpctCd;
//	private String spRiskCd;
    private String spDistrict;
    //	private String spSupervisor;
//
//	private String fnImpctCd;
//	private String fnRiskCd;
    private String fnDistrict;
//	private String fnSupervisor;

    private Date prjFwsd;
    private Date prjEwsd;

    private User schedUser;
    private Date schedDate;

    private String matterId;
    private Long examId;
    private String memberMarketCode;

    private String floorDistrict;

    public PsOutput() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "psOutputSeqSequence")
    @SequenceGenerator(name = "psOutputSeqSequence", sequenceName = "schdl_otpt_id_seq", allocationSize = 1)
    @Column(name = "SCHDL_OTPT_ID")
    @XmlElement(name = "outputId", required = true)
    public int getOutputId() {
        return outputId;
    }

    public void setOutputId(int outputId) {
        this.outputId = outputId;
    }


    @ManyToOne
    @JoinColumn(name = "FIRM_SSSN_ID")
    public PsSession getSession() {
        return session;
    }

    public void setSession(PsSession session) {
        this.session = session;
    }

//	@Column(name="MTTR_TYPE_NM")
//	@XmlElement(name = "mttrTypeName")	
//	public String getMttrTypeNm() {
//		return mttrTypeNm;
//	}
//	public void setMttrTypeNm(String mttrType) {
//		this.mttrTypeNm = mttrType;
//	}
//
//	@Column(name="MTTR_SUB_TYPE_NM")
//	@XmlElement(name = "mttrSubTypeName")
//	public String getMttrSubTypeNms() {
//		return mttrSubTypeNms;
//	}
//	public void setMttrSubTypeNms(String mttrSubType) {
//		this.mttrSubTypeNms = mttrSubType;
//	}

    @Column(name = "mttr_type_id")
    @XmlElement(name = "mttrSubTypeName")
    public Long getMttrTypeId() {
        return mttrTypeId;
    }

    public void setMttrTypeId(Long mttrTypeId) {
        this.mttrTypeId = mttrTypeId;
    }

    @Column(name = "mttr_sub_type_id")
    @XmlElement(name = "mttrSubTypeName")
    public Long getMttrSubTypeId() {
        return mttrSubTypeId;
    }

    public void setMttrSubTypeId(Long mttrSubTypeId) {
        this.mttrSubTypeId = mttrSubTypeId;
    }

    @Column(name = "exam_type_cd")
    @XmlElement(name = "examTypeCd")
    public String getExamTypeCd() {
        return examTypeCd;
    }

    public void setExamTypeCd(String examTypeCd) {
        this.examTypeCd = examTypeCd;
    }

    @Column(name = "exam_sub_type_cd")
    @XmlElement(name = "examSubTypeCd")
    public String getExamSubTypeCd() {
        return examSubTypeCd;
    }

    public void setExamSubTypeCd(String examSubTypeCd) {
        this.examSubTypeCd = examSubTypeCd;
    }

    @Type(type = "yes_no")
    @Column(name = "RQRD_FL")
    @XmlElement(name = "reFlag")
    public boolean getRequiredFl() {
        return requiredFl;
    }

    public void setRequiredFl(boolean requiredFl) {
        this.requiredFl = requiredFl;
    }


    //	@Column(name="SP_IMPCT_CD")
//	@XmlElement(name = "spImpactCd")	
//	public String getSpImpctCd() {
//		return spImpctCd;
//	}
//	public void setSpImpctCd(String spImpctCd) {
//		this.spImpctCd = spImpctCd;
//	}
//
//	@Column(name="SP_RISK_CD")
//	@XmlElement(name = "spRiskCd")	
//	public String getSpRiskCd() {
//		return spRiskCd;
//	}
//	public void setSpRiskCd(String spRiskCd) {
//		this.spRiskCd = spRiskCd;
//	}
//	
    @Column(name = "sp_dstrt_nm")
    @XmlElement(name = "spDistrictNm")
    public String getSpDistrict() {
        return spDistrict;
    }

    public void setSpDistrict(String spDistrict) {
        this.spDistrict = spDistrict;
    }

    //
//	@Column(name="sp_sprv_nm")
//	@XmlElement(name = "spSupervisorNm")	
//	public String getSpSupervisor() {
//		return spSupervisor;
//	}
//	public void setSpSupervisor(String spSupervisor) {
//		this.spSupervisor = spSupervisor;
//	}
//
//	@Column(name="FINOP_IMPCT_CD")
//	@XmlElement(name = "fnImpactCd")	
//	public String getFnImpctCd() {
//		return fnImpctCd;
//	}
//	public void setFnImpctCd(String fnImpctCd) {
//		this.fnImpctCd = fnImpctCd;
//	}
//	
//	@Column(name="FINOP_RISK_CD")
//	@XmlElement(name = "fnRiskCd")	
//	public String getFnRiskCd() {
//		return fnRiskCd;
//	}
//	public void setFnRiskCd(String fnRiskCd) {
//		this.fnRiskCd = fnRiskCd;
//	}
//
    @Column(name = "FINOP_DSTRT_NM")
    @XmlElement(name = "fnDistrictNm")
    public String getFnDistrict() {
        return fnDistrict;
    }

    public void setFnDistrict(String fnDistrict) {
        this.fnDistrict = fnDistrict;
    }
//
//	@Column(name="FINOP_sprv_nm")
//	@XmlElement(name = "fnSupervisorNm")	
//	public String getFnSupervisor() {
//		return fnSupervisor;
//	}
//	public void setFnSupervisor(String fnSupervisor) {
//		this.fnSupervisor = fnSupervisor;
//	}

	/*@Column(name="schdl_exam_snpsh_id")
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
	*/

    @Column(name = "fld_wrk_prjtd_dt")
    @XmlElement(name = "prjFwsd")
    public Date getPrjFwsd() {
        return prjFwsd;
    }

    public void setPrjFwsd(Date prjFwsd) {
        this.prjFwsd = prjFwsd;
    }

    @Column(name = "exam_wrk_prjtd_dt")
    @XmlElement(name = "prjEwsd")
    public Date getPrjEwsd() {
        return prjEwsd;
    }

    public void setPrjEwsd(Date prjEwsd) {
        this.prjEwsd = prjEwsd;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    @XmlElement(name = "prjUser", required = true)
    public User getSchedUser() {
        return schedUser;
    }

    public void setSchedUser(User schedUser) {
        this.schedUser = schedUser;
    }

    @Column(name = "updt_ts")
    @XmlElement(name = "schedDate")
    public Date getSchedDate() {
        return schedDate;
    }

    public void setSchedDate(Date schedDate) {
        this.schedDate = schedDate;
    }

    @Column(name = "mttr_id")
    public String getMatterId() {
        return matterId;
    }

    public void setMatterId(String matterId) {
        this.matterId = matterId;
    }

    @Column(name = "exam_id")
    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    @Column(name = "mbr_mkt_type_cd")
    public String getMemberMarketCode() {
		return memberMarketCode;
	}

	public void setMemberMarketCode(String memberMarketCode) {
		this.memberMarketCode = memberMarketCode;
	}

    @Column(name = "FLR_DSTRT_CD")
    public String getFloorDistrict() {
        return floorDistrict;
    }

    public void setFloorDistrict(String floorDistrict) {
        this.floorDistrict = floorDistrict;
    }

	@Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        if (obj instanceof PsOutput) {
            PsOutput o = (PsOutput) obj;
            return outputId == o.outputId && session.equals(o.session);
        } else {
            return false;
        }
    }


    @Override
    public int hashCode() {
        return (int) 31 * (outputId + session.hashCode());
    }

    @Transient
    public boolean isFinOpMatter() {
        return getExamTypeCd().equals(FIRST_FINOP_TYPE) || getExamTypeCd().equals(FINOP_TYPE)
                || getExamTypeCd().equals(FLOOR_TYPE) || getExamTypeCd().equals(ANC_TYPE);
    }

    public boolean hasMatter() {
        return null != matterId;
    }

    public boolean hasExam() {
        return null != examId;
    }
}
