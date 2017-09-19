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

public class RsaSPComponentImpl extends PsComponent {

	private final Logger log = LoggerFactory.getLogger(RsaSPComponentImpl.class);
	
	private PsCmp cmp;

	public RsaSPComponentImpl(PsCmp cmp) {
		super(cmp);
		
	}

	@Override
	public boolean isApplicable(PsFirm firm) {
		
		return false;
	}
	
	@Override
	public boolean isNmaApplicable(PsFirm firm) {
		// EXAM-8726
		// Not applicable for NMAs
		return true;
	}

	@Override
	public boolean isRsaNmaApplicable(PsFirm firm) {
		// EXAM-8726
		// Not applicable for NMAs
		return true;
	}

	@Override
	public Integer getFrequency(PsFirm firm) {

		return 4;
	}

	@Override
	public boolean isRequired(PsFirm firm) {

		if(firm.isNma(PsFirm.NMA_TYPE.NMA)){
			return false;
		}

		if(firm.isNma(PsFirm.NMA_TYPE.RSANMA)){
			return false;
		}


		PsTriggerDt triggerDt = getTriggerDt(firm);

		int currYear =  super.getPsYear();

		Integer freq=getFrequency(firm);
		if(freq==null || freq==0) return false;
		
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
		
		log.debug("RsaSPComponentImpl.getLastExamDt(). There are "+(lel!=null ? lel.size(): 0)+" last exams.");
		
		PsLastExam fle = PsLastExam.findCmpByType(lel, CMPNT_TYPE.RSA_SALES_PRACTICE);

		if (fle != null) {
			PsTriggerDt result=new PsTriggerDt(fle.getFldwrkStartDate(), TRIGGER_TYPE.LAST_EXAM);
			return result;
		}else{
			return null;
		}
	}

}
