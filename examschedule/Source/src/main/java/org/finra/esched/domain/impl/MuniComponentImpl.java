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

public class MuniComponentImpl extends PsComponent {

	private final Logger log = LoggerFactory.getLogger(MuniComponentImpl.class);

	public MuniComponentImpl(PsCmp cmp) {
		super(cmp);
	}

	@Override
	public boolean isApplicable(PsFirm firm) {
		log.debug("MuniComponentImpl.isApplicable() for firm "
				+ firm.getId().getFirmId());

		// EXAM-8151
		//
		// 1. The firm’s Sales Practice Impact must have a numeric value
		// 2. The firm must have a MSRB Indicator set to Yes
		// 3. The firm must have a MSRB effective date

		boolean hasSpFreq = hasSpFreq(firm);
		boolean isMsrbMmbr = firm.getMsrbMmbrFlag() != null
				&& firm.getMsrbMmbrFlag().booleanValue();
		boolean hasMsrbEffDt = firm.getMsrbMmbrEffDt() != null;

		if (hasSpFreq && isMsrbMmbr && hasMsrbEffDt) { //if (hasSpImpact && isMsrbMmbr && hasMsrbEffDt) {
			// log.debug("MuniComponentImpl.isApplicable("+firm.getId().getFirmId()+"): TRUE ("+hasSpImpact
			// +", "+isMsrbMmbr +", "+ hasMsrbEffDt+")");
			return true;
		}
		// log.debug("MuniComponentImpl.isApplicable("+firm.getId().getFirmId()+"): FALSE ("+hasSpImpact
		// +", "+isMsrbMmbr +", "+ hasMsrbEffDt+")");
		return false;
	}
	
	@Override
	public boolean isNmaApplicable(PsFirm firm) {
		// EXAM-8726
		// Applicable for NMAs
		return true;
	}

	@Override
	public boolean isRsaNmaApplicable(PsFirm firm) {
		// EXAM-8726
		// Applicable for NMAs
		return true;
	}

	@Override
	public Integer getFrequency(PsFirm firm) {

		// EXAM-8726
		// Applicable for NMAs with FQ=1
		if(firm.isNma(null)) return 1;
		
		// EXAM-8151
		//
		// 1. Frequency = 1; The firm must have the Accelerated = TRUE
		// 2. Frequency = 4; The firm must have Accelerated = FALSE

		boolean isAccelerated = firm.getAcceleratedFlag() != null
				&& firm.getAcceleratedFlag().booleanValue();

		if (isAccelerated) {
			log.debug("MuniComponentImpl.getFrequency() is 1, isAccelerated is "
					+ isAccelerated);
			return 1;
		} else {
			log.debug("MuniComponentImpl.getFrequency() is 4, isAccelerated is "
					+ isAccelerated);
			return 4;
		}
	}

	@Override
	public boolean isRequired(PsFirm firm) {
		
		// EXAM-8726
		// Required for NMAs
		boolean isNmaRequired=false;
		boolean isRsaNmaRequired=false;
		
		if(firm.isNma(NMA_TYPE.NMA)){
			// Component required if MSRB indicator is set to 'yes'
			if(firm.getMsrbMmbrFlag()!=null && firm.getMsrbMmbrFlag().booleanValue()) isNmaRequired= true;
		}
			
		if(firm.isNma(NMA_TYPE.RSANMA)){
			if(hasSpImpact(firm) && (firm.getMsrbMmbrFlag()!=null && firm.getMsrbMmbrFlag().booleanValue())) isRsaNmaRequired=true;
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
			int freq = getFrequency(firm);
			if (currYear - year >= freq)
				return true;
		}

		return false;

	}

	@Override
	public PsTriggerDt getTriggerDt(PsFirm firm) {

		// EXAM-8726
		// No trigger dates for NMAs
		if(firm.isNma(null)) return null;
		
		Date leDt = null;
		Date msrbEffDt = firm.getMsrbMmbrEffDt();

		List<PsLastExam> lel = firm.getLastExams();
		PsLastExam le = PsLastExam.findCmpByType(lel, CMPNT_TYPE.MUNICIPAL);
		if (le != null)
			leDt = le.getFldwrkStartDate();

		// Trigger date should be MAX of two dates: Last Exam Date or MSRB
		// Membership Date

		if (leDt == null && msrbEffDt == null)
			throw new RuntimeException(
					"MuniComponentImpl.getTriggerDt(): Firm have no MUNI exams nor MSRB Effective Date");
		if (leDt == null) {
			log.debug("MuniComponentImpl.getTriggerDt(): MSRB is a max date as there are no Last Exam of type MUNI");
			return new PsTriggerDt(msrbEffDt, TRIGGER_TYPE.MSRB_EFCTV_DT);
		}
		if (msrbEffDt == null) {
			log.debug("MuniComponentImpl.getTriggerDt(): LE is a max date as there are no MSRB Effective Date");
			return new PsTriggerDt(leDt, TRIGGER_TYPE.LAST_EXAM);
		}
		log.debug("MuniComponentImpl.getTriggerDt(): Both dates present, LE is the latest - "
				+ leDt.after(msrbEffDt));
		return (leDt.after(msrbEffDt)) ? new PsTriggerDt(leDt,
				TRIGGER_TYPE.LAST_EXAM) : new PsTriggerDt(msrbEffDt,
						TRIGGER_TYPE.MSRB_EFCTV_DT);

	}

}
