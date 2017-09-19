package org.finra.esched.dao.impl;


import org.finra.esched.dao.PsExamDao;
import org.finra.esched.domain.*;
import org.finra.esched.domain.PsApplicableCmp.RESP_DISTR_TYPE;
import org.finra.esched.domain.PsCmp.CMPNT_TYPE;
import org.finra.esched.domain.PsFirm.NMA_TYPE;
import org.finra.esched.domain.PsSessionStatus.PS_STATUS_TYPE;
import org.finra.esched.domain.impl.*;
import org.finra.esched.domain.ui.*;
import org.finra.esched.exception.ExamSubTypeInvalidException;
import org.finra.esched.exception.ExamTypeSubTypeInvalid;
import org.finra.esched.exception.PsSessionStatusException;
import org.finra.esched.service.rest.ui.PsSchedSessionRequest;
import org.finra.esched.service.rest.ui.PsSchedSessionResponse;
import org.finra.esched.service.rest.ui.ScheduleSessionResponse;
import org.finra.exam.common.domain.ui.ErrorInfoJson;
import org.finra.exam.common.domain.ui.ReturnCode;
import org.finra.exam.common.grid.GridRequest;
import org.finra.exam.common.security.domain.User;
import org.finra.exam.common.security.util.UserContext;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


@Repository("psExamDao")
public class PsExamDaoImpl extends BaseHibernateDAO implements PsExamDao {

	private final Logger log = LoggerFactory.getLogger(PsExamDaoImpl.class);


	
	@Override
	@Transactional
	public int createVersion(int staffId) {

		PsVersion ver = new PsVersion();
		ver.setEffDate(new Date());

		log.debug("User with staffId " + staffId
				+ " is creating a Preschedule Exam Version");

		ver.setStaffId(staffId);

		getSession().save(ver);
		getSession().flush();

		log.info("Version created (" + ver.getVersionId() + ")");

//		SQLQuery fQ = getSession().createSQLQuery(
//				"UPDATE schdl_firm SET schdl_exam_snpsh_id="
//						+ ver.getVersionId() + " WHERE schdl_exam_snpsh_id=0");
//		fQ.executeUpdate();
//
//		SQLQuery leQ = getSession().createSQLQuery(
//				"UPDATE schdl_firm_last_exams SET schdl_exam_snpsh_id="
//						+ ver.getVersionId() + " WHERE schdl_exam_snpsh_id=0");
//		leQ.executeUpdate();
//
//		SQLQuery baQ = getSession().createSQLQuery(
//				"UPDATE schdl_firm_bus_actvy SET schdl_exam_snpsh_id="
//						+ ver.getVersionId() + " WHERE schdl_exam_snpsh_id=0");
//		baQ.executeUpdate();
//
//		SQLQuery sroQ = getSession().createSQLQuery(
//				"UPDATE schdl_firm_sro_mbrsp SET schdl_exam_snpsh_id="
//						+ ver.getVersionId() + " WHERE schdl_exam_snpsh_id=0");
//		sroQ.executeUpdate();

		log.info("Firms data duplicated");

		return ver.getVersionId();
	}
	
	
	@Override
	@Transactional
	public int getCurrentVersion() {
		Integer ver=(Integer)getSession().createQuery("select max(v.versionId) from PsVersion v)").uniqueResult();
		return (ver!=null? ver.intValue() : 1); 
	}
	
	@Override
	@Transactional
	public Long getSchdlOtptSeq() {
		BigDecimal ver=(BigDecimal)getEntityManager().createNativeQuery("select schdl_otpt_id_seq.nextval from dual").getSingleResult();
		return (ver!=null? ver.longValue() : 1);
	}

	@Override
	@Transactional
	public void processVersion(int versionId) {

		log.info("processVersion(" + versionId + ").");

		// Load Firms, Components...
		List<PsFirm> fList = getSession()
				.createQuery("FROM PsFirm f WHERE f.id.versionId = :versionId")
				.setParameter("versionId", versionId).list();
		if (fList == null || (fList != null && fList.size() == 0))
			throw new RuntimeException(
					"processVersion() for versionId="
							+ versionId
							+ ": the list of Firms is empty. Did you forgot to create a version?");

		List<PsCmp> cList = getSession().createQuery("FROM PsCmp c").list();
		if (cList == null || (cList != null && cList.size() == 0))
			throw new RuntimeException("processVersion() for versionId="
					+ versionId + ": the list of Components is empty.");

		List<PsTriggerDtType> ttList = getSession().createQuery(
				"FROM PsTriggerDtType c").list();
		if (ttList == null || (ttList != null && ttList.size() == 0))
			throw new RuntimeException("processVersion() for versionId="
					+ versionId + ": the list of Trigger Date Types is empty.");

		// Process Components applicability.
		
		// For each firm... 
			Iterator<PsFirm> fI = fList.iterator();

			int appl = 0;
			int nAppl = 0;

			while (fI.hasNext()) {

				PsFirm f = fI.next();
				
				List<PsApplicableCmp> aCmps=new ArrayList<PsApplicableCmp>();
				
				//... process every component
				Iterator<PsCmp> cI = cList.iterator();

				while (cI.hasNext()) {
					PsCmp c = cI.next();
					PsCmp.CMPNT_TYPE type = c.getType();
					if (type == null)
						continue;

					log.debug("Processing Firm " + f.getId().getFirmId()+ "/"+ f.getId().getVersionId()+ "...");
					
					PsApplicableCmp cmp = processSession(c, f, ttList, versionId);
					
					if (cmp == null) {
						nAppl++;
						log.debug("- "+ c.getDesc() +" is NOT applicable");
					} else {
						appl++;
						log.debug("- "+ c.getDesc() +" is applicable");
						aCmps.add(cmp);	
				}
					
					
			}
				
			if(aCmps.size()>0){
				// As thats a PS run, create a new Session and add all applicable components to that session.
			
			try{	
				
				PsSessionStatus sNewStatus=(PsSessionStatus) getSession().load(
						PsSessionStatus.class, PsSessionStatus.PS_STATUS_TYPE.NEW.toString());
				
				PsSession s=new PsSession();
				s.setFirm(f);
				s.setStatus(sNewStatus);
				s.setFlDistrictCd(f.getFnDistrictCode());
				s.setFlDistrictTypeCode(f.getFnDistrictTypeCode());
				
				getSession().save(s);
				getSession().flush();
				
				Iterator<PsApplicableCmp> aCmpIt=aCmps.iterator();
				while(aCmpIt.hasNext()){
					PsApplicableCmp ac=aCmpIt.next();
					ac.setSession(s);
					
					// Pre-populate NMA flags...
					ac.setNmaFl(new Boolean(false));
					ac.setRsaNmaFl(new Boolean(false));
					
					getSession().save(ac);
					getSession().flush();
					
				}
				
			}catch(Exception ex){
				log.debug("Exception");
			}
			
				
			}
		}
	}

	@Override
	@Transactional
	public List<Integer> processNMAVersion(int versionId) {
		log.debug("processNMAVersion(" + versionId + ").");

		List<Integer> nmaFirmIds=new ArrayList<Integer>();
		
		// Load NMA Firms that has no applicable components (i.e. we didn't
		// processed them yet)...
		List<PsFirm> fList = getSession()
				.createQuery(
						"FROM PsFirm f WHERE ( (f.nmaFl IS NOT NULL AND f.nmaFl IS TRUE) OR (f.rsaNmaFl IS NOT NULL AND f.rsaNmaFl IS TRUE)) AND f.id.versionId = :versionId ").setParameter("versionId", versionId).list();
		if (fList == null || (fList != null && fList.size() == 0)){
			return null;
		}

		List<PsCmp> cList = getSession().createQuery("FROM PsCmp c").list();
		if (cList == null || (cList != null && cList.size() == 0))
			throw new RuntimeException(
					"processNMAVersion() for versionId="
							+ versionId
							+ ": the list of Components is empty. Did anyone forgot to upload reference data?");

		// Process Components applicability.

		// For each firm...

		Iterator<PsFirm> fI = fList.iterator();

		int appl = 0;
		int nAppl = 0;

		while (fI.hasNext()) {

			PsFirm f = fI.next();
			List<PsApplicableCmp> aCmps = new ArrayList<PsApplicableCmp>();

			log.debug("Processing NMAs for Firm " + f.getId().getFirmId() + "/"
					+ f.getId().getVersionId() + "...");
			
			// ... process every component
			Iterator<PsCmp> cI = cList.iterator();

			while (cI.hasNext()) {
				PsCmp c = cI.next();
				PsCmp.CMPNT_TYPE type = c.getType();
				if (type == null)
					continue;

				log.debug("Processing type " + type.toString() + "...");

				PsApplicableCmp cmp = processSession(c, f, null, versionId);

				if (cmp == null) {
					nAppl++;
					log.debug("- " + c.getDesc()
							+ " is NOT applicable");
				} else {
					appl++;
					log.debug("- " + c.getDesc() + " is applicable");
					aCmps.add(cmp);
				}
				
			}

			if (aCmps.size() > 0) {
				
				// As thats a NMA run, check for current version:
				// - if it's not 'Scheduled' or 'Error', i.e. it's 'Active' - add all applicable components to it, reset all approvals and set the status to 'NEW';
				// - if it's 'Scheduled' or 'Dissmissed' - create a new Session and add all applicable components to that session.
				
				PsSession s=f.getCurrentSession();
				
				if(s!=null && (!s.getStatus().getType().equals(PsSessionStatus.PS_STATUS_TYPE.SCHED) && !s.getStatus().getType().equals(PsSessionStatus.PS_STATUS_TYPE.ERROR))){

					s.setSpApprovedFl(false);
					s.setFnApprovedFl(false);
					PsSessionStatus sNewStatus=(PsSessionStatus) getSession().load(
							PsSessionStatus.class, PsSessionStatus.PS_STATUS_TYPE.NEW.toString());
					s.setStatus(sNewStatus);
					s.setFlDistrictCd(f.getFnDistrictCode());
					s.setFlDistrictTypeCode(f.getFnDistrictTypeCode());

					getSession().saveOrUpdate(s);
					getSession().flush();

					Iterator<PsApplicableCmp> aCmpIt=aCmps.iterator();
					
					while(aCmpIt.hasNext()){
						PsApplicableCmp ac=aCmpIt.next();
						PsApplicableCmp sc=s.getCmpntByType(ac.getCmp().getType());
						
						// EXAM-9460: if has NMA/RSA FFN and Session has FN, dont merge FFN into Session, 
						// According to latest requirements - keep the state of FN ---make FN required---
						
						PsApplicableCmp fnCmp=s.getCmpntByType(PsCmp.CMPNT_TYPE.FINOP);
						if(ac.getCmp().getType().equals(PsCmp.CMPNT_TYPE.FIRST_FINOP) && fnCmp!=null){
							// Don't add FFN to the Session as per EXAM-9460, According to latest requirements
							/* According to latest requirements - keep the state
							 
							// If original FN state is 'REQUIRED', clear any override
							if(fnCmp.isRequired()){
								fnCmp.setRequiredOvrd(null);
								fnCmp.setOvrdDate(null);
								fnCmp.setOvrdReason(null);
								fnCmp.setOvrdUser(null);
							}
							
							fnCmp.setRequired(true);
							*/
							
							fnCmp.setNmaFl(ac.isNmaFl());
							fnCmp.setRsaNmaFl(ac.isRsaNmaFl());
							
							getSession().saveOrUpdate(fnCmp);
							getSession().flush();
							
							// Skip all other logic
							continue;
						}
						
						if(sc!=null){
							// Component already exist in the Session - have to merge NMAs with existing ones
							
							// If session component's required flag was overriden...
							if(sc.isRequiredOvrd()!=null){
								
								// if NMA required & Session component original state is 'REQUIRED' - clear override
								if(ac.isRequired() && sc.isRequired()){
									// clear override
									sc.setRequiredOvrd(null);
									sc.setOvrdDate(null);
									sc.setOvrdReason(null);
									sc.setOvrdUser(null);
								}else{
									// keep override
								}
							}
							
							// If NMA component is required, session component should also be required
							if(ac.isRequired()){
								// NMA component is required, so we have to change session component to 'Required' anyway
								sc.setRequired(true);
							}else{
								// Don't need to do anything, as we not gonna replace anything required with not required
							}
								
							// Replicate NMA flags & Frequency & Trigger date to session component
							sc.setNmaFl(ac.isNmaFl());
							sc.setRsaNmaFl(ac.isRsaNmaFl());
							sc.setExamFreq(ac.getExamFreq());
							sc.setTriggerDt(ac.getTriggerDt());
							sc.setTriggerDtType(ac.getTriggerDtType());
							
							getSession().saveOrUpdate(sc);
							getSession().flush();
						}else{
							// Component not exist in the session
							// Add it to the session by setting Session and save
							ac.setSession(s);
							getSession().save(ac);
							getSession().flush();
						}
					}
					
				}else{
					// We have no active session, that means we have to create a new one...
					
					PsSessionStatus sNewStatus=(PsSessionStatus) getSession().load(
							PsSessionStatus.class, PsSessionStatus.PS_STATUS_TYPE.NEW.toString());
					
					s = new PsSession();
					s.setFirm(f);
					s.setStatus(sNewStatus);
					s.setFlDistrictCd(f.getFnDistrictCode());
					s.setFlDistrictTypeCode(f.getFnDistrictTypeCode());
					s.setSpApprovedFl(false);
					s.setFnApprovedFl(false);

					getSession().save(s);
					getSession().flush();

					Iterator<PsApplicableCmp> aCmpIt=aCmps.iterator();
					while(aCmpIt.hasNext()){
						// ... and all the applicable components to it.
						PsApplicableCmp ac=aCmpIt.next();
						ac.setSession(s);
						
						getSession().save(ac);
						getSession().flush();	
					}
				}
			}
			
			// Clear NMA flags for Firm to mark it as processed
			f.setNmaFl(new Boolean(false));
			f.setRsaNmaFl(new Boolean(false));
			
			nmaFirmIds.add(new Integer(f.getId().getFirmId()));
			
			getSession().saveOrUpdate(f);
			getSession().flush();
		}
		
		return nmaFirmIds;
	}
	
