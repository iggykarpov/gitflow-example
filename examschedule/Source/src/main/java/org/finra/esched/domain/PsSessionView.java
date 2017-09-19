package org.finra.esched.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonProperty;
import org.finra.exam.common.excel.annotation.ExtendedExcelGridItem;
import org.finra.exam.common.security.domain.User;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name="SCHDL_FIRM_VW")
@Immutable
public class PsSessionView implements Serializable{


	private int sssnId;
	private int firmId;
	private int versionId;

	// FIRM FIELDS

	private String firmNm;

	private String spImpctCd;
	private String spRiskCd;
	private String spDistrictCd;			// 10... M1...
	private String spRespDistrictTypeCd;	// SP/FN
	private String spDistrictDesc;			// New York... Manhattan 1
	private Integer spSupervisorStaffId;
	private String spSupervisorNm;


	private String fnImpctCd;
	private String fnRiskCd;
	private String fnDistrictCd;			// 10... M1...
	private String fnRespDistrictTypeCd;	// SP/FN
	private String fnDistrictDesc;			// New York... Manhattan 1
	private Integer fnSupervisorStaffId;
	private String fnSupervisorNm;

	// SESSION FIELDS

	private String auditMonth;

	private Date prjFwsd;
	private Date prjEwsd;

	private String sssnStatusCd;			// Status code
	private String sssnStatusDs;			// Status description
	private User sssnStatusUser;			// Who changed the status
	private Date sssnStatusDt;				// When status changed

	private Boolean spApprovedFl;			// Whether SP group of components was approved or no.
	private Boolean fnApprovedFl;			// Whether FN group of components was approved or no.
	private Boolean flApprovedFl;			// Whether FL group of components was approved or no.

	private User spApproveUser;
	private Date spApproveDt;

	private User fnApproveUser;
	private Date fnApproveDt;

	private User flApproveUser;
	private Date flApproveDt;

	private Boolean nmaFl;					// True whenever session has at least one NMA component

	// COMPONENTS FIELDS

	// - SP
	private Integer spId;                       // SCHDL_APLBL_CMPNT_ID
	private Boolean spRqFl;                 // RQRD_FL
	private Boolean spRqOvrrdFl;            // OVRD_RQRD_FL
	private Date spFwActualDate;            // from Last Exams
	private Date spFwPrjDate;             // from your view for projected dates
	private Boolean spNmaFl;
	private Boolean spRsaNmaFl;
	private Long spOvrrdReasonId;
	private String spOvrrdReasonDs;
	private String spOvrrdUserNm;
	private Date spOvrrdDate;
	private String spBusRvwTx;

	//- MUNI
	private Integer muniId;
	private Boolean muniRqFl;
	private Boolean muniRqOvrrdFl;
	private Date muniFwActualDate;
	private Date muniFwPrjDate;
	private Boolean muniNmaFl;
	private Boolean muniRsaNmaFl;
	private Long muniOvrrdReasonId;
	private String muniOvrrdReasonDs;
	private String muniOvrrdUserNm;
	private Date muniOvrrdDate;
	private String muniBusRvwTx;


	//- OPTION
	private Integer opId;
	private Boolean opRqFl;
	private Boolean opRqOvrrdFl;
	private Date opFwActualDate;
	private Date opFwPrjDate;
	private Boolean opNmaFl;
	private Boolean opRsaNmaFl;
	private Long opOvrrdReasonId;
	private String opOvrrdReasonDs;
	private String opOvrrdUserNm;
	private Date opOvrrdDate;
	private String opBusRvwTx;

	//- FFN
	private Integer ffnId;
	private Boolean ffnRqFl;
	private Boolean ffnRqOvrrdFl;
	private Date ffnFwActualDate;
	private Date ffnFwPrjDate;
	private Boolean ffnNmaFl;
	private Boolean ffnRsaNmaFl;
	private Long ffnOvrrdReasonId;
	private String ffnOvrrdReasonDs;
	private String ffnOvrrdUserNm;
	private Date ffnOvrrdDate;
	private String ffnBusRvwTx;

	//- FN
	private Integer fnId;
	private Boolean fnRqFl;
	private Boolean fnRqOvrrdFl;
	private Date fnFwActualDate;
	private Date fnFwPrjDate;
	private Boolean fnNmaFl;
	private Boolean fnRsaNmaFl;
	private Long fnOvrrdReasonId;
	private String fnOvrrdReasonDs;
	private String fnOvrrdUserNm;
	private Date fnOvrrdDate;
	private String fnBusRvwTx;

	//- FLOOR
	private Integer flId;
	private Boolean flRqFl;
	private Boolean flRqOvrrdFl;
	private Date flFwActualDate;
	private Date flFwPrjDate;
	private Boolean flNmaFl;
	private Boolean flRsaNmaFl;
	private Long flOvrrdReasonId;
	private String flOvrrdReasonDs;
	private String flOvrrdUserNm;
	private Date flOvrrdDate;
	private String floorBusRvwTx;

	//- ANC
	private Integer ancId;
	private Boolean ancRqFl;
	private Boolean ancRqOvrrdFl;
	private Date ancFwActualDate;
	private Date ancFwPrjDate;
	private Boolean ancNmaFl;
	private Boolean ancRsaNmaFl;
	private Long ancOvrrdReasonId;
	private String ancOvrrdReasonDs;
	private String ancOvrrdUserNm;
	private Date ancOvrrdDate;
	private String ancBusRvwTx;

	//- RSA_FINOP
	private Integer rsafnId;
	private Boolean rsafnRqFl;
	private Boolean rsafnRqOvrrdFl;
	private Date rsafnFwActualDate;
	private Date rsafnFwPrjDate;
	private Boolean rsafnNmaFl;
	private Boolean rsafnRsaNmaFl;
	private Long rsafnOvrrdReasonId;
	private String rsafnOvrrdReasonDs;
	private String rsafnOvrrdUserNm;
	private Date rsafnOvrrdDate;
	private String rsafnBusRvwTx;

	//- RSA_SP
	private Integer rsaspId;
	private Boolean rsaspRqFl;
	private Boolean rsaspRqOvrrdFl;
	private Date rsaspFwActualDate;
	private Date rsaspFwPrjDate;
	private Boolean rsaspNmaFl;
	private Boolean rsaspRsaNmaFl;
	private Long rsaspOvrrdReasonId;
	private String rsaspOvrrdReasonDs;
	private String rsaspOvrrdUserNm;
	private Date rsaspOvrrdDate;
	private String rsaspBusRvwTx;


	//- MUNI ADV
	private Integer maId;
	private Boolean maRqFl;
	private Boolean maRqOvrrdFl;
	private Date maFwActualDate;
	private Date maFwPrjDate;
	private Boolean maNmaFl;
	private Boolean maRsaNmaFl;
	private Long maOvrrdReasonId;
	private String maOvrrdReasonDs;
	private String maOvrrdUserNm;
	private Date maOvrrdDate;
	private String maBusRvwTx;

	//- SDF
	private Integer sdfId;
	private Boolean sdfRqFl;
	private Boolean sdfRqOvrrdFl;
	private Date sdfFwActualDate;
	private Date sdfFwPrjDate;
	private Boolean sdfNmaFl;
	private Boolean sdfRsaNmaFl;
	private Long sdfOvrrdReasonId;
	private String sdfOvrrdReasonDs;
	private String sdfOvrrdUserNm;
	private Date sdfOvrrdDate;
	private String sdfBusRvwTx;

	private String componentSp;
	//	private String componentOverrideUserSp;
	private String componentFinop;
	//	private String componentOverrideUserFinop;
	private String componentFirstFinop;
	//	private String componentOverrideUserFirstFinop;
	private String componentFloor;
	//	private String componentOverrideUserFloor;
	private String componentOption;
	//	private String componentOverrideUserOption;
	private String componentMunicipal;
	//	private String componentOverrideUserMunicipal;
	private String componentAnc;
//	private String componentOverrideUserAnc;

	private String componentRsaFn;
	private String componentRsaSp;
	private String componentSdf;
	private String componentMa;

	private String flDistrictCd;			// 10... M1...
	private String flRespDistrictTypeCd;	// SP/FN
	private String flDistrictDesc;			// New York... Manhattan 1

/*	private String businessReviewSp;
	private String businessReviewFinop;
	private String businessReviewFirstFinop;
	private String businessReviewFloor;
	private String businessReviewOption;
	private String businessReviewMunicipal;
	private String businessReviewAnc;
	private String businessReviewRsaFn;
	private String businessReviewRsaSp;
	private String businessReviewSdf;
	private String businessReviewMa;*/

	private Integer spFrequency;
	private String spImpact;
	private String spLikelihood;
	private String spComposite;

	private Integer finopFrequency;
	private String finopImpact;
	private String finopLikelihood;
	private String finopComposite;

	public PsSessionView() {

	}

	@Id
	@GeneratedValue
	@Column(name="FIRM_SSSN_ID")
	@JsonProperty("sssnId")
	public int getSssnId() {
		return sssnId;
	}
	public void setSssnId(int sssnId) {
		this.sssnId = sssnId;
	}


	@Column(name="FIRM_ID")
	@JsonProperty("firmId")
	@ExtendedExcelGridItem
	public int getFirmId() {
		return firmId;
	}
	public void setFirmId(int firmId) {
		this.firmId = firmId;
	}

