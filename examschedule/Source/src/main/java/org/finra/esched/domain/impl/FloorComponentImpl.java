package org.finra.esched.domain.impl;

import java.util.Calendar;
import java.util.List;

import org.finra.esched.domain.PsBa;
import org.finra.esched.domain.PsCmp;
import org.finra.esched.domain.PsCmp.CMPNT_TYPE;
import org.finra.esched.domain.PsComponent;
import org.finra.esched.domain.PsFirm;
import org.finra.esched.domain.PsLastExam;
import org.finra.esched.domain.PsSro.SRO_TYPE;
import org.finra.esched.domain.PsTriggerDt;
import org.finra.esched.domain.PsTriggerDtType.TRIGGER_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FloorComponentImpl extends PsComponent {

	private final Logger log = LoggerFactory.getLogger(FloorComponentImpl.class);
	
	private PsCmp cmp;
	
	public FloorComponentImpl(PsCmp cmp) {
		super(cmp);
	}

	@Override
	public boolean isApplicable(PsFirm firm) {
		

		// EXAM-8154
		//	1.	The firm’s Fin/Op Impact must have a numeric value (1,2,4)
		//	2.	The firm must require a FINOP exam or a First FINOP exam in the current preschedule year
		//	3.	The firm must have Firm Type “Floor Broker – without Direct Access” or “Floor Broker – with Direct Access” assigned to the firm
		//	4.	The firm must be an exchange member of ‘NYSE’ or ‘NYSE-MKT’ on the Firm Profile
		
		FFNComponentImpl ffnC=new FFNComponentImpl(this.cmp);
		boolean reqireFirstFinop=ffnC.isRequired(firm);
		
		FNComponentImpl fnC=new FNComponentImpl(this.cmp);
		boolean reqireFinop=fnC.isRequired(firm);
		
		boolean hasType=(firm.hasBaType(PsBa.FLOOR_BROKER_WITHOUT_DIRECT_ACCESS_ID) || firm.hasBaType(PsBa.FLOOR_BROKER_WITH_DIRECT_ACCESS_ID) || firm.hasBaType(PsBa.NYSE_DMM_ID));
		boolean hasExchng=(firm.hasSroType(SRO_TYPE.NYSE) || firm.hasSroType(SRO_TYPE.NYSEAMER));
		
		
		if(hasFnFreq(firm) && hasType && hasExchng){
			log.debug("FloorComponentImpl.isApplicable("+firm.getId().getFirmId()+"): TRUE ("+hasFnImpact(firm) +", ["+ reqireFirstFinop  +", "+ reqireFinop +"], "+  hasType  +", "+  hasExchng+")");
			return true;
		}
		
		log.debug("FloorComponentImpl.isApplicable("+firm.getId().getFirmId()+"): FALSE ("+hasFnImpact(firm) +", ["+ reqireFirstFinop  +", "+ reqireFinop +"], "+  hasType  +", "+  hasExchng+")");
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
		
		// Email: 3.	Why was a Floor Review exam required for firm 45986 when the last Floor Review exam was just done in 2014?[MV]  firm had business activity type of “NYSE DMM” which requires floor review exam every year.
		if(firm.hasBaType(PsBa.NYSE_DMM_ID)){
			return 1;
		}
		
		return 2;
	}

	@Override
	public boolean isRequired(PsFirm firm) {
		List<PsLastExam> lel = firm.getLastExams();
		PsTriggerDt triggerDt = getTriggerDt(firm);

		int currYear =  super.getPsYear();

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
		List<PsLastExam> lel = firm.getLastExams();
		
		log.debug("FloorComponentImpl.getLastExamDt(). There are "+(lel!=null ? lel.size(): 0)+" last exams.");
		
		PsLastExam le = PsLastExam.findCmpByType(lel, CMPNT_TYPE.FLOOR_REVIEW);

		if (le != null) {
			log.debug("There are FLOOR exam with FW start date of "+le.getFldwrkStartDate());
			PsTriggerDt result=new PsTriggerDt(le.getFldwrkStartDate(), TRIGGER_TYPE.LAST_EXAM);
			return result;
		}else{
			return null;
		}
	}

}