	private PsApplicableCmp processSession(PsCmp c, PsFirm f,List<PsTriggerDtType> ttList, int versionId) {
		PsCmp.CMPNT_TYPE type = c.getType();
		if (type == null)
			return null;

		boolean isApplicable = false;
		boolean isNmaApplicable = false;
		boolean isRsaNmaApplicable = false;
		
		Integer examFreq = 0;
		PsTriggerDt triggerDt = null;
		boolean isRequired = false;

		switch (type) {

		case FIRST_FINOP: {
			FFNComponentImpl ffnC = new FFNComponentImpl(c);
			isApplicable = ffnC.isApplicable(f);
			isNmaApplicable = f.isNma(NMA_TYPE.NMA) && ffnC.isNmaApplicable(f);
			isRsaNmaApplicable = f.isNma(NMA_TYPE.RSANMA) && ffnC.isRsaNmaApplicable(f);

			if ((!f.isNma(null) && isApplicable)  || (isNmaApplicable || isRsaNmaApplicable)) {
				examFreq = ffnC.getFrequency(f);
				triggerDt = ffnC.getTriggerDt(f);
				isRequired = ffnC.isRequired(f);
			}
			break;
		}
		case FINOP: {

			FNComponentImpl fnC = new FNComponentImpl(c);
			isApplicable = fnC.isApplicable(f);
			isNmaApplicable = f.isNma(NMA_TYPE.NMA) && fnC.isNmaApplicable(f);
			isRsaNmaApplicable = f.isNma(NMA_TYPE.RSANMA) && fnC.isRsaNmaApplicable(f);

			if ((!f.isNma(null) && isApplicable)  || (isNmaApplicable || isRsaNmaApplicable)) {
				examFreq = fnC.getFrequency(f);
				triggerDt = fnC.getTriggerDt(f);
				isRequired = fnC.isRequired(f);
			}

			break;
		}
		case SALES_PRACTICE: {

			SPComponentImpl spC = new SPComponentImpl(c);
			isApplicable = spC.isApplicable(f);
			isNmaApplicable = f.isNma(NMA_TYPE.NMA) && spC.isNmaApplicable(f);
			isRsaNmaApplicable = f.isNma(NMA_TYPE.RSANMA) && spC.isRsaNmaApplicable(f);
			
			if ((!f.isNma(null) && isApplicable)  || (isNmaApplicable || isRsaNmaApplicable)) {
				examFreq = spC.getFrequency(f);
				triggerDt = spC.getTriggerDt(f);
				isRequired = spC.isRequired(f);
			}

			break;
		}
		case FLOOR_REVIEW: {

			FloorComponentImpl flC = new FloorComponentImpl(c);
			isApplicable = flC.isApplicable(f);
			isNmaApplicable = f.isNma(NMA_TYPE.NMA) && flC.isNmaApplicable(f);
			isRsaNmaApplicable = f.isNma(NMA_TYPE.RSANMA) && flC.isRsaNmaApplicable(f);

			if ((!f.isNma(null) && isApplicable)  || (isNmaApplicable || isRsaNmaApplicable)) {
				examFreq = flC.getFrequency(f);
				triggerDt = flC.getTriggerDt(f);
				isRequired = flC.isRequired(f);
			}

			break;
		}
		case MUNICIPAL: {

			MuniComponentImpl mC = new MuniComponentImpl(c);
			isApplicable = mC.isApplicable(f);
			isNmaApplicable = f.isNma(NMA_TYPE.NMA) && mC.isNmaApplicable(f);
			isRsaNmaApplicable = f.isNma(NMA_TYPE.RSANMA) && mC.isRsaNmaApplicable(f);
			
			if ((!f.isNma(null) && isApplicable)  || (isNmaApplicable || isRsaNmaApplicable)) {
				examFreq = mC.getFrequency(f);
				triggerDt = mC.getTriggerDt(f);
				isRequired = mC.isRequired(f);
			}

			break;
		}
		case OPTIONS: {

			OptionsComponentImpl oC = new OptionsComponentImpl(c);
			isApplicable = oC.isApplicable(f);
			isNmaApplicable = f.isNma(NMA_TYPE.NMA) && oC.isNmaApplicable(f);
			isRsaNmaApplicable = f.isNma(NMA_TYPE.RSANMA) && oC.isRsaNmaApplicable(f);
			
			if ((!f.isNma(null) && isApplicable) || (isNmaApplicable || isRsaNmaApplicable)) {
				examFreq = oC.getFrequency(f);
				triggerDt = oC.getTriggerDt(f);
				isRequired = oC.isRequired(f);
			}

			break;
		}
		case ANC: {
			AncComponentImpl ancC = new AncComponentImpl(c);
			isApplicable = ancC.isApplicable(f);
			isNmaApplicable = f.isNma(NMA_TYPE.NMA) && ancC.isNmaApplicable(f);
			isRsaNmaApplicable = f.isNma(NMA_TYPE.RSANMA) && ancC.isRsaNmaApplicable(f);

			if ((!f.isNma(null) && isApplicable)  || (isNmaApplicable || isRsaNmaApplicable)) {
				examFreq = ancC.getFrequency(f);
				triggerDt = ancC.getTriggerDt(f);
				isRequired = ancC.isRequired(f);
			}

			break;
		}
		case MUNICIPAL_ADVISOR: {

			MuniAdvComponentImpl maC = new MuniAdvComponentImpl(c);
			isApplicable = maC.isApplicable(f);
			isNmaApplicable = f.isNma(NMA_TYPE.NMA) && maC.isNmaApplicable(f);
			isRsaNmaApplicable = f.isNma(NMA_TYPE.RSANMA) && maC.isRsaNmaApplicable(f);
			
			if ((!f.isNma(null) && isApplicable)  || (isNmaApplicable || isRsaNmaApplicable)) {
				examFreq = maC.getFrequency(f);
				triggerDt = maC.getTriggerDt(f);
				isRequired = maC.isRequired(f);
			}

			break;
		}
		case RSA_FINOP: {

			RsaFNComponentImpl fnC = new RsaFNComponentImpl(c);
			List<PsFirmBillableEntity> ret = getFirmBillableEntity(f.getId().getFirmId(), versionId, "N", "Y");
			isApplicable = ret != null && ret.size() > 0?true:false;
			isNmaApplicable = f.isNma(NMA_TYPE.NMA) && fnC.isNmaApplicable(f);
			isRsaNmaApplicable = f.isNma(NMA_TYPE.RSANMA) && fnC.isRsaNmaApplicable(f);

			if ((!f.isNma(null) && isApplicable)  || (isNmaApplicable || isRsaNmaApplicable)) {
				examFreq = fnC.getFrequency(f);
				triggerDt = fnC.getTriggerDt(f);
				isRequired = fnC.isRequired(f);
			}

			break;
		}
		case RSA_SALES_PRACTICE: {

			RsaSPComponentImpl spC = new RsaSPComponentImpl(c);
			List<PsFirmBillableEntity> ret = getFirmBillableEntity(f.getId().getFirmId(), versionId, "Y", "N");
			isApplicable = ret != null && ret.size() > 0?true:false;
			isNmaApplicable = f.isNma(NMA_TYPE.NMA) && spC.isNmaApplicable(f);
			isRsaNmaApplicable = f.isNma(NMA_TYPE.RSANMA) && spC.isRsaNmaApplicable(f);

			if ((!f.isNma(null) && isApplicable)  || (isNmaApplicable || isRsaNmaApplicable)) {
				examFreq = spC.getFrequency(f);
				triggerDt = spC.getTriggerDt(f);
				isRequired = spC.isRequired(f);
			}

			break;
		}
			case SDF: {

				SdfComponentImpl sdfC = new SdfComponentImpl(c);
				isApplicable = sdfC.isApplicable(f);
				isNmaApplicable = f.isNma(NMA_TYPE.NMA) && sdfC.isNmaApplicable(f);
				isRsaNmaApplicable = f.isNma(NMA_TYPE.RSANMA) && sdfC.isRsaNmaApplicable(f);

				if ((!f.isNma(null) && isApplicable)  || (isNmaApplicable || isRsaNmaApplicable)) {
					examFreq = sdfC.getFrequency(f);
					triggerDt = sdfC.getTriggerDt(f);
					isRequired = sdfC.isRequired(f);
				}

				break;
			}
		default:
			// Thats weird, we shouldn't be here...
			break;
		}

		if ((!f.isNma(null) && isApplicable) || (isNmaApplicable || isRsaNmaApplicable)) {
			// Create new PsApplicableCmp
			PsApplicableCmp aCmp = new PsApplicableCmp();
			//aCmp.setFirm(f);
			aCmp.setCmp(c);
			aCmp.setExamFreq(examFreq);
			
			if(f.isNma(null)){
				aCmp.setNmaFl(isNmaApplicable);
				aCmp.setRsaNmaFl(isRsaNmaApplicable);
			}
						
			if (triggerDt != null) {
				aCmp.setTriggerDt(triggerDt.getTrggrDt());

				PsTriggerDtType tt = PsTriggerDtType.findByType(ttList,
						triggerDt.getTrggrDtType());
				if (tt == null) {
					log.error("Unable to get TriggerType of "
							+ triggerDt.getTrggrDtType()
							+ " for Firm Id "
							+ f.getId().getFirmId()
							+ " Check that the Triggertype exist in lookup table");
					return null;
				}

				aCmp.setTriggerDtType(tt);
			}

			aCmp.setRequired(isRequired);

			return aCmp;
		}

		return null;

	}

