package org.finra.esched.domain.ui;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.finra.esched.domain.PsApplicableCmp;
import org.finra.esched.domain.PsLastExam;
import org.finra.esched.domain.PsOvrdReason;
import org.finra.exam.common.security.domain.User;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

@XmlRootElement(name = "psCmpntOutput")
@XmlAccessorType(XmlAccessType.NONE)
@Immutable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsCmpntView {

	private String cmpntCd;
	private int aCmpntId;
	private String cmpntDs;

	private boolean required;

	private Boolean requiredOvrd;

	private PsOvrdReason ovrdReason;

	private User ovrdUser;
	private Date ovrdDate;

	private Date fldwrkStartDate;
	private Date fldwrkStartPrjDate;

	public PsCmpntView() {

	}

	public PsCmpntView(PsApplicableCmp aCmp, PsLastExam le) {
		this();

		this.aCmpntId = aCmp.getId();
		this.cmpntCd = aCmp.getCmp().getId();
		this.cmpntDs = aCmp.getCmp().getDesc();

		this.required = aCmp.isRequired();

		this.requiredOvrd = aCmp.isRequiredOvrd();

		this.ovrdReason = aCmp.getOvrdReason();

		this.ovrdUser = aCmp.getOvrdUser();
		this.ovrdDate = aCmp.getOvrdDate();

		if (le != null) {

			fldwrkStartDate = le.getFldwrkStartDate();
			fldwrkStartPrjDate = le.getFldwrkStartPrjDate();
		}
	}

	@XmlElement(name = "cmpntCd")
	public String getCmpntCd() {
		return cmpntCd;
	}

	public void setCmpntCd(String cmpntCd) {
		this.cmpntCd = cmpntCd;
	}

	@XmlElement(name = "cmpntDs")
	public String getCmpntDs() {
		return cmpntDs != null ? cmpntDs.trim() : cmpntDs;
	}

	public void setCmpntDs(String cmpntDs) {
		this.cmpntDs = cmpntDs;
	}

	@XmlElement(name = "aCmpntId")
	public int getaCmpntId() {
		return aCmpntId;
	}

	public void setaCmpntId(int applCmpntId) {
		this.aCmpntId = applCmpntId;
	}

	@XmlElement(name = "reqFl")
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@XmlElement(name = "reqOvrrdFl")
	public Boolean getRequiredOvrd() {
		return requiredOvrd;
	}

	public void setRequiredOvrd(Boolean requiredOvrd) {
		this.requiredOvrd = requiredOvrd;
	}

	@XmlElement(name = "ovrrdReason")
	public PsOvrdReason getOvrdReason() {
		return ovrdReason;
	}

	public void setOvrdReason(PsOvrdReason ovrdReason) {
		this.ovrdReason = ovrdReason;
	}

	@XmlElement(name = "ovrrdUser")
	public User getOvrdUser() {
		return ovrdUser;
	}

	public void setOvrdUser(User ovrdUser) {
		this.ovrdUser = ovrdUser;
	}

	@XmlElement(name = "ovrrdDate")
	public Date getOvrdDate() {
		return ovrdDate;
	}

	public void setOvrdDate(Date ovrdDate) {
		this.ovrdDate = ovrdDate;
	}

	@XmlElement(name = "fwsdDate")
	public Date getFldwrkStartDate() {
		return fldwrkStartDate;
	}

	public void setFldwrkStartDate(Date fldwrkStartDate) {
		this.fldwrkStartDate = fldwrkStartDate;
	}

	@XmlElement(name = "fwsdPrjDate")
	public Date getFldwrkStartPrjDate() {
		return fldwrkStartPrjDate;
	}

	public void setFldwrkStartPrjDate(Date fldwrkStartPrjDate) {
		this.fldwrkStartPrjDate = fldwrkStartPrjDate;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		if (obj instanceof PsCmpntView) {
			PsCmpntView o = (PsCmpntView) obj;
			return aCmpntId == o.aCmpntId
					&& cmpntCd.equalsIgnoreCase(o.cmpntCd);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (int) 31 * (aCmpntId + cmpntCd.hashCode());
	}
}
