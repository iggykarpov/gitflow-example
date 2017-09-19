package org.finra.esched.domain.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.finra.esched.domain.PsCmp;
import org.finra.esched.domain.PsCmp.CMPNT_TYPE;
import org.finra.esched.domain.PsComponent;
import org.finra.esched.domain.PsFirm;
import org.finra.esched.domain.PsLastExam;
import org.finra.esched.domain.PsTriggerDt;
import org.finra.esched.domain.PsTriggerDtType.TRIGGER_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FNComponentImpl extends PsComponent {

	private final Logger log = LoggerFactory.getLogger(FNComponentImpl.class);
	
	private PsCmp cmp;

	public FNComponentImpl(PsCmp cmp) {
		super(cmp);
		
	}

	@Override
	public boolean isApplicable(PsFirm firm) {
		
		FFNComponentImpl ffnC=new FFNComponentImpl(this.cmp);
		boolean reqireFirstFinop=ffnC.isRequired(firm);
		
//		boolean hasFnImpact= hasFnImpact(firm);
//		boolean hasFnRisk= hasFnRisk(firm);

		//Finop will not be applicable if there is no Finop Frequency
		if(!reqireFirstFinop && hasFnFreq(firm)){
			return true;
		}

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
//		String riskCd=firm.getFnRiskCode();
//		int impactCd=firm.getFnImpact();
//		
//		int[] fqByRisk=(int[])fqs.get(riskCd);
//		
//		if(fqByRisk!=null){
//			return fqByRisk[impactCd];
//		}
//		return 0;
		return firm.getFinopFrequency();
	}

	@Override
	public boolean isRequired(PsFirm firm) {
		PsTriggerDt triggerDt = getTriggerDt(firm);

		int currYear =  super.getPsYear();

		Integer freq=getFrequency(firm);
		if(freq==null ||  freq==0) return false;
		
		if (triggerDt != null) {
			Calendar dt = Calendar.getInstance();
			dt.setTime(triggerDt.getTrggrDt());
			int year = dt.get(Calendar.YEAR);
			if (currYear - year >= freq)
				return true;
		} else {
			return true;
		}
		return false;
	}

	@Override
	public PsTriggerDt getTriggerDt(PsFirm firm) {
		List<PsLastExam> lel = firm.getLastExams();
		
		log.debug("FNComponentImpl.getLastExamDt(). There are "+(lel!=null ? lel.size(): 0)+" last exams.");
		
		// For FN, we have to check for previous FFN and select the latest date among two of them
		// Overwise, FN required/not required generated improperly for cases when we has FFN in last exams but not FN.
		PsLastExam fle = PsLastExam.findCmpByType(lel, CMPNT_TYPE.FINOP);
		PsLastExam ffle = PsLastExam.findCmpByType(lel, CMPNT_TYPE.FIRST_FINOP);

		Date maxDate= last(ffle!=null ? ffle.getFldwrkStartDate(): null , fle!=null ? fle.getFldwrkStartDate(): null);
		
		if (fle != null && fle.getFldwrkStartDate().equals(maxDate)) {
			log.debug("There are FN exam with FW start date of "+fle.getFldwrkStartDate());
			PsTriggerDt result=new PsTriggerDt(fle.getFldwrkStartDate(), TRIGGER_TYPE.LAST_EXAM);
			return result;
		}else if(ffle != null && ffle.getFldwrkStartDate().equals(maxDate)){
			log.debug("There are FFN exam with FW start date of "+ffle.getFldwrkStartDate());
			PsTriggerDt result=new PsTriggerDt(ffle.getFldwrkStartDate(), TRIGGER_TYPE.LAST_EXAM);
			return result;
		}else{
			return null;
		}
	}

}