	@Override
	public void scheduleSession(PsSchedSessionView examRq, User schedUser) throws Exception{
		
		if(examRq==null)
			throw new Exception("Request is null");
		
		PsSession sssn=null;
		sssn=(PsSession)getSession().get(PsSession.class, examRq.getSssnId());

		if(sssn.hasExamStatus(PS_STATUS_TYPE.SCHED) || sssn.hasExamStatus(PS_STATUS_TYPE.ERROR))
			throw new Exception("scheduleSession(id="+sssn.getId()+"): session is in SCHEDULED/ERROR status, therefore it will not be scheduled again");

		if(!(sssn.isFnApprovedFl() && sssn.isSpApprovedFl()))
			throw new Exception("scheduleSession(id="+sssn.getId()+"): session is not fully approved and will not be scheduled");

		List<PsApplicableCmp> spCmps = new ArrayList<PsApplicableCmp>();
		List<PsApplicableCmp> fnCmps = new ArrayList<PsApplicableCmp>();
		List<PsApplicableCmp> flrCmps = new ArrayList<PsApplicableCmp>();


		// Get all applicable components for the Firm
		List<PsApplicableCmp> cmps = sssn.getaCmps();
		Iterator<PsApplicableCmp> cmpIt = cmps.iterator();
		
		// Remove not required components from the list, distribute cmps by responsible district type
		while (cmpIt.hasNext()) {
			PsApplicableCmp cmp = cmpIt.next();
			if ((!cmp.isRequired() && cmp.isRequiredOvrd()==null) || (cmp.isRequiredOvrd()!=null && !cmp.isRequiredOvrd().booleanValue())) {
				cmpIt.remove();
				continue;
			}
			if(cmp.getCmp().getRespDistrType()==RESP_DISTR_TYPE.SP){
				spCmps.add(cmp);
				cmpIt.remove();
			}
			if(cmp.getCmp().getRespDistrType()==RESP_DISTR_TYPE.FN){
				fnCmps.add(cmp);
				cmpIt.remove();
			}
			if(cmp.getCmp().getRespDistrType()==RESP_DISTR_TYPE.FL){
				flrCmps.add(cmp);
				cmpIt.remove();
			}
		}
		
		if(cmps.size()==0 && spCmps.size()==0 && fnCmps.size()==0)
			throw new Exception("Somehow firm with no required components sneak through...");

		
		// Create separate records for components that is required, but not related to SP/FN groups (e.x. ANC)
		cmpIt = cmps.iterator();
		while (cmpIt.hasNext()) {
			PsApplicableCmp cmp = cmpIt.next();
			storeExam(sssn, cmp, null, sssn.getPrjFwsd(), sssn.getPrjEwsd(), schedUser);
			cmpIt.remove();
		}
		
		if(cmps.size()>0)
			throw new Exception("There are required components left that are not processed for Session Id:"+examRq.getSssnId());
		

		if (flrCmps != null && flrCmps.size() > 0){
		    if (sssn.getFirm().getFnDistrictCode().equals(sssn.getFlDistrictCd()))
				//if floor district is the same as finop district, include floor component as part of finop exam
				fnCmps.add(flrCmps.get(0));
			else
				//else create separate floor exam
				//as per EXAM-16581 - do not set any milestone dates
				storeExam(sssn, flrCmps.get(0), null, null, null, schedUser);
		}
		// Sort each collection according to priority id
		Collections.sort(spCmps);
		Collections.sort(fnCmps);
		
		// Check the districts.
		String spDistrictCd = sssn.getFirm().getSpDistrictCode();
		String fnDistrictCd = sssn.getFirm().getFnDistrictCode();
		
		if (spDistrictCd.equalsIgnoreCase(fnDistrictCd)) {
			// If SP & FN districts are the same - should be one combined output record;
			
			spCmps.addAll(fnCmps);
			Collections.sort(spCmps);
			
			if(spCmps.size()>0){
				PsApplicableCmp type=spCmps.get(0);
				spCmps.remove(0);
				List<PsApplicableCmp> subTypes=spCmps;
				storeExam(sssn, type, subTypes, sssn.getPrjFwsd(), sssn.getPrjEwsd(), schedUser);
			}
		}else{
			// If SP & FN districts are different - should be 2 output records;

			if(spCmps.size()>0){
				PsApplicableCmp spType = spCmps.get(0);
				spCmps.remove(0);
				List<PsApplicableCmp> spSubTypes = spCmps;
				storeExam(sssn, spType, spSubTypes, sssn.getPrjFwsd(),
						sssn.getPrjEwsd(), schedUser);
			}

			if(fnCmps.size()>0){
				PsApplicableCmp fnType = fnCmps.get(0);
				fnCmps.remove(0);
				List<PsApplicableCmp> fnSubTypes = fnCmps;
				storeExam(sssn, fnType, fnSubTypes, sssn.getPrjFwsd(),
						sssn.getPrjEwsd(), schedUser);
			}
		}
		
		//Session status must be set in PsExamWebServiceImpl.createExamsMatters to prevent concurency scheduling
		
		getSession().saveOrUpdate(sssn);
		getSession().flush();
	}

	private void storeExam(PsSession session, PsApplicableCmp type,
			List<PsApplicableCmp> subTypes, Date prjFwsd, Date prjEwsd, User schedUser) throws Exception{

		if (type == null) {
			return;
		}

		PsOutput o = new PsOutput();
		o.setSession(session);
		o.setFnDistrict(session.getFirm().getFnDistrictCode());
//		o.setFnImpctCd(type.getFirm().getFnImpctCode());
//		o.setFnRiskCd(type.getFirm().getFnRiskCode());
//		o.setFnSupervisor(type.getFirm().getFnSupervisorNm());
		
//		o.setMttrTypeNm(type.getCmp().getDesc());
		o.setMttrTypeId(getMttrTypeIdByCmpntCd(type));
		o.setExamTypeCd(getExamTypeCdByCmpntCd(type));

		if(subTypes!=null){
			o.setMttrSubTypeId(getMttrSubTypeIdByCmpntCds(subTypes));
			o.setExamSubTypeCd(getExamSubTypeIdByCmpntCds(subTypes));
			if(o.getExamSubTypeCd() != null && !o.getExamSubTypeCd().isEmpty())
				checkExamTypeSubTypeValid(o.getExamTypeCd(), o.getExamSubTypeCd());
		}

		o.setRequiredFl(type.isRequired());
		o.setSpDistrict(session.getFirm().getSpDistrictCode());
//		o.setSpImpctCd(type.getFirm().getSpImpctCode());
//		o.setSpRiskCd(type.getFirm().getSpRiskCode());
//		o.setSpSupervisor(type.getFirm().getSpSupervisorNm());
		o.setSchedDate(new Date());
		o.setSchedUser(schedUser);
		o.setPrjFwsd(prjFwsd);
		o.setPrjEwsd(prjEwsd);

		o.setFloorDistrict(session.getFlDistrictCd());
		//o.setVersionId(type.getFirm().getId().getVersionId());

		getSession().save(o);
		getSession().flush();

	}

	@Transactional
	public void checkExamTypeSubTypeValid(String examTypeCode, String examSubTypeCode) throws Exception {
		String query = "SELECT nvl(count(1), 0) valid_check " +
				"  FROM exam_type_exam_sub_type_lk " +
				"WHERE exam_type_cd = :examTypeCode AND exam_sub_type_cd = :examSubTypeCode" ;
		SQLQuery q = getSession().createSQLQuery(query);
		q.setParameter("examTypeCode", examTypeCode);
		q.setParameter("examSubTypeCode", examSubTypeCode);
		BigDecimal count =  (BigDecimal)q.uniqueResult();
		if(count == null || count.intValue() == 0)
			throw new ExamTypeSubTypeInvalid("Combination of ExamType: "+examTypeCode+ " and ExamSubtype : "+examSubTypeCode +" is invalid.");
	}
	

	@Transactional
	public void verifyComponents() {
		
		int bugsFound = 0;
		StringBuffer result = new StringBuffer();
		
		// Check what all firms in PS are in STAR
		
		int versionId=getCurrentVersion();
		String psHql= "SELECT DISTINCT o.firm.id.firmId from PsSession o WHERE o.firm.id.versionId="+versionId+" ORDER BY o.firm.id.firmId ASC";
		Query psQuery = getSession().createQuery(psHql);
		
		String stHql= "SELECT DISTINCT s.firm.id.firmId from PsSession s WHERE s.firm.id.versionId="+versionId+" AND s.firm.id.firmId NOT IN(SELECT DISTINCT o.firmId from StarOutput o)";
		Query stQuery = getSession().createQuery(stHql);
		List<Integer> stFirmIdList=stQuery.list();
		
		StringBuffer sb=new StringBuffer();
		Iterator<Integer> stFirmIdListIt=stFirmIdList.iterator();
		while(stFirmIdListIt.hasNext()){
			Integer firmId=stFirmIdListIt.next();
			if(sb.length()>0) sb.append(", ");
			sb.append(firmId);
		}
		
		if(sb.length()>0){
			result.append("Following Firms are in PS but not in STAR output: \n"+sb.toString()+"\n");
			// The case when Firm is in STAR but missing in PS will be checked as part of the logic below...
		}
			
		
		String hql = "SELECT DISTINCT o.firmId from StarOutput o ORDER BY o.firmId ASC";

		Query query = getSession().createQuery(hql);
		List<Integer> results = query.list();

		Iterator<Integer> it = results.iterator();
		
		// For each FIRM ID...
		while (it.hasNext()) {

			Integer firmId = it.next();
			
			StringBuffer bugs = new StringBuffer();

			PsFirm firm=(PsFirm)getSession().get(PsFirm.class, new PsFirmPK(firmId, versionId));
			
			// Load all records from STAR
			List<StarOutput> sList = getSession()
					.createQuery("FROM StarOutput o WHERE o.firmId=:firmId")
					.setParameter("firmId", firmId).list();

			List<PsApplicableCmp> pList=null;
			
			try{
				pList= getSession()
					.createQuery(
							"FROM PsApplicableCmp c WHERE c.session.firm.id.firmId=:firmId AND c.session.firm.id.versionId="+versionId)
					.setParameter("firmId", firmId).list();
			}catch(Exception ex){
				bugs.append("- missing PsFirm " + firmId+" for version Id "+versionId 
						+ " in reference data\n"+ex.getMessage());
				bugsFound++;
				result.append("Firm " + firmId + "\n" + bugs.toString()+"\n");
				continue;
			}
				
			// 1. Check that we have same firm in both outputs
			if (pList == null || (pList != null && pList.size() == 0)) {
				// log.debug("- exists in STAR output, but missing from PS output. Check that we have that firm in PS reference data");
				bugs.append("- exists in STAR but missing in PS output\n");
				bugsFound++;
				continue;
			}
			
			

			// 2. Check that we have same components as for STAR output
			Iterator<StarOutput> soIt = sList.iterator();
			Iterator<PsApplicableCmp> psApplIt = pList.iterator();

			List<String> psMttrTypes = new ArrayList<String>();
			List<String> psMttrTypesRq = new ArrayList<String>();

			while (psApplIt.hasNext()) {
				PsApplicableCmp c = psApplIt.next();
				psMttrTypes.add(c.getCmp().getType().toString());

				if (c.isRequired())
					psMttrTypesRq.add(c.getCmp().getType().toString());
			}

			List<String> soMttrTypes = new ArrayList<String>();
			List<String> soMttrTypesRq = new ArrayList<String>();
			while (soIt.hasNext()) {
				StarOutput so = soIt.next();

				if (so == null)
					throw new RuntimeException(
							"Did Rene forgot to populate SCHED.SCHED_ID coulmn?");

				if (so.getMttrType().toUpperCase().contains("SALES")
						&& !soMttrTypes.contains("SALES_PRACTICE")) {
					soMttrTypes.add("SALES_PRACTICE");
					if (so.isRequired())
						soMttrTypesRq.add("SALES_PRACTICE");
				}
				if (so.getMttrType().toUpperCase().contains("FIRST FINOP")) {
					if (!soMttrTypes.contains("FIRST_FINOP")) {
						soMttrTypes.add("FIRST_FINOP");
						if (so.isRequired())
							soMttrTypesRq.add("FIRST_FINOP");
					}
				} else if (so.getMttrType().toUpperCase().contains("FINOP")) {
					if (!soMttrTypes.contains("FINOP")) {
						soMttrTypes.add("FINOP");
						if (so.isRequired())
							soMttrTypesRq.add("FINOP");
					}
				}
				if (so.getMttrType().toUpperCase().contains("MUNICIPAL")
						&& !soMttrTypes.contains("MUNICIPAL")) {
					soMttrTypes.add("MUNICIPAL");
					if (so.isRequired())
						soMttrTypesRq.add("MUNICIPAL");
				}
				if (so.getMttrType().toUpperCase().contains("FLOOR")
						&& !soMttrTypes.contains("FLOOR_REVIEW")) {
					soMttrTypes.add("FLOOR_REVIEW");
					if (so.isRequired())
						soMttrTypesRq.add("FLOOR_REVIEW");
				}
				if (so.getMttrType().toUpperCase().contains("OPTION")
						&& !soMttrTypes.contains("OPTIONS")) {
					soMttrTypes.add("OPTIONS");
					if (so.isRequired())
						soMttrTypesRq.add("OPTIONS");
				}
				if (so.getMttrType().toUpperCase().contains("ALTERNATE")
						&& !soMttrTypes.contains("ANC")) {
					soMttrTypes.add("ANC");
					if (so.isRequired())
						soMttrTypesRq.add("ANC");
				}
				if (so.getMttrSubType() != null) {
					if (so.getMttrSubType().toUpperCase().contains("SALES")
							&& !soMttrTypes.contains("SALES_PRACTICE")) {
						soMttrTypes.add("SALES_PRACTICE");
						if (so.isRequired())
							soMttrTypesRq.add("SALES_PRACTICE");
					}
					if (so.getMttrSubType().toUpperCase()
							.contains("FIRST FINOP")) {
						if (!soMttrTypes.contains("FIRST_FINOP")) {
							soMttrTypes.add("FIRST_FINOP");
							if (so.isRequired())
								soMttrTypesRq.add("FIRST_FINOP");
						}
					} else if (so.getMttrSubType().toUpperCase()
							.contains("FINOP")) {
						if (!soMttrTypes.contains("FINOP")) {
							soMttrTypes.add("FINOP");
							if (so.isRequired())
								soMttrTypesRq.add("FINOP");
						}
					}
					if (so.getMttrSubType().toUpperCase().contains("MUNICIPAL")
							&& !soMttrTypes.contains("MUNICIPAL")) {
						soMttrTypes.add("MUNICIPAL");
						if (so.isRequired())
							soMttrTypesRq.add("MUNICIPAL");
					}
					if (so.getMttrSubType().toUpperCase().contains("FLOOR")
							&& !soMttrTypes.contains("FLOOR_REVIEW")) {
						soMttrTypes.add("FLOOR_REVIEW");
						if (so.isRequired())
							soMttrTypesRq.add("FLOOR_REVIEW");
					}
					if (so.getMttrSubType().toUpperCase().contains("OPTION")
							&& !soMttrTypes.contains("OPTIONS")) {
						soMttrTypes.add("OPTIONS");
						if (so.isRequired())
							soMttrTypesRq.add("OPTIONS");
					}
					if (so.getMttrSubType().toUpperCase().contains("ALTERNATE")
							&& !soMttrTypes.contains("ANC")) {
						soMttrTypes.add("ANC");
						if (so.isRequired())
							soMttrTypesRq.add("ANC");
					}
				}
			}

			if (soMttrTypes.size() != psMttrTypes.size())
				bugs.append("- have different number of types in STAR("
						+ soMttrTypes.size() + ") and in PS("
						+ psMttrTypes.size() + ") output:\n");

			// Compare Lists...

			Iterator<String> slIt = soMttrTypes.iterator();

			while (slIt.hasNext()) {
				String starType = slIt.next();
				if (!psMttrTypes.contains(starType)) {
					bugs.append("- missing " + starType
							+ " type in PS output\n");
					bugsFound++;
				}

				if (soMttrTypesRq.contains(starType)
						&& !psMttrTypesRq.contains(starType)) {
					bugs.append("- "
							+ starType
							+ " is required in STAR but not required in PS output\n");
					bugsFound++;
				}
			}

			Iterator<String> plIt = psMttrTypes.iterator();

			while (plIt.hasNext()) {
				String psType = plIt.next();
				if (!soMttrTypes.contains(psType)) {
					
					PsLastExam ffle = PsLastExam.findCmpByType(firm.getLastExams(), CMPNT_TYPE.FIRST_FINOP);
					PsLastExam fle = PsLastExam.findCmpByType(firm.getLastExams(), CMPNT_TYPE.FINOP);
					
					bugs.append("- have " + psType
							+ " type in PS but not in STAR "
							+ (psType.equalsIgnoreCase("FIRST_FINOP") ? "(Last Exam:"+(ffle!=null ? ffle.getFldwrkStartDate(): "N/A")+")":"")
							+ (psType.equalsIgnoreCase("FINOP") ? "(Last Exam:"+(fle!=null ? fle.getFldwrkStartDate() : "N/A")+")":""));					
					bugs.append("\n");
					bugsFound++;
				}

				if (psMttrTypesRq.contains(psType)
						&& !soMttrTypesRq.contains(psType)) {
					bugs.append("- " + psType
							+ " is required in PS but not required in STAR\n");
					bugsFound++;
				}
			}

			if (bugs.length() > 0){
				
				String date = (firm.getMembershipEffDt().getMonth()+1)+"/"+(firm.getMembershipEffDt().getDate())+"/"+(firm.getMembershipEffDt().getYear()+1900);
				
				result.append("Firm " + firmId + "(Member since "+date+"):\n" + bugs.toString()+"\n");
				
			}
		}

		log.debug(result.toString() + "\n" + bugsFound + " discrepancies found");
	}