	@Column(name="SCHDL_EXAM_SNPSH_ID")
	@JsonProperty("vrsnId")
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}

	@Column(name="FIRM_NM")
	@JsonProperty("firmNm")
	@ExtendedExcelGridItem
	public String getFirmNm() {
		return firmNm;
	}
	public void setFirmNm(String firmNm) {
		this.firmNm = firmNm;
	}

	// - SP
	@Column(name="SP_IMPCT_CD")
	@JsonProperty("spImpactCd")
	@ExtendedExcelGridItem
	public String getSpImpctCd() {
		return spImpctCd;
	}
	public void setSpImpctCd(String spImpctCd) {
		this.spImpctCd = spImpctCd;
	}

	@Column(name="SP_RISK_CD")
	@JsonProperty("spRiskCd")
	@ExtendedExcelGridItem
	public String getSpRiskCd() {
		return spRiskCd;
	}
	public void setSpRiskCd(String spRiskCd) {
		this.spRiskCd = spRiskCd;
	}

	@Column(name="SP_DSTRT_CD")
	@JsonProperty("spDistrCd")
	public String getSpDistrictCd() {
		return spDistrictCd;
	}
	public void setSpDistrictCd(String spDistrictCd) {
		this.spDistrictCd = spDistrictCd;
	}

	@Column(name="SP_DSTRT_TYPE_CD")
	@JsonProperty("spDistrTypeCd")
	public String getSpRespDistrictTypeCd() {
		return spRespDistrictTypeCd;
	}
	public void setSpRespDistrictTypeCd(String spRespDistrictTypeCd) {
		this.spRespDistrictTypeCd = spRespDistrictTypeCd;
	}

	@Column(name="SP_DSTRT_DS")
	@JsonProperty("spDistrDs")
	@ExtendedExcelGridItem
	public String getSpDistrictDesc() {
		return spDistrictDesc;
	}
	public void setSpDistrictDesc(String spDistrictDesc) {
		this.spDistrictDesc = spDistrictDesc;
	}

	@Column(name="SP_SPRV_STAFF_NM")
	@JsonProperty("spSupervisorNm")
	@ExtendedExcelGridItem
	public String getSpSupervisorNm() {
		return spSupervisorNm;
	}
	public void setSpSupervisorNm(String spSupervisorNm) {
		this.spSupervisorNm = spSupervisorNm;
	}

	@Column(name="FINOP_IMPCT_CD")
	@JsonProperty("fnImpactCd")
	@ExtendedExcelGridItem
	public String getFnImpctCd() {
		return fnImpctCd;
	}
	public void setFnImpctCd(String fnImpctCd) {
		this.fnImpctCd = fnImpctCd;
	}

	@Column(name="FINOP_RISK_CD")
	@JsonProperty("fnRiskCd")
	@ExtendedExcelGridItem
	public String getFnRiskCd() {
		return fnRiskCd;
	}
	public void setFnRiskCd(String fnRiskCd) {
		this.fnRiskCd = fnRiskCd;
	}

	@Column(name="FINOP_DSTRT_CD")
	@JsonProperty("fnDistrCd")
	public String getFnDistrictCd() {
		return fnDistrictCd;
	}
	public void setFnDistrictCd(String fnDistrictCd) {
		this.fnDistrictCd = fnDistrictCd;
	}

	@Column(name="FINOP_DSTRT_TYPE_CD")
	@JsonProperty("fnDistrTypeCd")
	public String getFnRespDistrictTypeCd() {
		return fnRespDistrictTypeCd;
	}
	public void setFnRespDistrictTypeCd(String fnRespDistrictTypeCd) {
		this.fnRespDistrictTypeCd = fnRespDistrictTypeCd;
	}

	@Column(name="FINOP_DSTRT_DS")
	@JsonProperty("fnDistrDs")
	@ExtendedExcelGridItem
	public String getFnDistrictDesc() {
		return fnDistrictDesc;
	}
	public void setFnDistrictDesc(String fnDistrictDesc) {
		this.fnDistrictDesc = fnDistrictDesc;
	}

	@Column(name="FINOP_SPRV_STAFF_NM")
	@JsonProperty("fnSupervisorNm")
	@ExtendedExcelGridItem
	public String getFnSupervisorNm() {
		return fnSupervisorNm;
	}

	public void setFnSupervisorNm(String fnSupervisorNm) {
		this.fnSupervisorNm = fnSupervisorNm;
	}

	@Column(name="AUDIT_MONTH_TX")
	@JsonProperty("auditMonth")
	@ExtendedExcelGridItem
	public String getAuditMonth() {
		return auditMonth;
	}
	public void setAuditMonth(String auditMonth) {
		this.auditMonth = auditMonth;
	}

	@Column(name="FLD_WRK_START_DT")
	@JsonProperty("fwsdDt")
	@ExtendedExcelGridItem
	public Date getPrjFwsd() {
		return prjFwsd;
	}
	public void setPrjFwsd(Date prjFwsd) {
		this.prjFwsd = prjFwsd;
	}

	@Column(name="EXAM_WRK_START_DT")
	@JsonProperty("ewsdDt")
	@ExtendedExcelGridItem
	public Date getPrjEwsd() {
		return prjEwsd;
	}
	public void setPrjEwsd(Date prjEwsd) {
		this.prjEwsd = prjEwsd;
	}


	@Column(name="SCHDL_SSSN_STTS_CD")
	@JsonProperty("sssnStatusCd")
	public String getSssnStatusCd() {
		return sssnStatusCd;
	}
	public void setSssnStatusCd(String sssnStatusCd) {
		this.sssnStatusCd = sssnStatusCd;
	}


	@Column(name="SCHDL_SSSN_STTS_DS")
	@JsonProperty("sssnStatusDs")
	@ExtendedExcelGridItem
	public String getSssnStatusDs() {
		return sssnStatusDs;
	}
	public void setSssnStatusDs(String sssnStatusDs) {
		this.sssnStatusDs = sssnStatusDs;
	}

	@ManyToOne
	@JoinColumn(name="USER_ID")
	@JsonProperty("sssnStatusUser")
	public User getSssnStatusUser() {
		return sssnStatusUser;
	}
	public void setSssnStatusUser(User sssnStatusUser) {
		this.sssnStatusUser = sssnStatusUser;
	}

	@Column(name="UPDT_TS")
	@JsonProperty("sssnStatusDt")
	public Date getSssnStatusDt() {
		return sssnStatusDt;
	}
	public void setSssnStatusDt(Date sssnStatusDt) {
		this.sssnStatusDt = sssnStatusDt;
	}

	@Column(name="SP_APPRV_FL")
	@JsonProperty("spApprovedFl")
	public Boolean isSpApprovedFl() {
		return spApprovedFl;
	}
	public void setSpApprovedFl(Boolean spApprovedFl) {
		this.spApprovedFl = spApprovedFl;
	}

	@ManyToOne
	@JoinColumn(name="sp_apprv_user_id")
	@JsonProperty("spApproveUser")
	public User getSpApproveUser() {
		return spApproveUser;
	}
	public void setSpApproveUser(User spApproveUser) {
		this.spApproveUser = spApproveUser;
	}

	@Column(name="sp_apprv_updt_ts")
	@JsonProperty("spApproveDt")
	public Date getSpApproveDt() {
		return spApproveDt;
	}
	public void setSpApproveDt(Date spApproveDt) {
		this.spApproveDt = spApproveDt;
	}

	@Column(name="FINOP_APPRV_FL")
	@JsonProperty("fnApprovedFl")
	public Boolean isFnApprovedFl() {
		return fnApprovedFl;
	}
	public void setFnApprovedFl(Boolean fnApprovedFl) {
		this.fnApprovedFl = fnApprovedFl;
	}

	@ManyToOne
	@JoinColumn(name="finop_apprv_user_id")
	@JsonProperty("fnApproveUser")
	public User getFnApproveUser() {
		return fnApproveUser;
	}
	public void setFnApproveUser(User fnApproveUser) {
		this.fnApproveUser = fnApproveUser;
	}


	@Column(name="finop_apprv_updt_ts")
	@JsonProperty("fnApproveDt")
	public Date getFnApproveDt() {
		return fnApproveDt;
	}
	public void setFnApproveDt(Date fnApproveDt) {
		this.fnApproveDt = fnApproveDt;
	}

	@Column(name="FLR_APPRV_FL")
	@JsonProperty("flApprovedFl")
	public Boolean getFlApprovedFl() {
		return flApprovedFl;
	}

	public void setFlApprovedFl(Boolean flApprovedFl) {
		this.flApprovedFl = flApprovedFl;
	}

	@ManyToOne
	@JoinColumn(name="FLR_APPRV_USER_ID")
	@JsonProperty("flApproveUser")
	public User getFlApproveUser() {
		return flApproveUser;
	}

	public void setFlApproveUser(User flApproveUser) {
		this.flApproveUser = flApproveUser;
	}

	@Column(name="FLR_APPRV_UPDT_TS")
	@JsonProperty("flApproveDt")
	public Date getFlApproveDt() {
		return flApproveDt;
	}

	public void setFlApproveDt(Date flApproveDt) {
		this.flApproveDt = flApproveDt;
	}

	@Column(name="NMA_FL")
	@JsonProperty("nmaFl")
	public Boolean isNmaFl() {
		return nmaFl;
	}
	public void setNmaFl(Boolean nmaFl) {
		this.nmaFl = nmaFl;
	}

	// SP

	@Column(name="SP_APLBL_ID")
	@JsonProperty("spId")
	public Integer getSpId() {
		return spId;
	}
	public void setSpId(Integer spId) {
		this.spId = spId;
	}

	@Column(name="SP_RQRD_FL")
	@JsonProperty("spReqrdFl")
	public Boolean isSpRqFl() {
		return spRqFl;
	}
	public void setSpRqFl(Boolean spRqFl) {
		this.spRqFl = spRqFl;
	}

	@Column(name="SP_OVRD_RQRD_FL")
	@JsonProperty("spReqrdOvrrdFl")
	public Boolean getSpRqOvrrdFl() {
		return spRqOvrrdFl;
	}
	public void setSpRqOvrrdFl(Boolean spRqOvrrdFl) {
		this.spRqOvrrdFl = spRqOvrrdFl;
	}

	@Column(name="SP_FLD_WRK_START_ACTL_DT")
	@JsonProperty("spFwActualDt")
	public Date getSpFwActualDate() {
		return spFwActualDate;
	}
	public void setSpFwActualDate(Date spFwActualDate) {
		this.spFwActualDate = spFwActualDate;
	}

	@Column(name="SP_FLD_WRK_START_PRJTD_DT")
	@JsonProperty("spFwPrjDt")
	public Date getSpFwPrjDate() {
		return spFwPrjDate;
	}
	public void setSpFwPrjDate(Date spFwPrjDate) {
		this.spFwPrjDate = spFwPrjDate;
	}

	@Column(name="SP_NMA_FL")
	@JsonProperty("spNmaFl")
	public Boolean getSpNmaFl() {
		return spNmaFl;
	}
	public void setSpNmaFl(Boolean spNmaFl) {
		this.spNmaFl = spNmaFl;
	}

	@Column(name="SP_RSA_NMA_FL")
	@JsonProperty("spRsaNmaFl")
	public Boolean getSpRsaNmaFl() {
		return spRsaNmaFl;
	}
	public void setSpRsaNmaFl(Boolean spRsaNmaFl) {
		this.spRsaNmaFl = spRsaNmaFl;
	}

	@Column(name="SP_USER_FULL_NM")
	@JsonProperty("spOvrrdUserNm")
	@ExtendedExcelGridItem
	public String getSpOvrrdUserNm() {
		return spOvrrdUserNm;
	}
	public void setSpOvrrdUserNm(String spOvrrdUserNm) {
		this.spOvrrdUserNm = spOvrrdUserNm;
	}

	@Column(name="SP_LAST_OVRD_UPDT_TS")
	@JsonProperty("spOvrrdDt")
	public Date getSpOvrrdDate() {
		return spOvrrdDate;
	}
	public void setSpOvrrdDate(Date spOvrrdDate) {
		this.spOvrrdDate = spOvrrdDate;
	}

	@Column(name="SP_BUS_RVW_TX")
	@JsonProperty("spBusinessReview")
	@ExtendedExcelGridItem
	public String getSpBusRvwTx() {return spBusRvwTx;}
	public void setSpBusRvwTx(String spBusRvwTx) {this.spBusRvwTx = spBusRvwTx;}

	// MUNI

	@Column(name="MUNI_APLBL_ID")
	@JsonProperty("muniId")
	public Integer getMuniId() {
		return muniId;
	}
	public void setMuniId(Integer muniId) {
		this.muniId = muniId;
	}

	@Column(name="MUNI_RQRD_FL")
	@JsonProperty("muniReqrdFl")
	public Boolean isMuniRqFl() {
		return muniRqFl;
	}
	public void setMuniRqFl(Boolean muniRqFl) {
		this.muniRqFl = muniRqFl;
	}

	@Column(name="MUNI_OVRD_RQRD_FL")
	@JsonProperty("muniReqrdOvrrdFl")
	public Boolean getMuniRqOvrrdFl() {
		return muniRqOvrrdFl;
	}
	public void setMuniRqOvrrdFl(Boolean muniRqOvrrdFl) {
		this.muniRqOvrrdFl = muniRqOvrrdFl;
	}

	@Column(name="MUNI_FLD_WRK_START_ACTL_DT")
	@JsonProperty("muniFwActualDt")
	public Date getMuniFwActualDate() {
		return muniFwActualDate;
	}
	public void setMuniFwActualDate(Date muniFwActualDate) {
		this.muniFwActualDate = muniFwActualDate;
	}

	@Column(name="MUNI_FLD_WRK_START_PRJTD_DT")
	@JsonProperty("muniFwPrjDt")
	public Date getMuniFwPrjDate() {
		return muniFwPrjDate;
	}
	public void setMuniFwPrjDate(Date muniFwPrjDate) {
		this.muniFwPrjDate = muniFwPrjDate;
	}

	@Column(name="MUNI_NMA_FL")
	@JsonProperty("muniNmaFl")
	public Boolean getMuniNmaFl() {
		return muniNmaFl;
	}
	public void setMuniNmaFl(Boolean muniNmaFl) {
		this.muniNmaFl = muniNmaFl;
	}

	@Column(name="MUNI_RSA_NMA_FL")
	@JsonProperty("muniRsaNmaFl")
	public Boolean getMuniRsaNmaFl() {
		return muniRsaNmaFl;
	}
	public void setMuniRsaNmaFl(Boolean muniRsaNmaFl) {
		this.muniRsaNmaFl = muniRsaNmaFl;
	}

	@Column(name="MUNI_USER_FULL_NM")
	@JsonProperty("muniOvrrdUserNm")
	@ExtendedExcelGridItem
	public String getMuniOvrrdUserNm() {
		return muniOvrrdUserNm;
	}
	public void setMuniOvrrdUserNm(String muniOvrrdUserNm) {
		this.muniOvrrdUserNm = muniOvrrdUserNm;
	}

	@Column(name="MUNI_LAST_OVRD_UPDT_TS")
	@JsonProperty("muniOvrrdDt")
	public Date getMuniOvrrdDate() {
		return muniOvrrdDate;
	}
	public void setMuniOvrrdDate(Date muniOvrrdDate) {
		this.muniOvrrdDate = muniOvrrdDate;
	}

	@Column(name="MUNI_BUS_RVW_TX")
	@JsonProperty("muniBusinessReview")
	@ExtendedExcelGridItem
	public String getMuniBusRvwTx() {return muniBusRvwTx;}
	public void setMuniBusRvwTx(String muniBusRvwTx) {this.muniBusRvwTx = muniBusRvwTx;}


	// OPTION

	@Column(name="OPTIONS_APLBL_ID")
	@JsonProperty("opId")
	public Integer getOpId() {
		return opId;
	}
	public void setOpId(Integer opId) {
		this.opId = opId;
	}

	@Column(name="OPTIONS_RQRD_FL")
	@JsonProperty("opReqrdFl")
	public Boolean isOpRqFl() {
		return opRqFl;
	}
	public void setOpRqFl(Boolean opRqFl) {
		this.opRqFl = opRqFl;
	}

	@Column(name="OPTIONS_OVRD_RQRD_FL")
	@JsonProperty("opReqrdOvrrdFl")
	public Boolean getOpRqOvrrdFl() {
		return opRqOvrrdFl;
	}
	public void setOpRqOvrrdFl(Boolean opRqOvrrdFl) {
		this.opRqOvrrdFl = opRqOvrrdFl;
	}

	@Column(name="OPTIONS_FLD_WRK_START_ACTL_DT")
	@JsonProperty("opFwActualDt")
	public Date getOpFwActualDate() {
		return opFwActualDate;
	}
	public void setOpFwActualDate(Date opFwActualDate) {
		this.opFwActualDate = opFwActualDate;
	}

	@Column(name="OPTIONS_FLD_WRK_START_PRJTD_DT")
	@JsonProperty("opFwPrjDt")
	public Date getOpFwPrjDate() {
		return opFwPrjDate;
	}
	public void setOpFwPrjDate(Date opFwPrjDate) {
		this.opFwPrjDate = opFwPrjDate;
	}

	@Column(name="OPTIONS_NMA_FL")
	@JsonProperty("opNmaFl")
	public Boolean getOpNmaFl() {
		return opNmaFl;
	}
	public void setOpNmaFl(Boolean opNmaFl) {
		this.opNmaFl = opNmaFl;
	}

	@Column(name="OPTIONS_RSA_NMA_FL")
	@JsonProperty("opRsaNmaFl")
	public Boolean getOpRsaNmaFl() {
		return opRsaNmaFl;
	}
	public void setOpRsaNmaFl(Boolean opRsaNmaFl) {
		this.opRsaNmaFl = opRsaNmaFl;
	}

	@Column(name="OPTIONS_USER_FULL_NM")
	@JsonProperty("opOvrrdUserNm")
	@ExtendedExcelGridItem
	public String getOpOvrrdUserNm() {
		return opOvrrdUserNm;
	}
	public void setOpOvrrdUserNm(String opOvrrdUserNm) {
		this.opOvrrdUserNm = opOvrrdUserNm;
	}

	@Column(name="OPTIONS_LAST_OVRD_UPDT_TS")
	@JsonProperty("opOvrrdDt")
	public Date getOpOvrrdDate() {
		return opOvrrdDate;
	}
	public void setOpOvrrdDate(Date opOvrrdDate) {
		this.opOvrrdDate = opOvrrdDate;
	}

	@Column(name="OPTN_BUS_RVW_TX")
	@JsonProperty("opBusinessReview")
	@ExtendedExcelGridItem
	public String getOpBusRvwTx() {return opBusRvwTx;}
	public void setOpBusRvwTx(String opBusRvwTx) {this.opBusRvwTx = opBusRvwTx;}

	// FFN

	@Column(name="FRST_FN_APLBL_ID")
	@JsonProperty("ffnId")
	public Integer getFfnId() {
		return ffnId;
	}
	public void setFfnId(Integer ffnId) {
		this.ffnId = ffnId;
	}

	@Column(name="FRST_FN_RQRD_FL")
	@JsonProperty("ffnReqrdFl")
	public Boolean isFfnRqFl() {
		return ffnRqFl;
	}
	public void setFfnRqFl(Boolean ffnRqFl) {
		this.ffnRqFl = ffnRqFl;
	}

	@Column(name="FRST_FN_OVRD_RQRD_FL")
	@JsonProperty("ffnReqrdOvrrdFl")
	public Boolean getFfnRqOvrrdFl() {
		return ffnRqOvrrdFl;
	}
	public void setFfnRqOvrrdFl(Boolean ffnRqOvrrdFl) {
		this.ffnRqOvrrdFl = ffnRqOvrrdFl;
	}

	@Column(name="FRST_FN_FLD_WRK_START_ACTL_DT")
	@JsonProperty("ffnFwActualDt")
	public Date getFfnFwActualDate() {
		return ffnFwActualDate;
	}
	public void setFfnFwActualDate(Date ffnFwActualDate) {
		this.ffnFwActualDate = ffnFwActualDate;
	}

	@Column(name="FRST_FN_FLD_WRK_START_PRJTD_DT")
	@JsonProperty("ffnFwPrjDt")
	public Date getFfnFwPrjDate() {
		return ffnFwPrjDate;
	}
	public void setFfnFwPrjDate(Date ffnFwPrjDate) {
		this.ffnFwPrjDate = ffnFwPrjDate;
	}

	@Column(name="FRST_FN_NMA_FL")
	@JsonProperty("ffnNmaFl")
	public Boolean getFfnNmaFl() {
		return ffnNmaFl;
	}
	public void setFfnNmaFl(Boolean ffnNmaFl) {
		this.ffnNmaFl = ffnNmaFl;
	}

	@Column(name="FRST_FN_RSA_NMA_FL")
	@JsonProperty("ffnRsaNmaFl")
	public Boolean getFfnRsaNmaFl() {
		return ffnRsaNmaFl;
	}
	public void setFfnRsaNmaFl(Boolean ffnRsaNmaFl) {
		this.ffnRsaNmaFl = ffnRsaNmaFl;
	}

	@Column(name="FRST_FN_USER_FULL_NM")
	@JsonProperty("ffnOvrrdUserNm")
	@ExtendedExcelGridItem
	public String getFfnOvrrdUserNm() {
		return ffnOvrrdUserNm;
	}
	public void setFfnOvrrdUserNm(String ffnOvrrdUserNm) {
		this.ffnOvrrdUserNm = ffnOvrrdUserNm;
	}

	@Column(name="FRST_FN_LAST_OVRD_UPDT_TS")
	@JsonProperty("ffnOvrrdDt")
	public Date getFfnOvrrdDate() {
		return ffnOvrrdDate;
	}
	public void setFfnOvrrdDate(Date ffnOvrrdDate) {
		this.ffnOvrrdDate = ffnOvrrdDate;
	}

	@Column(name="FRST_FINOP_BUS_RVW_TX")
	@JsonProperty("ffnBusinessReview")
	@ExtendedExcelGridItem
	public String getFfnBusRvwTx() {return ffnBusRvwTx;}
	public void setFfnBusRvwTx(String ffnBusRvwTx) {this.ffnBusRvwTx = ffnBusRvwTx;}

	// FN

	@Column(name="FN_APLBL_ID")
	@JsonProperty("fnId")
	public Integer getFnId() {
		return fnId;
	}
	public void setFnId(Integer fnId) {
		this.fnId = fnId;
	}

	@Column(name="FN_RQRD_FL")
	@JsonProperty("fnReqrdFl")
	public Boolean isFnRqFl() {
		return fnRqFl;
	}
	public void setFnRqFl(Boolean fnRqFl) {
		this.fnRqFl = fnRqFl;
	}

	@Column(name="FN_OVRD_RQRD_FL")
	@JsonProperty("fnReqrdOvrrdFl")
	public Boolean getFnRqOvrrdFl() {
		return fnRqOvrrdFl;
	}
	public void setFnRqOvrrdFl(Boolean fnRqOvrrdFl) {
		this.fnRqOvrrdFl = fnRqOvrrdFl;
	}

	@Column(name="FN_FLD_WRK_START_ACTL_DT")
	@JsonProperty("fnFwActualDt")
	public Date getFnFwActualDate() {
		return fnFwActualDate;
	}
	public void setFnFwActualDate(Date fnFwActualDate) {
		this.fnFwActualDate = fnFwActualDate;
	}

	@Column(name="FN_FLD_WRK_START_PRJTD_DT")
	@JsonProperty("fnFwPrjDt")
	public Date getFnFwPrjDate() {
		return fnFwPrjDate;
	}
	public void setFnFwPrjDate(Date fnFwPrjDate) {
		this.fnFwPrjDate = fnFwPrjDate;
	}

	@Column(name="FN_NMA_FL")
	@JsonProperty("fnNmaFl")
	public Boolean getFnNmaFl() {
		return fnNmaFl;
	}
	public void setFnNmaFl(Boolean fnNmaFl) {
		this.fnNmaFl = fnNmaFl;
	}

	@Column(name="FN_RSA_NMA_FL")
	@JsonProperty("fnRsaNmaFl")
	public Boolean getFnRsaNmaFl() {
		return fnRsaNmaFl;
	}
	public void setFnRsaNmaFl(Boolean fnRsaNmaFl) {
		this.fnRsaNmaFl = fnRsaNmaFl;
	}

	@Column(name="FN_USER_FULL_NM")
	@JsonProperty("fnOvrrdUserNm")
	@ExtendedExcelGridItem
	public String getFnOvrrdUserNm() {
		return fnOvrrdUserNm;
	}
	public void setFnOvrrdUserNm(String fnOvrrdUserNm) {
		this.fnOvrrdUserNm = fnOvrrdUserNm;
	}

	@Column(name="FN_LAST_OVRD_UPDT_TS")
	@JsonProperty("fnOvrrdDt")
	public Date getFnOvrrdDate() {
		return fnOvrrdDate;
	}
	public void setFnOvrrdDate(Date fnOvrrdDate) {
		this.fnOvrrdDate = fnOvrrdDate;
	}

	@Column(name="FINOP_BUS_RVW_TX")
	@JsonProperty("fnBusinessReview")
	@ExtendedExcelGridItem
	public String getFnBusRvwTx() {return fnBusRvwTx;}
	public void setFnBusRvwTx(String fnBusRvwTx) {this.fnBusRvwTx = fnBusRvwTx;}

	// FLOOR

	@Column(name="FLOOR_APLBL_ID")
	@JsonProperty("flId")
	public Integer getFlId() {
		return flId;
	}
	public void setFlId(Integer flId) {
		this.flId = flId;
	}

	@Column(name="FLOOR_RQRD_FL")
	@JsonProperty("flReqrdFl")
	public Boolean isFlRqFl() {
		return flRqFl;
	}
	public void setFlRqFl(Boolean flRqFl) {
		this.flRqFl = flRqFl;
	}

	@Column(name="FLOOR_OVRD_RQRD_FL")
	@JsonProperty("flReqrdOvrrdFl")
	public Boolean getFlRqOvrrdFl() {
		return flRqOvrrdFl;
	}
	public void setFlRqOvrrdFl(Boolean flRqOvrrdFl) {
		this.flRqOvrrdFl = flRqOvrrdFl;
	}

	@Column(name="FLOOR_FLD_WRK_START_ACTL_DT")
	@JsonProperty("flFwActualDt")
	public Date getFlFwActualDate() {
		return flFwActualDate;
	}
	public void setFlFwActualDate(Date flFwActualDate) {
		this.flFwActualDate = flFwActualDate;
	}

	@Column(name="FLOOR_FLD_WRK_START_PRJTD_DT")
	@JsonProperty("flFwPrjDt")
	public Date getFlFwPrjDate() {
		return flFwPrjDate;
	}
	public void setFlFwPrjDate(Date flFwPrjDate) {
		this.flFwPrjDate = flFwPrjDate;
	}

	@Column(name="FLOOR_NMA_FL")
	@JsonProperty("flNmaFl")
	public Boolean getFlNmaFl() {
		return flNmaFl;
	}
	public void setFlNmaFl(Boolean flNmaFl) {
		this.flNmaFl = flNmaFl;
	}

	@Column(name="FLOOR_RSA_NMA_FL")
	@JsonProperty("flRsaNmaFl")
	public Boolean getFlRsaNmaFl() {
		return flRsaNmaFl;
	}
	public void setFlRsaNmaFl(Boolean flRsaNmaFl) {
		this.flRsaNmaFl = flRsaNmaFl;
	}

	@Column(name="FLOOR_USER_FULL_NM")
	@JsonProperty("flOvrrdUserNm")
	@ExtendedExcelGridItem
	public String getFlOvrrdUserNm() {
		return flOvrrdUserNm;
	}
	public void setFlOvrrdUserNm(String flOvrrdUserNm) {
		this.flOvrrdUserNm = flOvrrdUserNm;
	}

	@Column(name="FLOOR_LAST_OVRD_UPDT_TS")
	@JsonProperty("flOvrrdDt")
	public Date getFlOvrrdDate() {
		return flOvrrdDate;
	}
	public void setFlOvrrdDate(Date flOvrrdDate) {
		this.flOvrrdDate = flOvrrdDate;
	}

	@Column(name="FLOOR_REVIEW_BUS_RVW_TX")
	@JsonProperty("flBusinessReview")
	@ExtendedExcelGridItem
	public String getFloorBusRvwTx() {return floorBusRvwTx;}
	public void setFloorBusRvwTx(String floorBusRvwTx) {this.floorBusRvwTx = floorBusRvwTx;}

	// ANC

	@Column(name="ANC_APLBL_ID")
	@JsonProperty("ancId")
	public Integer getAncId() {
		return ancId;
	}
	public void setAncId(Integer ancId) {
		this.ancId = ancId;
	}

	@Column(name="ANC_RQRD_FL")
	@JsonProperty("ancReqrdFl")
	public Boolean isAncRqFl() {
		return ancRqFl;
	}
	public void setAncRqFl(Boolean ancRqFl) {
		this.ancRqFl = ancRqFl;
	}

	@Column(name="ANC_OVRD_RQRD_FL")
	@JsonProperty("ancReqrdOvrrdFl")
	public Boolean getAncRqOvrrdFl() {
		return ancRqOvrrdFl;
	}
	public void setAncRqOvrrdFl(Boolean ancRqOvrrdFl) {
		this.ancRqOvrrdFl = ancRqOvrrdFl;
	}

	@Column(name="ANC_FLD_WRK_START_ACTL_DT")
	@JsonProperty("ancFwActualDt")
	public Date getAncFwActualDate() {
		return ancFwActualDate;
	}
	public void setAncFwActualDate(Date ancFwActualDate) {
		this.ancFwActualDate = ancFwActualDate;
	}

	@Column(name="ANC_FLD_WRK_START_PRJTD_DT")
	@JsonProperty("ancFwPrjDt")
	public Date getAncFwPrjDate() {
		return ancFwPrjDate;
	}
	public void setAncFwPrjDate(Date ancFwPrjDate) {
		this.ancFwPrjDate = ancFwPrjDate;
	}

	@Column(name="ANC_NMA_FL")
	@JsonProperty("ancNmaFl")
	public Boolean getAncNmaFl() {
		return ancNmaFl;
	}
	public void setAncNmaFl(Boolean ancNmaFl) {
		this.ancNmaFl = ancNmaFl;
	}

	@Column(name="ANC_RSA_NMA_FL")
	@JsonProperty("ancRsaNmaFl")
	public Boolean getAncRsaNmaFl() {
		return ancRsaNmaFl;
	}
	public void setAncRsaNmaFl(Boolean ancRsaNmaFl) {
		this.ancRsaNmaFl = ancRsaNmaFl;
	}

	@Column(name="ANC_USER_FULL_NM")
	@JsonProperty("ancOvrrdUserNm")
	@ExtendedExcelGridItem
	public String getAncOvrrdUserNm() {
		return ancOvrrdUserNm;
	}
	public void setAncOvrrdUserNm(String ancOvrrdUserNm) {
		this.ancOvrrdUserNm = ancOvrrdUserNm;
	}

	@Column(name="ANC_LAST_OVRD_UPDT_TS")
	@JsonProperty("ancOvrrdDt")
	public Date getAncOvrrdDate() {
		return ancOvrrdDate;
	}
	public void setAncOvrrdDate(Date ancOvrrdDate) {
		this.ancOvrrdDate = ancOvrrdDate;
	}

	@Column(name="ANC_BUS_RVW_TX")
	@JsonProperty("ancBusinessReview")
	@ExtendedExcelGridItem
	public String getAncBusRvwTx() {return ancBusRvwTx;}
	public void setAncBusRvwTx(String ancBusRvwTx) {this.ancBusRvwTx = ancBusRvwTx;}



	@Column(name="SP_OVRD_RSN_ID")
	@JsonProperty("spOvrrdReasonId")
	@ExtendedExcelGridItem
	public Long getSpOvrrdReasonId() {
		return spOvrrdReasonId;
	}
	public void setSpOvrrdReasonId(Long spOvrrdReasonId) {
		this.spOvrrdReasonId = spOvrrdReasonId;
	}

	@Column(name="SP_OVRD_RSN_DS")
	@ExtendedExcelGridItem
	public String getSpOvrrdReasonDs() {
		return spOvrrdReasonDs;
	}
	public void setSpOvrrdReasonDs(String spOvrrdReasonDs) {
		this.spOvrrdReasonDs = spOvrrdReasonDs;
	}

	@Column(name="MUNI_OVRD_RSN_ID")
	@JsonProperty("muniOvrrdReasonId")
	@ExtendedExcelGridItem
	public Long getMuniOvrrdReasonId() {
		return muniOvrrdReasonId;
	}
	public void setMuniOvrrdReasonId(Long muniOvrrdReasonId) {
		this.muniOvrrdReasonId = muniOvrrdReasonId;
	}


	@Column(name="MUNI_OVRD_RSN_DS")
	@ExtendedExcelGridItem
	public String getMuniOvrrdReasonDs() {
		return muniOvrrdReasonDs;
	}
	public void setMuniOvrrdReasonDs(String muniOvrrdReasonDs) {
		this.muniOvrrdReasonDs = muniOvrrdReasonDs;
	}

	@Column(name="OPTIONS_OVRD_RSN_ID")
	@JsonProperty("opOvrrdReasonId")
	@ExtendedExcelGridItem
	public Long getOpOvrrdReasonId() {
		return opOvrrdReasonId;
	}
	public void setOpOvrrdReasonId(Long opOvrrdReasonId) {
		this.opOvrrdReasonId = opOvrrdReasonId;
	}

	@Column(name="OPTIONS_OVRD_RSN_DS")
	@ExtendedExcelGridItem
	public String getOpOvrrdReasonDs() {
		return opOvrrdReasonDs;
	}
	public void setOpOvrrdReasonDs(String opOvrrdReasonDs) {
		this.opOvrrdReasonDs = opOvrrdReasonDs;
	}

	@Column(name="FRST_FN_OVRD_RSN_ID")
	@JsonProperty("ffnOvrrdReasonId")
	@ExtendedExcelGridItem
	public Long getFfnOvrrdReasonId() {
		return ffnOvrrdReasonId;
	}
	public void setFfnOvrrdReasonId(Long ffnOvrrdReasonId) {
		this.ffnOvrrdReasonId = ffnOvrrdReasonId;
	}

	@Column(name="FRST_FN_OVRD_RSN_DS")
	@ExtendedExcelGridItem
	public String getFfnOvrrdReasonDs() {
		return ffnOvrrdReasonDs;
	}
	public void setFfnOvrrdReasonDs(String ffnOvrrdReasonDs) {
		this.ffnOvrrdReasonDs = ffnOvrrdReasonDs;
	}

	@Column(name="FN_OVRD_RSN_ID")
	@JsonProperty("fnOvrrdReasonId")
	@ExtendedExcelGridItem
	public Long getFnOvrrdReasonId() {
		return fnOvrrdReasonId;
	}
	public void setFnOvrrdReasonId(Long fnOvrrdReasonId) {
		this.fnOvrrdReasonId = fnOvrrdReasonId;
	}

	@Column(name="FN_OVRD_RSN_DS")
	@ExtendedExcelGridItem
	public String getFnOvrrdReasonDs() {
		return fnOvrrdReasonDs;
	}
	public void setFnOvrrdReasonDs(String fnOvrrdReasonDs) {
		this.fnOvrrdReasonDs = fnOvrrdReasonDs;
	}

	@Column(name="FLOOR_OVRD_RSN_ID")
	@JsonProperty("flOvrrdReasonId")
	@ExtendedExcelGridItem
	public Long getFlOvrrdReasonId() {
		return flOvrrdReasonId;
	}
	public void setFlOvrrdReasonId(Long flOvrrdReasonId) {
		this.flOvrrdReasonId = flOvrrdReasonId;
	}

	@Column(name="FLOOR_OVRD_RSN_DS")
	@ExtendedExcelGridItem
	public String getFlOvrrdReasonDs() {
		return flOvrrdReasonDs;
	}
	public void setFlOvrrdReasonDs(String flOvrrdReasonDs) {
		this.flOvrrdReasonDs = flOvrrdReasonDs;
	}

	@Column(name="ANC_OVRD_RSN_ID")
	@JsonProperty("ancOvrrdReasonId")
	@ExtendedExcelGridItem
	public Long getAncOvrrdReasonId() {
		return ancOvrrdReasonId;
	}
	public void setAncOvrrdReasonId(Long ancOvrrdReasonId) {
		this.ancOvrrdReasonId = ancOvrrdReasonId;
	}

	@Column(name="ANC_OVRD_RSN_DS")
	@ExtendedExcelGridItem
	public String getAncOvrrdReasonDs() {
		return ancOvrrdReasonDs;
	}
	public void setAncOvrrdReasonDs(String ancOvrrdReasonDs) {
		this.ancOvrrdReasonDs = ancOvrrdReasonDs;
	}

	@Transient
	@ExtendedExcelGridItem
	public String getComponentSp() {
		if (spRqOvrrdFl != null){
			return (spRqOvrrdFl ? "Y": "N");
		}else if(spRqFl != null){
			return spRqFl ? "Y" : "N";
		}
		return "";
	}

	public void setComponentSp(String componentSp) {
		this.componentSp = componentSp;
	}


	/*@Column(name="SP_USER_FULL_NM")
	@ExtendedExcelGridItem
	public String getComponentOverrideUserSp() {
		return componentOverrideUserSp;
	}

	public void setComponentOverrideUserSp(String componentOverrideUserSp) {
		this.componentOverrideUserSp = componentOverrideUserSp;
	}
*/

	@Transient
	@ExtendedExcelGridItem
	public String getComponentFinop() {
		if (fnRqOvrrdFl != null){
			return (fnRqOvrrdFl ? "Y": "N");
		}else if(fnRqFl != null){
			return fnRqFl ? "Y" : "N";
		}
		return "";
	}

	public void setComponentFinop(String componentFinop) {
		this.componentFinop = componentFinop;
	}

	/*@Column(name="FN_USER_FULL_NM")
	@ExtendedExcelGridItem
	public String getComponentOverrideUserFinop() {
		return componentOverrideUserFinop;
	}

	public void setComponentOverrideUserFinop(String componentOverrideUserFinop) {
		this.componentOverrideUserFinop = componentOverrideUserFinop;
	}
*/

	@Transient
	@ExtendedExcelGridItem
	public String getComponentFirstFinop() {
		if (ffnRqOvrrdFl != null){
			return (ffnRqOvrrdFl ? "Y": "N");
		}else if(ffnRqFl != null){
			return ffnRqFl ? "Y" : "N";
		}
		return "";
	}

	public void setComponentFirstFinop(String componentFirstFinop) {
		this.componentFirstFinop = componentFirstFinop;
	}


	/*@Column(name="FRST_FN_USER_FULL_NM")
	@ExtendedExcelGridItem
	public String getComponentOverrideUserFirstFinop() {
		return componentOverrideUserFirstFinop;
	}

	public void setComponentOverrideUserFirstFinop(
			String componentOverrideUserFirstFinop) {
		this.componentOverrideUserFirstFinop = componentOverrideUserFirstFinop;
	}*/


	@Transient
	@ExtendedExcelGridItem
	public String getComponentFloor() {
		if (flRqOvrrdFl != null){
			return (flRqOvrrdFl ? "Y": "N");
		}else if(flRqFl != null){
			return flRqFl ? "Y" : "N";
		}
		return "";
	}

	public void setComponentFloor(String componentFloor) {
		this.componentFloor = componentFloor;
	}


	/*@Column(name="FLOOR_USER_FULL_NM")
	@ExtendedExcelGridItem
	public String getComponentOverrideUserFloor() {
		return componentOverrideUserFloor;
	}

	public void setComponentOverrideUserFloor(String componentOverrideUserFloor) {
		this.componentOverrideUserFloor = componentOverrideUserFloor;
	}*/


	@Transient
	@ExtendedExcelGridItem
	public String getComponentOption() {
		if (opRqOvrrdFl != null){
			return (opRqOvrrdFl ? "Y": "N");
		}else if(opRqFl != null){
			return opRqFl ? "Y" : "N";
		}
		return "";
	}

	public void setComponentOption(String componentOption) {
		this.componentOption = componentOption;
	}


	/*@Column(name="OPTIONS_USER_FULL_NM")
	@ExtendedExcelGridItem
	public String getComponentOverrideUserOption() {
		return componentOverrideUserOption;
	}

	public void setComponentOverrideUserOption(String componentOverrideUserOption) {
		this.componentOverrideUserOption = componentOverrideUserOption;
	}*/


	@Transient
	@ExtendedExcelGridItem
	public String getComponentMunicipal() {
		if (muniRqOvrrdFl != null){
			return (muniRqOvrrdFl ? "Y": "N");
		}else if(muniRqFl != null){
			return muniRqFl ? "Y" : "N";
		}
		return "";
	}

	public void setComponentMunicipal(String componentMunicipal) {
		this.componentMunicipal = componentMunicipal;
	}


	/*@Column(name="MUNI_USER_FULL_NM")
	@ExtendedExcelGridItem
	public String getComponentOverrideUserMunicipal() {
		return componentOverrideUserMunicipal;
	}

	public void setComponentOverrideUserMunicipal(
			String componentOverrideUserMunicipal) {
		this.componentOverrideUserMunicipal = componentOverrideUserMunicipal;
	}*/


	@Transient
	@ExtendedExcelGridItem
	public String getComponentAnc() {
		if (ancRqOvrrdFl != null){
			return (ancRqOvrrdFl ? "Y": "N");
		}else if(ancRqFl != null){
			return ancRqFl ? "Y" : "N";
		}
		return "";
	}

	public void setComponentAnc(String componentAnc) {
		this.componentAnc = componentAnc;
	}


	@Column(name="SP_SPRV_STAFF_ID")
	@JsonProperty("spSupervisorStaffId")
	public Integer getSpSupervisorStaffId() {
		return spSupervisorStaffId;
	}
	public void setSpSupervisorStaffId(Integer spSupervisorStaffId) {
		this.spSupervisorStaffId = spSupervisorStaffId;
	}

	@Column(name="FINOP_SPRV_STAFF_ID")
	@JsonProperty("fnSupervisorStaffId")
	public Integer getFnSupervisorStaffId() {
		return fnSupervisorStaffId;
	}
	public void setFnSupervisorStaffId(Integer fnSupervisorStaffId) {
		this.fnSupervisorStaffId = fnSupervisorStaffId;
	}

	@Column(name="RSA_FINOP_APLBL_ID")
	@JsonProperty("rsaFnId")
	public Integer getRsafnId() {
		return rsafnId;
	}
	public void setRsafnId(Integer rsafnId) {
		this.rsafnId = rsafnId;
	}

	@Column(name="RSA_FINOP_RQRD_FL")
	@JsonProperty("rsaFnReqrdFl")
	public Boolean getRsafnRqFl() {
		return rsafnRqFl;
	}
	public void setRsafnRqFl(Boolean rsafnRqFl) {
		this.rsafnRqFl = rsafnRqFl;
	}

	@Column(name="RSA_FINOP_OVRD_RQRD_FL")
	@JsonProperty("rsaFnReqrdOvrrdFl")
	public Boolean getRsafnRqOvrrdFl() {
		return rsafnRqOvrrdFl;
	}
	public void setRsafnRqOvrrdFl(Boolean rsafnRqOvrrdFl) {
		this.rsafnRqOvrrdFl = rsafnRqOvrrdFl;
	}

	@Column(name="RSAFN_FLD_WRK_START_ACTL_DT")
	@JsonProperty("rsaFnFwActualDt")
	public Date getRsafnFwActualDate() {
		return rsafnFwActualDate;
	}

	public void setRsafnFwActualDate(Date rsafnFwActualDate) {
		this.rsafnFwActualDate = rsafnFwActualDate;
	}

	@Column(name="RSAFN_FLD_WRK_START_PRJTD_DT")
	@JsonProperty("rsaFnFwPrjDt")
	public Date getRsafnFwPrjDate() {
		return rsafnFwPrjDate;
	}
	public void setRsafnFwPrjDate(Date rsafnFwPrjDate) {
		this.rsafnFwPrjDate = rsafnFwPrjDate;
	}

	@Column(name="RSA_FINOP_NMA_FL")
	@JsonProperty("rsaFnNmaFl")
	public Boolean getRsafnNmaFl() {
		return rsafnNmaFl;
	}
	public void setRsafnNmaFl(Boolean rsafnNmaFl) {
		this.rsafnNmaFl = rsafnNmaFl;
	}

	@Column(name="RSA_FINOP_RSA_NMA_FL")
	@JsonProperty("rsaFnRsaNmaFl")
	public Boolean getRsafnRsaNmaFl() {
		return rsafnRsaNmaFl;
	}
	public void setRsafnRsaNmaFl(Boolean rsafnRsaNmaFl) {
		this.rsafnRsaNmaFl = rsafnRsaNmaFl;
	}

	@Column(name="RSA_FINOP_OVRD_RSN_ID")
	@JsonProperty("rsaFnOvrrdReasonId")
	@ExtendedExcelGridItem
	public Long getRsafnOvrrdReasonId() {
		return rsafnOvrrdReasonId;
	}
	public void setRsafnOvrrdReasonId(Long rsafnOvrrdReasonId) {
		this.rsafnOvrrdReasonId = rsafnOvrrdReasonId;
	}

	@Column(name="RSA_FINOP_OVRD_RSN_DS")
	@ExtendedExcelGridItem
	public String getRsafnOvrrdReasonDs() {
		return rsafnOvrrdReasonDs;
	}
	public void setRsafnOvrrdReasonDs(String rsafnOvrrdReasonDs) {
		this.rsafnOvrrdReasonDs = rsafnOvrrdReasonDs;
	}

	@Column(name="RSA_FINOP_USER_FULL_NM")
	@JsonProperty("rsaFnOvrrdUserNm")
	@ExtendedExcelGridItem
	public String getRsafnOvrrdUserNm() {
		return rsafnOvrrdUserNm;
	}
	public void setRsafnOvrrdUserNm(String rsafnOvrrdUserNm) {
		this.rsafnOvrrdUserNm = rsafnOvrrdUserNm;
	}

	@Column(name="RSA_FINOP_BUS_RVW_TX")
	@JsonProperty("rsaFnBusinessReview")
	@ExtendedExcelGridItem
	public String getRsafnBusRvwTx() {return rsafnBusRvwTx;}
	public void setRsafnBusRvwTx(String rsafnBusRvwTx) {this.rsafnBusRvwTx = rsafnBusRvwTx;}

	@Column(name="RSA_FINOP_LAST_OVRD_UPDT_TS")
	@JsonProperty("rsaFnOvrrdDt")
	public Date getRsafnOvrrdDate() {
		return rsafnOvrrdDate;
	}

	public void setRsafnOvrrdDate(Date rsafnOvrrdDate) {
		this.rsafnOvrrdDate = rsafnOvrrdDate;
	}

	@Column(name="RSA_SP_APLBL_ID")
	@JsonProperty("rsaSpId")
	public Integer getRsaspId() {
		return rsaspId;
	}

	public void setRsaspId(Integer rsaspId) {
		this.rsaspId = rsaspId;
	}

	@Column(name="RSA_SP_RQRD_FL")
	@JsonProperty("rsaSpReqrdFl")
	public Boolean getRsaspRqFl() {
		return rsaspRqFl;
	}

	public void setRsaspRqFl(Boolean rsaspRqFl) {
		this.rsaspRqFl = rsaspRqFl;
	}

	@Column(name="RSA_SP_OVRD_RQRD_FL")
	@JsonProperty("rsaSpReqrdOvrrdFl")
	public Boolean getRsaspRqOvrrdFl() {
		return rsaspRqOvrrdFl;
	}

	public void setRsaspRqOvrrdFl(Boolean rsaspRqOvrrdFl) {
		this.rsaspRqOvrrdFl = rsaspRqOvrrdFl;
	}

	@Column(name="RSASP_FLD_WRK_START_ACTL_DT")
	@JsonProperty("rsaSpFwActualDt")
	public Date getRsaspFwActualDate() {
		return rsaspFwActualDate;
	}

	public void setRsaspFwActualDate(Date rsaspFwActualDate) {
		this.rsaspFwActualDate = rsaspFwActualDate;
	}

	@Column(name="RSASP_FLD_WRK_START_PRJTD_DT")
	@JsonProperty("rsaSpFwPrjDt")
	public Date getRsaspFwPrjDate() {
		return rsaspFwPrjDate;
	}

	public void setRsaspFwPrjDate(Date rsaspFwPrjDate) {
		this.rsaspFwPrjDate = rsaspFwPrjDate;
	}

	@Column(name="RSA_SP_NMA_FL")
	@JsonProperty("rsaSpNmaFl")
	public Boolean getRsaspNmaFl() {
		return rsaspNmaFl;
	}
	public void setRsaspNmaFl(Boolean rsaspNmaFl) {
		this.rsaspNmaFl = rsaspNmaFl;
	}

	@Column(name="RSA_SP_RSA_NMA_FL")
	@JsonProperty("rsaSpRsaNmaFl")
	public Boolean getRsaspRsaNmaFl() {
		return rsaspRsaNmaFl;
	}
	public void setRsaspRsaNmaFl(Boolean rsaspRsaNmaFl) {
		this.rsaspRsaNmaFl = rsaspRsaNmaFl;
	}

	@Column(name="RSA_SP_OVRD_RSN_ID")
	@JsonProperty("rsaSpOvrrdReasonId")
	@ExtendedExcelGridItem
	public Long getRsaspOvrrdReasonId() {
		return rsaspOvrrdReasonId;
	}
	public void setRsaspOvrrdReasonId(Long rsaspOvrrdReasonId) {
		this.rsaspOvrrdReasonId = rsaspOvrrdReasonId;
	}

	@Column(name="RSA_SP_OVRD_RSN_DS")
	@ExtendedExcelGridItem
	public String getRsaspOvrrdReasonDs() {
		return rsaspOvrrdReasonDs;
	}
	public void setRsaspOvrrdReasonDs(String rsaspOvrrdReasonDs) {
		this.rsaspOvrrdReasonDs = rsaspOvrrdReasonDs;
	}

	@Column(name="RSA_SP_USER_FULL_NM")
	@JsonProperty("rsaSpOvrrdUserNm")
	@ExtendedExcelGridItem
	public String getRsaspOvrrdUserNm() {
		return rsaspOvrrdUserNm;
	}
	public void setRsaspOvrrdUserNm(String rsaspOvrrdUserNm) {
		this.rsaspOvrrdUserNm = rsaspOvrrdUserNm;
	}

	@Column(name="RSA_SP_BUS_RVW_TX")
	@JsonProperty("rsaSpBusinessReview")
	@ExtendedExcelGridItem
	public String getRsaspBusRvwTx() {return rsaspBusRvwTx;}
	public void setRsaspBusRvwTx(String rsaspBusRvwTx) {this.rsaspBusRvwTx = rsaspBusRvwTx;}

	@Column(name="RSA_SP_LAST_OVRD_UPDT_TS")
	@JsonProperty("rsaSpOvrrdDt")
	public Date getRsaspOvrrdDate() {
		return rsaspOvrrdDate;
	}
	public void setRsaspOvrrdDate(Date rsaspOvrrdDate) {
		this.rsaspOvrrdDate = rsaspOvrrdDate;
	}

	@Column(name="MUNI_ADV_APLBL_ID")
	@JsonProperty("muniAdvId")
	public Integer getMaId() {
		return maId;
	}
	public void setMaId(Integer maId) {
		this.maId = maId;
	}

	@Column(name="MUNI_ADV_RQRD_FL")
	@JsonProperty("muniAdvReqrdFl")
	public Boolean getMaRqFl() {
		return maRqFl;
	}
	public void setMaRqFl(Boolean maRqFl) {
		this.maRqFl = maRqFl;
	}

	@Column(name="MUNI_ADV_OVRD_RQRD_FL")
	@JsonProperty("muniAdvReqrdOvrrdFl")
	public Boolean getMaRqOvrrdFl() {
		return maRqOvrrdFl;
	}
	public void setMaRqOvrrdFl(Boolean maRqOvrrdFl) {
		this.maRqOvrrdFl = maRqOvrrdFl;
	}

	@Column(name="MA_FLD_WRK_START_ACTL_DT")
	@JsonProperty("muniAdvFwActualDt")
	public Date getMaFwActualDate() {
		return maFwActualDate;
	}
	public void setMaFwActualDate(Date maFwActualDate) {
		this.maFwActualDate = maFwActualDate;
	}

	@Column(name="MA_FLD_WRK_START_PRJTD_DT")
	@JsonProperty("muniAdvFwPrjDt")
	public Date getMaFwPrjDate() {
		return maFwPrjDate;
	}
	public void setMaFwPrjDate(Date maFwPrjDate) {
		this.maFwPrjDate = maFwPrjDate;
	}

	@Column(name="MUNI_ADV_NMA_FL")
	@JsonProperty("muniAdvNmaFl")
	public Boolean getMaNmaFl() {
		return maNmaFl;
	}
	public void setMaNmaFl(Boolean maNmaFl) {
		this.maNmaFl = maNmaFl;
	}

	@Column(name="MUNI_ADV_RSA_NMA_FL")
	@JsonProperty("muniAdvRsaNmaFl")
	public Boolean getMaRsaNmaFl() {
		return maRsaNmaFl;
	}
	public void setMaRsaNmaFl(Boolean maRsaNmaFl) {
		this.maRsaNmaFl = maRsaNmaFl;
	}

	@Column(name="MUNI_ADV_OVRD_RSN_ID")
	@JsonProperty("muniAdvOvrrdReasonId")
	@ExtendedExcelGridItem
	public Long getMaOvrrdReasonId() {
		return maOvrrdReasonId;
	}
	public void setMaOvrrdReasonId(Long maOvrrdReasonId) {
		this.maOvrrdReasonId = maOvrrdReasonId;
	}

	@Column(name="MUNI_ADV_OVRD_RSN_DS")
	@ExtendedExcelGridItem
	public String getMaOvrrdReasonDs() {
		return maOvrrdReasonDs;
	}
	public void setMaOvrrdReasonDs(String maOvrrdReasonDs) {
		this.maOvrrdReasonDs = maOvrrdReasonDs;
	}

	@Column(name="MUNI_ADV_USER_FULL_NM")
	@JsonProperty("muniAdvOvrrdUserNm")
	@ExtendedExcelGridItem
	public String getMaOvrrdUserNm() {
		return maOvrrdUserNm;
	}
	public void setMaOvrrdUserNm(String maOvrrdUserNm) {
		this.maOvrrdUserNm = maOvrrdUserNm;
	}

	@Column(name="MUNI_ADV_BUS_RVW_TX")
	@JsonProperty("muniAdvBusinessReview")
	@ExtendedExcelGridItem
	public String getMaBusRvwTx() {return maBusRvwTx;}
	public void setMaBusRvwTx(String maBusRvwTx) {this.maBusRvwTx = maBusRvwTx;}

	@Column(name="MUNI_ADV_LAST_OVRD_UPDT_TS")
	@JsonProperty("muniAdvOvrrdDt")
	public Date getMaOvrrdDate() {
		return maOvrrdDate;
	}
	public void setMaOvrrdDate(Date maOvrrdDate) {
		this.maOvrrdDate = maOvrrdDate;
	}

	@Column(name="SDF_APLBL_ID")
	@JsonProperty("sdfId")
	public Integer getSdfId() {
		return sdfId;
	}
	public void setSdfId(Integer sdfId) {
		this.sdfId = sdfId;
	}

	@Column(name="SDF_RQRD_FL")
	@JsonProperty("sdfReqrdFl")
	public Boolean getSdfRqFl() {
		return sdfRqFl;
	}
	public void setSdfRqFl(Boolean sdfRqFl) {
		this.sdfRqFl = sdfRqFl;
	}

	@Column(name="SDF_OVRD_RQRD_FL")
	@JsonProperty("sdfReqrdOvrrdFl")
	public Boolean getSdfRqOvrrdFl() {
		return sdfRqOvrrdFl;
	}
	public void setSdfRqOvrrdFl(Boolean sdfRqOvrrdFl) {
		this.sdfRqOvrrdFl = sdfRqOvrrdFl;
	}

	@Column(name="SDF_FLD_WRK_START_ACTL_DT")
	@JsonProperty("sdfFwActualDt")
	public Date getSdfFwActualDate() {
		return sdfFwActualDate;
	}
	public void setSdfFwActualDate(Date sdfFwActualDate) {
		this.sdfFwActualDate = sdfFwActualDate;
	}

	@Column(name="SDF_FLD_WRK_START_PRJTD_DT")
	@JsonProperty("sdfFwPrjDt")
	public Date getSdfFwPrjDate() {
		return sdfFwPrjDate;
	}
	public void setSdfFwPrjDate(Date sdfFwPrjDate) {
		this.sdfFwPrjDate = sdfFwPrjDate;
	}

	@Column(name="SDF_NMA_FL")
	@JsonProperty("sdfNmaFl")
	public Boolean getSdfNmaFl() {
		return sdfNmaFl;
	}
	public void setSdfNmaFl(Boolean sdfNmaFl) {
		this.sdfNmaFl = sdfNmaFl;
	}

	@Column(name="SDF_RSA_NMA_FL")
	@JsonProperty("sdfRsaNmaFl")
	public Boolean getSdfRsaNmaFl() {
		return sdfRsaNmaFl;
	}
	public void setSdfRsaNmaFl(Boolean sdfRsaNmaFl) {
		this.sdfRsaNmaFl = sdfRsaNmaFl;
	}

	@Column(name="SDF_OVRD_RSN_ID")
	@JsonProperty("sdfOvrrdReasonId")
	@ExtendedExcelGridItem
	public Long getSdfOvrrdReasonId() {
		return sdfOvrrdReasonId;
	}
	public void setSdfOvrrdReasonId(Long sdfOvrrdReasonId) {
		this.sdfOvrrdReasonId = sdfOvrrdReasonId;
	}

	@Column(name="SDF_OVRD_RSN_DS")
	@ExtendedExcelGridItem
	public String getSdfOvrrdReasonDs() {
		return sdfOvrrdReasonDs;
	}
	public void setSdfOvrrdReasonDs(String sdfOvrrdReasonDs) {
		this.sdfOvrrdReasonDs = sdfOvrrdReasonDs;
	}

	@Column(name="SDF_USER_FULL_NM")
	@JsonProperty("sdfOvrrdUserNm")
	@ExtendedExcelGridItem
	public String getSdfOvrrdUserNm() {
		return sdfOvrrdUserNm;
	}
	public void setSdfOvrrdUserNm(String sdfOvrrdUserNm) {
		this.sdfOvrrdUserNm = sdfOvrrdUserNm;
	}

	@Column(name="SDF_LAST_OVRD_UPDT_TS")
	@JsonProperty("sdfOvrrdDt")
	public Date getSdfOvrrdDate() {
		return sdfOvrrdDate;
	}
	public void setSdfOvrrdDate(Date sdfOvrrdDate) {
		this.sdfOvrrdDate = sdfOvrrdDate;
	}

	@Column(name="SDF_BUS_RVW_TX")
	@JsonProperty("sdfBusinessReview")
	@ExtendedExcelGridItem
	public String getSdfBusRvwTx() {return sdfBusRvwTx;}
	public void setSdfBusRvwTx(String sdfBusRvwTx) {this.sdfBusRvwTx = sdfBusRvwTx;}

	@Column(name="FLR_DSTRT_CD")
	@JsonProperty("flDistrCd")
	public String getFlDistrictCd() {
		return flDistrictCd;
	}
	public void setFlDistrictCd(String flDistrictCd) {
		this.flDistrictCd = flDistrictCd;
	}

	@Column(name="FLR_DSTRT_TYPE_CD")
	@JsonProperty("flDistrTypeCd")
	public String getFlRespDistrictTypeCd() {
		return flRespDistrictTypeCd;
	}
	public void setFlRespDistrictTypeCd(String flRespDistrictTypeCd) {
		this.flRespDistrictTypeCd = flRespDistrictTypeCd;
	}

	@Column(name="FLR_DSTRT_DS")
	@JsonProperty("flDistrDs")
	public String getFlDistrictDesc() {
		return flDistrictDesc;
	}
	public void setFlDistrictDesc(String flDistrictDesc) {
		this.flDistrictDesc = flDistrictDesc;
	}

	@Transient
	@ExtendedExcelGridItem
	public String getComponentRsaFn() {
		if (rsafnRqOvrrdFl != null){
			return (rsafnRqOvrrdFl ? "Y": "N");
		}else if(rsafnRqFl != null){
			return rsafnRqFl ? "Y" : "N";
		}
		return "";
	}

	public void setComponentRsaFn(String componentRsaFn) {
		this.componentRsaFn = componentRsaFn;
	}

	@Transient
	@ExtendedExcelGridItem
	public String getComponentRsaSp() {
		if (rsaspRqOvrrdFl != null){
			return (rsaspRqOvrrdFl ? "Y": "N");
		}else if(rsaspRqFl != null){
			return rsaspRqFl ? "Y" : "N";
		}
		return "";
	}

	public void setComponentRsaSp(String componentRsaSp) {
		this.componentRsaSp = componentRsaSp;
	}

	@Transient
	@ExtendedExcelGridItem
	public String getComponentSdf() {
		if (sdfRqOvrrdFl != null){
			return (sdfRqOvrrdFl ? "Y": "N");
		}else if(sdfRqFl != null){
			return sdfRqFl ? "Y" : "N";
		}
		return "";
	}

	public void setComponentSdf(String componentSdf) {
		this.componentSdf = componentSdf;
	}

	@Transient
	@ExtendedExcelGridItem
	public String getComponentMa() {
		if (maRqOvrrdFl != null){
			return (maRqOvrrdFl ? "Y": "N");
		}else if(maRqFl != null){
			return maRqFl ? "Y" : "N";
		}
		return "";
	}

	public void setComponentMa(String componentMa) {
		this.componentMa = componentMa;
	}

