package org.finra.esched.domain.ui;

import org.finra.esched.domain.PsOvrdReason;
import org.finra.esched.domain.PsSessionStatus;
import org.finra.exam.common.domain.ui.ReturnCodeJson;

import java.util.List;


public class PsLookUpResponse extends ReturnCodeJson{
	
	public PsLookUpResponse() {
	
	}

	private List<PsOvrdReason> cmpntOvrdRqReasons;

	private List<PsOvrdReason> cmpntOvrdNonRqReasons;

	private List<PsDistrictView> cmpntDistricts;

	private List<PsSessionStatus> statuses;

	private int psSnapshotId;

	private String psUserDistrictCd;

	private Boolean isSchedulingAccess;

	private String[] impactsFlags;

	private String[] riskFlags;

	private List<PsExamCategoryType> examcategorytypes;

	private List<PsExamTypeType> examTypeTypes;

	private List<PsExamSubTypeType> examSubTypeTypes;

	private List<PsRegulatorySignificance> regulatorySignifiance;

	public List<PsOvrdReason> getCmpntOvrdRqReasons() {
		return cmpntOvrdRqReasons;
	}

	public void setCmpntOvrdRqReasons(List<PsOvrdReason> cmpntOvrdRqReasons) {
		this.cmpntOvrdRqReasons = cmpntOvrdRqReasons;
	}

	public List<PsOvrdReason> getCmpntOvrdNonRqReasons() {
		return cmpntOvrdNonRqReasons;
	}

	public void setCmpntOvrdNonRqReasons(List<PsOvrdReason> cmpntOvrdNonRqReasons) {
		this.cmpntOvrdNonRqReasons = cmpntOvrdNonRqReasons;
	}

	public List<PsDistrictView> getCmpntDistricts() {
		return cmpntDistricts;
	}

	public void setCmpntDistricts(List<PsDistrictView> cmpntDistricts) {
		this.cmpntDistricts = cmpntDistricts;
	}

	public List<PsSessionStatus> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<PsSessionStatus> statuses) {
		this.statuses = statuses;
	}

	public int getPsSnapshotId() {
		return psSnapshotId;
	}

	public void setPsSnapshotId(int psSnapshotId) {
		this.psSnapshotId = psSnapshotId;
	}

	public String getPsUserDistrictCd() {
		return psUserDistrictCd;
	}

	public void setPsUserDistrictCd(String psUserDistrictCd) {
		this.psUserDistrictCd = psUserDistrictCd;
	}

	public Boolean getSchedulingAccess() {
		return isSchedulingAccess;
	}

	public void setSchedulingAccess(Boolean schedulingAccess) {
		isSchedulingAccess = schedulingAccess;
	}

	public String[] getImpactsFlags() {
		return impactsFlags;
	}

	public void setImpactsFlags(String[] impactsFlags) {
		this.impactsFlags = impactsFlags;
	}

	public String[] getRiskFlags() {
		return riskFlags;
	}

	public void setRiskFlags(String[] riskFlags) {
		this.riskFlags = riskFlags;
	}

	public List<PsExamCategoryType> getExamcategorytypes() {
		return examcategorytypes;
	}

	public void setExamcategorytypes(List<PsExamCategoryType> examcategorytypes) {
		this.examcategorytypes = examcategorytypes;
	}

	public List<PsExamTypeType> getExamTypeTypes() {
		return examTypeTypes;
	}

	public void setExamTypeTypes(List<PsExamTypeType> examTypeTypes) {
		this.examTypeTypes = examTypeTypes;
	}

	public List<PsExamSubTypeType> getExamSubTypeTypes() {
		return examSubTypeTypes;
	}

	public void setExamSubTypeTypes(List<PsExamSubTypeType> examSubTypeTypes) {
		this.examSubTypeTypes = examSubTypeTypes;
	}

	public List<PsRegulatorySignificance> getRegulatorySignifiance() {
		return regulatorySignifiance;
	}

	public void setRegulatorySignifiance(List<PsRegulatorySignificance> regulatorySignifiance) {
		this.regulatorySignifiance = regulatorySignifiance;
	}
}