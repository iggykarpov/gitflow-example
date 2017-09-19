package org.finra.esched.domain.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.finra.esched.domain.PsCmp;
import org.finra.esched.domain.PsCmp.CMPNT_TYPE;
import org.finra.esched.domain.PsComponent;
import org.finra.esched.domain.PsFirm;
import org.finra.esched.domain.PsFirm.NMA_TYPE;
import org.finra.esched.domain.PsLastExam;
import org.finra.esched.domain.PsTriggerDt;
import org.finra.esched.domain.PsTriggerDtType.TRIGGER_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MuniAdvComponentImpl extends PsComponent {

	private final Logger log = LoggerFactory.getLogger(MuniAdvComponentImpl.class);

	public MuniAdvComponentImpl(PsCmp cmp) {
		super(cmp);
	}

	@Override
	public boolean isApplicable(PsFirm firm) {
		log.debug("MuniAdvComponentImpl.isApplicable() for firm "
				+ firm.getId().getFirmId());

		// EXAM-16192
		//
		// 1. The firm must have a MuniAdv set to Yes

		boolean isMuniAdv = firm.getMuniAdvFlag() != null
				&& firm.getMuniAdvFlag().booleanValue();
		
		if (isMuniAdv) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isNmaApplicable(PsFirm firm) {
		// Applicable for NMAs
		return true;
	}

	@Override
	public boolean isRsaNmaApplicable(PsFirm firm) {
		// Applicable for NMAs
		return true;
	}

	@Override
	public Integer getFrequency(PsFirm firm) {

		// Applicable for NMAs with FQ=1
		if(firm.isNma(null)) return 1;
		
		// EXAM-16192
		//
		// 1. Frequency = 4; 

		return 4;
	}

	@Override
	public boolean isRequired(PsFirm firm) {
		
		// Required for NMAs
		boolean isNmaRequired=false;
		boolean isRsaNmaRequired=false;
		
		if(firm.isNma(NMA_TYPE.NMA)){
			if(firm.getMuniAdvFlag()!=null && firm.getMuniAdvFlag().booleanValue()) isNmaRequired= true;
		}

		if(firm.isNma(NMA_TYPE.RSANMA)){
			if(firm.getMuniAdvFlag()!=null && firm.getMuniAdvFlag().booleanValue()) isRsaNmaRequired=true;
		}

		if(firm.isNma(null)){
			return isNmaRequired || isRsaNmaRequired;
		}
		
		// For others...
		
		PsTriggerDt triggerDt = getTriggerDt(firm);

		int currYear =  super.getPsYear();

		// If years since trigger date is more or equal to frequency - exam is
		// required.

		if (triggerDt != null) {
			Calendar dt = Calendar.getInstance();
			dt.setTime(triggerDt.getTrggrDt());
			int year = dt.get(Calendar.YEAR);
			Integer freq = getFrequency(firm);
			if (currYear - year < freq)
				return false;
		}

		return true;

	}

	@Override
	public PsTriggerDt getTriggerDt(PsFirm firm) {

		// EXAM-8726
		// No trigger dates for NMAs
		if(firm.isNma(null)) return null;
		
		Date leDt = null;

		List<PsLastExam> lel = firm.getLastExams();
		PsLastExam le = PsLastExam.findCmpByType(lel, CMPNT_TYPE.MUNICIPAL_ADVISOR);
		if (le != null)
			leDt = le.getFldwrkStartDate();

		return (leDt != null) ? new PsTriggerDt(leDt,TRIGGER_TYPE.LAST_EXAM) : null;

	}

}
