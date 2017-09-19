package org.finra.esched.domain.impl;

import org.finra.esched.domain.*;
import org.finra.esched.domain.PsCmp.CMPNT_TYPE;
import org.finra.esched.domain.PsTriggerDtType.TRIGGER_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SdfComponentImpl extends PsComponent {

	private final Logger log = LoggerFactory.getLogger(SdfComponentImpl.class);

	public SdfComponentImpl(PsCmp cmp) {
		super(cmp);
	}

	@Override
	public boolean isApplicable(PsFirm firm) {
		log.debug("SdfComponentImpl.isApplicable() for firm "
				+ firm.getId().getFirmId());

		return true;
	}
	
	@Override
	public boolean isNmaApplicable(PsFirm firm) {
		// Applicable for NMAs
		return false;
	}

	@Override
	public boolean isRsaNmaApplicable(PsFirm firm) {
		// Applicable for NMAs
		return false;
	}

	@Override
	public Integer getFrequency(PsFirm firm) {

		// Applicable for NMAs with FQ=1
		if(firm.isNma(null)) return 100;
		

		return 100;
	}

	@Override
	public boolean isRequired(PsFirm firm) {
		
		return false;

	}

	@Override
	public PsTriggerDt getTriggerDt(PsFirm firm) {

		return null;

	}

}
