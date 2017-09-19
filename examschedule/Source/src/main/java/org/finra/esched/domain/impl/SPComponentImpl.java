package org.finra.esched.domain.impl;

import java.util.HashMap;
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

public class SPComponentImpl extends PsComponent {
	
	private final Logger log = LoggerFactory.getLogger(SPComponentImpl.class);
	
//	HashMap fqs=new HashMap();
//	
//	int [] riskA= {0,1,1,0,1};
//	int [] riskB= {0,1,1,0,1};
//	int [] riskC= {0,1,2,0,2};
//	int [] riskD= {0,1,2,0,3};
//	int [] riskE= {0,1,2,0,4};
	
	public SPComponentImpl(PsCmp cmp) {
		super(cmp);
		
		//setId(cmp.getId());
		//setDesc(cmp.getDesc());
		
//		fqs.put("A", riskA);
//		fqs.put("B", riskB);
//		fqs.put("C", riskC);
//		fqs.put("D", riskD);
//		fqs.put("E", riskE);
		
	}
	
	@Override
	public boolean isApplicable(PsFirm firm) {
		
		// SP will not be applicable if there is no SP frequency
		return hasSpFreq(firm);
		
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
		// Applicable for RSANMAs
		return true;
	}

	@Override
	public Integer getFrequency(PsFirm firm) {
		
		// EXAM-8726
		// 1 for NMAs
		if(firm.isNma(null)) return 1;

		
//		String riskCd=firm.getSpRiskCode();
//		int impactCd=firm.getSpImpact();
//		
//		int[] fqByRisk=(int[])fqs.get(riskCd);
//		
//		if(fqByRisk!=null){
//			return fqByRisk[impactCd];
//		}
//		return 0;

		return firm.getSpFrequency();
	
	}

	@Override
	public boolean isRequired(PsFirm firm) {
		// EXAM-8726
		boolean isNmaRequired=false;
		boolean isRsaNmaRequired=false;
		
		// Required if SP component is required for that firm
		if(firm.isNma(NMA_TYPE.NMA)) isNmaRequired=true;
			
		if(firm.isNma(NMA_TYPE.RSANMA)){
			// Required if firm has SP Impact (not 'N/A')
			if(hasSpImpact(firm)) isRsaNmaRequired=true;
		}
			
		if(firm.isNma(null)){
			return isNmaRequired || isRsaNmaRequired;
		}
		
		return super.isRequired(firm);
		
	}
	
	@Override
	public PsTriggerDt getTriggerDt(PsFirm firm) {
		
		// EXAM-8726
		// Non for NMAs
		if(firm.isNma(null)) return null;

		
		List<PsLastExam> lel = firm.getLastExams();
		
		log.debug("SPComponentImpl.getLastExamDt(). There are "+(lel!=null ? lel.size(): 0)+" last exams for firm "+firm.getId().getFirmId());
		
		PsLastExam le = PsLastExam.findCmpByType(lel, CMPNT_TYPE.SALES_PRACTICE);

		if (le != null) {
			log.debug("There are SP exam with FW start date of "+le.getFldwrkStartDate());
			PsTriggerDt result=new PsTriggerDt(le.getFldwrkStartDate(), TRIGGER_TYPE.LAST_EXAM);
			return result;
		}else{
			log.debug("There are no SP exams for this firm");
			return null;
		}
	}

}