	@Override
	public PsSessionView getPsExamsById(Long id)throws Exception {
		PsSessionView sessionView = getEntityManager().find(PsSessionView.class, id.intValue());

		return sessionView;
	}

	@Override
	@Transactional
	public List<PsSessionView> getPsExams(GridRequest rq) {
		
		String hql = "";
		hql+=getPsExamsHQL(rq);
		hql+= " ORDER BY sv.nmaFl DESC, sv.firmNm ASC";
		
		int pageNum = rq.getPageNum();
		int pageSize = rq.getPageSize();

		// VRuzha: default first result is always 0
		Query query = getSession().createQuery(hql).setFirstResult(0);
		
		// VRuzha: to EXPORT all records, pageNum & pageSize will be -1
		// Otherwise, apply paging...
		if(pageNum!=-1 && pageSize!=-1){
		
			int start = (pageNum * pageSize) - pageSize;
			int max = pageSize;

			query.setFirstResult(start).setMaxResults(max);
		}
		List<PsSessionView> result = query.list();

		return result;
		
	}
	
	
	public String getPsExamsHQL(GridRequest rq) {

		// Base query
		String hql = "FROM PsSessionView sv ";

		// Filter query
		if (rq.hasSmartFilters()) {

			Long vrsnId=Long.valueOf(getCurrentVersion());  //rq.getFromSmartFilters("vrsnId", Long.class);
			
			String crdFilter = rq.getFromSmartFilters("crdFilter", String.class);
			
			String impactFilter = rq.getFromSmartFilters("impctFilter",	String.class);
			String riskFilter = rq.getFromSmartFilters("riskFilter", String.class);
			String distrFilter = rq.getFromSmartFilters("distrFilter", String.class);
			String statusFilter = rq.getFromSmartFilters("statusFilter",String.class);
			Boolean ovrrdFilter = rq.getFromSmartFilters("ovrrdFilter",	Boolean.class);
			Boolean hasRqFilter = rq.getFromSmartFilters("hasRqFilter",	Boolean.class);
			Boolean hasNmaFilter=rq.getFromSmartFilters("hasNmaFilter",	Boolean.class);
			Boolean spFilter = rq.getFromSmartFilters("spFilter", Boolean.class);
			Boolean muniFilter = rq.getFromSmartFilters("muniFilter", Boolean.class);
			Boolean opFilter = rq.getFromSmartFilters("opFilter", Boolean.class);
			Boolean ffnFilter = rq.getFromSmartFilters("ffnFilter", Boolean.class);
			Boolean fnFilter = rq.getFromSmartFilters("fnFilter", Boolean.class);
			Boolean flFilter = rq.getFromSmartFilters("flFilter", Boolean.class);
			Boolean ancFilter = rq.getFromSmartFilters("ancFilter",	Boolean.class);
			Boolean sdfFilter = rq.getFromSmartFilters("sdfFilter",	Boolean.class);
			Boolean rsaFnFilter = rq.getFromSmartFilters("rsaFnFilter", Boolean.class);
			Boolean rsaSpFilter = rq.getFromSmartFilters("rsaSpFilter", Boolean.class);
			Boolean muniAdvFilter = rq.getFromSmartFilters("muniAdvFilter",	Boolean.class);			
			StringBuffer filters = new StringBuffer();

			// List<String> filters = new ArrayList<String>();

			StringBuffer textFilter = new StringBuffer();

			if(crdFilter!=null){
				
				String[] searchWords=crdFilter.replaceAll("\\,", " ").replaceAll("\\.", " ").trim().split(" ");
				
				for(int i=0; i<searchWords.length; i++){
					String word=searchWords[i];
					if(word.equalsIgnoreCase("")) continue;
					
					StringBuffer wordFilter = new StringBuffer();
					if(word.matches("^[0-9]+$")) wordFilter.append(" sv.firmId="+word.trim());
					
					if(!word.matches("^[0-9]+$")){
						if(wordFilter.length()>0) wordFilter.append(" OR");
						wordFilter.append(" lower(sv.firmNm) LIKE lower('%"+word.trim()+"%')");
					}
					
					if(!word.matches("^[0-9]+$")){
						if(wordFilter.length()>0) wordFilter.append(" OR");
						wordFilter.append(" lower(sv.spSupervisorNm) LIKE lower('%"+word.trim()+"%')");
					}
					
					
					if(!word.matches("^[0-9]+$")){
						if(wordFilter.length()>0) wordFilter.append(" OR");
						wordFilter.append(" lower(sv.fnSupervisorNm) LIKE lower('%"+word.trim()+"%')");
					}
					
					if(textFilter.length()>0) textFilter.append(" OR");
					textFilter.append(" ("+wordFilter.toString()+")");
				}
			}
			
			if(textFilter.length()>0) filters.append("("+textFilter.toString()+")");
			
			
			// IMPACTS, RISKS, OTHERS...
			
			if (impactFilter != null && !impactFilter.equalsIgnoreCase("")){
				if(filters.length()>0) filters.append(" AND");
				
				filters.append(" (sv.spImpact='"+impactFilter+"' OR sv.finopImpact='"+impactFilter+"')");
			}
			
			if (riskFilter != null && !riskFilter.equalsIgnoreCase("")){
				if(filters.length()>0) filters.append(" AND");
				
				filters.append(" (sv.spLikelihood='"+riskFilter+"' OR sv.finopLikelihood='"+riskFilter+"')");
			}
			
			if (distrFilter != null && !distrFilter.equalsIgnoreCase("")){
				if(filters.length()>0) filters.append(" AND");
				filters.append(" (sv.spDistrictCd='"+distrFilter+"' OR sv.fnDistrictCd='"+distrFilter+"')");	
			}
			
			if (statusFilter != null && !statusFilter.equalsIgnoreCase("")){
				if(filters.length()>0) filters.append(" AND");
				if(statusFilter.equalsIgnoreCase("ACTIVE")){
					filters.append(" (sv.sssnStatusCd='NEW' OR sv.sssnStatusCd='REVIEW' OR sv.sssnStatusCd='PENDING' OR sv.sssnStatusCd='ERROR')");
				}else{
					filters.append(" (sv.sssnStatusCd='"+statusFilter+"')");
				}
			}
			
			// MANUAL CHANGES
			
			StringBuffer mnlChanges=new StringBuffer();
			
			if (ovrrdFilter != null && ovrrdFilter.booleanValue()) {
				mnlChanges.append("(sv.spRqOvrrdFl IS NOT NULL or sv.muniRqOvrrdFl IS NOT NULL or sv.opRqOvrrdFl IS NOT NULL or sv.ffnRqOvrrdFl IS NOT NULL or sv.fnRqOvrrdFl IS NOT NULL or sv.flRqOvrrdFl IS NOT NULL or sv.ancRqOvrrdFl IS NOT NULL)");
			}
			// If 'manual override' filter is present, skip 'has required' filter to produce expected results. Overwise, records with reqFl=false & manualOverrideFl=true will be omitted.
			if (hasRqFilter != null && hasRqFilter.booleanValue() && !(ovrrdFilter != null && ovrrdFilter.booleanValue())) {
				if(mnlChanges.length()>0) mnlChanges.append(" AND ");
				//mnlChanges.append("((COALESCE(sv.spRqOvrrdFl, sv.spRqFl) IS TRUE ) or (COALESCE(sv.muniRqOvrrdFl, sv.muniRqFl) IS TRUE) or (COALESCE(sv.opRqOvrrdFl, sv.opRqFl) IS TRUE) or (COALESCE(sv.ffnRqOvrrdFl, sv.ffnRqFl) IS TRUE) or (COALESCE(sv.fnRqOvrrdFl, sv.fnRqFl) IS TRUE) or (COALESCE(sv.flRqOvrrdFl, sv.flRqFl) IS TRUE) or (COALESCE(sv.ancRqOvrrdFl, sv.ancRqFl) IS TRUE))");
				mnlChanges.append(" EXISTS (SELECT 1 FROM PsApplicableCmp sac WHERE sac.session.id=sv.sssnId AND (COALESCE(sac.requiredOvrd, sac.required) IS TRUE))");
			}
			
			if (hasNmaFilter != null && hasNmaFilter.booleanValue()) {
				if(mnlChanges.length()>0) mnlChanges.append(" AND ");
				mnlChanges.append("(sv.spNmaCd IS NOT NULL or sv.muniNmaCd IS NOT NULL or sv.opNmaCd IS NOT NULL or sv.ffnNmaCd IS NOT NULL or sv.fnNmaCd IS NOT NULL or sv.flNmaCd IS NOT NULL or sv.ancNmaCd IS NOT NULL)");
			}
			
			if(mnlChanges.length()>0){
				if(filters.length()>0) filters.append(" AND ");
				filters.append("("+mnlChanges.toString()+")");
			}
			
			// TYPES
			int numOfTypes=0;
			StringBuffer cmpTypes=new StringBuffer();
			
			if (spFilter != null && spFilter.booleanValue()){
				if(cmpTypes.length()>0) cmpTypes.append(", ");
				cmpTypes.append("'SALES_PRACTICE'");
				numOfTypes++;
			}
			if (muniFilter != null && muniFilter.booleanValue()){
				if(cmpTypes.length()>0) cmpTypes.append(", ");
				cmpTypes.append("'MUNICIPAL'");
				numOfTypes++;
			}
			if (opFilter != null && opFilter.booleanValue()){
				if(cmpTypes.length()>0) cmpTypes.append(", ");
				cmpTypes.append("'OPTIONS'");
				numOfTypes++;
			}
			if (ffnFilter != null && ffnFilter.booleanValue()){
				if(cmpTypes.length()>0) cmpTypes.append(", ");
				cmpTypes.append("'FIRST_FINOP'");
				numOfTypes++;
			}
			if (fnFilter != null && fnFilter.booleanValue()){
				if(cmpTypes.length()>0) cmpTypes.append(", ");
				cmpTypes.append("'FINOP'");
				numOfTypes++;
			}
			if (flFilter != null && flFilter.booleanValue()){
				if(cmpTypes.length()>0) cmpTypes.append(", ");
				cmpTypes.append("'FLOOR_REVIEW'");
				numOfTypes++;
			}
			if (ancFilter != null && ancFilter.booleanValue()){
				if(cmpTypes.length()>0) cmpTypes.append(", ");
				cmpTypes.append("'ANC'");
				numOfTypes++;
			}
			if (sdfFilter != null && sdfFilter.booleanValue()){
				if(cmpTypes.length()>0) cmpTypes.append(", ");
				cmpTypes.append("'SDF'");
				numOfTypes++;
			}
			if (rsaFnFilter != null && rsaFnFilter.booleanValue()){
				if(cmpTypes.length()>0) cmpTypes.append(", ");
				cmpTypes.append("'RSA_FINOP'");
				numOfTypes++;
			}
			if (rsaSpFilter != null && rsaSpFilter.booleanValue()){
				if(cmpTypes.length()>0) cmpTypes.append(", ");
				cmpTypes.append("'RSA_SALES_PRACTICE'");
				numOfTypes++;
			}
			if (muniAdvFilter != null && muniAdvFilter.booleanValue()){
				if(cmpTypes.length()>0) cmpTypes.append(", ");
				cmpTypes.append("'MUNICIPAL_ADVISOR'");
				numOfTypes++;
			}
			

			
			if(cmpTypes.length()>0){
				if(filters.length()>0) filters.append(" AND");
				filters.append(" ("+numOfTypes+"=(SELECT COUNT(*) FROM PsApplicableCmp ac WHERE ac.session.id=sv.sssnId AND ac.cmp.id IN ("+cmpTypes.toString()+"))) ");
			}			
			
			hql += " WHERE " + filters.toString();
			if(filters.length()>0) hql += " AND";
			hql += " sv.versionId="+vrsnId;
			
		}

		return hql;
	}

