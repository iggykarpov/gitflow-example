package org.finra.esched.domain.impl;

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

public class FFNComponentImpl extends PsComponent {

	private final Logger log = LoggerFactory.getLogger(FFNComponentImpl.class);

	public FFNComponentImpl(PsCmp cmp) {
		super(cmp);
	}
	
	@Override
	public boolean isApplicable(PsFirm firm) {


		// EXAM-8153
		//
		// 1. The firm’s Finop Impact must have a numeric value (1,2,4)
		// 2. The firm's membership date is after any previously conducted exams of type FIRST FINOP/FINOP/RUTINE OR those exams doesnt exist.
		
		Date mmbrDate=firm.getMembershipEffDt();
		PsTriggerDt triggerDt = getTriggerDt(firm);
		
		boolean isNewMember= false;
		
		// If membership effective date is null, FFN is not applicable
		// if(mmbrDate==null) return false; // and there is no last last exam;
		
		if(triggerDt!=null && triggerDt.getTrggrDtType()==TRIGGER_TYPE.MMBRSHP_DT) isNewMember=true;
		
		List<PsLastExam> lel = firm.getLastExams();
		PsLastExam ffle = PsLastExam.findCmpByType(lel, CMPNT_TYPE.FIRST_FINOP);
		PsLastExam fle = PsLastExam.findCmpByType(lel, CMPNT_TYPE.FINOP);
		PsLastExam rle = PsLastExam.findCmpByType(lel, CMPNT_TYPE.ROUTINE);
		boolean hasLastValidExam=(ffle!=null || fle!=null || rle!=null);	
		
		if (hasFnFreq(firm) && (isNewMember || !hasLastValidExam)){ // No membership date && No previously conducted exams - trigger data will be null if no membership and no exam dates
			return true;
		}
		//log.debug("FFNComponent.isApplicable("+firm.getId().getFirmId()+"): FALSE ("+hasFnImpact(firm)+")");
		return false;
	}
	
	@Override
	public boolean isNmaApplicable(PsFirm firm) {
		return true;
	}

	@Override
	public boolean isRsaNmaApplicable(PsFirm firm) {
		return true;
	}


	@Override
	public Integer getFrequency(PsFirm firm) {
		
		// Doesn't matter for us
		return 1;
	}

	@Override
	public boolean isRequired(PsFirm firm) {
		
		boolean isNmaRequired=false;
		boolean isRsaNmaRequired=false;
		
		if(firm.isNma(NMA_TYPE.NMA)){
			// Always required
			isNmaRequired= true;
		}
			
		if(firm.isNma(NMA_TYPE.RSANMA)){
			// Always required
			isRsaNmaRequired=true;
		}
			
		if(firm.isNma(null)){
			return isNmaRequired || isRsaNmaRequired;
		}
		
		// If component is applicable, exam is required.
		return isApplicable(firm);
	}

	@Override
	public PsTriggerDt getTriggerDt(PsFirm firm) {
		
		// EXAM-8726
		// No trigger dates for NMA's
		if(firm.isNma(null)) return null;
		
		List<PsLastExam> lel = firm.getLastExams();
		
		log.debug("OptionsComponentImpl.getLastExamDt(). There are "+(lel!=null ? lel.size(): 0)+" last exams.");
		
		Date mmbrDate=firm.getMembershipEffDt();
		
		PsLastExam ffle = PsLastExam.findCmpByType(lel, CMPNT_TYPE.FIRST_FINOP);
		PsLastExam fle = PsLastExam.findCmpByType(lel, CMPNT_TYPE.FINOP);
		PsLastExam rle = PsLastExam.findCmpByType(lel, CMPNT_TYPE.ROUTINE);
		
		Date maxDate= last(last(ffle!=null ? ffle.getFldwrkStartDate(): null , fle!=null ? fle.getFldwrkStartDate(): null), rle!=null ? rle.getFldwrkStartDate(): null);
		
		if(maxDate==null && mmbrDate==null){
			return null;
		}else if((maxDate==null && mmbrDate!=null) || (maxDate!=null && mmbrDate!=null && mmbrDate.after(maxDate))) {
			PsTriggerDt result=new PsTriggerDt(mmbrDate, TRIGGER_TYPE.MMBRSHP_DT);
			return result;
		}else{
			PsTriggerDt result=new PsTriggerDt(maxDate, TRIGGER_TYPE.LAST_EXAM);
			return result;
		}
	}
}
