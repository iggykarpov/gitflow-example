PS.View = (function() {

	var debug, g, oFilters, filterTimer = null;
	
	var CMPNT_APPROVED_CLS="fa-thumbs-up";
	var CMPNT_NOT_APPROVED_CLS="fa-thumbs-o-up";
	var SCHEDULED_STTS_CD="SCHED";
	var ERROR_STTS_CD="ERROR";
	var SCHEDULING_NOT_ALLOWED_MSG="Exam cannot be scheduled unless at least one component is required, all domains are approved and all dates, if entered, are valid.";
	var BUSINESS_REVIEW="BUSINESS_REVIEW";
	var GETPSCMPNTS_URL="services/PS/getPsCmpnts";
	
	var PS_SNAPSHOT_ID;
	var PS_USER_DISTRICT_CD;
	var SCHEDULING_ACCESS;
	var CMPNT_OVERRIDE_TO_RQ_REASONS;
	var CMPNT_OVERRIDE_TO_NRQ_REASONS;
	var IMPACTS_FL;
	var RISKS_FL;
	var DISTRICTS_FL;
	var STATUS_FL;
	
	var psPhase;
	var isDirty= false;
	var psComponents = {
			"sp": [{"key":"sp", "label": "SP"},
			       {"key":"muni", "label": "MUNI"},
			       {"key":"muniAdv", "label": "MUNI ADV"},
			       {"key":"op", "label": "OPTIONS"},
			       {"key":"rsaSp", "label": "RSA-SP"},
			       {"key":"sdf", "label": "SDF"}
			       ],
			 "fn": [{"key":"ffn", "label": "FIRST FN"},
			       {"key":"fn", "label": "FN"},
			       {"key":"rsaFn", "label": "RSA-FN"},
			       {"key":"anc", "label": "ANC"}
			       ],
			 "floor": [{"key":"fl", "label": "FLOOR"}
			       ]
	};
	var sortedDistricts;
	var overrideReasonsMap = {};
    var firmSessionMap = {};
	return {
		init: function(){
            $.subscribe("onSaveFirmSession", function(evt, data){
                //console.log(data.title + ": " + data.url);
                console.log("onSaveFirmSession");
                //if(EXAM.GA) EXAM.GA.onEventHit({eventCategory: "Firm Session", eventAction: "Save", eventLabel: ""})
            });



			var parent = window.parent ? window.parent.document : document;
			if(parent.hasAnnualPermission){
				PS.View.loadLookup();
				$("#container").show();
			}else{
				$("#container").hide();
				$('#loading').show();
				$.ajax({
					type: "POST",
					url: "/esched/services/PS/checkUserPermissions",
					cache: false,
					async: true,
					dataType: "xml"
				}).done(function( oXML ) {
					 $("#loading").hide();
					 oXML = $(oXML);
					 var oUserPerm = oXML.find("userpermissions");
					 var returncode=oUserPerm.attr("status");
					 if (returncode !== "1") {
						reportError(oUserPerm);
						return false;
					 }
						
					 var hasAnnualPermission = oUserPerm.find("annualplanning").length && oUserPerm.find("annualplanning").text() == "Y";

					 if(hasAnnualPermission){
						 PS.View.loadLookup();
						 $("#container").show();
					 }else{
						 $("#container").remove();
						 EXAM.Error.create({"error" : 'You do not have permission to view this page.'});
					 }				 
					
				}).fail(function( jqXHR, textStatus ) {
					$('#loading').hide();
					$("#container").remove();
					EXAM.Error.create({"error" : 'Request failed.'});
				});	
			}
			
		},
		loadLookup: function(){
			$('#loading').show();
			$.ajax({
				type: "POST",
				url: "services/PS/getLookUpLists",
				cache: false,
				async: true,
				dataType: "json"
			}).done(function( data ) {
								
				$('#loading').hide();
				
				PS_SNAPSHOT_ID = data.psSnapshotId;
				PS_USER_DISTRICT_CD = data.psUserDistrictCd;
				SCHEDULING_ACCESS = data.schedulingAccess;
				CMPNT_OVERRIDE_TO_RQ_REASONS = data.cmpntOvrdRqReasons;
                CMPNT_OVERRIDE_TO_NRQ_REASONS = data.cmpntOvrdNonRqReasons;
				IMPACTS_FL = data.impactsFlags;
				RISKS_FL = data.riskFlags;
				DISTRICTS_FL = data.cmpntDistricts;
				STATUS_FL = data.statuses;
				
				
				CMPNT_OVERRIDE_TO_RQ_REASONS.sort(function(a, b){
					var x=a["desc"].toLowerCase(),
						y=b["desc"].toLowerCase();
						return x<y ? -1 : x>y ? 1 : 0;
				});
				
				CMPNT_OVERRIDE_TO_NRQ_REASONS.sort(function(a, b){
					var x=a["desc"].toLowerCase(),
						y=b["desc"].toLowerCase();
						return x<y ? -1 : x>y ? 1 : 0;
				});
				
				var overrideReasons = CMPNT_OVERRIDE_TO_RQ_REASONS.concat(CMPNT_OVERRIDE_TO_NRQ_REASONS);
				for(var i in overrideReasons) {
					overrideReasonsMap[overrideReasons[i].id] = overrideReasons[i];
                }
				
				PS.View.initPrescheduler();
				
			}).fail(function( jqXHR, textStatus ) {
				$('#loading').hide();
				EXAM.Error.create({"error" : 'Request failed.'});
			});	
		},
		initPrescheduler : function() {								
			
			if (!SCHEDULING_ACCESS) 
				$('.tabHighlight').css("left", "92px");
			
			// run keep-alive procedure to fix EXAM-9698
			// ping server every 15 minutes
			try {
				window.setInterval(PS.View.keepAlive, 900000);
			}
			catch(err) {
			    //NOP
			}
			
			debug = getParameter("debug");
			oFilters = PS.grid.filter.clear();
			$('#loading').show();
			g = PS.grid.create();
			g.setURL(GETPSCMPNTS_URL);
			g.setPageSize(25);
			g.setPageNumber(1);
			g.setExclURL('services/PS/exportExcel');
			g.setCallback(null, PS.View.renderGrid); // set container to null, don't need to be component for now.
			g.init();
			

			

			if (PS_SNAPSHOT_ID == null || PS_SNAPSHOT_ID == undefined) {
				EXAM.Error.create({
					"error" : 'Unable to find current snapshot of data'
				});
				$('#loading').hide();
				return;
			}

			/*if (IMPACTS_FL != null) {
				var sHTML = [];
				sHTML.push('<option value="null">Any Impact</option>');
				IMPACTS_FL.forEach(function(val) {
					sHTML.push('<option value="' + val + '">' + val
							+ '</option>');
				});
				$("#impctFilter").html(sHTML.join(''));
			}

			if (RISKS_FL != null) {
				var sHTML = [];
				sHTML.push('<option value="null">Any Likelihood</option>');
				RISKS_FL.forEach(function(val) {
					sHTML.push('<option value="' + val + '">' + val
							+ '</option>');
				});
				$("#riskFilter").html(sHTML.join(''));
			}
*/
			if (DISTRICTS_FL != null) {
				var sHTML = [];
				sHTML.push('<option value="null">Any District</option>');

				sortedDistricts=DISTRICTS_FL;
								
				sortedDistricts.sort(function(a, b){
					var x=a["name"].toLowerCase(),
						y=b["name"].toLowerCase();
						return x<y ? -1 : x>y ? 1 : 0;
				});
				
				sortedDistricts.forEach(function(val) {
					sHTML.push('<option value="' + val["code"] + '">' + val["name"]+ '</option>');
				});
				
				$("#distrFilter").html(sHTML.join(''));
			}
			
			if (STATUS_FL != null) {
				var sHTML = [];
				sHTML.push('<option value="null">Any Status</option>');

				STATUS_FL.forEach(function(val) {
					sHTML.push('<option value="' + val["id"] + '">' + val["desc"] + '</option>');
				});

				$("#statusFilter").html(sHTML.join(''));
			}


            $('#filters select').selectize({
                create: false
            });

			PS.View.resetFilters();



            $('#filters .componentFilters input[type=checkbox]').unbind("click").click(PS.View.filterCheckboxUpdated);


			/* 
			$(document).ready(function() {
				$('#selectAll').click(function(event) { // on click
					if (this.checked) { // check select status
						$('.rowSelectCheckbox').each(function() { 
							// loop through each checkbox
							if(!this.disabled)
								this.checked = true; // select all checkboxes
						});
					} else {
						$('.rowSelectCheckbox').each(function() { 
							if(!this.disabled)
								this.checked = false; // deselect all checkboxes
						});
					}
				});				

			});
			*/

		},
        resetFilters: function(){
            //clear the filters


            $('#filters input[type=checkbox]').each(function(){
                $(this).prop('checked', false);
                PS.View.filterUpdated($(this).attr('id'), false);
            });

            // Populate filters
            $("#hasRqFilter").attr('checked', true);
            this.filterUpdated($("#hasRqFilter").attr('id'), true);

           // $("#statusFilter").val("ACTIVE");
            $("#statusFilter")[0].selectize.setValue('ACTIVE');
            this.filterUpdated($("#statusFilter").attr('id'), 'ACTIVE');

            this.filterUpdated("vrsnId", PS_SNAPSHOT_ID);

            /*$("#impctFilter")[0].selectize.setValue(null);
            this.filterUpdated($("#impctFilter").attr('id'), null);

            $("#riskFilter")[0].selectize.setValue(null);
            this.filterUpdated($("#riskFilter").attr('id'), null);
*/
            // Default District filter to User District
            if(PS_USER_DISTRICT_CD != null){
               // $("#distrFilter").val(PS_USER_DISTRICT_CD);
                $("#distrFilter")[0].selectize.setValue(PS_USER_DISTRICT_CD);
                this.filterUpdated($("#distrFilter").attr('id'), PS_USER_DISTRICT_CD);
            }

            $('#crdFilter').val('');
            this.filterUpdated($("#crdFilter").attr('id'), '', 1);
        },
		nextPage : function() {
			return g.nextPage();
		},
		prevPage : function() {
			return g.prevPage();
		},
		filterCheckboxUpdated: function(evt) {	
			PS.View.filterUpdated($(this).attr('id'), $(this).prop('checked'));
		},	
		filterUpdated: function(id, value, delay) {
	
			oFilters.update(id, value);
	
			window.clearTimeout(filterTimer);
			filterTimer = window.setTimeout(function() {
				g.setPageNumber(1, true);
			}, delay ? delay : 2000);
		},
		
		keepAlive : function(){
			
			$.ajax({
				type : "GET",
				url : "services/PS/keepAlive",
				data : "",
				cache : false,
				dataType : "text"
			}).done(function(data) {
				
			}).fail(function(jqXHR, textStatus) {
	
			});
		},
		
		showOvrrdDialog : function(sCmpnt_SssnId_CmpntId, crd, cmpntName, originalVal, newVal, sssnId){
			
			var oDialog = EXAM.Dialog.create({
				'title' : 'Override Component Requirement',
				'width' : '450px'
			});
			
			var fnDestroy = function() {
				$('#' + sCmpnt_SssnId_CmpntId).parent("label").removeClass("overridden");
				$('#' + sCmpnt_SssnId_CmpntId).prop('checked', originalVal);
	
				oDialog.destroy();
			};
	
			oDialog.setDismissCallback(fnDestroy);
			oDialog.setCancelCallback(fnDestroy);
			
			
			if (CMPNT_OVERRIDE_TO_RQ_REASONS == null || CMPNT_OVERRIDE_TO_NRQ_REASONS == null) {
	
				oDialog.setContent('<p>Unable to load override reasons.</p>');
				oDialog.enablePersistButton(false);
				oDialog.setDismissCallback(fnDestroy);
				oDialog.setCancelCallback(fnDestroy);
				oDialog.setPersistCallback(function() {
					oDialog.destroy();
				});
					
				return;
			} else {
				var dialogId = oDialog.getID();

				var sHTML = [];
				sHTML.push('<div class="overrideCompWrapper">');
                sHTML.push('<div class="alert alert-danger" id="errorMsg_'+ dialogId + '"></div>');
				sHTML.push('<p> Provide a reason for changing '
					+'\'' + cmpntName
					+ '\' component from '
					+ (originalVal ? " 'Required'" : "'Not Required'") + ' to '
					+ (newVal ? "'Required'" : "'Not Required'")
					+' for CRD ' + crd + '</p>'
				);
					
				sHTML.push('<select id="reasonFilter_'+ dialogId + '" >');			
				sHTML.push('</select>');
				sHTML.push('<div><textarea id="businessReviewComment_'+dialogId+'" class="businessReviewComment" placeholder="Explanation (required)" maxlength="1000" rows="3"></textarea></div>');
				sHTML.push('</div>');
	
				oDialog.setContent(sHTML.join(''));
				
				var options = (!originalVal && newVal) ? CMPNT_OVERRIDE_TO_RQ_REASONS : CMPNT_OVERRIDE_TO_NRQ_REASONS;
				var selOptions = [];
				
				options.forEach(function(val) {
					if(psPhase==BUSINESS_REVIEW || val["businessReview"]=="N"){
						selOptions.push({value: val["id"], text:val["desc"], businessreview: val["businessReview"] })	
						//sHTML.push('<option value="' + val["id"] + '" businessreview="'+val["businessReview"]+'">' + val["desc"]+ '</option>');
					}
				});
				var isBusinessReview = false;
				if($('#reasonFilter_'+ dialogId)[0].selectize){
					$('#reasonFilter_'+ dialogId)[0].selectize.destroy(); //must destroy the selectize instance
				}
				var dropdown = $('#reasonFilter_'+ dialogId).selectize({
					create: false,
			        options: selOptions,
			        items: [options[0]["id"]],
			        onChange: function(value){	
			        	var data = this.options[value];		
			        	isBusinessReview = data["businessreview"]=="Y";
						PS.View.selectOvrrdReason(isBusinessReview);
					},
					onDelete: function(value){ //prevent delete
						return false;
					}
				});
	
				oDialog.setPersistCallback(function() {
					oDialog.enablePersistButton(false);
					PS.View.saveOvrrd(oDialog, sCmpnt_SssnId_CmpntId, originalVal, newVal, sssnId, isBusinessReview);
				});
			}
			return oDialog;
		},
		saveOvrrd: function(oDialog, sCmpnt_SssnId_CmpntId, originalVal, newVal, sssnId, isBusinessReview){
			
			var oDialogId=oDialog!=null ? oDialog.getID() : null;
			var reasonDs = $("#reasonFilter_" + oDialogId + " option:selected").text();
			var reasonId = $("#reasonFilter_"+ oDialogId).val();
			var cmptIds = sCmpnt_SssnId_CmpntId.split("_");
			var componentData = {
					"id":cmptIds[2], 
					"reqrdOvrrdFl": newVal, 
					"ovrdReasonId": reasonId
					};

			if(isBusinessReview) {
                var businessReviewText = $.trim($("#businessReviewComment_"+oDialogId).val());
                if(businessReviewText.length < 1){
                    $("#errorMsg_"+ oDialogId).show().html("Please provide a business review explanation.");
                    oDialog.enablePersistButton(true);
                    return false;
                }
				componentData["businessReviewText"] = businessReviewText;
            }
            $("#errorMsg_"+ oDialogId).hide();
			var data = {"componentRequestList" : [componentData]};
			
			PS.View.saveFirmSession(sssnId, data, function(respData){			
				if (oDialog != undefined) oDialog.destroy();
				
			}, function(jqXHR, textStatus){
				if (oDialog != undefined) oDialog.destroy();
			});		
			
			
		},	
		
		cmpntOvrrd: function(sCmpnt_SssnId_CmpntId, crd, cmpntName, originalVal, newVal) {
	
			/*
			 * VRuzha
			 * 
			 * Requirement overridden if it's value manually changed (to required/not
			 * required, doesnt matter)
			 * 
			 * 1. If requirement overridden, highlight checkbox in red; If returned to
			 * original value - remove highlighting; 2. If requirement overridden, show
			 * dialog to select the reason. If it's returned to original state - just
			 * save the changes in DB.
			 * 
			 */
	
			var sssnId=sCmpnt_SssnId_CmpntId.split("_")[1];
			
			// Apply highlighting immediately. Keep/Remove it later on the base of
			// results of back-end call...
			if (originalVal != newVal) {
				$('#' + sCmpnt_SssnId_CmpntId).parent("label").addClass("overridden");
			} else {
				$('#' + sCmpnt_SssnId_CmpntId).parent("label").removeClass("overridden");
			}
	
			// If reverting back overridden value - just save the changes, dont show
			// dialog
			if (newVal == originalVal) {
				PS.View.saveOvrrd(null, sCmpnt_SssnId_CmpntId, originalVal, newVal, sssnId);
				$('#' + sCmpnt_SssnId_CmpntId).parent("label").attr("title" , "");
				PS.View.updateSelCheckBox(sssnId);
				return;
			}else{
				// Show dialog
				PS.View.showOvrrdDialog(sCmpnt_SssnId_CmpntId, crd, cmpntName, originalVal, newVal, sssnId);
			}
	
		},
	
		renderGrid: function(data) {
            firmSessionMap = {};

			if (data["status"] !== 1) {
				reportError(data);
				return false;
			}
			
			psPhase = data["annualPlanningPhase"];

			//toggle Schedule button based on phase
			$("#schedule").toggle(psPhase!=BUSINESS_REVIEW);
			
			
			var sHTML = [];
			sHTML.push('<div class="grid_content">');
			var exams = data["psExams"];
	
			for (var i = 0; i < exams.length; i++) {
				var oChild = exams[i];	
				var sssnId = oChild["sssnId"];
                firmSessionMap[sssnId] = {"firmId": oChild["firmId"], "firmNm": oChild["firmNm"]};
				sHTML.push('<div class="grid_row" id="grid_row_'+sssnId+'">');
				sHTML.push(PS.View.getCardHTML(oChild));	
				sHTML.push('</div>');
			}
			sHTML.push('</div>');
	
			$('#grid').html(sHTML.join(''));
			$('#prescheduler').scrollTop(0);	
			$('#loading').hide();		
			
			$('#grid select').selectize({
				create: false
			});
	
		},
		getCardHTML: function(oChild){
			var oEntry = {};
	
			var sssnId = oChild["sssnId"];		
			var crd = oChild["firmId"];
			var vrsnId = oChild["vrsnId"];
			
			var sssnStatusCd = oChild["sssnStatusCd"];
			var sssnStatusDs = oChild["sssnStatusDs"];
			if(sssnStatusDs!=null) sssnStatusDs=sssnStatusDs.toUpperCase();
			
			var sssnStatusUser = oChild["sssnStatusUser"];
			var sssnStatusUserNm= (sssnStatusUser!=null && sssnStatusUser["firstname"]!=null && sssnStatusUser["lastname"]!=null ? (sssnStatusUser["lastname"]+ ", " + sssnStatusUser["firstname"]) : "");
			var sssnStatusDt = PS.View.formatDate(new Date(oChild["sssnStatusDt"]));
	
			var firmNm = oChild["firmNm"];
			
			var spDistrictCd=oChild["spDistrCd"]!=null && oChild["spDistrCd"].toString().trim()!="" ? oChild["spDistrCd"] : null;
			var spDistrictNm = oChild["spDistrDs"];
			var spDistrictTypeCd = oChild["spDistrTypeCd"];
			
			var spSupervisorStaffId=oChild["spSupervisorStaffId"]!=null && oChild["spSupervisorStaffId"].toString().trim()!="" ?  oChild["spSupervisorStaffId"] : null;
			var spSupervisorNm=oChild["spSupervisorNm"];
			
			//var spApprovedFl = oChild["spApprovedFl"];
			var spApproveUser = oChild["spApproveUser"];
			var spApproveUserNm=(spApproveUser!=null && spApproveUser["firstname"]!=null && spApproveUser["lastname"]!=null ? (spApproveUser["lastname"]+ ", " + spApproveUser["firstname"]) : "");
			var spApproveDt =  PS.View.formatDate(new Date(oChild["spApproveDt"]));
					
			var fnDistrictCd=oChild["fnDistrCd"]!=null && oChild["fnDistrCd"].toString().trim()!="" ? oChild["fnDistrCd"] : null;
			var fnDistrictNm = oChild["fnDistrDs"];
			var fnDistrictTypeCd = oChild["fnDistrTypeCd"];
			
			var fnSupervisorStaffId=oChild["fnSupervisorStaffId"]!=null && oChild["fnSupervisorStaffId"].toString().trim()!="" ? oChild["fnSupervisorStaffId"] : null;
			var fnSupervisorNm=oChild["fnSupervisorNm"];
			
			//var fnApprovedFl = oChild["fnApprovedFl"];
			var fnApproveUser = oChild["fnApproveUser"];
			var fnApproveUserNm=(fnApproveUser!=null && fnApproveUser["firstname"]!=null && fnApproveUser["lastname"]!=null ? (fnApproveUser["lastname"]+ ", " + fnApproveUser["firstname"]) : "");
			var fnApproveDt = PS.View.formatDate(new Date(oChild["fnApproveDt"]));
			
			var flDistrictCd=oChild["flDistrCd"]!=null && oChild["flDistrCd"].toString().trim()!="" ? oChild["flDistrCd"] : null;
			var flDistrictNm = oChild["flDistrDs"];
			var flDistrictTypeCd = oChild["flDistrTypeCd"];
			
			var flSupervisorStaffId=oChild["flSupervisorStaffId"]!=null && oChild["flSupervisorStaffId"].toString().trim()!="" ? oChild["flSupervisorStaffId"] : null;
			var flSupervisorNm=oChild["flSupervisorNm"];
			
			//var flApprovedFl = oChild["flApprovedFl"];
			var flApproveUser = oChild["flApproveUser"];
			var flApproveUserNm=(flApproveUser!=null && flApproveUser["firstname"]!=null && flApproveUser["lastname"]!=null ? (flApproveUser["lastname"]+ ", " + flApproveUser["firstname"]) : "");
			var flApproveDt = PS.View.formatDate(new Date(oChild["flApproveDt"]));
            var flAppl = oChild["flId"] !=null;
					
			var auditMonth = oChild["auditMonth"];
			
			var fwsd=oChild["fwsdDt"] ? PS.View.formatDate(new Date(oChild["fwsdDt"])) : "";
			var ewsd=oChild["ewsdDt"] ? PS.View.formatDate(new Date(oChild["ewsdDt"])) : "";
			
			// Prepare data
			
			var isScheduled = (sssnStatusCd == SCHEDULED_STTS_CD || sssnStatusCd == ERROR_STTS_CD);
			var hasError =  sssnStatusCd == ERROR_STTS_CD;
			var isScheduleError = sssnStatusCd == ERROR_STTS_CD;	
			var isNma = (oChild["nmaFl"]) == true;			
			var isNonColabExam = (spDistrictTypeCd.toUpperCase()) === (fnDistrictTypeCd.toUpperCase());
			
			var nonColabRespDistrictCls=(spDistrictTypeCd.toUpperCase()=="SP" ? "sp_resp" : (spDistrictTypeCd.toUpperCase()=="FN"? "fn_resp": ""));
			
			var spRespDistrictCls=(spDistrictTypeCd.toUpperCase()=="SP" ? "sp_resp" : (spDistrictTypeCd.toUpperCase()=="FN"? "fn_resp": ""));
			var fnRespDistrictCls=(fnDistrictTypeCd.toUpperCase()=="SP" ? "sp_resp" : (fnDistrictTypeCd.toUpperCase()=="FN"? "fn_resp": ""));
			
			var isSpApproved= oChild["spApprovedFl"]==true;
			var isFnApproved= oChild["fnApprovedFl"]==true;
			var isFlApproved= oChild["flApprovedFl"]==true;
			
			var spApprovedCls=isSpApproved ? CMPNT_APPROVED_CLS : CMPNT_NOT_APPROVED_CLS;
			var fnApprovedCls=isFnApproved ? CMPNT_APPROVED_CLS : CMPNT_NOT_APPROVED_CLS;
			var flApprovedCls=isFlApproved ? CMPNT_APPROVED_CLS : CMPNT_NOT_APPROVED_CLS;
			
			var isSchedulingAllowed= !isScheduled && (isSpApproved && isFnApproved && isFlApproved);
			
			var fnFrequency = oChild["fnFrequency"] || "N/A";
			var fnComposite = oChild["fnComposite"];
			var fnImpact = oChild["fnImpact"];
			var fnLikelihood = oChild["fnLikelihood"];

			var spFrequency = oChild["spFrequency"] || "N/A";
			var spComposite = oChild["spComposite"];
			var spImpact = oChild["spImpact"];
			var spLikelihood = oChild["spLikelihood"];

			var hasNma = false;
			var hasRsaNma = false;
			var nmaTypesDs=[];
	
			var sHTML = [];
			sHTML.push('<div class="table_row_container card form-inline '+(isNonColabExam ? nonColabRespDistrictCls +'_district' : '')+'" sssnid="'+sssnId+'">');
			sHTML.push('<div class="alert alert-success"><i class="fa fa-check"></i>Updated Successfully!</div>');
			
			sHTML.push('<table cellspacing="0" cellpadding="0" style="width:100%; table-layout: fixed;"><tr class="table_row">');
			
			if(psPhase!=BUSINESS_REVIEW){
				sHTML.push('<td class="slct" id="slct_wr_'+sssnId+'">');				
					sHTML.push('<input type="checkbox" id="sel_' + sssnId + '" class="rowSelectCheckbox" '+( !isSchedulingAllowed ? "disabled " : "")+(!isSchedulingAllowed ? 'title=\"'+SCHEDULING_NOT_ALLOWED_MSG+'\"' : '')+'/>');				
				sHTML.push('</td>');
			}
			
			sHTML.push('<td class="fname noselect" id="firmNmLnk_'+sssnId+'">');			
						
				
				//rendering components table
				var sCompHTML = [];
				sCompHTML.push('<table cellspacing="0" cellpadding="0" style="width: 100%;" class="components '+(isNonColabExam ? 'non_colab' : 'colab')+'">');
					sCompHTML.push('<tr>');
						sCompHTML.push('<th class="sp"><div>Sales Practice</div></th>');
						sCompHTML.push('<th class="fn"><div>FINOP</div></th>');
						sCompHTML.push('<th class="floor"><div>Floor</div></th>');
					sCompHTML.push('</tr>');
					
					sCompHTML.push('<tr>');
					
					$.each(psComponents, function(compType){
						sCompHTML.push('<td class="'+compType+'">');
	
						sCompHTML.push('<table cellspacing="0" cellpadding="0" style="width: 100%;" class="'+compType+'_details '+nonColabRespDistrictCls+'">');
							sCompHTML.push('<tr>');

								var _components = psComponents[compType];
								
								$.each(_components, function( idx, item ) {
									var key = item["key"];
									var compAppl = oChild[key + "Id"] !=null;
									var compId = compAppl ? oChild[key + "Id"] : null;
									var isCompRqFl = (compAppl && oChild[key + "ReqrdFl"]==true) ? true : false;
									var isCompRqOvrrdFl = (compAppl && oChild[key + "ReqrdOvrrdFl"]==true ) ? true : (oChild[key + "ReqrdOvrrdFl"]==false ? false : undefined);
									var ovrrdReasonObj = overrideReasonsMap[oChild[key + "OvrrdReasonId"]];
									var ovrrdReason = ovrrdReasonObj ? (ovrrdReasonObj["businessReview"] == "Y" ? ovrrdReasonObj["desc"] + (oChild[key + "BusinessReview"] ? ", " +oChild[key + "BusinessReview"] : "") : ovrrdReasonObj["desc"]) : "";
									var compOvvrdReasonDs= compAppl ? ovrrdReason : "";
									
									var compOvvrdUserNm= compAppl ? formatText(oChild[key + "OvrrdUserNm"], false) : "";
									var compOvvrdDt= (compAppl && oChild[key + "OvrrdDt"]) ? PS.View.formatDate(new Date(oChild[key + "OvrrdDt"])) : null;
									var compOvvrdDs="'"+compOvvrdReasonDs+"' by "+compOvvrdUserNm+" on "+compOvvrdDt;
									
									var compFwsdDate = (compAppl && oChild[key + "FwActualDt"]) ? PS.View.formatDate(new Date(oChild[key + "FwActualDt"])) : "N/A";
									var compFwsdPrjDate = (compAppl && oChild[key + "FwPrjDt"]) ? PS.View.formatDate(new Date(oChild[key + "FwPrjDt"])) : "N/A";
																	
									var hasCompNmaFl = (compAppl && oChild[key + "NmaFl"]==true) ? true : false;
									var hasCompRsaNmaFl = (compAppl && oChild[key + "RsaNmaFl"]==true) ? true : false;
									
									var compNmaCls = hasCompNmaFl || hasCompRsaNmaFl ? "nmaCmpnt": "";
									
									if(hasCompNmaFl) hasNma = true;
									if(hasCompRsaNmaFl) hasRsaNma = true;
									
									sCompHTML.push('<td>');
										if (compAppl) {									
											//sCompHTML.push('<label '+(isCompRqOvrrdFl!=undefined ? 'title="'+compOvvrdDs+'"': '')+' class="'+compNmaCls+' '+ (isCompRqOvrrdFl != undefined ? "overridden" : "")+ '"><input type="checkbox" id="cmpnt_'+sssnId+'_'+compId+'"'+ (isScheduled ? ' disabled ' : '')+ ' onclick="PS.View.cmpntOvrrd(\'cmpnt_'+sssnId+ '_'+compId+ '\', '+ crd+ ', \''+item.label+'\', '+ isCompRqFl+ ', this.checked);" '+ ((isCompRqOvrrdFl || (isCompRqOvrrdFl == undefined && isCompRqFl)) ? "checked=checked" : "") + '" />'+item.label+'</label>');
											sCompHTML.push('<span class="checkbox">');
												sCompHTML.push('<label ' +(isCompRqOvrrdFl!=undefined ? 'title="'+compOvvrdDs+'"': '')+' class="'+compNmaCls+' '+ (isCompRqOvrrdFl != undefined ? "overridden" : "")+ '">');
												sCompHTML.push('<input type="checkbox" id="cmpnt_'+sssnId+'_'+compId+'"'+ (isScheduled ? ' disabled ' : '')+ ' onclick="PS.View.cmpntOvrrd(\'cmpnt_'+sssnId+ '_'+compId+ '\', '+ crd+ ', \''+item.label+'\', '+ isCompRqFl+ ', this.checked);" '+ ((isCompRqOvrrdFl || (isCompRqOvrrdFl == undefined && isCompRqFl)) ? "checked=checked" : "") + ' />');
												sCompHTML.push('<span class="icon"></span>' + item.label);
                                                if(ovrrdReasonObj && ovrrdReasonObj["businessReview"] == "Y"){
                                                    sCompHTML.push('<i class="fa fa-exclamation-circle" aria-hidden="true"></i>')
                                                }
												sCompHTML.push('</label>');
											sCompHTML.push('</span>');
											
										} else {
											sCompHTML.push('<label class="not_appl">'+item.label+'</label>');
										}

										sCompHTML.push('<div class="date" >');
                                        /*if(ovrrdReasonObj && ovrrdReasonObj["businessReview"] == "Y"){
                                            sCompHTML.push('<i class="fa fa-exclamation-circle" aria-hidden="true" title="Business Review"></i>')
                                        }*/
                                        sCompHTML.push((compAppl ? (compFwsdPrjDate != "N/A" ? "<i class='fa fa-calendar'></i>&nbsp;<span title='Projected date'>"+ compFwsdPrjDate + "</span>" : compFwsdDate) : "&nbsp;") );
                                        sCompHTML.push('</div>');
									sCompHTML.push('</td>');
								});							
									
								sCompHTML.push('</tr>');
							sCompHTML.push('</table>');		
						sCompHTML.push('</td>');
					});						
		
				sCompHTML.push('</tr>');
				sCompHTML.push('<tr>');
					// Check if same district or different responsible districts
					if (isNonColabExam) {
							var supervisorsStr=(spSupervisorStaffId!=null ? spSupervisorNm : "") + (fnSupervisorStaffId!=null && spSupervisorStaffId!=fnSupervisorStaffId ? (spSupervisorStaffId!=null ? "/"+fnSupervisorNm : fnSupervisorNm): "");
							var districtsStr=(spDistrictCd!=null ? spDistrictNm.toUpperCase() : "")+ (fnDistrictCd!=null && spDistrictCd!=fnDistrictCd ? (spDistrictCd!=null ? "/"+fnDistrictNm.toUpperCase() : fnDistrictNm.toUpperCase()) : "");
							var isSpFnApproved = spDistrictTypeCd.toUpperCase()=="SP" ? isSpApproved : isFnApproved;
							var spFnApprovedCls = spDistrictTypeCd.toUpperCase()=="SP" ? spApprovedCls : fnApprovedCls;
							var spFnApproveUserNm = spDistrictTypeCd.toUpperCase()=="SP" ? spApproveUserNm : fnApproveUserNm;
							var spFnApproveDt = spDistrictTypeCd.toUpperCase()=="SP" ? spApproveDt : fnApproveDt;
							
							sCompHTML.push('<td colspan="2" class="district '+ nonColabRespDistrictCls +'"><div>');
							if(districtsStr){
								sCompHTML.push('<div title="'+supervisorsStr+'"><span>'+ districtsStr +'</span><span class="type">'+(spDistrictTypeCd.toUpperCase()=="SP" ? 'Sales Practice' : 'FINOP')+'</span></div>');
								sCompHTML.push((!isScheduled ? ('&nbsp;<span class="approveToggleBtn"><label ><input type="checkbox" name="'+spDistrictTypeCd.toLowerCase()+'ApprovedFl" onclick="PS.View.approveDistrict(\''+sssnId+'\', this);" '+(isSpFnApproved ? 'checked' : '')+'/><i class="fa '+spFnApprovedCls+'" '+(isSpFnApproved ? ('title="Approved by '+spFnApproveUserNm+' on '+spFnApproveDt+'"') : '')+'></i></label></span>') : '<i class="fa '+spFnApprovedCls+'" '+(isSpFnApproved ? 'title="Approved by '+spFnApproveUserNm+' on '+spFnApproveDt+'"' : '')+'></i>'));		
							}
							sCompHTML.push('</div></td>');
						
					} else {				
							sCompHTML.push('<td class="district sp_resp"><div>');
							if(spDistrictNm){
								sCompHTML.push('<div title="'+spSupervisorNm+'"><span>'+ spDistrictNm.toUpperCase() +'</span><span class="type">Sales Practice</span></div>');
								sCompHTML.push((!isScheduled ? ('&nbsp;<span class="approveToggleBtn"><label ><input type="checkbox" name="spApprovedFl" onclick="PS.View.approveDistrict(\''+sssnId+'\', this);" '+(isSpApproved ? 'checked' : '')+'/><i class="fa '+spApprovedCls+'" '+(isSpApproved ? ('title="Approved by '+spApproveUserNm+' on '+spApproveDt+'"') : '')+'></i></label></span>') : '<i class="fa '+spApprovedCls+'" '+(isSpApproved ? 'title="Approved by '+spApproveUserNm+' on '+spApproveDt+'"' : '')+'></i>'));			
							}
							sCompHTML.push('</div></td>');
							sCompHTML.push('<td class="district fn_resp"><div>');
							if(fnDistrictNm){
								sCompHTML.push('<div title="'+fnSupervisorNm+'"><span>'+ fnDistrictNm.toUpperCase()+'</span><span class="type">FINOP</span></div>');
								sCompHTML.push((!isScheduled ? ('&nbsp;<span class="approveToggleBtn"><label ><input type="checkbox" name="fnApprovedFl" onclick="PS.View.approveDistrict(\''+sssnId+'\', this);" '+(isFnApproved ? 'checked' : '')+'/><i class="fa '+fnApprovedCls+'" '+(isFnApproved ? ('title="Approved by '+fnApproveUserNm+' on '+fnApproveDt+'"') : '')+'></i></label></span>') : '<i class="fa '+fnApprovedCls+'" '+(isFnApproved ? 'title="Approved by '+fnApproveUserNm+' on '+fnApproveDt+'"' : '')+'></i>'));			
							}
							sCompHTML.push('</div></td>');
					}
					
					sCompHTML.push('<td  class="district floor editable ' +(fnDistrictCd != flDistrictCd ? 'modified ' : '')+'" rowspan="2">');
					
					var iconHTML = '<i class="fa '+flApprovedCls+'" '+(isFlApproved ? ('title="Approved by '+flApproveUserNm+' on '+flApproveDt+'"') : '')+'></i>';
					if(flDistrictNm){
						if(!isScheduled && flAppl){
                            sCompHTML.push('<div>');
							sCompHTML.push('<select style="min-width: 170px;" onchange="PS.View.updatedFlDistrict('+sssnId+', this);" districtcd="'+ fnDistrictCd+'" >');
							
							sortedDistricts.forEach(function(val) {
								if(val["code"] == 'TF' || val["code"] == fnDistrictCd || val["code"] == spDistrictCd )
									sCompHTML.push('<option value="' + val["code"] + '" '+(flDistrictCd==val["code"] ? 'selected' : '')+'>' + val["name"]+ '</option>');
							});
							sCompHTML.push('</select>');
							sCompHTML.push('&nbsp;<span class="approveToggleBtn"><label ><input type="checkbox" name="flApprovedFl" onclick="PS.View.approveDistrict(\''+sssnId+'\', this);" '+(isFlApproved ? 'checked' : '')+'/>'+iconHTML+'</label></span>');
                            sCompHTML.push('</div>');
                        }else{
                            sCompHTML.push('<div ' + ( flSupervisorNm ? 'title="'+flSupervisorNm+'" ' : '') +'class="floorDistrictText">');
							sCompHTML.push('<span>'+ flDistrictNm.toUpperCase()+'</span>&nbsp;');
							sCompHTML.push(iconHTML);
                            sCompHTML.push('</div>');
						}
					}
					sCompHTML.push('</td>');
				sCompHTML.push('</tr>');
				
				//scores
				sCompHTML.push('<tr >');
					sCompHTML.push('<td class="spRiskInfo">');
						sCompHTML.push('<table cellpadding="0" cellspacing="0" width="100%" class="riskInfo">');
							sCompHTML.push('<tr>');
								sCompHTML.push('<td>'+ (spImpact ? 'SP IMPACT <span class="scoreBucket">'+spImpact+'</span>' : '&nbsp;') + '</td>');
								sCompHTML.push('<td>'+ (spLikelihood ? 'SP LIKELIHOOD <span class="scoreBucket">'+spLikelihood+'</span>' : '&nbsp;') +'</td>');
								sCompHTML.push('<td>'+ (spComposite ? 'SP COMPOSITE <span class="scoreBucket">'+spComposite+'</span>': '&nbsp;') + '</td>');
							sCompHTML.push('</tr>');
						sCompHTML.push('</table>');							
					sCompHTML.push('</td>');							
					
					sCompHTML.push('<td class="fnRiskInfo">');
						sCompHTML.push('<table cellpadding="0" cellspacing="0" width="100%" class="riskInfo">');						
							sCompHTML.push('<tr>');
								sCompHTML.push('<td>'+ (fnImpact ? 'FN IMPACT <span class="scoreBucket">'+fnImpact+'</span>' : '&nbsp;')+'</td>');
								sCompHTML.push('<td>'+ (fnLikelihood ? 'FN LIKELIHOOD <span class="scoreBucket">'+fnLikelihood+'</span>' : '&nbsp;')+'</td>');
								sCompHTML.push('<td>'+ (fnComposite ? 'FN COMPOSITE <span class="scoreBucket">'+fnComposite+'</span>' : '&nbsp;' ) + '</td>');
							sCompHTML.push('</tr>');
						sCompHTML.push('</table>');
					sCompHTML.push('</td>');					
				sCompHTML.push('</tr>');				
			sCompHTML.push('</table>');
						
			if(hasNma) nmaTypesDs.push("NMA");
			if(hasRsaNma) nmaTypesDs.push("RSANMA");			
			nmaTypesDs = nmaTypesDs.join("/");		

			var statuses = [];
			statuses.push("SP: " + (isSpApproved ? "Approved" : "Pending"));
			statuses.push("FN: " + (isFnApproved ? "Approved" : "Pending"));
			statuses.push("FLOOR: " + (isFlApproved ? "Approved" : "Pending"));
			statuses = statuses.join(" | ");

			//card header
			sHTML.push('<div class="cardHeader">');
				sHTML.push('<div class="firmName">' + (isNma ? ("<span class='nma'>" + nmaTypesDs + "</span>") : "") + firmNm +' ('+crd+ ')' + (auditMonth ? ' |<span class="metadata auditMonth">Audit Month: '+auditMonth+'</span>': "") + '</div>');

				sHTML.push('<div id="stts_'+sssnId+'" class="status '+(hasError ? 'alert-danger' : 'alert-warning')+'" '+ (isScheduleError ? 'title="An error has occured. We will try to reschedule this record tonight."' : '')+'>');
					sHTML.push(sssnStatusDs);

					sHTML.push('<span class="statusTooltip">');
					sHTML.push(statuses);
					sHTML.push('</span>');

				sHTML.push('</div>');
				
				if(isScheduled){
					sHTML.push('<div class="metadata scheduledInfo">');
						sHTML.push((sssnStatusUserNm!="" ? ((hasError ? "Error by " : "Scheduled by ")+sssnStatusUserNm) : "" )+ (sssnStatusDt!="" ? (" on "+sssnStatusDt) : "" ));
					sHTML.push('</div>');
				}
			sHTML.push('</div>');
			//card header ends
								
			//add components table to main html
			sHTML = sHTML.concat(sCompHTML);		
			
			sHTML.push('<div class="form-group">');
				sHTML.push('<label>Exam Announcement Date:</label>');
				sHTML.push('<div class="ewsd date-wrapper">');
				if (!isScheduled) {
					sHTML.push('<label class="input-group">');
						sHTML.push('<input class="form-control" id="ewsd_' + sssnId + '" onchange="PS.View.processEWSD(\'ewsd_' + sssnId + '\', this.value)" onclick="PS.View.setEwsdDate(this.id)"  '+(ewsd!="" ? 'value="'+ewsd+'"' : '')+'/>');
						sHTML.push('<div class="input-group-addon" id="ewsd_dp_'+sssnId+'"  onclick="PS.View.setEwsdDate(\''+'ewsd_'+ sssnId+'\')">');
								sHTML.push('<span class="glyphicon glyphicon-calendar"></span>');
						sHTML.push('</div>');
					sHTML.push('</label>');				
				}else{
                    sHTML.push(ewsd);

                }
					
				sHTML.push('</div>');
			sHTML.push('</div>');
			
			sHTML.push('<div class="form-group">');
				sHTML.push('<label>On-Site Date:</label>');
				sHTML.push('<div class="fwsd date-wrapper">');
				if (!isScheduled) {
					sHTML.push('<label class="input-group">');
						sHTML.push('<input class="form-control" id="fwsd_' + sssnId + '" onchange="PS.View.processFWSD(\'fwsd_' + sssnId + '\', this.value)" onclick="PS.View.setFwsdDate(this.id)"  '+(fwsd!="" ? 'value="'+fwsd+'"' : '')+'/>');
						sHTML.push('<div class="input-group-addon" id="fwsd_dp_'+sssnId+'"  onclick="PS.View.setFwsdDate(\''+'fwsd_'+ sssnId+'\')">');
								sHTML.push('<span class="glyphicon glyphicon-calendar"></span>');
						sHTML.push('</div>');
					sHTML.push('</label>');
				}else{
                    sHTML.push(fwsd);
                }
				sHTML.push('</div>');
			sHTML.push('</div>');		
			
			sHTML.push('<div class="actionWrapper clearfix"><a class="save">SAVE</a><a class="cancel">CANCEL</a></div>');
			
			sHTML.push('</td>');		
	
			sHTML.push('</tr></table>');
			sHTML.push('</div>');
			
			return sHTML.join('');
		},
		
		updateSelCheckBox: function(sssnId){
			
			var isSpFnApprovedFl=$("#appr_icn_SPFN_"+sssnId)!=null && $("#appr_icn_SPFN_"+sssnId).hasClass(CMPNT_APPROVED_CLS);
			var isSpApprovedFl=$("#appr_icn_SP_"+sssnId)!=null && $("#appr_icn_SP_"+sssnId).hasClass(CMPNT_APPROVED_CLS);
			var isFnApprovedFl=$("#appr_icn_FN_"+sssnId)!=null && $("#appr_icn_FN_"+sssnId).hasClass(CMPNT_APPROVED_CLS);
					
			var isFWSDValidFl=PS.View.validateDateValue("fwsd_"+sssnId, $("#fwsd_"+sssnId).val());
			var isEWSDValidFl=PS.View.validateDateValue("ewsd_"+sssnId, $("#ewsd_"+sssnId).val());
			var isAnyCmpntRequiredFl=false;
			
			
			$("[id^='cmpnt_"+sssnId+"_']").each(function() {
				if($(this).attr('checked')){
					isAnyCmpntRequiredFl=true;
				}
			});
			
			
			if((isSpFnApprovedFl || (isSpApprovedFl && isFnApprovedFl)) && isFWSDValidFl && isEWSDValidFl && isAnyCmpntRequiredFl){
				$("#sel_"+sssnId).removeAttr("disabled"); //html('<input type="checkbox" id="sel_' + sssnId + '" class="rowSelectCheckbox"/>');
				$("#sel_"+sssnId).removeAttr("title");
			}else{
				$("#sel_"+sssnId).attr('disabled', true);
				$("#sel_"+sssnId).attr('checked', false);
				$("#sel_"+sssnId).attr('title', SCHEDULING_NOT_ALLOWED_MSG);
			}
			
		},
	
		setFwsdDate: function(fwsd_sssnId) {
			
			var id = fwsd_sssnId.split("_");
			
			$('#fwsd_' + id[1]).datepicker({
				//altField : $("#fwsd_" + id[1]),
				dateFormat : 'mm/dd/y',
				showOtherMonths : true,
				selectOtherMonths : true,
				changeMonth : true,
				changeYear : true,
				minDate: new Date(),
				yearRange : '-0:+1',
				firstDay : 0,
				defaultDate : $("#fwsd_" + id[1]).val()
				,onSelect : PS.View.onSelectFWSD
			});
			$('#fwsd_' + id[1]).datepicker('show');
			
			/*$field=$('#' + fwsd_sssnId);
			if($field.val()!=""){
				$field.val($field.val());
			}else{
				$field.val("");
			}*/
	
		},
		
		onSelectFWSD: function( sDate, dpObj){
			PS.View.processFWSD(dpObj.id, sDate);
		},
		
		processFWSD: function(fwsd_sssnId, sDate){
			
	//		if($("#"+fwsd_sssnId).val()==sDate) return;
			var sssnId=fwsd_sssnId.split("_")[1];
			
			if(PS.View.validateDateValue(fwsd_sssnId, sDate)){
			
				$("#"+fwsd_sssnId).removeAttr('title');						
				var fwsd=(sDate!=null && sDate.toString().trim()!="") ? sDate : "";
				var data= {fwsdDt: fwsd};
				
				PS.View.saveFirmSession(sssnId, data);
				
			}else{
				PS.View.updateSelCheckBox(sssnId);
			}
		},
		
		setEwsdDate: function(ewsd_sssnId) {
			
			var id = ewsd_sssnId.split("_");
			
			$('#ewsd_'+ id[1]).datepicker({ 
				//altField : $("#ewsd_" + id[1]), 
				dateFormat : 'mm/dd/y',
				showOtherMonths : true,
				selectOtherMonths : true,
				changeMonth : true,
				changeYear : true,
				minDate: new Date(),
				yearRange : '-0:+1',
				firstDay : 0,
				defaultDate : $("#ewsd_" + id[1]).val()
				,onSelect : PS.View.onSelectEWSD
			});
			$('#ewsd_' + id[1]).datepicker('show');
			
			/*$field=$('#' + ewsd_sssnId);
			if($field.val()!=""){
				$field.val($field.val());
			}else{
				$field.val("");
			}*/
	
		},
		
		onSelectEWSD: function( sDate, dpObj){
			var sssnId=dpObj.id.split("_")[1];
			PS.View.processEWSD('ewsd_'+sssnId, sDate);
		},
			
		processEWSD: function(ewsd_sssnId, sDate){
			
			var sssnId=ewsd_sssnId.split("_")[1];
			
			if(PS.View.validateDateValue(ewsd_sssnId, sDate)){
				
				$("#"+ewsd_sssnId).removeAttr('title');		
				
				var ewsd=(sDate!=null && sDate.toString().trim()!="") ? sDate : "";
				var data = { ewsdDt: ewsd };
				
				PS.View.saveFirmSession(sssnId, data);
				
			}else{
				PS.View.updateSelCheckBox(sssnId);
			}
		},
	
		validateDateValue: function(fldNm_sssnId, sDate) {
			var fieldNm=fldNm_sssnId.split("_")[0];
			var sssnId=fldNm_sssnId.split("_")[1];
			
			// Field is optional
			if(sDate==null || sDate.toString().trim()==""){
				$("#"+fieldNm+"_" + sssnId).removeClass('has-error');
				$("#"+fieldNm+"_" + sssnId).removeAttr('title');
				
				return true;
			}
			
			// returns true if valid date
			if (/^(0?[1-9]|1[012])\/(0?[1-9]|[12]\d|3[01])\/(\d{2}|(20\d{2}))$/.test(sDate)
					&& parseInputDate(sDate) && (
							parseInputDate(sDate)>=(new Date()).setHours(0,0,0,0)
					) || sDate == '') {
				$("#"+fieldNm+"_" + sssnId).removeClass('has-error');
				$("#"+fieldNm+"_" + sssnId).removeAttr('title');
				return true;
			}
			$("#"+fieldNm+"_" + sssnId).addClass('has-error');
			$("#"+fieldNm+"_" + sssnId).attr('title','Enter a valid date');
			return false;
		},	
		approveDistrict: function(sssnId, el){	
			var $el = $(el);
			var data = {};	
			var checked = $el.prop("checked");	
			$el.next("i").toggleClass(CMPNT_APPROVED_CLS, checked).toggleClass(CMPNT_NOT_APPROVED_CLS, !checked);		
			data[$el.attr("name")] =  checked;	
			
			PS.View.saveFirmSession(sssnId, data);
		},
		getPSCmpts: function(sssnId, callback, errorCallback){
			$('#loading').show();
			$.ajax({
				type : "POST",
				url : GETPSCMPNTS_URL + '/'+ sssnId,
				cache : false,
				async : true,
				contentType: 'application/json',
				dataType : 'json'
				}).done(function(respData) {
					$('#loading').hide();
					var data = respData;
					if (!data || data["status"] == "-1") {
						EXAM.Error.create({"error" : 'Request failed'});
						return;
					}
					
					//redraw cards
					var exams = data["psExams"];
					for (var i = 0; i < exams.length; i++) {
						PS.View.redrawRow(exams[i]);					
					}					
					
					if(callback) callback(respData);
					
				}).fail(function(jqXHR, textStatus) {
					$('#loading').hide();
					
					EXAM.Error.create({
						"error" : 'Request failed. ' + textStatus
					});
					
					if(errorCallback) errorCallback(jqXHR, textStatus);
				}
			);
		},
		saveFirmSession: function(sssnId, data, callback, errorCallback){
			$('#loading').show();
            $.publish("onSaveFirmSession");
			$.ajax({
				type : "POST",
				url: 'services/PS/saveFirmSession/'+ sssnId,
				data : JSON.stringify(data),
				cache : false,
				async : true,
				contentType: 'application/json',
				dataType : "json"
			}).done(function(respData) {
				$('#loading').hide();
				var data = respData;
				if (!data || data["status"] == "-1") {
					EXAM.Error.create({"error" : 'Unable to save firm session.'});
					return;
				}
				
				//redraw cards
				var exams = data["psExams"];

				for (var i = 0; i < exams.length; i++) {
					var oChild = exams[i];	
					var gridRow = PS.View.redrawRow(oChild);
					gridRow.find(".alert").fadeIn().delay(1200).fadeOut(800);				
				}			
				
				if(callback) callback(respData);
				
			}).fail(function(jqXHR, textStatus) {
				$('#loading').hide();
				
				EXAM.Error.create({
					"error" : 'Unable to save firm session. ' + textStatus
				});
				
				//refresh the row
				PS.View.getPSCmpts(sssnId);
				if(errorCallback) errorCallback(jqXHR, textStatus);
				
			});
		},
		redrawRow: function(oExam){	
			var sssnId = oExam["sssnId"];
			
			var gridRow = $('#grid_row_' + sssnId);
            if(gridRow.find("select").length) {
                gridRow.find("select")[0].selectize.destroy(); //must destroy the selectize instance
            }
			gridRow.html(PS.View.getCardHTML(oExam));	
			gridRow.find("select").selectize({
				create: false
			});
			
			return gridRow;
		},
		schedule: function() {
			var selected = $('input:checkbox:checked.rowSelectCheckbox').map(
					function() {
						return this.id;
					}).get();
			if (selected.length < 1){
                var content = "Please select at least one exam to schedule.";
                var oDialog = EXAM.Dialog.alert({'width': '500px'});
                oDialog.setContent("<div class='schedulingErrorWrapper'>" +content + "</div>");
                return;
			}


			var payload = {
				schedule : {}
			};
			var sessions = [];
	
			// var isInvalid=false;
			var notValidDates=[];
			//var notValidHrs=[];
			
			selected.forEach(function(id) {
	
				var sssnId = id.split("_")[1];
	
				var session = {};
				session["sssnId"] = sssnId;

				var ewsdId = 'ewsd_' + sssnId;
                var ewsdValue = $("#" +ewsdId).val();
                var isEwsdValid = PS.View.validateDateValue(ewsdId, ewsdValue);

                var fwsdId = 'fwsd_' + sssnId;
                var fwsdValue = $("#" +fwsdId).val();
                var isFwsdValid = PS.View.validateDateValue(fwsdId, fwsdValue);

                if(isFwsdValid && isEwsdValid) {
                    sessions.push(session);
                }else{
                    var firmSession = firmSessionMap[sssnId];
                    var divHTML = "<div class='sessionList'>" + firmSession.firmNm + " (" + firmSession.firmId + ")" + "</div>";
                    notValidDates.push(divHTML);
				}
				/*var dateValue = $('#fwsd_' + sssnId).val();
				session["prjFwsd"] = parseInputDate(dateValue);
	
				dateValue = $('#ewsd_' + sssnId).val();
				session["prjEwsd"] = parseInputDate(dateValue);*/

			});
			
			if(notValidDates.length>0){
				//alert((notValidDates.length>0 ? "Please enter a valid Projected FWSD for CRD(s): "+notValidDates.join(", ")+"\n" : "")+(notValidHrs.length>0 ? "Please enter a valid Budgeted Hrs for CRD(s): "+notValidHrs.join(", ") : ""));

                var oDialog = EXAM.Dialog.alert({'width': '500px'});
                var sErrors = [];
                sErrors.push("<div class='errorGroup'>");
                sErrors.push("<div class='errorDesc'>Please enter valid dates for the following:</div>");
                sErrors.push(notValidDates.join(""));
                sErrors.push("</div>");

                oDialog.setContent("<div class='schedulingErrorWrapper'>" +sErrors.join("") + "</div>");
				return;
			}

            $('#loading').show();
			$.ajax({
				type : "POST",
                url : 'services/PS/schedPS',
                data : JSON.stringify({sessions : sessions}),
				cache : false,
				async : true,
				contentType: 'application/json',
				dataType : "json"
			}).done(function(data) {

                $('#loading').hide();
				if (!data || !data["sessionResponses"]) {
					EXAM.Error.create({"error" : 'Schedule Request failed.'});
                    return;
				}
                var sResponses = data["sessionResponses"];
                var errorsMap = {};
                $.each(sResponses, function(idx, resp){
                    var status = resp["status"];
                    if(status!= 1){
                        var errors = errorsMap[status] || [];
                        var firmSession = firmSessionMap[resp["sssnId"]];
                        var divHTML = "<div class='sessionList'>" + firmSession.firmNm + " (" + firmSession.firmId + ")" + "</div>";
                        errors.push(divHTML);
                        errorsMap[status] = errors;
                    }
                });

                if(!$.isEmptyObject(errorsMap)) {
                    var errorDesc = {
                        "-104": "These firms had a combination of exam type and sub type that are not valid:",
                        "-102": "The combination of components selected for the firms below are invalid:",
                        "-1": "There was an issue scheduling the following:"
                    };
                    var sErrors = [];
                    $.each(errorsMap, function(cd, item){
                        sErrors.push("<div class='errorGroup'>");
                        sErrors.push("<div class='errorDesc'>" + errorDesc[cd] +"</div>");
                        sErrors = sErrors.concat(item);
                        sErrors.push("</div>");
                    });

                    PS.View.showScheduleErrorDialog(sErrors.join(""));
                }else {
                    g.render();
                   // g.setPageNumber(1, true);
                }
            }).fail(function(jqXHR, textStatus) {
                EXAM.Error.create({
                    "error" : 'Request failed: Unable to parse XML.' + textStatus
                });
                $('#loading').hide();

            });
        },
        showScheduleErrorDialog: function(content){
            var oDialog = EXAM.Dialog.create({'title': 'Oops! Something went wrong...', 'width': '500px'});
            oDialog.hideCancel();
            oDialog.setContent("<div class='schedulingErrorWrapper'>" +content + "</div>");

            oDialog.setDismissCallback(function(){
                //g.setPageNumber(1, true);
                oDialog.destroy();
                g.render();
            });
            oDialog.setPersistCallback(function(){
                //g.setPageNumber(1, true);
                oDialog.destroy();
                g.render();
            });
        },
		
		selectSssn: function(sssnId){
			var isChecked=$("#sel_"+sssnId).attr('checked');
			var isDisabled=$("#sel_"+sssnId).attr('disabled');
			if(!isDisabled) $("#sel_"+sssnId).attr('checked', !isChecked);
		},
		updatedFlDistrict: function(sssnId, el){				
			var data = {"flDistrictCd": $(el).val()};			
			PS.View.saveFirmSession(sssnId, data);
		},
		selectOvrrdReason: function(showTextarea){
			$(".businessReviewComment").toggle(showTextarea);
		},
		formatDate: function(s) {
			if (s == "")
				return null;
			try {
				var d=$.datepicker.formatDate('mm/dd/y', s);
				return d;
			} catch(e) {
				return null;	
			}
		}
	};
})();

$(function() {
	PS.View.init();
});

