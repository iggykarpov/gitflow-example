/**
 *
 */
package org.finra.esched.service.star.common;

/**
 * @author arellanr
 */
public class CreateMatterBuilder {/*

    private static final Logger log = Logger.getLogger(CreateMatterBuilder.class);
    private static final Integer COORDINATOR_ROLE = 6;
    private static final Integer SUPERVISOR_ROLE = 4;
    private static final Integer MATTER_MANAGEMENT = 2;
    private static final Boolean PRIMARY_FLAG = Boolean.TRUE;
    private static final String FIRM_TYPE = "F";
    private static final String REP_TYPE = "R";
    public static final int EXAM_WORK_START_DATE_NAME_ID = 83;
    public static final int ON_SITE_START_DATE_NAME_ID = 4;
    private static final Integer POTENTIAL_RESPONDENT = 14;

    public CreateMatterCommand composeCreateMatterCommand(String correlationId, Integer firmId, Long branchId,
                                                          Integer matterType, Integer matterSubType, Integer coordinator, Integer supervisor,
                                                          Date examWorkStart, Date onSiteStartDate, List<PsFirmBillableEntity> beList,
                                                          String spDistrictCode, String finOpDistrictCode, PsMarketMatterMetaData marketMatterMetaData) {
        log.info("enter::");
        CreateMatterCommand cmt = new CreateMatterCommand();

        try {
            log.info("processing matter header and dates");
            Date today = new Date();
            cmt.setDepartmentReceivedDate(getXMLGregorianCalendar(today));
            cmt.setFINRAReceivedDate(getXMLGregorianCalendar(today));
            cmt.setInitialBudgetedStaffCarryOverHours(null);
            cmt.setInitialBudgetedStaffHours(null);
            cmt.setIsOutOfTown(false);

            cmt.setMatterStateID(MATTER_MANAGEMENT); // pre-populate Matter State to Matter Management
            cmt.setRegulatorySignificanceID(null);
            cmt.setRevisedBudgetedStaffHours(null);
            cmt.setMatterTypeID(matterType);
            cmt.setMatterSubTypeID(matterSubType);
            cmt.setIsPreScheduleMatter(MatterServiceClient.matterIsPreScheduleMatter(Boolean.TRUE));

            List<CreateBillableEntityCommand> matteBECreateTypeList = new ArrayList<>();
            Map<Integer, Integer> bes = new HashMap<Integer, Integer>();
            if (beList != null && beList.size() > 0) {
	            for (PsFirmBillableEntity bean : beList) {
	            	if (!bes.containsKey(bean.getBillableEntity())) {
		                matteBECreateTypeList.add(
		                        createBECommand(bean.getBillableEntity(),
		                                bean.getCurrentFlag().equals("Y"),
		                                correlationId + "BE" + bean.getBillableEntity(),
		                                spDistrictCode, finOpDistrictCode));
		                bes.put(bean.getBillableEntity(), bean.getBillableEntity());
	            	}
	            }
	            cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().addAll(matteBECreateTypeList);
            }

            List<CreateStaffCommand> matterStaffCreateTypeList = new ArrayList<>();
            //inserting coordinator is optional -- removing it will lessen the chances of errors
            //matterStaffCreateTypeList.add(createStaffCommand(coordinator, correlationId + "C"));
            matterStaffCreateTypeList.add(createStaffCommand(supervisor, correlationId + "S"));
            cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().addAll(matterStaffCreateTypeList);
            List<CreateMatterStaffAssociationCommand> matterStaffAssociationCommands = new ArrayList<>();
            //matterStaffAssociationCommands.add(createMatterStaffAssociation(coordinator, correlationId + "CA"));
            matterStaffAssociationCommands.add(createMatterStaffAssociation(supervisor, correlationId + "SA"));
            cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().addAll(matterStaffAssociationCommands);

            List<CreateDateCommand> matterDateCreateTypeList = new ArrayList<>();
            if (examWorkStart != null) matterDateCreateTypeList.add(
                    createDateCommand(EXAM_WORK_START_DATE_NAME_ID, 1, getXMLGregorianCalendar(examWorkStart), correlationId + "EWSP"));
            if (onSiteStartDate != null) matterDateCreateTypeList.add(
                    createDateCommand(ON_SITE_START_DATE_NAME_ID, 1, getXMLGregorianCalendar(onSiteStartDate), correlationId + "OSSP"));
            cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().addAll(matterDateCreateTypeList);

            log.info("processing matter contact");
            CreateContactCommand contact = new CreateContactCommand();
            contact.setBranchID(branchId);
            contact.setFirmID(firmId);
            contact.setRowType(FIRM_TYPE);
            contact.setIsPrimary(PRIMARY_FLAG);
            contact.setContactTypeID(POTENTIAL_RESPONDENT);
            cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().add(contact);
            
            //add products, matter description and regulatory significance for Market Reg matters
            //Member Reg matters do not have products, matter description and regulatory significance
            if (marketMatterMetaData != null) {
            	if (marketMatterMetaData.getMarketTypeCode().equals("EQTY")) {
					log.info("processing matter product");
					if (marketMatterMetaData.getProductId() != null) {
						CreateProductCommand product = null;
						product = new CreateProductCommand();
						product.setProductID(marketMatterMetaData.getProductId()); 
						cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().add(product);
					}       
            	} else if (marketMatterMetaData.getMarketTypeCode().equals("OPTN")) {
// as per Danielle ... no need to create Products for options matters            		
//					if (marketMatterMetaData.getProductId() != null) {
//					
//						List<CreateProductCommand> productList = new ArrayList<>();
//			            if (beList != null && beList.size() > 0) {
//				            for (PsFirmBillableEntity bean : beList) {
//				            	productList.add(createProductCommand(bean.getBillableEntity(), marketMatterMetaData.getProductId(), correlationId));
//				            }
//				            cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().addAll(productList);
//			            }
//					}    
            	}
            	cmt.setMatterDescription(marketMatterMetaData.getMatterDescription());
            	cmt.setRegulatorySignificanceID(marketMatterMetaData.getRegulatorSignificanceId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("exit::");
        return cmt;
    }
    

    public CreateMatterCommand composeCreateMatterCommand(String correlationId, PsMatter bean) {
		log.info("enter::");
		CreateMatterCommand cmt = new CreateMatterCommand();
		
		try {
			log.info("processing matter header");
			cmt.setDepartmentReceivedDate(getXMLGregorianCalendar(bean.getInitialDepartmentReceivedDate()));
			cmt.setFINRAReceivedDate(getXMLGregorianCalendar(bean.getInitialFINRAReceivedDate()));
			cmt.setInitialBudgetedStaffHours(bean.getInitialBudgetedStaffHours());
			cmt.setIsOutOfTown(bean.getIsOutOfTown());
			
			cmt.setMatterStateID(bean.getMatterStateID()); 
			cmt.setRegulatorySignificanceID(bean.getRegulatorySignificanceID());
			cmt.setRevisedBudgetedStaffHours(bean.getRevisedBudgetedStaffHours());
			cmt.setMatterTypeID(bean.getMatterTypeID());
			cmt.setMatterSubTypeID(bean.getMatterSubTypeID());
			cmt.setIsPreScheduleMatter(MatterServiceClient.matterIsPreScheduleMatter(bean.getIsPreScheduleMatter()));
			
			log.info("processing matter BEs");
			List<CreateBillableEntityCommand> matteBECreateTypeList = new ArrayList<>();
			if (bean.getBeList() != null && bean.getBeList().size() > 0) {
				for (PsFirmBillableEntity be : bean.getBeList()) {
				matteBECreateTypeList.add(createBECommand(be.getBillableEntity(), be.getCurrentFlag().equals("Y"), correlationId + "BE" + be.getBillableEntity()));
				}
				cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().addAll(matteBECreateTypeList);
			}
			
			log.info("processing matter staff");
			List<CreateStaffCommand> matterStaffCreateTypeList = new ArrayList<>();
			List<CreateMatterStaffAssociationCommand> matterStaffAssociationCommands = new ArrayList<>();
			if (bean.getStaffList() != null && bean.getStaffList().size() > 0) {
				Map<Integer, Integer> staffNamesList = new HashMap<Integer, Integer>();
				Map<Integer, Integer> staffRoleList = new HashMap<Integer, Integer>(); //this is needed to handle users that are assigned to both Staff and Exam Lead Roles
				boolean includeFlag = false;
				Integer roleId;
				for (org.finra.exam.common.domain.ui.CreateExamStaff staffBean:bean.getStaffList()) {
					includeFlag = false;
					roleId = staffBean.getRoleId();
					if (roleId == 8) {
						roleId = 3; //role Id of attorney in STAR = 3
						includeFlag = true;
					} else if (roleId == 2 || roleId == 3) {
						//exam lead and staff roles are mapped to STAFF role in STAR with ID 2
						//also, if a user is assigned to both roles, we only need to assign that user to one role with ID 2 in star.
						if (!staffRoleList.containsKey(staffBean.getApplicationUserId())) {
							roleId = 2;
							staffRoleList.put(staffBean.getApplicationUserId(), staffBean.getApplicationUserId());
							includeFlag = true;
						} 
					} else
						includeFlag = true;
					if (includeFlag) {
						matterStaffCreateTypeList.add(createStaffCommand(staffBean.getApplicationUserId(), roleId, staffBean.getPrimaryFlag(), correlationId + "S" + staffBean.getApplicationUserId() + staffBean.getRoleId()));
						if(staffBean.getApplicationUserId() != null && !staffNamesList.containsKey(staffBean.getApplicationUserId())){
							staffNamesList.put(staffBean.getApplicationUserId(), staffBean.getApplicationUserId());
							matterStaffAssociationCommands.add(createMatterStaffAssociation(staffBean.getApplicationUserId(), correlationId + "SA"));
						}	
					}
				}
				cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().addAll(matterStaffCreateTypeList);
				cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().addAll(matterStaffAssociationCommands);
			}
			
			log.info("processing matter date");
			List<CreateDateCommand> matterDateCreateTypeList = new ArrayList<>();
			if (bean.getDateList() != null && bean.getDateList().size() > 0) {
				for (PsMatterDate dateBean:bean.getDateList()) {
					matterDateCreateTypeList.add(createDateCommand(dateBean.getMatterDateNameId(), dateBean.getMatterDateTypeId(), getXMLGregorianCalendar(dateBean.getMatterDate()), correlationId + "D" + dateBean.getMatterDateNameId()+dateBean.getMatterDateTypeId()));
				}
				cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().addAll(matterDateCreateTypeList);
			}
			
			log.info("processing matter contact");
			List<CreateContactCommand> matterContactCreateTypeList = new ArrayList<>();
			if (bean.getContactList() != null && bean.getContactList().size() > 0) {
				//FIXME
				for (CreateExamContact contactBean:bean.getContactList()) {
					matterContactCreateTypeList.add(createContactCommand(contactBean, correlationId));
				}
			}
			cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().addAll(matterContactCreateTypeList);

			log.info("processing matter product");
			CreateProductCommand product = null;
			if (bean.getProductId() != null) {
				product = new CreateProductCommand();
				product.setProductID(bean.getProductId()); 
				product.setSecurityName(bean.getSecurityName());
				cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().add(product);
			}
			
			CreateOriginCommand origin = null;
			if (bean.getOriginId()!=null) {
				origin = new CreateOriginCommand();
				origin.setOriginID(bean.getOriginId()); 
				cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().add(origin);
			}
			
			CreateCommentCommand comment = null; 
			if (bean.getCommentId() !=null) {
				comment = new CreateCommentCommand();
				comment.setCommentDate(getXMLGregorianCalendar(new Date())); 
				comment.setCommentText(bean.getCommentText());
				comment.setCommentTypeID(bean.getCommentId());
				cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().add(comment);
			}
			
			CreateCauseCommand cause = null;
			if (bean.getCauseCodeId() != null) {
				cause = new CreateCauseCommand();
				cause.setCauseCodeID(bean.getCauseCodeId());
				cmt.getCreateCommentOrCreateCorrespondenceOrCreateDate().add(cause);
			}
			
			} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("exit::");
		return cmt;
		}    

    public UpdateMatterCommand composeUpdateMatterCommand(PsOutput fnBean, PsOutput spBean, String correlationId) {
        UpdateMatterCommand command = new UpdateMatterCommand();
        command.setMatterID(fnBean.getMatterId());
        command.setSendNotifications(false);
        command.setCorrelationID(correlationId);
        command.getCreateCommentOrCreateCorrespondenceOrCreateDate().
                add(createJointEffortCommand(spBean.getMatterId(), correlationId));
        return command;
    }

    private CreateMatterStaffAssociationCommand createMatterStaffAssociation(Integer staffId, String correlationId) {
        CreateMatterStaffAssociationCommand command = new CreateMatterStaffAssociationCommand();
        command.setCorrelationID(correlationId);
        command.setStaffID(staffId);
        command.setIsStaffCurrent(Boolean.FALSE);
        return command;
    }
    
    private CreateStaffCommand createStaffCommand(int staffId, String correlationId) {
        CreateStaffCommand command = new ObjectFactory().createCreateStaffCommand();
        command.setCorrelationID(correlationId);
        command.setStaffID(staffId);
        command.setIsPrimary(true);
        return command;
    }
    
    private CreateStaffCommand createStaffCommand(int staffId, Integer roleId, Boolean primaryFlag, String correlationId) {
        CreateStaffCommand command = new ObjectFactory().createCreateStaffCommand();
        command.setCorrelationID(correlationId);
        command.setStaffID(staffId);
        if (roleId != null) command.setRoleID(roleId);
        command.setIsPrimary(primaryFlag);
        return command;
    }    

    private CreateBillableEntityCommand createBECommand(int beCode, Boolean currentFlag, String correlationId, String spDistrictCode, String finopDistrictCode) {
        CreateBillableEntityCommand command = new ObjectFactory().createCreateBillableEntityCommand();
        command.setBillableEntityID(beCode);
        command.setCorrelationID(correlationId);
        command.setIsTimeTracking(Boolean.TRUE);
        command.setIsCurrent(currentFlag);
        return command;
    }
    
    private CreateBillableEntityCommand createBECommand(int beCode, Boolean currentFlag, String correlationId) {
        CreateBillableEntityCommand command = new ObjectFactory().createCreateBillableEntityCommand();
        command.setBillableEntityID(beCode);
        command.setCorrelationID(correlationId);
        command.setIsTimeTracking(Boolean.TRUE);
        command.setIsCurrent(currentFlag);
        return command;
    }    

    private CreateDateCommand createDateCommand(int dateNameId, int dateTypeId, XMLGregorianCalendar dateValue, String correlationId) {
        CreateDateCommand command = new CreateDateCommand();
        command.setDateNameID(dateNameId);
        command.setDateTypeID(dateTypeId);
        command.setCorrelationID(correlationId);
        command.setDateValue(dateValue);
        return command;
    }

    private CreateJointEffortCommand createJointEffortCommand(String spMatter, String correlationId) {
        CreateJointEffortCommand command = new CreateJointEffortCommand();
        command.setAdministrativeLeadStaffID(null);
        command.setCoordinationPerformed(false);
        command.setCoordinationRequested(true);
        command.setCorrelationID(correlationId + "_JEC");
        command.getUnusedTypeOrCreateJointEffortParticipant().
                add(createJointEffortParticipantCommand(spMatter, correlationId));
        return command;
    }

    private CreateJointEffortParticipantCommand createJointEffortParticipantCommand(String matterId, String correlationId) {
        CreateJointEffortParticipantCommand command = new CreateJointEffortParticipantCommand();
        command.setMatterID(matterId);
        command.setParticipantTypeID(113);
        command.setDistrictID(null);
        command.setActualStartDate(null);
        command.setProjectedStartDate(null);
        command.setCorrelationID(correlationId + "_JEPC");
        return command;
    }


    private XMLGregorianCalendar getXMLGregorianCalendar(Date date) {
        XMLGregorianCalendar retVal = null;
        try {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(date);
            retVal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (Exception e) {
            log.error("Failed to create XMl date for Date" + date, e);
        }
        return retVal;
    }

    private boolean isTFCEDistrict(String spDistrictCode, String finopDistrictCode) {
    	if (spDistrictCode.equals("TF") || spDistrictCode.equals("TE") || spDistrictCode.equals("EX")) return true;
    	if (finopDistrictCode.equals("TF") || finopDistrictCode.equals("TE") || finopDistrictCode.equals("EX")) return true;
    	return false;
    }

    private CreateProductCommand createProductCommand(Integer be, Integer productId, String correlationId) {
    	CreateProductCommand command = new CreateProductCommand();
    	command.setProductID(productId);
    	command.setMarketID(be);
        command.setCorrelationID(correlationId + "_PRDCT");
        return command;
    }

    //FIXME
    private CreateContactCommand createContactCommand(CreateExamContact contact, String correlationId) {
    	CreateContactCommand ccc = new CreateContactCommand();
    	String correlationIdText = null;
		
    	if (contact.getCrdType() == CreateExamContact.FIRM || contact.getCrdType() == CreateExamContact.BRANCH) {
    		ccc.setBranchID(contact.getBranchCrdNumber().longValue());
    		ccc.setFirmID(contact.getCrdNumber());
    		ccc.setRowType(FIRM_TYPE);
    		correlationIdText = FIRM_TYPE + contact.getCrdNumber() + contact.getBranchCrdNumber();
			log.info("Contact from TTM: " + FIRM_TYPE + "_CRD:" + contact.getCrdNumber() + "_BRANCHCRD:" + contact.getBranchCrdNumber() + "_PRIMARY:" + contact.getPrimaryFlag() + "_PR:" + contact.getPotentialRespondentFlag());
    	}
    	if (contact.getCrdType() == CreateExamContact.REGISTERED_REP) {
    		ccc.setRepID(contact.getCrdNumber().longValue());
    		ccc.setRowType(REP_TYPE);
    		correlationIdText = REP_TYPE + contact.getCrdNumber();
			log.info("Contact from TTM: " + REP_TYPE + "_CRD:" + contact.getCrdNumber() + "_PRIMARY:" + contact.getPrimaryFlag() + "_PR:" + contact.getPotentialRespondentFlag());
    	}
    	ccc.setIsPrimary(contact.getPrimaryFlag());
    	if (contact.getPotentialRespondentFlag()) ccc.setContactTypeID(POTENTIAL_RESPONDENT);
    	ccc.setCorrelationID(correlationId + "_" + correlationIdText + "_CNTCT");
        return ccc;
    }*/
}
