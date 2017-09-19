package org.finra.esched.domain.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.finra.esched.domain.PsCmp;
import org.finra.esched.domain.PsCmp.CMPNT_TYPE;
import org.finra.esched.domain.PsFirm.NMA_TYPE;
import org.finra.esched.domain.PsComponent;
import org.finra.esched.domain.PsFirm;
import org.finra.esched.domain.PsLastExam;
import org.finra.esched.domain.PsTriggerDt;
import org.finra.esched.domain.PsTriggerDtType.TRIGGER_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptionsComponentImpl extends PsComponent {

	private final Logger log = LoggerFactory.getLogger(OptionsComponentImpl.class);

	public enum APPL_STATUS {
		APPROVED, PENDING_TERMINATION, SUSPENDED, RECEIVERSHIP, APPROVED_REINSTATED;
	};
	
	public int VALID_DOEA_ID=9;
	public String VALID_DOEA="FINRA";
	
	public OptionsComponentImpl(PsCmp cmp) {
		super(cmp);
	}

	@Override
	public boolean isApplicable(PsFirm firm) {
		
		// EXAM-8150
		//
		// 1.	The firm’s Sales Practice Impact must have a numeric value (1,2,4)
		// 2.	DOEA is FINRA

		boolean hasSpFreq= hasSpFreq(firm);
		boolean hasValidDOEA=(firm.getDoeaId()==VALID_DOEA_ID); //getDoeaDs()!=null && firm.getDoeaDs().trim().equalsIgnoreCase(VALID_DOEA);
		
		if(hasSpFreq && hasValidDOEA){ //if(hasSpImpact && hasValidDOEA){
			return true;
		}
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
		// Not applicable for RSANMAs
		return false;
	}

	@Override
	public Integer getFrequency(PsFirm firm) {
		
		// EXAM-8726
		// For NMAs FQ=1
		if(firm.isNma(NMA_TYPE.NMA)) return 1;

		
		return 4;
	}

	@Override
	public boolean isRequired(PsFirm firm) {
		
		// EXAM-8726
		boolean isNmaRequired=false;
		boolean isRsaNmaRequired=false;
		
		if(firm.isNma(NMA_TYPE.NMA)){
			// Required if Firm DOEA is 'FINRA'
			if(firm.getDoeaId()==VALID_DOEA_ID)// !=null && firm.getDoeaDs().trim().equalsIgnoreCase(VALID_DOEA))
				isNmaRequired= true;
		}
			
		if(firm.isNma(null)){
			return isNmaRequired || isRsaNmaRequired;
		}
				
		// For others
		
		PsTriggerDt triggerDt  = getTriggerDt(firm);

		int currYear =  super.getPsYear();
		
		//  Email:	Why as an Options exam NOT required for firm 3526 in the 2015 snapshot when the last Options exam was just done in 2011?[MV]  SP Impact of the firm was “N/A” hence no options requirement.
		if(!hasSpImpact(firm)){
			return false;
		}
		
		
		if (triggerDt != null) {
			Calendar dt = Calendar.getInstance();
			dt.setTime(triggerDt.getTrggrDt());
			int year = dt.get(Calendar.YEAR);
			Integer freq=getFrequency(firm);
			
			if (currYear - year >= freq)
				return true;
		} else {
			return true;
		}
		return false;
	}

	@Override
	public PsTriggerDt getTriggerDt(PsFirm firm) {
		
		// EXAM-8726
		// No trigger dates for NMAs
		if(firm.isNma(NMA_TYPE.NMA)) return null;

		// For others...
		
		List<PsLastExam> lel = firm.getLastExams();
		
		log.debug("OptionsComponentImpl.getLastExamDt(). There are "+(lel!=null ? lel.size(): 0)+" last exams.");
		
		Date leDt=null;
		PsLastExam le = PsLastExam.findCmpByType(lel, CMPNT_TYPE.OPTIONS);
		if(le!=null) leDt=le.getFldwrkStartDate();
		
		Date npobDt=firm.getNpobDt();
		
		
		// Trigger date should be the MAX of two dates: Last Exam Date or NPOB Date
		
		if (leDt == null && npobDt == null) return null; //throw new RuntimeException("OptionsComponentImpl.getTriggerDt(): Firm have no MUNI exams nor MSRB Effective Date");
		if (leDt == null){
			log.debug("OptionsComponentImpl.getTriggerDt(): MSRB is a max date as there are no Last Exam of type MUNI");
		    return new PsTriggerDt(npobDt, TRIGGER_TYPE.NPOB);
		}
		if (npobDt == null){
		    log.debug("OptionsComponentImpl.getTriggerDt(): LE is a max date as there are no MSRB Effective Date");
		    return new PsTriggerDt(leDt, TRIGGER_TYPE.LAST_EXAM);
        }
        log.debug("OptionsComponentImpl.getTriggerDt(): Both dates present, LE is the latest - "+leDt.after(npobDt));
        return (leDt.before(npobDt)) ? new PsTriggerDt(npobDt, TRIGGER_TYPE.NPOB) : new PsTriggerDt(leDt, TRIGGER_TYPE.LAST_EXAM);
	}

}
