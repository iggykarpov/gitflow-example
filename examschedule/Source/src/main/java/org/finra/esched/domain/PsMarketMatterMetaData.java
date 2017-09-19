package org.finra.esched.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Date;
import java.util.Objects;

@XmlRootElement(name = "marketMatterMetaData")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="SCHDL_MKT_MTTR_META_DATA")
public class PsMarketMatterMetaData {

	@Id
	@GeneratedValue
	@Column(name="MKT_TYPE_CD")	
	private String marketTypeCode;
	@Column(name="MNGR_USER_ID")	
	private Integer managerUserId;
	@Column(name="DSTRT_ID")
	private Integer districtId;
	@Column(name="DSTRT_CD")
	private String districtCode;
	@Column(name="EXAM_TYPE_CD")
	private String examTypeCode;
	@Column(name="EXAM_SUB_TYPE_CD")
	private String examSubTypeCode;
	@Column(name="MTTR_TYPE_ID")
	private Integer matterTypeId;
	@Column(name="MTTR_SUB_TYPE_ID")
	private Integer matterSubTypeId;
	@Column(name="PRDCT_ID")
	private Integer productId;
	@Column(name="MTTR_DESC_TX")
	private String matterDescription;
	@Column(name="RGLTY_SGNFC_ID")
	private Integer regulatorSignificanceId;
	@Column(name="MTTR_CMMNT_STAFF_ID")
	private Integer matterCommentStaffId;
	
	public PsMarketMatterMetaData() {

	}



	public String getMarketTypeCode() {
		return marketTypeCode;
	}



	public void setMarketTypeCode(String marketTypeCode) {
		this.marketTypeCode = marketTypeCode;
	}



	public Integer getManagerUserId() {
		return managerUserId;
	}



	public void setManagerUserId(Integer managerUserId) {
		this.managerUserId = managerUserId;
	}



	public Integer getMatterTypeId() {
		return matterTypeId;
	}



	public void setMatterTypeId(Integer matterTypeId) {
		this.matterTypeId = matterTypeId;
	}



	public String getExamTypeCode() {
		return examTypeCode;
	}



	public void setExamTypeCode(String examTypeCode) {
		this.examTypeCode = examTypeCode;
	}


	public Integer getDistrictId() {
		return districtId;
	}



	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}



	public String getDistrictCode() {
		return districtCode;
	}



	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}



	public String getExamSubTypeCode() {
		return examSubTypeCode;
	}



	public void setExamSubTypeCode(String examSubTypeCode) {
		this.examSubTypeCode = examSubTypeCode;
	}



	public Integer getMatterSubTypeId() {
		return matterSubTypeId;
	}



	public void setMatterSubTypeId(Integer matterSubTypeId) {
		this.matterSubTypeId = matterSubTypeId;
	}



	public Integer getProductId() {
		return productId;
	}



	public void setProductId(Integer productId) {
		this.productId = productId;
	}



	public String getMatterDescription() {
		return matterDescription;
	}



	public void setMatterDescription(String matterDescription) {
		this.matterDescription = matterDescription;
	}



	public Integer getRegulatorSignificanceId() {
		return regulatorSignificanceId;
	}



	public void setRegulatorSignificanceId(Integer regulatorSignificanceId) {
		this.regulatorSignificanceId = regulatorSignificanceId;
	}



	public Integer getMatterCommentStaffId() {
		return matterCommentStaffId;
	}



	public void setMatterCommentStaffId(Integer matterCommentStaffId) {
		this.matterCommentStaffId = matterCommentStaffId;
	}



	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsMarketMatterMetaData) {
			PsMarketMatterMetaData o = (PsMarketMatterMetaData)obj;
			return Objects.equals(marketTypeCode, o.marketTypeCode);
		} else {
            return false;
        }
	}
	

}
