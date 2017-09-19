package org.finra.esched.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class PsMatter implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public PsMatter() {
	}
	
	private String matterId;
	private Integer districtId;
	private Date initialDepartmentReceivedDate;
	private Date initialFINRAReceivedDate;
	private BigDecimal initialBudgetedStaffHours;
	private Boolean isOutOfTown;
	private Integer matterStateID;
	private Integer regulatorySignificanceID;
	private BigDecimal revisedBudgetedStaffHours;
	private Integer matterTypeID;
	private Integer matterSubTypeID;
	private Boolean isPreScheduleMatter;
	private List<PsFirmBillableEntity> beList;
	private List<PsMatterDate> dateList;



	private Integer productId;
	private String securityName;
	private Integer originId;
	private Integer commentId;
	private String commentText;
	private Integer causeCodeId;
	
	public String getMatterId() {
		return matterId;
	}

	public void setMatterId(String matterId) {
		this.matterId = matterId;
	}

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	public Date getInitialDepartmentReceivedDate() {
		return initialDepartmentReceivedDate;
	}

	public void setInitialDepartmentReceivedDate(Date initialDepartmentReceivedDate) {
		this.initialDepartmentReceivedDate = initialDepartmentReceivedDate;
	}

	public Date getInitialFINRAReceivedDate() {
		return initialFINRAReceivedDate;
	}

	public void setInitialFINRAReceivedDate(Date initialFINRAReceivedDate) {
		this.initialFINRAReceivedDate = initialFINRAReceivedDate;
	}

	public BigDecimal getInitialBudgetedStaffHours() {
		return initialBudgetedStaffHours;
	}

	public void setInitialBudgetedStaffHours(BigDecimal initialBudgetedStaffHours) {
		this.initialBudgetedStaffHours = initialBudgetedStaffHours;
	}

	public Boolean getIsOutOfTown() {
		return isOutOfTown;
	}

	public void setIsOutOfTown(Boolean isOutOfTown) {
		this.isOutOfTown = isOutOfTown;
	}

	public Integer getMatterStateID() {
		return matterStateID;
	}

	public void setMatterStateID(Integer matterStateID) {
		this.matterStateID = matterStateID;
	}

	public Integer getRegulatorySignificanceID() {
		return regulatorySignificanceID;
	}

	public void setRegulatorySignificanceID(Integer regulatorySignificanceID) {
		this.regulatorySignificanceID = regulatorySignificanceID;
	}

	public BigDecimal getRevisedBudgetedStaffHours() {
		return revisedBudgetedStaffHours;
	}

	public void setRevisedBudgetedStaffHours(BigDecimal revisedBudgetedStaffHours) {
		this.revisedBudgetedStaffHours = revisedBudgetedStaffHours;
	}

	public Integer getMatterTypeID() {
		return matterTypeID;
	}

	public void setMatterTypeID(Integer matterTypeID) {
		this.matterTypeID = matterTypeID;
	}

	public Integer getMatterSubTypeID() {
		return matterSubTypeID;
	}

	public void setMatterSubTypeID(Integer matterSubTypeID) {
		this.matterSubTypeID = matterSubTypeID;
	}

	public Boolean getIsPreScheduleMatter() {
		return isPreScheduleMatter;
	}

	public void setIsPreScheduleMatter(Boolean isPreScheduleMatter) {
		this.isPreScheduleMatter = isPreScheduleMatter;
	}

	public List<PsFirmBillableEntity> getBeList() {
		return beList;
	}

	public void setBeList(List<PsFirmBillableEntity> beList) {
		this.beList = beList;
	}

	public List<PsMatterDate> getDateList() {
		return dateList;
	}

	public void setDateList(List<PsMatterDate> dateList) {
		this.dateList = dateList;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public Integer getOriginId() {
		return originId;
	}

	public void setOriginId(Integer originId) {
		this.originId = originId;
	}

	public Integer getCommentId() {
		return commentId;
	}

	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public Integer getCauseCodeId() {
		return causeCodeId;
	}

	public void setCauseCodeId(Integer causeCodeId) {
		this.causeCodeId = causeCodeId;
	}

	@Override
	public boolean equals(Object obj) {
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return 1;
	}
}
