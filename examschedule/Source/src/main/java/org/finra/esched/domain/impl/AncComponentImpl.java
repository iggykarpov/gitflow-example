package org.finra.esched.domain.impl;

import org.finra.esched.domain.PsBa;
import org.finra.esched.domain.PsCmp;
import org.finra.esched.domain.PsComponent;
import org.finra.esched.domain.PsFirm;
import org.finra.esched.domain.PsTriggerDt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AncComponentImpl extends PsComponent {

	private final Logger log = LoggerFactory.getLogger(AncComponentImpl.class);

	public AncComponentImpl(PsCmp cmp) {
		super(cmp);
	}

	@Override
	public boolean isApplicable(PsFirm firm) {
		
		boolean hasFnFreq = hasFnFreq(firm);
		boolean hasBusinessActivity = firm
				.hasBaType(PsBa.ALTERNATE_NET_CAPITAL_ID);

		if (hasFnFreq && hasBusinessActivity) {
			//log.debug("AncComponentImpl.isApplicable("+firm.getId().getFirmId()+"): TRUE ("+hasFnImpact +", "+ hasBusinessActivity+")");
			return true;
		}
		//log.debug("AncComponentImpl.isApplicable("+firm.getId().getFirmId()+"): FALSE ("+hasFnImpact +", "+ hasBusinessActivity+")");
		return false;
	}

	@Override
	public boolean isNmaApplicable(PsFirm firm) {
		// EXAM-8726
		// Not applicable for NMAs
		return false;
	}

	@Override
	public boolean isRsaNmaApplicable(PsFirm firm) {
		// EXAM-8726
		// Not applicable for NMAs
		return false;
	}
	
	@Override
	public Integer getFrequency(PsFirm firm) {
		// Always 1
		return 1;
	}

	@Override
	public boolean isRequired(PsFirm firm) {
		if (isApplicable(firm)) {
			return true;
		}
		return false;
	}

	@Override
	public PsTriggerDt getTriggerDt(PsFirm firm) {	
		// Don't care as frequency always 1, but let's try to find one...
		return null;
	}
}
