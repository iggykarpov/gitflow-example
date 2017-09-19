package org.finra.esched.domain;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)*/
@XmlRootElement(name = "psFirm")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Table(name="schdl_firm")
public class PsFirm implements java.io.Serializable {
	
	public enum NMA_TYPE {
		NMA, RSANMA
	};

	private final Logger log = LoggerFactory.getLogger(PsFirm.class);
	
	// Constructors

	/** default constructor */
	public PsFirm() {
	}
	
	
	private PsFirmPK id;
	
	private String 	firmName;
	private String 	fStatusDesc;
	private Boolean rsaFlag;
	
	private Boolean acceleratedFlag;
	private Boolean msrbMmbrFlag;
	private Date msrbMmbrEffDt;
	
	private List<PsLastExam> lastExams;
	
	private List<PsBa> busActivities;
	private List<PsSro> sros;
	private Date membershipEffDt;
	
	private String spImpctCode;
	private String spRiskCode;
	private String spDistrictCode;
	private String spDistrictTypeCode;
	private String spDistrictDesc;
	//private String spSupervisorNm;
	
	private String fnImpctCode;
	private String fnRiskCode;
	private String fnDistrictCode;
	private String fnDistrictTypeCode;
	private String fnDistrictDesc;
	//private String fnSupervisorNm;
	
	private Date npobDt;
	private int doeaId;
	private String doeaDs;
	private Integer mainBranchId;

	private Integer spFrequency;
	private Integer finopFrequency;
	
	private String auditMonth;
	
	private List<PsSession> sessions;
	
	// 'true' indicates that Firm should be processed via NMA/RSANMA process
	private Boolean nmaFl;
	private Boolean rsaNmaFl;
	private Boolean muniAdvFlag;
	
	@EmbeddedId
	@XmlElement(name = "id", required = true)
	public PsFirmPK getId() {
		return id;
	}
	public void setId(PsFirmPK id) {
		this.id = id;
	}
	