	@Override
	@Transactional
	public int getPsExamsCount(GridRequest rq) {

		String hql = "SELECT COUNT(*) ";
		hql+=getPsExamsHQL(rq);

		Query query = getSession().createQuery(hql);
		Long result = (Long) query.uniqueResult();

		return result != null ? result.intValue() : 0;
	}

	@Override
	@Transactional
	public List<PsOvrdReason> getOvrdReasons(boolean isMr) {
		//@Vruzha: this convention is stupid, should be the over way around
		// [6/29/2015 4:19 PM] Arellano, Rene: 
		//	- MN reasons apply when a Not Required is moved to Required
		//	- MR reasons apply when a Required is moved to Not Required

		
		String type=isMr ? "MN" : "MR";
		Query query = getSession().createQuery("FROM PsOvrdReason r WHERE r.type=:type ORDER BY r.desc ASC").setParameter("type", type);
		List<PsOvrdReason> result=query.list();
		return result;
	}
	
	@Override
	@Transactional
	public PsSchedSessionResponse schedPsSessions(PsSchedSessionRequest rq, User schedUser) throws Exception{
		log.debug(rq.toString());
		PsSchedSessionResponse response = new PsSchedSessionResponse();
		response.setSessionResponses(new ArrayList<ScheduleSessionResponse>());

		List<PsSchedSessionView> examRqs=rq.getSessions();
		if(examRqs==null || examRqs.isEmpty())
			return response;
		
		Iterator<PsSchedSessionView> eIt=examRqs.iterator();
		while(eIt.hasNext()){
			ScheduleSessionResponse scheduleSessionResponse = new ScheduleSessionResponse();
			scheduleSessionResponse.setErrorInfoJsonList(new ArrayList<ErrorInfoJson>());
			PsSchedSessionView eRq = eIt.next();
			try {

				scheduleSessionResponse.setSssnId(eRq.getSssnId());

				scheduleSession(eRq, schedUser);

				scheduleSessionResponse.setStatus(ReturnCode.STATUS_OK);
			} catch (Exception ex){
				if(ex instanceof ExamSubTypeInvalidException)
					scheduleSessionResponse.setStatus(ReturnCode.STATUS_INVALID_SUBTYPE);
				else if (ex instanceof ExamTypeSubTypeInvalid)
					scheduleSessionResponse.setStatus(ReturnCode.STATUS_INVALID_EXAM_TYPE_SUBTYPE);
				else
					scheduleSessionResponse.setStatus(ReturnCode.STATUS_ERROR);
				deleteInvalidSessions(eRq.getSssnId());
			}
			response.getSessionResponses().add(scheduleSessionResponse);
		}
		return response;
	}


	public void deleteInvalidSessions(int firmSssnId) throws Exception {
		SQLQuery updateQuery = getSession().createSQLQuery(
				"delete FROM schdl_otpt " +
						" WHERE firm_sssn_id = :firmSssnId and mbr_mkt_type_cd is null");
		updateQuery.setInteger("firmSssnId", firmSssnId);
		int count = updateQuery.executeUpdate();
		getSession().flush();
	}

	@Override
	@Transactional
	public List<PsDistrictView> getDistricts(){
		
		String query = "select d.dstrt_id as id, d.dstrt_cd as code, d.dstrt_nm as name from staff_dstrt_grp_vw d where d.ew_dstrt_fl = 'Y' " +
				"order by dstrt_nm " ;
		SQLQuery q = getSession().createSQLQuery(query);
		q.addEntity(PsDistrictView.class);
		
		List<PsDistrictView> result=q.list();
		return result;
	}
	
	@Override
	@Transactional
	public PsDistrictView getUserDistrict(User user){
		SQLQuery q = getSession().createSQLQuery("select d.dstrt_id as id, d.dstrt_cd as code, d.dstrt_nm as name from dstrt d where d.dstrt_id="+user.getDistrictId()+" AND d.actv_fl = 'Y'");
		q.addEntity(PsDistrictView.class);
		
		PsDistrictView result=(PsDistrictView)q.uniqueResult();
		return result;
	}
	
	@Override
	@Transactional
	public List<PsSessionStatus> getStatuses(){
		Query query = getSession().createQuery("FROM PsSessionStatus r ORDER BY r.priorityId ASC");
		List<PsSessionStatus> result=query.list();
		return result;
	}
	
	
	private Long getMttrTypeIdByCmpntCd(PsApplicableCmp type) throws Exception{
		
		if (type == null) return null;
		
		String sql=""
		+"  select M.MTTR_TYPE_ID"
		+ " from SCHDL_EXAM_TYPE_CMPNT_MAP M"
		+ " where M.genrt_fl='Y' AND M.SCHDL_CMPNT_CD = '"+type.getCmp().getId()+"'";
		
		SQLQuery query = getSession().createSQLQuery(sql);
		BigDecimal result =null;
		
		try{
			result=(BigDecimal) query.uniqueResult();
		}catch(NonUniqueResultException ex){
			throw new RuntimeException("Non unique result for the given type code for Component Id ="+type.getCmp().getId());
		}
		 
		if(result==null)
			throw new RuntimeException("MttrTypeId is NULL for Component Id:"+type.getCmp().getId()+" and Type Code:"+type.getCmp().getType().toString());
		
		return result!=null ? new Long(result.longValue()): null;
	}

	
	private String getExamTypeCdByCmpntCd(PsApplicableCmp type) throws Exception{
		
		if (type == null) return null;
		
		String sql=""
		+"  select M.EXAM_TYPE_CD"
		+ " from SCHDL_EXAM_TYPE_CMPNT_MAP M"
		+ " where M.genrt_fl='Y' AND M.SCHDL_CMPNT_CD = '"+type.getCmp().getId()+"'";
		
		SQLQuery query = getSession().createSQLQuery(sql);
		String result =null;
		
		try{
			result=(String) query.uniqueResult();
		}catch(NonUniqueResultException ex){
			throw new RuntimeException("Non unique result of Exam Type Cd for the given Component Id ="+type.getCmp().getId()+" and Type Code:"+type.getCmp().getType().toString());
		}
		 
		if(result==null)
			throw new RuntimeException("Exam Type Cd is NULL for Component Id:"+type.getCmp().getId()+" and Type Code:"+type.getCmp().getType().toString());
		
		return result;
	}

	
	private Long getMttrSubTypeIdByCmpntCds(List<PsApplicableCmp> types) throws Exception{
		
		if (types == null || (types != null && types.size()==0))
			return null;
		
		StringBuffer mttrSubTypesCDs = new StringBuffer();
		Iterator<PsApplicableCmp> stIt = types.iterator();

		while (stIt.hasNext()) {
			PsApplicableCmp st = stIt.next();
			if (mttrSubTypesCDs.length() > 0){
				mttrSubTypesCDs.append(", ");
			}
			mttrSubTypesCDs.append("'"+st.getCmp().getId()+"', 1");
		}
		
		
		String sql=""
				+" select t.id"
				+" from ("
				+" 		Select"
							+" M.MTTR_SUB_TYPE_ID id,"
							+" sum(decode(M.SCHDL_CMPNT_CD, "+ (mttrSubTypesCDs.toString()) +", 0)) found_cmp,"
							+" count(*) cnt_cmp"
						+" From SCHDL_EXAM_SUB_TYPE_CMPNT_MAP M"
						+" where M.genrt_fl='Y'"
						+" Group By M.MTTR_SUB_TYPE_ID) t"
				+" where t.found_cmp = cnt_cmp"
				+" and t.found_cmp = "+types.size();
		
		SQLQuery query = getSession().createSQLQuery(sql);
		BigDecimal result =null;
		
		// List of types in case have to show error
		Iterator<PsApplicableCmp> cmpIt=types.iterator();
		StringBuffer sb=new StringBuffer();
		while(cmpIt.hasNext()){
			if(sb.length()>0) sb.append(", ");
			sb.append(cmpIt.next().getCmp().getType().toString());
		}
		
		try{
			result=(BigDecimal) query.uniqueResult();
		}catch(NonUniqueResultException ex){
			throw new RuntimeException("Non unique result for the given set of sub types codes for Session Id: "+types.get(0).getSession().getId()+ " and Type Codes: "+sb.toString());
		}
		
		if(result==null){
			throw new ExamSubTypeInvalidException("MttrSubTypeId is NULL for Session Id:"+types.get(0).getSession().getId()+ " and Type Codes: "+sb.toString());
		}
		
		return result!=null ? new Long(result.longValue()): null;
	}
	
