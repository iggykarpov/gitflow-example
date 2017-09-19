package org.finra.esched.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public abstract class PsComponent {
	
	private PsCmp cmp;
	
	public PsComponent(PsCmp cmp) {
		this.cmp=cmp;
	}
	
	public int getPsYear(){
		Calendar dt = Calendar.getInstance();
		int currYear = dt.get(Calendar.YEAR);
		// Preschedule year is always NEXT year;
		currYear++;
		
		return currYear;
	}
	
	public abstract boolean isApplicable(PsFirm firm);
	
	public abstract boolean isNmaApplicable(PsFirm firm);
	public abstract boolean isRsaNmaApplicable(PsFirm firm);
	
	public abstract Integer getFrequency(PsFirm firm);
	public abstract PsTriggerDt getTriggerDt(PsFirm firm);
	
	public boolean isRequired(PsFirm firm){
		List<PsLastExam> lel = firm.getLastExams();
		PsTriggerDt triggerDt = getTriggerDt(firm);

		int currYear =  getPsYear();

		Integer freq=getFrequency(firm);
		if(freq==null || freq==0) return false;
		
		if (triggerDt != null) {
			Calendar dt = Calendar.getInstance();
			dt.setTime(triggerDt.getTrggrDt());
			int year = dt.get(Calendar.YEAR);
			if ( currYear - year >= freq)
				return true;
		} else {
			return true;
		}
		return false;
	}

	public boolean hasSpImpact(PsFirm firm){
		return firm.getSpImpctCode()!=null && !firm.getSpImpctCode().trim().equalsIgnoreCase("") && firm.getSpImpact()!=0;
	}
	
	public boolean hasSpRisk(PsFirm firm){
		return firm.getSpRiskCode()!=null && !firm.getSpRiskCode().trim().equalsIgnoreCase("") && firm.getSpRiskCode().trim().toUpperCase().matches("[A-E]");
	}
	
	public boolean hasFnImpact(PsFirm firm){
		return firm.getFnImpctCode()!=null && !firm.getFnImpctCode().trim().equalsIgnoreCase("") && firm.getFnImpact()!=0;
	}
	
	public boolean hasFnRisk(PsFirm firm){
		return firm.getFnRiskCode()!=null && !firm.getFnRiskCode().trim().equalsIgnoreCase("") && firm.getFnRiskCode().trim().toUpperCase().matches("[A-E]");
	}
	
	public static Date last(Date a, Date b) {
	    return a == null ? b : (b == null ? a : (a.after(b) ? a : b));
	}

	public boolean hasFnFreq(PsFirm firm){
		return firm.getFinopFrequency()!=null;
	}

	public boolean hasSpFreq(PsFirm firm){
		return firm.getSpFrequency()!=null;
	}
}