	@Column(name="FIRM_NM")
	@XmlElement(name = "firmName")
	public String getFirmName() {
		return firmName;
	}
	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}
	
	@Column(name="FINRA_STTS_NM")
	@XmlElement(name = "finraStatusDesc")	
	public String getFinraStatusDesc() {
		return fStatusDesc;
	}
	public void setFinraStatusDesc(String finraStatusDesc) {
		this.fStatusDesc = finraStatusDesc;
	}
	
	@Type(type = "yes_no")
	@Column(name="RSA_FL")
	@XmlElement(name = "rsaFlag")	
	public Boolean getRsaFlag() {
		return rsaFlag;
	}
	public void setRsaFlag(Boolean rsaFlag) {
		this.rsaFlag = rsaFlag;
	}
	
	@Type(type = "yes_no")
	@Column(name="ACCEL_ANNL_MNCPL_FL")
	@XmlElement(name = "acceleratedFlag")	
	public Boolean getAcceleratedFlag() {
		return acceleratedFlag;
	}
	public void setAcceleratedFlag(Boolean acceleratedFlag) {
		this.acceleratedFlag = acceleratedFlag;
	}
	
	@Type(type = "yes_no")
	@Column(name="MSRB_MBR_FL")
	@XmlElement(name = "msrbMmbrFlag")	
	public Boolean getMsrbMmbrFlag() {
		return msrbMmbrFlag;
	}
	public void setMsrbMmbrFlag(Boolean msrbMmbrFlag) {
		this.msrbMmbrFlag = msrbMmbrFlag;
	}
	
	
	
	@Column(name="MSRB_EFCTV_DT")
	@XmlElement(name = "msrbEffDt")	
	public Date getMsrbMmbrEffDt() {
		return msrbMmbrEffDt;
	}
	public void setMsrbMmbrEffDt(Date msrbMmbrEffDt) {
		this.msrbMmbrEffDt = msrbMmbrEffDt;
	}
	
	@Column(name="MBRSP_EFCTV_DT")
	@XmlElement(name = "membershipEffDt")	
	public Date getMembershipEffDt() {
		return membershipEffDt;
	}
	public void setMembershipEffDt(Date membershipEffDt) {
		this.membershipEffDt = membershipEffDt;
	}
	
	
	@OneToMany(mappedBy="firm")
	public List<PsLastExam> getLastExams() {
		return lastExams;
	}
	public void setLastExams(List<PsLastExam> lastExams) {
		this.lastExams = lastExams;
	}
	
	@OneToMany(mappedBy="firm")
	public List<PsBa> getBusActivities() {
		return busActivities;
	}
	public void setBusActivities(List<PsBa> busActivities) {
		this.busActivities = busActivities;
	}
	
	
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="firm")
	public List<PsSro> getSros() {
		return sros;
	}
	public void setSros(List<PsSro> sros) {
		this.sros = sros;
	}
	
	
	@Column(name="SP_IMPCT_CD")
	@XmlElement(name = "spImpactCd")	
	public String getSpImpctCode() {
		return spImpctCode;
	}
	public void setSpImpctCode(String spImpctCode) {
		this.spImpctCode = spImpctCode;
	}
	
	
	@Transient
	public int getSpImpact() {
		try{
			return new Integer(getSpImpctCode()).intValue(); 
		}catch(Exception ex){
			return 0;
		}
	}
	
	@Column(name="SP_RISK_CD")
	@XmlElement(name = "spRiskCd")	
	public String getSpRiskCode() {
		return spRiskCode;
	}
	public void setSpRiskCode(String spRiskCode) {
		this.spRiskCode = spRiskCode;
	}
	
	
	
	@Column(name="FINOP_IMPCT_CD")
	@XmlElement(name = "fnImpactCd")	
	public String getFnImpctCode() {
		return fnImpctCode;
	}
	public void setFnImpctCode(String fnImpctCode) {
		this.fnImpctCode = fnImpctCode;
	}
	@Transient
	public int getFnImpact() {
		try{
			return new Integer(getFnImpctCode()).intValue(); 
		}catch(Exception ex){
			return 0;
		}
	}
	
	
	@Column(name="FINOP_RISK_CD")
	@XmlElement(name = "fnRiskCd")	
	public String getFnRiskCode() {
		return fnRiskCode;
	}
	public void setFnRiskCode(String fnRiskCode) {
		this.fnRiskCode = fnRiskCode;
	}
	
	
	@Transient
	public boolean hasBaType(int type){
		if(getBusActivities()==null) return false;
		
		Iterator<PsBa> baIt=this.busActivities.iterator();
		while(baIt.hasNext()){
			PsBa ba=baIt.next();
			if(ba.getActivityId()==type) return true;
		}
		
		return false;
	}
	
	@Transient
	public boolean hasSroType(PsSro.SRO_TYPE type){
		if(getSros()==null) return false;
		
		Iterator<PsSro> sroIt=this.sros.iterator();
		while(sroIt.hasNext()){
			PsSro sro=sroIt.next();
			if(sro.getType()==type) return true;
		}
		
		return false;
	}
	
	
	@Column(name="NPOB_VRFCN_DT")
	@XmlElement(name = "npobDt")	
	public Date getNpobDt() {
		return npobDt;
	}
	public void setNpobDt(Date npobDt) {
		this.npobDt = npobDt;
	}
	
	@Column(name="DOEA_ID")
	@XmlElement(name = "doeaId")	
	public int getDoeaId() {
		return doeaId;
	}
	public void setDoeaId(int doeaId) {
		this.doeaId = doeaId;
	}
	
	@Column(name="DOEA_DS")
	@XmlElement(name = "doeaDs")	
	public String getDoeaDs() {
		return doeaDs;
	}
	public void setDoeaDs(String doea) {
		this.doeaDs = doea;
	}
	
	@Column(name="SP_DSTRT_CD")
	@XmlElement(name = "spDistrCode")
	public String getSpDistrictCode() {
		return spDistrictCode;
	}
	public void setSpDistrictCode(String spDistrictCode) {
		this.spDistrictCode = spDistrictCode;
	}
	
	@Column(name="SP_DSTRT_DS")
	@XmlElement(name = "spDistrDesc")
	public String getSpDistrictDesc() {
		return spDistrictDesc;
	}
	public void setSpDistrictDesc(String spDistrictDesc) {
		this.spDistrictDesc = spDistrictDesc;
	}

	@Column(name="FINOP_DSTRT_DS")
	@XmlElement(name = "fnDistrDesc")
	public String getFnDistrictDesc() {
		return fnDistrictDesc;
	}
	public void setFnDistrictDesc(String fnDistrictDesc) {
		this.fnDistrictDesc = fnDistrictDesc;
	}
	
	
	@Column(name="FINOP_DSTRT_CD")
	@XmlElement(name = "fnDistrCode")
	public String getFnDistrictCode() {
		return fnDistrictCode;
	}
	public void setFnDistrictCode(String fnDistrictCode) {
		this.fnDistrictCode = fnDistrictCode;
	}
	
	
	/*@Column(name="SP_SPRV_STAFF_NM")
	@XmlElement(name = "fnDistrCode")
	public String getSpSupervisorNm() {
		return spSupervisorNm;
	}
	public void setSpSupervisorNm(String spSupervisorNm) {
		this.spSupervisorNm = spSupervisorNm;
	}
	
	@Column(name="FINOP_SPRV_STAFF_NM")
	@XmlElement(name = "fnDistrCode")
	public String getFnSupervisorNm() {
		return fnSupervisorNm;
	}
	public void setFnSupervisorNm(String fnSupervisorNm) {
		this.fnSupervisorNm = fnSupervisorNm;
	}*/
	
	@Column(name="AUDIT_MONTH_TX")
	@XmlElement(name = "auditMonth")
	public String getAuditMonth() {
		return auditMonth;
	}
	public void setAuditMonth(String auditMonth) {
		this.auditMonth = auditMonth;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="firm")
	public List<PsSession> getSessions() {
		return sessions;
	}
	public void setSessions(List<PsSession> sessions) {
		this.sessions = sessions;
	}
	
	
	@Column(name="NMA_FL")
	//@Convert(converter=BooleanTFConverter.class)
	@XmlElement(name = "isNmaFl")	
	public Boolean isNmaFl() {
		return nmaFl;
	}
	public void setNmaFl(Boolean nmaFl) {
		this.nmaFl = nmaFl;
	}

	@Column(name="RSA_NMA_FL")
	//@Convert(converter=BooleanTFConverter.class)
	@XmlElement(name = "isRsaNmaFl")	
	public Boolean isRsaNmaFl() {
		return rsaNmaFl;
	}
	public void setRsaNmaFl(Boolean rsaNmaFl) {
		this.rsaNmaFl = rsaNmaFl;
	}
	
	