	private String getExamSubTypeIdByCmpntCds(List<PsApplicableCmp> types) throws Exception{
		
		if (types == null || (types != null && types.size()==0)) return null;
		
		StringBuffer mttrSubTypesCDs = new StringBuffer();
		Iterator<PsApplicableCmp> stIt = types.iterator();

		while (stIt.hasNext()) {
			PsApplicableCmp st = stIt.next();
			if (mttrSubTypesCDs.length() > 0){
				mttrSubTypesCDs.append(", ");
			}
			mttrSubTypesCDs.append("'"+st.getCmp().getId()+"', 1");
		}
		
		
		String sql=""
				+" select t.cd"
				+" from ("
				+" 		Select"
							+" M.EXAM_SUB_TYPE_CD cd,"
							+" sum(decode(M.SCHDL_CMPNT_CD, "+ (mttrSubTypesCDs.toString()) +", 0)) found_cmp,"
							+" count(*) cnt_cmp"
						+" From SCHDL_EXAM_SUB_TYPE_CMPNT_MAP M"
						+" where M.genrt_fl='Y'"
						+" Group By M.EXAM_SUB_TYPE_CD) t"
				+" where t.found_cmp = cnt_cmp"
				+" and t.found_cmp = "+types.size();
		
		SQLQuery query = getSession().createSQLQuery(sql);
		String result =null;
		
		// List of types in case have to show error
		Iterator<PsApplicableCmp> cmpIt=types.iterator();
		StringBuffer sb=new StringBuffer();
		while(cmpIt.hasNext()){
			if(sb.length()>0) sb.append(", ");
			sb.append(cmpIt.next().getCmp().getType().toString());
		}
		
		try{
			result=(String) query.uniqueResult();
		}catch(NonUniqueResultException ex){
			throw new RuntimeException("Non unique result for the given set of sub types codes for Session Id:"+types.get(0).getSession().getId()+ " and Type Codes: "+sb.toString());
		}
		
		if(result==null){
			throw new ExamSubTypeInvalidException("ExamSubTypeCd is NULL for Session Id:"+types.get(0).getSession().getId()+ " and Type Codes: "+sb.toString());
		}
		
		return result!=null ? result: null;
	}	
	
	@Override
	@Transactional
	public List<PsSession> getListForReprocessing(int versionId, String status) {

		log.info("getListForReprocessing(" + versionId + ").");

		// Load Firms, Components...
		List<PsSession> retVal = (List<PsSession>) getSession()
				.createQuery("FROM PsSession s WHERE s.firm.id.versionId = :versionId and s.status.id = :status order by s.id ASC")
				.setParameter("status", status)
				.setParameter("versionId", versionId)
				.list();
		
		Iterator<PsSession> it=retVal.iterator();
		while(it.hasNext()){
			PsSession sssn=it.next();
			sssn.getExams().iterator();
			sssn.getaCmps().iterator();
		}
		return retVal;
	}
	
	@Override
	@Transactional
	public PsFirmStaffDistrict getFirmStaff(int firmId) {

		log.info("getFirmStaff(" + firmId + ").");

		// Load Firms, Components...
		PsFirmStaffDistrict retVal = (PsFirmStaffDistrict) getSession()
				.createQuery("FROM PsFirmStaffDistrict f WHERE f.firmId = :firmId")
				.setParameter("firmId", firmId).uniqueResult();
		return retVal;
	}
	

	@Override
	@Transactional
	public void saveExamWorkspaceSyncLog(PsExamWorkspaceSyncLog bean) {

		getSession().saveOrUpdate(bean);
		getSession().flush();

	}	
	

	@Override
	@Transactional
	public int loadInputData(int versionId) {

		int retVal;
		try {
			getEntityManager()
					.createStoredProcedureQuery("schdl_load_input_data_sp")
					.registerStoredProcedureParameter("pi_schdl_exam_snpsh_id",
							Long.class, ParameterMode.IN)
					.setParameter("pi_schdl_exam_snpsh_id", Long.valueOf(versionId))
					.executeUpdate();
			retVal = 1;
			log.info("Input Data Loaded");
		} catch (Exception e) {
			retVal = -1;
			
		}

		return retVal;
	}
	
	@Override
	@Transactional
	public int loadRSANMAData(int versionId) {

		int retVal;
		try {
			getEntityManager()
					.createStoredProcedureQuery("schdl_load_nma_firms_sp")
					.registerStoredProcedureParameter("pi_schdl_exam_snpsh_id",
							Long.class, ParameterMode.IN)
					.setParameter("pi_schdl_exam_snpsh_id", Long.valueOf(versionId))
					.executeUpdate();
			retVal = 1;
			log.info("RSANMA Data Loaded");
		} catch (Exception e) {
			retVal = -1;
			log.error(e.getMessage());
		}

		return retVal;
	}


	

	private void approveSP(Integer sssnId, User approveUser, Boolean state) {
		PsSession sssn = (PsSession) getSession().get(
				PsSession.class, sssnId);
		
		if(sssn!=null){
			
			Date approveDate=new Date();
			
			if(sssn.getFirm().getSpDistrictTypeCode().equalsIgnoreCase(sssn.getFirm().getFnDistrictTypeCode())){
				// If state is not provided - just flip it. Uf provided, set to specific state
				sssn.setFnApprovedFl(state!=null? state.booleanValue() : !sssn.isFnApprovedFl());
				sssn.setFnApproveUser(approveUser);
				sssn.setFnApproveDt(approveDate);
			}
			
			// If state is not provided - just flip it. Uf provided, set to specific state
			sssn.setSpApprovedFl(state!=null? state.booleanValue() : !sssn.isSpApprovedFl());
			sssn.setSpApproveUser(approveUser);
			sssn.setSpApproveDt(approveDate);
			
			PsSessionStatus status=setStatus(sssn);
			sssn.setStatus(status);
			getSession().saveOrUpdate(sssn);
			getSession().flush();
		}else{
			throw new RuntimeException("Unable to find a Session with ID="+sssnId.toString());
		}
	}
	
	private void approveFN(Integer sssnId, User approveUser, Boolean state) {
		PsSession sssn = (PsSession) getSession().get(
				PsSession.class, sssnId);
		if(sssn!=null){
			
			Date approveDate=new Date();
			
			if(sssn.getFirm().getSpDistrictTypeCode().equalsIgnoreCase(sssn.getFirm().getFnDistrictTypeCode())){
				// If state is not provided - just flip it. Uf provided, set to specific state
				sssn.setSpApprovedFl(state!=null? state.booleanValue() : !sssn.isSpApprovedFl());
				sssn.setSpApproveUser(approveUser);
				sssn.setSpApproveDt(approveDate);
			}
			
			// If state is not provided - just flip it. Uf provided, set to specific state
			sssn.setFnApprovedFl(state!=null? state.booleanValue() : !sssn.isFnApprovedFl());
			sssn.setFnApproveUser(approveUser);
			sssn.setFnApproveDt(approveDate);

			PsSessionStatus status=setStatus(sssn);
			sssn.setStatus(status);

			getSession().saveOrUpdate(sssn);
			getSession().flush();
		}else{
			throw new RuntimeException("Unable to find a Session with ID="+sssnId.toString());
		}
		
	}

	@Override
	@Transactional
	public void setSessionStatus(Integer sssnId, String status) {
		PsSession sssn = (PsSession) getSession().load(
				PsSession.class, sssnId);
		
		if(sssn!=null){
			
			sssn.setStatus(new PsSessionStatus(status));
			getSession().saveOrUpdate(sssn);
			getSession().flush();
		}else{
			throw new RuntimeException("Unable to find a Session with ID="+sssnId.toString());
		}
	}

	@Override
	@Transactional
	public PsSession getPsSessionById(Integer sssnId) {	
		PsSession sssn=(PsSession) getSession().get(PsSession.class, sssnId);
		sssn.getExams().iterator();
		
		return sssn;
	}



	@Override
	@Transactional
	public List<PsFirmBillableEntity> getFirmBillableEntity(int firmId, int versionId, String spRequirement, String finopRequirement) {

		log.info("getFirmBillableEntity(" + firmId + " " + versionId + ")");
		
		String query = "FROM PsFirmBillableEntity f WHERE f.firmId = :firmId " + 
						"AND f.versionId = :versionId " + 
						"AND (f.spRequirement is null OR f.spRequirement = :spRequirement) AND (f.finopRequirement is null OR f.finopRequirement = :finopRequirement) " ;
		List<PsFirmBillableEntity> retVal = (List<PsFirmBillableEntity>) getSession()
				.createQuery(query)
				.setParameter("firmId", firmId)
				.setParameter("versionId", versionId)
				.setParameter("spRequirement", spRequirement)
				.setParameter("finopRequirement", finopRequirement)
				.list();
		return retVal;
	}




	@Override
	@Transactional
	public PsFirm getPsFirmById(Integer firmId) {
		PsFirm firm=(PsFirm) getSession().get(PsFirm.class, new PsFirmPK(firmId.intValue(), getCurrentVersion()));
		Iterator<PsSession> it=firm.getSessions().iterator();
		while(it.hasNext()){
			PsSession sssn=it.next();
			sssn.getaCmps().iterator();
		}
		
		return firm;
	}


	@Override
	@Transactional
	public void saveEmailNotificationsLog(PsEmailNotificationsLog bean) {
		getSession().saveOrUpdate(bean);
		getSession().flush();
	}	

	@Override
	@Transactional
	public String getExamComponentFromMatterType(String component, int matterType){
		
		String sql=""
		+ "	SELECT CASE WHEN COUNT (1) > 0 THEN 'Y' ELSE 'N' END ret_val"
		+ " FROM schdl_exam_type_cmpnt_map"
		+ "	WHERE schdl_cmpnt_cd = :component AND mttr_type_id = :matterType";
		
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("component", component);
		query.setParameter("matterType", matterType);
		Character result =null;
		
		try{
			result = (Character) query.uniqueResult();
		}catch(NonUniqueResultException ex){
			throw new RuntimeException("Non unique result of ExamComponentFromMatterType " +  component + ", " + matterType);
		}
		 
		if(result==null) throw new RuntimeException("Exam Type Cd is NULL for Component Id:" +  component + ", " + matterType);
		
		return result.toString();
	}	

	@Override
	@Transactional
	public String getExamComponentFromMatterSubType(String component, int matterSubType){
		
		String sql=""
		+ "	SELECT CASE WHEN COUNT (1) > 0 THEN 'Y' ELSE 'N' END ret_val"
		+ " FROM schdl_exam_sub_type_cmpnt_map"
		+ "	WHERE schdl_cmpnt_cd = :component AND mttr_sub_type_id = :matterSubType";
		
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("component", component);
		query.setParameter("matterSubType", matterSubType);
		Character result =null;
		
		try{
			result=(Character) query.uniqueResult();
		}catch(NonUniqueResultException ex){
			throw new RuntimeException("Non unique result of ExamComponentFromMatterSubType " +  component + ", " + matterSubType);
		}
		 
		if(result==null) throw new RuntimeException("Exam Sub Type Cd is NULL for Component Id:" +  component + ", " + matterSubType);
		
		return result.toString();
	}	
	
	@Override
	@Transactional
	public boolean isRoleAccess(long appUserId, long roleId) throws Exception{
		boolean roleAccess = false;
		String sql="select count(*) from aplcn_user_role where aplcn_role_id = :roleId and aplcn_user_id = :appUserId";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("appUserId", appUserId);
		query.setParameter("roleId", roleId);
		BigDecimal result=(BigDecimal) query.uniqueResult();
		if(result != null && result.longValue() > 0)
			roleAccess = true;
		
		return roleAccess;
	}
	
	@Override
	@Transactional
	public List<PsMatterDate> getBranchCauseMatterDate(Integer spExamId){
		
		String sql=""
		+ "	SELECT rownum id, star_date_name_id, star_date_type_id, event_date"
		+ "	FROM mlstn_event me"
		+ "	INNER JOIN mlstn_event_type met ON me.event_type_id = met.id"
		+ "	INNER JOIN exam_vw ev ON me.cntxt_key = ev.model_id"
		+ "	WHERE exam_id = :examId";
		SQLQuery q = getSession().createSQLQuery(sql);
		q.setParameter("examId", spExamId);
		q.addEntity(PsMatterDate.class);
		
		List<PsMatterDate> result=(List<PsMatterDate>)q.list();
		return result;
	}	

	@Override
	@Transactional
	public List<PsMatterStaff> getBranchCauseMatterStaff(Integer spExamId){
		
		String sql=""
				+ "	SELECT rownum id,aplcn_user_id, role_id, seda.prmry_fl prmry_fl"
				+ "	FROM staff_exam_dmn_asgnt seda"
				+ "	INNER JOIN exam_dmn ed ON seda.exam_dmn_id = ed.exam_dmn_id"
				+ "	WHERE exam_id = :examId and role_id = 5";
		SQLQuery q = getSession().createSQLQuery(sql);
		q.setParameter("examId", spExamId);
		q.addEntity(PsMatterStaff.class);
		
		List<PsMatterStaff> result=(List<PsMatterStaff>)q.list();
		return result;
	}

	@Override
	@Transactional
	public PsMatterStarData getBranchCauseMatterStarData(Integer spExamId){
		
		String sql=""
		+ "	SELECT m.mttr_id, m.rglty_sgnfc_id, m.rcvd_dt"
		+ "	FROM exam_vw ev INNER JOIN mttr m ON ev.model_id = m.mttr_id"
		+ "	WHERE exam_id = :examId";
		SQLQuery q = getSession().createSQLQuery(sql);
		q.setParameter("examId", spExamId);
		q.addEntity(PsMatterStarData.class);
		PsMatterStarData result=(PsMatterStarData) q.uniqueResult();
		return result;
	}	