/*	@Column(name="SP_BUS_RVW_TX")
	@JsonProperty("spBusinessReview")
	public String getBusinessReviewSp() {
		return businessReviewSp;
	}

	public void setBusinessReviewSp(String businessReviewSp) {
		this.businessReviewSp = businessReviewSp;
	}

	@Column(name="FINOP_BUS_RVW_TX")
	@JsonProperty("fnBusinessReview")
	public String getBusinessReviewFinop() {
		return businessReviewFinop;
	}

	public void setBusinessReviewFinop(String businessReviewFinop) {
		this.businessReviewFinop = businessReviewFinop;
	}

	@Column(name="FRST_FINOP_BUS_RVW_TX")
	@JsonProperty("ffnBusinessReview")
	public String getBusinessReviewFirstFinop() {
		return businessReviewFirstFinop;
	}

	public void setBusinessReviewFirstFinop(String businessReviewFirstFinop) {
		this.businessReviewFirstFinop = businessReviewFirstFinop;
	}

	@Column(name="FLOOR_REVIEW_BUS_RVW_TX")
	@JsonProperty("flBusinessReview")
	public String getBusinessReviewFloor() {
		return businessReviewFloor;
	}

	public void setBusinessReviewFloor(String businessReviewFloor) {
		this.businessReviewFloor = businessReviewFloor;
	}

	@Column(name="OPTN_BUS_RVW_TX")
	@JsonProperty("opBusinessReview")
	public String getBusinessReviewOption() {
		return businessReviewOption;
	}

	public void setBusinessReviewOption(String businessReviewOption) {
		this.businessReviewOption = businessReviewOption;
	}

	@Column(name="MUNI_BUS_RVW_TX")
	@JsonProperty("muniBusinessReview")
	public String getBusinessReviewMunicipal() {
		return businessReviewMunicipal;
	}

	public void setBusinessReviewMunicipal(String businessReviewMunicipal) {
		this.businessReviewMunicipal = businessReviewMunicipal;
	}

	@Column(name="ANC_BUS_RVW_TX")
	@JsonProperty("ancBusinessReview")
	public String getBusinessReviewAnc() {
		return businessReviewAnc;
	}
	public void setBusinessReviewAnc(String businessReviewAnc) {
		this.businessReviewAnc = businessReviewAnc;
	}

	@Column(name="RSA_FINOP_BUS_RVW_TX")
	@JsonProperty("rsafnBusinessReview")
	public String getBusinessReviewRsaFn() {
		return businessReviewRsaFn;
	}

	public void setBusinessReviewRsaFn(String businessReviewRsaFn) {
		this.businessReviewRsaFn = businessReviewRsaFn;
	}

	@Column(name="RSA_SP_BUS_RVW_TX")
	@JsonProperty("rsaspBusinessReview")
	public String getBusinessReviewRsaSp() {
		return businessReviewRsaSp;
	}

	public void setBusinessReviewRsaSp(String businessReviewRsaSp) {
		this.businessReviewRsaSp = businessReviewRsaSp;
	}

	@Column(name="SDF_BUS_RVW_TX")
	@JsonProperty("sdfBusinessReview")
	public String getBusinessReviewSdf() {
		return businessReviewSdf;
	}

	public void setBusinessReviewSdf(String businessReviewSdf) {
		this.businessReviewSdf = businessReviewSdf;
	}

	@Column(name="MUNI_ADV_BUS_RVW_TX")
	@JsonProperty("muniAdvBusinessReview")
	public String getBusinessReviewMa() {
		return businessReviewMa;
	}

	public void setBusinessReviewMa(String businessReviewMa) {
		this.businessReviewMa = businessReviewMa;
	}*/

	@Column(name="SP_FREQ_NB")
	@JsonProperty("spFrequency")
	public Integer getSpFrequency() {
		return spFrequency;
	}

	public void setSpFrequency(Integer spFrequency) {
		this.spFrequency = spFrequency;
	}

	@Column(name="SP_IMPCT_DS")
	@JsonProperty("spImpact")
	@ExtendedExcelGridItem
	public String getSpImpact() {
		return spImpact;
	}
	public void setSpImpact(String spImpact) {
		this.spImpact = spImpact;
	}

	@Column(name="SP_LKLHD_DS")
	@JsonProperty("spLikelihood")
	@ExtendedExcelGridItem
	public String getSpLikelihood() {
		return spLikelihood;
	}
	public void setSpLikelihood(String spLikelihood) {
		this.spLikelihood = spLikelihood;
	}

	@Column(name="SP_CMPST_DS")
	@JsonProperty("spComposite")
	@ExtendedExcelGridItem
	public String getSpComposite() {
		return spComposite;
	}
	public void setSpComposite(String spComposite) {
		this.spComposite = spComposite;
	}

	@Column(name="FINOP_FREQ_NB")
	@JsonProperty("fnFrequency")
	public Integer getFinopFrequency() {
		return finopFrequency;
	}
	public void setFinopFrequency(Integer finopFrequency) {
		this.finopFrequency = finopFrequency;
	}

	@Column(name="FINOP_IMPCT_DS")
	@JsonProperty("fnImpact")
	@ExtendedExcelGridItem
	public String getFinopImpact() {
		return finopImpact;
	}
	public void setFinopImpact(String finopImpact) {
		this.finopImpact = finopImpact;
	}

	@Column(name="FINOP_LKLHD_DS")
	@JsonProperty("fnLikelihood")
	@ExtendedExcelGridItem
	public String getFinopLikelihood() {
		return finopLikelihood;
	}
	public void setFinopLikelihood(String finopLikelihood) {
		this.finopLikelihood = finopLikelihood;
	}

	@Column(name="FINOP_CMPST_DS")
	@JsonProperty("fnComposite")
	@ExtendedExcelGridItem
	public String getFinopComposite() {
		return finopComposite;
	}

	public void setFinopComposite(String finopComposite) {
		this.finopComposite = finopComposite;
	}

	/*	@Column(name="ANC_USER_FULL_NM")
	@ExtendedExcelGridItem
	public String getComponentOverrideUserAnc() {
		return componentOverrideUserAnc;
	}

	public void setComponentOverrideUserAnc(String componentOverrideUserAnc) {
		this.componentOverrideUserAnc = componentOverrideUserAnc;
	}
*/
	@Override
	public int hashCode() {
		return (int) 31 * (sssnId+firmId + versionId + sssnStatusCd.hashCode());
	}
	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		if (obj instanceof PsSessionView) {
			PsSessionView o = (PsSessionView) obj;
			return sssnId == o.sssnId && firmId==o.firmId && versionId == o.versionId && sssnStatusCd.equals(o.sssnStatusCd);
		} else {
			return false;
		}
	}
}