/*	@Converter
	public static class BooleanTFConverter implements AttributeConverter<Boolean, String>{
	    @Override
	    public String convertToDatabaseColumn(Boolean value) {
	        if (Boolean.TRUE.equals(value)) {
	            return "1";
	        } else {
	            return "0";
	        }
	    }
	    @Override
	    public Boolean convertToEntityAttribute(String value) {
	        return "1".equals(value);
	    }
	}
*/	
	
	@Transient
	public boolean isNma(NMA_TYPE type){
		if((nmaFl==null || (nmaFl!=null && !nmaFl.booleanValue())) && (rsaNmaFl==null || (rsaNmaFl!=null && !rsaNmaFl.booleanValue()))) return false;
		
		// If type is not null, check that we has requested type
		if(type!=null){
			if(type==NMA_TYPE.NMA && nmaFl!=null && nmaFl.booleanValue()==true){
				return true;
			}else if(type==NMA_TYPE.RSANMA && rsaNmaFl!=null && rsaNmaFl.booleanValue()==true){
				return true;
			}
			return false;
			
		// otherwise, check that we have at least one that we care about
		}else{	
			return ((nmaFl!=null && nmaFl==true) || (rsaNmaFl!=null && rsaNmaFl==true));
		}
	}
	
	@Column(name="sp_dstrt_type_cd")
	@XmlElement(name = "spDistrCode")
	public String getSpDistrictTypeCode() {
		return spDistrictTypeCode;
	}
	public void setSpDistrictTypeCode(String spDistrictTypeCode) {
		this.spDistrictTypeCode = spDistrictTypeCode;
	}
	
	@Column(name="finop_dstrt_type_cd")
	@XmlElement(name = "fnDistrCode")
	public String getFnDistrictTypeCode() {
		return fnDistrictTypeCode;
	}
	public void setFnDistrictTypeCode(String fnDistrictTypeCode) {
		this.fnDistrictTypeCode = fnDistrictTypeCode;
	}
	
	@Column(name="main_brnch_id")
	@XmlElement(name = "mainBranchId")
	public Integer getMainBranchId() {
		return mainBranchId;
	}
	public void setMainBranchId(Integer mainBranchId) {
		this.mainBranchId = mainBranchId;
	}
	@Column(name="SP_FREQ_NB")
	public Integer getSpFrequency() {
		return spFrequency;
	}
	public void setSpFrequency(Integer spFrequency) {
		this.spFrequency = spFrequency;
	}
	@Column(name="FINOP_FREQ_NB")
	public Integer getFinopFrequency() {
		return finopFrequency;
	}
	public void setFinopFrequency(Integer finopFrequency) {
		this.finopFrequency = finopFrequency;
	}
	@Type(type = "yes_no")
	@Column(name="MA_FL")
	@XmlElement(name = "muniAdvFlag")
	public Boolean getMuniAdvFlag() {
		return muniAdvFlag;
	}
	public void setMuniAdvFlag(Boolean muniAdvFlag) {
		this.muniAdvFlag = muniAdvFlag;
	}
	@Transient
	public PsSession getCurrentSession(){
		List<PsSession> sList=this.getSessions();
		if(sList==null || (sList!=null && sList.size()==0)) return null;
		
		Collections.sort(sList);
		// Return obj with highest id... i.e. most current
		return sList.get(sList.size()-1);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsFirm) {
			PsFirm o = (PsFirm)obj;
            return id.getFirmId() == o.id.getFirmId() && id.getVersionId()==o.id.getVersionId();
        } else {
            return false;
        }
	}
	
	
	@Override
	public int hashCode() {
		return (int) 31 *(id.getFirmId() + id.getVersionId());
	}
}