	@Override
	@Transactional
	public PsMarketMatterMetaData getMarketMatterMetaData(String marketTypeCode) {

		log.info("getListForReprocessing");

		PsMarketMatterMetaData retVal = (PsMarketMatterMetaData) getSession()
				.createQuery("FROM PsMarketMatterMetaData where marketTypeCode = :marketTypeCode")
				.setParameter("marketTypeCode", marketTypeCode)
				.uniqueResult();
		
		return retVal;
	}

	@Override
	@Transactional
	public void resetMarketSessions(String marketTypeCode) throws Exception {
		SQLQuery updateQuery = getSession().createSQLQuery(
				"UPDATE schdl_firm_sssn " +
						"   SET schdl_sssn_stts_cd = 'NEW' " +
						" WHERE firm_sssn_id IN (SELECT firm_sssn_id " +
						"                          FROM schdl_otpt " +
						"                         WHERE     mbr_mkt_type_cd = :markettypecode " +
						"                               AND mttr_id IS NULL " +
						"                               AND exam_id IS NULL) ");
		updateQuery.setString("markettypecode", marketTypeCode);
		updateQuery.executeUpdate();
		getSession().flush();
	}
	@Override
	@Transactional
	public List<PsSession> getMarketListForReprocessing(int versionId) {

		log.info("getMarketListForReprocessing(" + versionId + ").");

		// Load Firms, Components...
		List<PsSession> retVal = (List<PsSession>) getSession()
				.createQuery("FROM PsSession s WHERE s.firm.id.versionId = :versionId "
						+ "order by s.id ASC")
				.setParameter("versionId", versionId)
				.list();
		
		Iterator<PsSession> it=retVal.iterator();
		while(it.hasNext()){
			PsSession sssn=it.next();
			sssn.getExams().iterator();
			sssn.getaCmps().iterator();
		}
		return retVal;
	}	

	@Override
	@Transactional
	public PsFirmStaffMarket getFirmStaffMarket(int firmId, String marketTypeCode) {

		log.info("getFirmStaff(" + firmId + ").");

		PsFirmStaffMarket retVal = (PsFirmStaffMarket) getSession()
				.createQuery("FROM PsFirmStaffMarket f WHERE f.firmId = :firmId and f.marketTypeCode = :marketTypeCode")
				.setParameter("firmId", firmId)
				.setParameter("marketTypeCode", marketTypeCode)
				.uniqueResult();
		return retVal;
	}	
	
	@Override
	@Transactional
	public List<PsFirmMarketBillableEntity> getFirmMarketBillableEntity(int firmId, String marketTypeCode) {

		log.info("getFirmMarketBillableEntity(" + firmId + ").");

		// Load Firms, Components...
		List<PsFirmMarketBillableEntity> retVal = (List<PsFirmMarketBillableEntity>) getSession()
				.createQuery("FROM PsFirmMarketBillableEntity f WHERE f.firmId = :firmId and f.marketTypeCode = :marketTypeCode")
				.setParameter("firmId", firmId)
				.setParameter("marketTypeCode", marketTypeCode)
				.list();
		return retVal;
	}
	
	@Override
	@Transactional
	public List<PsExamCategoryType> getExamCategories(){
		
		String sql="" +
				"  SELECT ecl.exam_ctgry_cd, exam_ctgry_ds "+
				"    FROM exam_ctgry_lk ecl "+
				"         WHERE ecl.exam_ctgry_cd in ('CYCLE', 'CAUSE') "+
				"ORDER BY exam_ctgry_ds desc ";
		SQLQuery q = getSession().createSQLQuery(sql);
		q.addEntity(PsExamCategoryType.class);
		List<PsExamCategoryType> result=(List<PsExamCategoryType>) q.list();
		return result;
	}	
	
	@Override
	@Transactional
	public List<PsExamTypeType> getExamTypes(){
		
		String sql="" +
				"SELECT etl.exam_type_cd || etecl.exam_ctgry_cd exam_type_id, " +
			    "   etl.exam_type_cd, " +
			    "  etl.exam_type_ds, " +
			    "   etecl.exam_ctgry_cd " +
				"FROM exam_type_lk etl " +
			    "   INNER JOIN exam_type_ctgry_mttr_ctgry etecl " +
			    "      ON etl.exam_type_cd = etecl.exam_type_cd "+
			    "     AND etl.actv_fl = 'Y' "+
			    "     AND etl.exam_type_cd not in (select exam_type_cd from schdl_xcld_exam_type) "+				
				"ORDER BY UPPER(etl.exam_type_ds) ";
		SQLQuery q = getSession().createSQLQuery(sql);
		q.addEntity(PsExamTypeType.class);
		List<PsExamTypeType> result=(List<PsExamTypeType>) q.list();
		return result;
	}		
	
	@Override
	@Transactional
	public List<PsExamSubTypeType> getExamSubTypes(){
		
		String sql="" +
				"SELECT estl.exam_sub_type_cd || estetl.exam_type_cd exam_sub_type_id, estl.exam_sub_type_cd, estl.exam_sub_type_ds, estetl.exam_type_cd "+
				"  FROM exam_sub_type_lk estl "+
				"       INNER JOIN exam_type_exam_sub_type_lk estetl "+
				"          ON estl.exam_sub_type_cd = estetl.exam_sub_type_cd "+
				"         AND estl.actv_fl = 'Y' "+
			    "         AND estl.exam_sub_type_cd not in (select exam_sub_type_cd from schdl_xcld_exam_sub_type) "+								
				"ORDER BY UPPER(estl.exam_sub_type_ds) ";
		SQLQuery q = getSession().createSQLQuery(sql);
		q.addEntity(PsExamSubTypeType.class);
		List<PsExamSubTypeType> result=(List<PsExamSubTypeType>) q.list();
		return result;
	}		
	
	@Override
	@Transactional
	public List<PsRegulatorySignificance> getRegulatorySignificance(){
		
		String sql="" +
				"SELECT rglty_sgnfc_id, rglty_sgnfc_ds " +
				"FROM rglty_sgnfc_lk WHERE rglty_sgnfc_id > 3 ";
		SQLQuery q = getSession().createSQLQuery(sql);
		q.addEntity(PsRegulatorySignificance.class);
		List<PsRegulatorySignificance> result=(List<PsRegulatorySignificance>) q.list();
		return result;
	}	
	
	@Override
	@Transactional
	public Integer getMatterTypeId(String examTypeCd){

		String sql="select mttr_type_id from mttr_type_lk where upper(mttr_type_cd) = upper(:examTypeCd)";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("examTypeCd", examTypeCd);
		
		BigDecimal result=(BigDecimal) query.uniqueResult();
		
		return result.intValue();
	}

	@Override
	@Transactional
	public Integer getMatterSubTypeId(String examSubTypeCd){

		String sql="select mttr_sub_type_id from mttr_sub_type_lk where mttr_sub_type_cd = :examSubTypeCd";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("examSubTypeCd", examSubTypeCd);
		
		BigDecimal result=(BigDecimal) query.uniqueResult();
		
		if (result == null)
			return null;
		else
			return result.intValue();
	}
	
	@Override
	@Transactional
	public String getDistrictCode(Integer districtId){

		String sql="select dstrt_cd from dstrt where dstrt_id = :districtId";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("districtId", districtId);
		
		String result=(String) query.uniqueResult();
		
		return result;
	}	
	
	@Override
	@Transactional
	public BigDecimal getExamDomainId(Integer examId){

		String sql="select dmn_id from exam_dmn where exam_id = :examId";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("examId", examId);
		
		BigDecimal result=(BigDecimal) query.uniqueResult();
		
		return result;
	}		
	
	@Override
	@Transactional
	public String getUserId(Integer applicationUserId){

		String sql="select user_nm from aplcn_user where aplcn_user_id = :applicationUserId";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("applicationUserId", applicationUserId);
		
		String result=(String) query.uniqueResult();
		
		return result;
	}	

	@Override
	@Transactional
	public String getAnnualPlanningPhase(){

		String sql="select phase_cd from schdl_annl_plng_phase";
		SQLQuery query = getSession().createSQLQuery(sql);
		
		String result=(String) query.uniqueResult();
		
		return result;
	}

	@Override
	@Transactional
	public void saveOutput(PsOutput bean) throws Exception{
		PsOutput psOutput = getEntityManager().find(PsOutput.class, bean.getOutputId());
		psOutput.setExamId(bean.getExamId());
		psOutput.setMatterId(bean.getMatterId());
		saveBean(psOutput);
	}

	/***
	 * This method is used to save the entire data for session and set of components. Please send only a value if y
	 * want it to be considered for save. if "" is sent then value will be reset.
	 * @param saveFirmSessionRequest
	 * @throws Exception
     */
	@Transactional
	public PsSessionView saveFirmSession(SaveFirmSessionRequest saveFirmSessionRequest) throws Exception {
		// Save session
		User user = UserContext.getUser();

		PsSession sssn = getEntityManager().find(PsSession.class, saveFirmSessionRequest.getId().intValue());

		// EXAM-10604
		// Allow approval/disapproval only if Session is NOT in ERROR or SCHEDULED status
		if(sssn.hasExamStatus(PS_STATUS_TYPE.SCHED) || sssn.hasExamStatus(PS_STATUS_TYPE.ERROR))
			throw new PsSessionStatusException("session is in invalid state.");


		//saving the component
		//Iterate through all the components and save all the individual components before saving the session object.
		//If we are overriding the required flag then we store the values for date, reason, business review text , user
		//etc. We also reset the approval flags if we are saving the value for required flag.
		if(saveFirmSessionRequest.getComponentRequestList() != null){
			for(SaveComponentRequest cmpRequest : saveFirmSessionRequest.getComponentRequestList()){
					PsApplicableCmp aCmpnt = getEntityManager().find(PsApplicableCmp.class, cmpRequest.getId());
					//if there is no override and we are
					if(aCmpnt.isRequired() == cmpRequest.getReqrdOvrrdFl().booleanValue()){
						aCmpnt.setRequiredOvrd(null);
						aCmpnt.setOvrdDate(null);
						aCmpnt.setOvrdUser(null);
						aCmpnt.setOvrdReason(null);
						aCmpnt.setBusinessReviewText(null);
					}else{
						aCmpnt.setRequiredOvrd(cmpRequest.getReqrdOvrrdFl());
						aCmpnt.setOvrdDate(new Date());
						aCmpnt.setOvrdUser(user);
						aCmpnt.setOvrdReason(getEntityManager().find(PsOvrdReason.class, cmpRequest.getOvrdReasonId()));
						aCmpnt.setBusinessReviewText(cmpRequest.getBusinessReviewText());
					}

				if(aCmpnt.getCmp().getRespDistrType() == RESP_DISTR_TYPE.SP)
					saveFirmSessionRequest.setSpApprovedFl(false);
				else if(aCmpnt.getCmp().getRespDistrType() == RESP_DISTR_TYPE.FN)
					saveFirmSessionRequest.setFnApprovedFl(false);
				else
					saveFirmSessionRequest.setFlApprovedFl(false);
				saveBean(aCmpnt);
			}
		}

		//saving the floor district code and type code
		if(saveFirmSessionRequest.getFlDistrictCd() != null) {
			saveFirmSessionRequest.setFlApprovedFl(false);//reset the the FL approved status
			sssn.setFlDistrictCd(saveFirmSessionRequest.getFlDistrictCd());
			if(saveFirmSessionRequest.getFlDistrictCd().equalsIgnoreCase("M1") || saveFirmSessionRequest.getFlDistrictCd().equalsIgnoreCase("M3"))
				sssn.setFlDistrictTypeCode("FN");
			else
				sssn.setFlDistrictTypeCode("SP");
		}

		//saving the SP Approved flag
		if(saveFirmSessionRequest.isSpApprovedFl() != null )
			setResetApprovalFlags(sssn, "sp", saveFirmSessionRequest.isSpApprovedFl().booleanValue());

		//saving the FN Approved flag
		if(saveFirmSessionRequest.isFnApprovedFl() != null )
			setResetApprovalFlags(sssn, "fn", saveFirmSessionRequest.isFnApprovedFl().booleanValue());

		//saving the FN Approved flag
		if(saveFirmSessionRequest.getFlApprovedFl() != null )
			setResetApprovalFlags(sssn, "fl", saveFirmSessionRequest.getFlApprovedFl().booleanValue());


		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
		df.setLenient(false);

		//saving the Field work start date
		String fwsdDt = saveFirmSessionRequest.getFwsdDt();
		if(fwsdDt != null)
			sssn.setPrjFwsd(fwsdDt.equalsIgnoreCase("") ? null : df.parse(fwsdDt));


		//saving the onsite/examwork start date
		String ewsdDt = saveFirmSessionRequest.getEwsdDt();
		if(ewsdDt != null)
			sssn.setPrjEwsd(ewsdDt.equalsIgnoreCase("") ? null : df.parse(ewsdDt));

		sssn.setStatus(setStatus(sssn));

		saveBean(sssn);

		return getEntityManager().find(PsSessionView.class, saveFirmSessionRequest.getId().intValue());

	}

	@Override
	public void saveBean(Object o) throws Exception {
		getSession().saveOrUpdate(o);
		getSession().flush();
	}

	@Override
	@Transactional
	public void saveBeanInTransaction(Object o) throws Exception {
		getSession().saveOrUpdate(o);
		getSession().flush();
	}

	public Integer saveComponentMap(PsTypeSubTypeComponentMap map) {
		getSession().saveOrUpdate(map);
		getSession().flush();
		//getSession().getTransaction().commit();
		return map.getTypeSubTypeComponentMapId();
	}

	//The logic is to reset the flags which also have same features
	public PsSession setResetApprovalFlags(PsSession sssn, String flagType, boolean setFlag) throws Exception{
		Date approveDate=new Date();
		User user = UserContext.getUser();
		List<String> districtCodeList = new ArrayList<>();

		switch (flagType){
			case "sp":
				sssn.setSpApprovedFl((setFlag ? true : false));
				sssn.setSpApproveDt((setFlag ? approveDate:null));
				sssn.setSpApproveUser((setFlag ? user : null));

				//if the sp district type code is same as Finop district type code we reset the fn flag same as sp
				if(getDistrictTypeCodeFromSession(sssn, "sp").equalsIgnoreCase(getDistrictTypeCodeFromSession(sssn, "fn"))){
					sssn.setFnApprovedFl((setFlag ? true : false));
					sssn.setFnApproveDt((setFlag ? approveDate:null));
					sssn.setFnApproveUser((setFlag ? user : null));
				}

				//check if Fl district code matches((SP or FINOP district code if district type code is same) else (SP district code if they are different))
				districtCodeList = getSPFinopDistrictCodeListBasedOnType(sssn, "sp");
				if(districtCodeList != null && !districtCodeList.isEmpty() && districtCodeList.contains(getDistrictCodeFromSession(sssn, "fl"))){
					sssn.setFlApprovedFl((setFlag ? true : false));
					sssn.setFlApproveDt((setFlag ? approveDate:null));
					sssn.setFlApproveUser((setFlag ? user : null));
				}
				break;
			case "fn":
				sssn.setFnApprovedFl((setFlag ? true : false));
				sssn.setFnApproveDt((setFlag ? approveDate:null));
				sssn.setFnApproveUser((setFlag ? user : null));

				//if the FINOP district type code is same as SP district type code we reset the sp flag same as FINOP
				if(getDistrictTypeCodeFromSession(sssn, "fn").equalsIgnoreCase(getDistrictTypeCodeFromSession(sssn, "sp"))){
					sssn.setSpApprovedFl((setFlag ? true : false));
					sssn.setSpApproveDt((setFlag ? approveDate:null));
					sssn.setSpApproveUser((setFlag ? user : null));
				}

				//check if Fl district code matches((SP or FINOP district code if district type code is same) else (FN district code if they are different))
				districtCodeList = getSPFinopDistrictCodeListBasedOnType(sssn, "fn");
				if(districtCodeList != null && !districtCodeList.isEmpty() && districtCodeList.contains(getDistrictCodeFromSession(sssn, "fl"))){
					sssn.setFlApprovedFl((setFlag ? true : false));
					sssn.setFlApproveDt((setFlag ? approveDate:null));
					sssn.setFlApproveUser((setFlag ? user : null));
				}
				break;
			case "fl":
				sssn.setFlApprovedFl((setFlag ? true : false));
				sssn.setFlApproveDt((setFlag ? approveDate:null));
				sssn.setFlApproveUser((setFlag ? user : null));

				//if district type code of SP and FINOP are same then SP and FINOP get approval if finop district code matches either SP of FINOP district
				//if the FINOP district type code is same as SP district type code we reset the sp flag same as FINOP
				if(getDistrictTypeCodeFromSession(sssn, "fn").equalsIgnoreCase(getDistrictTypeCodeFromSession(sssn, "sp"))) {
					districtCodeList = getSPFinopDistrictCodeListBasedOnType(sssn, "");//no need to send type because the district type code matches
					//it would return both sp and fn district codes.

					//fl district matches either sp or finop
					if(districtCodeList != null && !districtCodeList.isEmpty() && districtCodeList.contains(getDistrictCodeFromSession(sssn, "fl"))){
						sssn.setSpApprovedFl((setFlag ? true : false));
						sssn.setSpApproveDt((setFlag ? approveDate:null));
						sssn.setSpApproveUser((setFlag ? user : null));

						sssn.setFnApprovedFl((setFlag ? true : false));
						sssn.setFnApproveDt((setFlag ? approveDate:null));
						sssn.setFnApproveUser((setFlag ? user : null));
					}
				}else{

					//Give approval to SP if the district code of SP matches district code of FL
					if(getDistrictCodeFromSession(sssn, "sp").equalsIgnoreCase(getDistrictCodeFromSession(sssn, "fl"))){
						sssn.setSpApprovedFl((setFlag ? true : false));
						sssn.setSpApproveDt((setFlag ? approveDate:null));
						sssn.setSpApproveUser((setFlag ? user : null));
					}

					//Give approval to FINOP if the district code of SP matches district code of FL
					if(getDistrictCodeFromSession(sssn, "fn").equalsIgnoreCase(getDistrictCodeFromSession(sssn, "fl"))){
						sssn.setFnApprovedFl((setFlag ? true : false));
						sssn.setFnApproveDt((setFlag ? approveDate:null));
						sssn.setFnApproveUser((setFlag ? user : null));
					}
				}
				break;
			default:
				//do nothing by default
		}
		return sssn;
	}

	String getDistrictTypeCodeFromSession(PsSession sssn, String type){
		String districtTypeCode = "";
		switch (type.toLowerCase()) {
			case "sp":
				districtTypeCode = sssn.getFirm().getSpDistrictTypeCode();
				break;
			case "fn":
				districtTypeCode = sssn.getFirm().getFnDistrictTypeCode();
				break;
			default:
				districtTypeCode = sssn.getFlDistrictTypeCode();
				break;
		}
		districtTypeCode = districtTypeCode == null ? "" : districtTypeCode;
		return districtTypeCode;
	}

	String getDistrictCodeFromSession(PsSession sssn, String type){
		String districtCode = "";
		switch (type.toLowerCase()) {
			case "sp":
				districtCode = sssn.getFirm().getSpDistrictCode();
				break;
			case "fn":
				districtCode = sssn.getFirm().getFnDistrictCode();
				break;
			default:
				districtCode = sssn.getFlDistrictCd();
				break;
		}
		districtCode = districtCode == null ? "" : districtCode;
		return districtCode;
	}

	List<String> getSPFinopDistrictCodeListBasedOnType(PsSession sssn, String type) {
		List<String> distCodeList = new ArrayList<>();
		if(getDistrictTypeCodeFromSession(sssn, "sp").equalsIgnoreCase(getDistrictTypeCodeFromSession(sssn, "fn"))){
			distCodeList.add(getDistrictCodeFromSession(sssn, "sp"));
			distCodeList.add(getDistrictCodeFromSession(sssn, "sp"));
		}else
			distCodeList.add(getDistrictCodeFromSession(sssn, type));

		return distCodeList;
	}
	public PsSessionStatus setStatus(PsSession sssn) {
		PsSessionStatus status = (PsSessionStatus)getSession().get(PsSessionStatus.class, PsSessionStatus.PS_STATUS_TYPE.PENDING.toString());;

		if(sssn.isFnApprovedFl() && sssn.isSpApprovedFl() && sssn.isFlApprovedFl())
			status=(PsSessionStatus)getSession().get(PsSessionStatus.class, PsSessionStatus.PS_STATUS_TYPE.REVIEW.toString());

		return status;
	}


	@Override
	public ScheduleSessionResponse createExamsMatters(PsSchedSessionRequest psSchedSessionRequest) throws Exception {

		return null;
	}

	@Override
	@Transactional
	public List<PsTypeSubTypeComponentMapView> getPsTypeSubTypeComponentMapView() throws Exception {
		String hql = "FROM PsTypeSubTypeComponentMapView";
		Query query = getSession().createQuery(hql);
		List<PsTypeSubTypeComponentMapView> result = query.list();

		return result;
	}

	@Override
	@Transactional
	public List<PsTypeSubTypeComponentMapView> saveOrUpdateTypeSubTypeMappings(PsTypeSubTypeComponentMapView[] psTypeSubTypeComponentMapViewArray) {
		{
            User user = UserContext.getUser();
            String userName = user.getLastName() + "," + user.getFirstName();
            String userId = user.getUserId();
			List<PsTypeSubTypeComponentMapView> result = null;
			List<Integer> typeSubTypeComponentMapIds =  new ArrayList<>();
			Integer mapId = null;

			try {
				for(PsTypeSubTypeComponentMapView mapView : psTypeSubTypeComponentMapViewArray) {
                    mapView.setLastUpdateUserName(userName);
                    mapView.setLastUpdateUserId(userId);
					PsTypeSubTypeComponentMap map = copyMapViewToMap(mapView);
                    map.setLastUpdateUserId(userId);
					mapId = saveComponentMap(map);
					typeSubTypeComponentMapIds.add(mapId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				result = getPsTypeSubTypeComponentMapView(typeSubTypeComponentMapIds);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;

		}
	}


	PsTypeSubTypeComponentMap copyMapViewToMap(PsTypeSubTypeComponentMapView mapView) {
		PsTypeSubTypeComponentMap map = new PsTypeSubTypeComponentMap();

		if("SUBTYPE".equals(mapView.getTypeSubTypeCode())) {
			map.setExamSubTypeCode(mapView.getExamTypeSubTypeCode());
		}
		else
			if("TYPE".equals(mapView.getTypeSubTypeCode())) {
				map.setExamTypeCode(mapView.getExamTypeSubTypeCode());
		}
		map.setTypeSubTypeCode(mapView.getTypeSubTypeCode());
		map.setAncFlag(mapView.getAncFlag());
		map.setFinopFlag(mapView.getFinopFlag());
		map.setFirstFinopFlag(mapView.getFirstFinopFlag());
		map.setFloorFlag(mapView.getFloorFlag());
		map.setMoreComponentsFlag(mapView.getMoreComponentsFlag());
		map.setMunicipalAdvisorFlag(mapView.getMunicipalAdvisorFlag());
		map.setMunicipalFlag(mapView.getMunicipalFlag());
		map.setOptionFlag(mapView.getOptionFlag());
		map.setRsaFinopFlag(mapView.getRsaFinopFlag());
		map.setRsaSpFlag(mapView.getRsaSpFlag());
		map.setSdfFlag(mapView.getSdfFlag());
		map.setSpFlag(mapView.getSpFlag());
		map.setTypeSubTypeComponentMapId(mapView.getTypeSubTypeComponentMapId());
        map.setLastUpdateDate(new Date());
        User user = UserContext.getUser();
        map.setLastUpdateUserId(user.getUserId());
		return map;
	}

	public List<PsTypeSubTypeComponentMapView> getPsTypeSubTypeComponentMapView(List<Integer> typeSubTypeComponentMapIds) throws Exception {
		List<PsTypeSubTypeComponentMapView> result = new ArrayList<>();
		String hql = "FROM PsTypeSubTypeComponentMapView V WHERE V.typeSubTypeComponentMapId = ";

		for(Integer id : typeSubTypeComponentMapIds) {
			Query query = getSession().createQuery(hql + id);
			PsTypeSubTypeComponentMapView view = (PsTypeSubTypeComponentMapView) query.uniqueResult();
			result.add(view);
		}

		return result;
	}
    @Override
    @Transactional

    /**
     * Create schdlLoadComponentMapping request
     *
     * @throws Exception
     */
    public void schdlLoadComponentMapping() throws Exception {
        log.debug("Running schdlLoadComponentMapping stored proc ... "); //defined in PsTypeSubTypeComponentMap
        StoredProcedureQuery query = getEntityManager().createNamedStoredProcedureQuery("schdlLoadComponentMapping");
        query.execute();
        return;
    }

}
