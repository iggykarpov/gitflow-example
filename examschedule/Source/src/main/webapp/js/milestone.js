if(window.location.host.lastIndexOf('finra.org') != -1) {
	document.domain = 'finra.org';
}

document.examWorkspaceComponent = {
	preClose : function(callback) {
		// reset dialog padding
		//$('.inner-popup-content', parent.document).attr('style', function(i, style) {
			//return style.replace(/margin[^;]+;?/g, '');
		//});
		
		if(!milestones.isDirty()) {
			callback.allowClose();
		} else {
			callback.addObstruction('Dirty');
			callback.obstructClose();
		}
	},
	workspaceAccess : {
		onChange : function(typeOfChange) {
			alert('Fire onChange event: ' + typeOfChange);
		},
		requestClose : function() {
			$(document.body).html('');
		}
	}
};

$(function() {
	milestones.init();
	
	// fix dialog padding
	//$('.inner-popup-content', parent.document).css('margin', '0');
});

var milestones = function() {
	var examId = null;
	var mattersJSON = {};
	var milestonesJSON = {};
	var targetJSON = {};
	var otherDatesJSON = {};
	var unsavedChangesJSON = {};
	var unsavedExamHoursJSON = {};
	var examHoursJSON = {};
	var commitmentsExistsArr = {}; 
	
	var isLoadingData = false;
	
	var STAR_TYPE_IDS = { PROJECTED: '1', REVISED: '2', ACTUAL: '3' };
	var STAR_TYPE_LABELS = { '1' : 'Projected', '2' : 'Revised', '3' : 'Actual', 'none' : 'Not Set' };
	var STAR_TYPE_CHAR = { '1' : 'P', '2' : 'R', '3' : 'A', 'none' : '' };
	var STAR_TYPE_COLORS = { '1' : { color: '#6a9cff'/*'#a9c6ff'*/ }, '2' : { color: '#6a9cff' }, '3' : { color: '#0055ff' }, 'none' : { color: 'transparent'/*'#b7b7b7'*/ } };
	var EXAM_HOURS_TYPES = { 'staffhoursinitial' : 1, 'staffhoursrevised' : 2, 'carryoverhoursinitial' : 3, 'carryoverhoursrevised' :  4};
	
	var DOMAIN_NAMES = { '1' : 'FINOP', '2' : 'SP', '3' : 'TFCE', '4' : 'Other' };
	
	var IS_MATTER_OPEN = (EXAM_INFO && EXAM_INFO['matterStatusId'] && EXAM_INFO['matterStatusId'] == 1);
	var IS_EXAM_OPEN = (EXAM_INFO && EXAM_INFO['examStatusId'] && EXAM_INFO['examStatusId'] == 1);
	var IS_STAFF_ASSIGNED = (EXAM_INFO && EXAM_INFO['staffAssignStatusId'] && EXAM_INFO['staffAssignStatusId'] == 1);
	var IS_READ_ONLY = (!IS_EXAM_OPEN || !IS_STAFF_ASSIGNED);
	
	var EXAM_COMPLETE_ID = '10';
	var ONSITE_START_ID = '2';
	var EXAM_WORK_START = '1';
	var IS_PRESERVE_EXAM_COMPLETE_ACTUAL = false;
	
	var LAST_UPDATED = null;
	
	var SHOULD_CLOSE_WINDOW = false;
	
	var NO_DATE = ' - ';
	
	var $container = $();
	
	var areEqual = function(date1, date2){
		if(date1 == '' && date2 == '')
			return true;
	    return removeServerTimestamp(date1) == removeServerTimestamp(date2);
	};
	
	var removeServerTimestamp = function(d) {
		return d.substring(0, d.indexOf('T'));
	};
	
	var getParamsFromURL = function() {
		examId = getParameter('examid');
		if (!examId) {
			showError('onLoad()', 'Exam ID is required.');
			return false;
		}
		return true;
	};
	
	var generateFrameHTML = function() {
		var sHTML = [];
		sHTML.push('<div id="loading"></div>');
		
		sHTML.push('<div id="errors"></div>');
		sHTML.push('<div id="readOnlyMode"></div>');
		
		sHTML.push('<div id="changes">');
		sHTML.push('<div class="updated-text"></div><div class="save-text"></div>');
		sHTML.push('</div>');
		
		sHTML.push('<div id="content">');
		sHTML.push('<div id="tabs">');
		sHTML.push('<ul>');
		sHTML.push('<li><a href="#milestones">Milestones</a></li>');
		sHTML.push('<li><a href="#otherdates">Other Dates</a></li>');
		sHTML.push('<li><a href="#hours">Exam Hours</a></li>');
		sHTML.push('</ul>');
		sHTML.push('<div id="milestones" class="tab"></div>');
		sHTML.push('<div id="otherdates" class="tab"></div>');
		sHTML.push('<div id="hours" class="tab">');
		sHTML.push('</div>');
		sHTML.push('</div>');
		
		sHTML.push('<div id="footers">');
		sHTML.push('<div id="save"></div>');
		
		sHTML.push('<div id="legend">');
		sHTML.push('<div class="legend-item next">Next Milestone</div>');
		sHTML.push('</div>');
		
		sHTML.push('</div>');
		
		$('body').html(sHTML.join(''));

		generateSaveButtons();
		
		$container = $('body').find('#content');
		$container.find('#tabs').tabs({ heightStyle: 'fill' });
	};
	
	var generateSaveButtons = function(){
		var sHTML = [];
		if(IS_READ_ONLY) {
			sHTML.push('<input type="button" class="save-close-button" value="Ok" onclick="document.examWorkspaceComponent.workspaceAccess.requestClose(); return false;"/>');
		} else {
			sHTML.push('<input type="button" class="save-close-button" value="Save and Exit" onclick="milestones.save(true)"/>');
			sHTML.push('<input type="button" class="save-button" value="Save" onclick="milestones.save()"/>');
			sHTML.push('<a href="#" class="cancel" onclick="document.examWorkspaceComponent.workspaceAccess.requestClose(); return false;">Cancel</a>');
		}
		$('#save').html(sHTML.join(''));
	};
	
	var setupMatterExamHour = function(type, initRevised, $examHour){
		var matterId = $examHour.attr('matterid');
		if(!examHoursJSON[matterId][type])
			examHoursJSON[matterId][type] = {};
		var examHourInfo =  {
				id : $examHour.find(initRevised+type+'id').text(),
				typeid : EXAM_HOURS_TYPES[type+initRevised],
				original : $examHour.find(initRevised+type).text() == '' ? NO_DATE :  $examHour.find(initRevised+type).text(),
				current : $examHour.find(initRevised+type).text()== '' ? NO_DATE :  $examHour.find(initRevised+type).text(),
				updatedDate : $examHour.find(initRevised+type+'date').text(),
				updatedBy : $examHour.find(initRevised+type+'user').text()
		};
		examHoursJSON[matterId][type][initRevised] = examHourInfo;
	};
	
	var populateMatter = function(oDomain, milestoneId) {
		var matterJSON = {
			domainId : oDomain.attr('domainid'),
			isLate : oDomain.children('islate').text(),
			isApproaching : oDomain.children('isapproaching').text(),
			isNext : oDomain.children('isnext').text(),
			examId : oDomain.attr('examid'),
			isPrimary : oDomain.attr('primaryflag') == 'Y' ? 'true' : 'false',
			matterId : oDomain.attr('matterid'),
			name : DOMAIN_NAMES[oDomain.attr('domainid')],
			lastUpdatedDate : oDomain.children('lastupdateddate').text(),
			mattercategory : oDomain.children('mttrctgryid').text(),
			districtid : oDomain.attr('districtid'),
			districtname : oDomain.attr('districtname')
		};
		
		updateMatter(oDomain.attr('matterid'), matterJSON); // populate into list of domains
		
		matterJSON.dates = {};
		oDomain.children('dates').children('date').each(function() {
			var $date = $(this);
			var starTypeId = $date.children('startypeid').text();
			
			var oDate = {
				primaryKey : $date.attr('primarykey') || null,
				isEditable : $date.children('iseditable').text(),
				eventTypeId : $date.children('eventtypeid').text(),
				originalMilestoneDate : $date.children('milestonedate').text(), // original date on load (does not change)
				updatedDate : $date.children('updatedate').text(),
				updatedBy : $date.find('updatedby').length ? ($date.find('updatedby').attr('firstname') + ' ' + $date.find('updatedby').attr('lastname')) : '',
				starTypeId : starTypeId
			};
			if(matterJSON.isPrimary == 'true' && milestoneId == EXAM_COMPLETE_ID && starTypeId == STAR_TYPE_IDS.ACTUAL &&  IS_PRESERVE_EXAM_COMPLETE_ACTUAL) { // if on close fail reload
				var oExamCompletePrimary = milestones.getMilestone(EXAM_COMPLETE_ID, oDomain.attr('matterid'), STAR_TYPE_IDS.ACTUAL);
				oDate.milestoneDate = oExamCompletePrimary.milestoneDate; // copy over set values
				oDate.isUpdate = oExamCompletePrimary.isUpdate;
				oDate.previousMilestoneDate = oExamCompletePrimary.previousMilestoneDate;
				oDate.wasUpdate = oExamCompletePrimary.wasUpdate;
			} else {
				oDate.milestoneDate = $date.children('milestonedate').text(); // is current set date, will be saved if different from original
				oDate.isUpdate = ''; // is current update
				oDate.previousMilestoneDate = ''; // was previous date before edit mode was opened
				oDate.wasUpdate = ''; // was previously an update before edit mode was opened
			}
			matterJSON.dates[starTypeId] = oDate;
		});
		return matterJSON;
	};
	
	var updateMatter = function(mId, matterJSON) {
		var setMatter = mattersJSON[mId];
		if(setMatter == null || setMatter.lastUpdatedDate == '' || new Date(setMatter.lastUpdatedDate).getTime() < new Date(matterJSON.lastUpdatedDate).getTime())
			mattersJSON[mId] = $.extend({}, matterJSON);
		
		if(!LAST_UPDATED || new Date(LAST_UPDATED).getTime() < new Date(matterJSON.lastUpdatedDate).getTime()) // store last updated matter
			LAST_UPDATED = matterJSON.lastUpdatedDate;
	};
	
	var handleReadOnly = function(){
		if(!IS_READ_ONLY)
			return;
		
		var sHTML = [];
		sHTML.push('<div class="readOnlyIcon"></div>');
		sHTML.push('<div class="readOnlyMessage"><div class="readOnlyMessageContent">');

		sHTML.push('<div class="readOnlyArrow"></div>');
		sHTML.push('<div class="readOnlyTitle">Exam is Read Only</div>');
		sHTML.push('<p>This exam is read-only for the following reasons:</p>');

		sHTML.push('<ul>');
		if(!IS_EXAM_OPEN)
			sHTML.push('<li>The exam status has been set to closed</li>');
		if(!IS_MATTER_OPEN)
			sHTML.push('<li>STAR Matter has been closed</li>');
		if(!IS_STAFF_ASSIGNED)
			sHTML.push('<li>You may not have the proper access</li>');
		sHTML.push('</ul>');

		sHTML.push('</div></div>');

		var $readOnlySection = $('#readOnlyMode');
		$readOnlySection.html(sHTML.join('')).show();
		$readOnlySection.find('.readOnlyIcon').click(function() {
			$(this).siblings('.readOnlyMessage').toggle();
		});

		$(document).mouseup( function(e) { // Hide read only message if clicked
			if (!$readOnlySection.is(e.target) && $readOnlySection.has(e.target).length === 0)
				$readOnlySection.find('.readOnlyMessage').hide();
		});
		$readOnlySection.find('.readOnlyIcon').disableSelection();
		
		$container.addClass('readOnly');
	};
	
	var getCellClasses = function(oCell) {
		if(!IS_EXAM_OPEN)
			return '';
		var sClass = [];
		if(oCell.isNext == 'true')
			sClass.push('isnext');
		if(oCell.isLate == 'true')
			sClass.push('islate');
		if(oCell.isApproaching == 'true')
			sClass.push('isapproaching');
		return sClass.join(' ');
	};
	
	var isEditable = function(oCell) { // is one or more of the star dates editable
		var isEditable = false;
		for(var starId in STAR_TYPE_LABELS)
			if(starId != 'none' && oCell.dates[starId] && oCell.dates[starId].isEditable == 'true')
				isEditable = true;
		return isEditable;
	};
	
	var getEffectiveDate = function(oCell, forceType) {
		var oDate = null;
		if(forceType != null)
			oDate = oCell.dates[forceType];
		else if(oCell.dates['3'] && oCell.dates['3'].milestoneDate)
			oDate = oCell.dates['3'];
		else if(oCell.dates['2'] && oCell.dates['2'].milestoneDate)
			oDate = oCell.dates['2'];
		else if(oCell.dates['1'] && oCell.dates['1'].milestoneDate)
			oDate = oCell.dates['1'];
		else
			oDate = { milestoneDate: null, starTypeId : 'none' };
		return oDate;
	};
	
	var createExamHoursRow = function(examhourtype) {
		var tableHTML = [];
		for(var matterId in examHoursJSON) {
			var sEditable = !IS_READ_ONLY ? 'editable ' : 'readonly ';
			tableHTML.push('<div id="' + matterId+'_'+examhourtype + '" class="cell ' + sEditable + '" data-matter-id="' + matterId+'" exam-hour-type="'+examhourtype+'">');
			tableHTML.push(createExamHourCell(examhourtype, matterId));
			tableHTML.push('</div>');
		}
		
		return tableHTML.join('');
	};
	
	var createExamHourCell = function(examhourtype, matterId) {
		var oCell = examHoursJSON[matterId];
		var showType = oCell[examhourtype]['revised']['current'] != NO_DATE ? 'revised' : 'initial';
		var tableHTML = [];
		tableHTML.push('<div class="milestone-date-type"');
		tableHTML.push(' title=" '+(showType == 'revised' ? 'Revised' : 'Initial')+' Hours "');
		tableHTML.push(' style="background-color: #0055ff; color: #ffffff; border: 1px solid #0055ff;">'+(showType == 'revised' ? 'R' : 'I'));
		tableHTML.push('</div>');
		
		if(oCell[examhourtype]['initial'].current != oCell[examhourtype]['initial'].original ||
			oCell[examhourtype]['revised'].current != oCell[examhourtype]['revised'].original)
			tableHTML.push('<div class="has-edits" title="There are unsaved changes.">*</div>');
		tableHTML.push('<div class="milestone-date" >');
		tableHTML.push(oCell[examhourtype][showType]['current']);
		tableHTML.push('</div>');
		var sEditable = !IS_READ_ONLY ? 'editable ' : 'readonly ';
		tableHTML.push('<div class="milestone-edit"' + (IS_READ_ONLY == true ? ' title="This date cannot be modified."' : '') + '></div>');
		return tableHTML.join('');
	};
	
	var createMilestoneCell = function(oCell, forceType) {
		var oDate = getEffectiveDate(oCell, forceType);
		
		var sHTML = [];
		sHTML.push(getDateTypeFlag(oDate.starTypeId));
		
		var hasEdits = false;
		for(var starId in STAR_TYPE_LABELS)
			if(starId != 'none' && oCell.dates[starId] && oCell.dates[starId].isUpdate == 'true')
				hasEdits = true;
		if(hasEdits)
			sHTML.push('<div class="has-edits" title="There are unsaved changes.">*</div>');
		
		var sDate = parseMilestoneDatesFromServer(oDate.milestoneDate || '');
		sHTML.push('<div class="milestone-date" data-date="' + sDate + '">');
		sHTML.push(sDate != '' ? formatMilestoneDate(sDate) : NO_DATE);
		sHTML.push('</div>');
		
		sHTML.push('<div class="milestone-edit"' + (isEditable(oCell) != true ? ' title="This date cannot be modified."' : '') + '></div>');

		return sHTML.join('');
	};
	
	var getDateTypeFlag = function(sType, bFull) {
		var oColor = STAR_TYPE_COLORS[sType];
		var sHTML = [];
		sHTML.push('<div class="milestone-date-type" data-date-type-id="' + sType + '"');
		sHTML.push(' title="' + STAR_TYPE_LABELS[sType] + ' Date"');
		sHTML.push(' style="background-color: ' + oColor.color + '; color: #ffffff; border: 1px solid ' + oColor.color + ';">');
		sHTML.push(bFull ? STAR_TYPE_LABELS[sType] : STAR_TYPE_CHAR[sType]);
		sHTML.push('</div>');
		return sHTML.join('');
	};
	
	
	var attachExamHourActions = function($tab){
		var $table = $tab.find('.dateTable');
		var $matterScroll = $tab.find('.matterScroll');
		var $dateScroll = $tab.find('.dateScroll');
		
		/** Custom scroll */
		$table.scroll(function() {
			$matterScroll.scrollLeft($(this).scrollLeft());
			$dateScroll.scrollTop($(this).scrollTop());
		});
		
		/** Attach tooltip */
		$tab.tooltip({
			items : '.cell:not(.edit-hour)',
			tooltipClass : 'milestone-tooltip',
			show: { 'delay' : '400' },
			position:  { my: 'left top', at: 'left bottom', collision: 'fit flip', within: '#tabs' },
			content : function(a) {
				if(isLoadingData)
					return false;
				if($(this).hasClass('edit-hour'))
					return false;
				var matterId = $(this).attr('data-matter-id');
				var examHourType = $(this).attr('exam-hour-type');
				
				var oExamHour = null;
				oExamHour = examHoursJSON[matterId][examHourType];
				if(oExamHour == null || $.isEmptyObject(oExamHour))
					return false;
				var sHTML = [];
				sHTML.push(getExamHourToolTipContent(oExamHour['initial'], 'Initial'));
				sHTML.push(getExamHourToolTipContent(oExamHour['revised'], 'Revised'));

				return sHTML.join('');
			}
		});
		$('.ui-tooltip').remove();
		
		/** Edit hours */
		$tab.find('.cell.editable').click(function() {
			if($(this).hasClass('edit-hour'))
				return;
			var $this = $(this);
			
			var matterId = $(this).attr('data-matter-id');
			var examHourType = $(this).attr('exam-hour-type');
			var oExamHour = null;
			
			oExamHour = examHoursJSON[matterId][examHourType];
			if(oExamHour == null || $.isEmptyObject(oExamHour))
				return false;
			
			// UI Cleanup before starting edit mode
			$tab.find('.cell.edit-hour').not($this).each(function(){ $(this).find('.set-values').click(); });
			$('.ui-tooltip').remove();
			$this.find('#edit-hour').remove();
			$this.addClass('edit-hour');
			
			var $table = $this.closest('.table');
			if($this.offset().top - $table.offset().top > $table.outerHeight() / 2 - $this.outerHeight() / 2)
				$this.addClass('flipY');
			if($this.offset().left - $table.offset().left > $table.outerWidth() / 2 - $this.outerWidth() / 2)
				$this.addClass('flipX');
			
			var sHTML = [];
			sHTML.push('<div id="edit-hour"><div class="edit-header ellipsis">Edit Staff Hours</div>');
			sHTML.push('<div class="edit-type edit-section" edit-type="initial" >');
			sHTML.push('<label><input type="radio" name="hour-type" value="initial" '+(oExamHour['revised']['current'] == NO_DATE ? 'checked':'')+'/><span class="milestone-date-type" style="background-color: #0055ff; color: #ffffff; border: 1px solid #0055ff;">Initial </span> </label>');
			sHTML.push('<div class="edit-type-hour">'+oExamHour['initial']['current']+'</div>');
			sHTML.push('<div class="edit-type-changes" title="Exam Hour has been changed and not yet saved.">*</div>');
			sHTML.push('</div>');
			
			sHTML.push('<div class="edit-type edit-section" edit-type="revised" >');
			sHTML.push('<label><input type="radio" name="hour-type" value="revised" '+(oExamHour['revised']['current'] != NO_DATE ? 'checked':'')+'/><span class="milestone-date-type" style="background-color: #0055ff; color: #ffffff; border: 1px solid #0055ff;">Revised </span> </label>');
			sHTML.push('<div class="edit-type-hour">'+oExamHour['revised']['current']+'</div>');
			sHTML.push('<div class="edit-type-changes" title="Exam Hour has been changed and not yet saved.">*</div>');
			sHTML.push('</div>');
			
			sHTML.push('<a href="#" class="cancel-values">Cancel</a>');
			sHTML.push('<input type="button" class="set-values" value="Set Values"/>');
			sHTML.push('</div>');
			$(sHTML.join('')).appendTo($this);
			
			var $date = $this.find('.milestone-date');
			$date.html('<input class="date-input" type="text" />');
			$date.find('input').select();
			
			if(oExamHour['initial'].current == oExamHour['initial'].original)
				$this.find('div[edit-type=initial]').find('.edit-type-changes').hide();
			if(oExamHour['revised'].current == oExamHour['revised'].original)
				$this.find('div[edit-type=revised]').find('.edit-type-changes').hide();
			
			var $input = $date.find('input');
			var $edit = $("#edit-hour");
			var $radios = $edit.find('input[name="hour-type"]');
			var inputVal = oExamHour['revised']['current'] != NO_DATE ? oExamHour['revised']['current'] : oExamHour['initial']['current'];
			
			
			$input.val(inputVal != NO_DATE ? inputVal : '');
			$input.select();
			if(examHourType == 'staffhours' && $edit.find('input[name="hour-type"]:checked').val() == 'initial' && oExamHour['initial'].original != NO_DATE)
				$input.attr('disabled', true);
			
			$input.on('keyup', function() {
				$input.val($input.val().trim());
				$input.closest('.milestone-date').removeClass('invalidHour');
				if(isNaN($input.val()) || $input.val() < 0){
					$input.closest('.milestone-date').addClass('invalidHour');
						return;
				}
				if(oExamHour[$edit.find('input[name="hour-type"]:checked').val()].original ==  $input.val())
					$this.find('div[edit-type='+$edit.find('input[name="hour-type"]:checked').val()+']').find('.edit-type-changes').hide();
				else
					$this.find('div[edit-type='+$edit.find('input[name="hour-type"]:checked').val()+']').find('.edit-type-changes').show();
				
				$edit.find('input[name="hour-type"]:checked').closest('.edit-section').find('.edit-type-hour').html(($input.val() == '' ? NO_DATE : $input.val()));
			});
			
			$radios.change(function() {
				var examhour_type = $(this).val();
				if(examHourType == 'staffhours' && examhour_type == 'initial' && examHoursJSON[matterId][examHourType]['initial'].original != NO_DATE)
					$input.attr('disabled', true);
				else
					$input.attr('disabled', false);
				var examhour = $(this).closest('.edit-section').find('.edit-type-hour').html();
				$input.val( examhour != NO_DATE ? examhour : '');
				$this.children('.milestone-date-type').html(examhour_type == 'initial'?'I':'R');
				$date.find('input').select();
				$input.closest('.milestone-date').removeClass('invalidHour');
			});

			// Reset pending values and close edit mode
			$edit.find('.cancel-values').click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				
				removeHourEditMode($this);
				$this.html(createExamHourCell(examHourType, matterId));
			});
			
			$edit.find('.set-values').click(function(e) {
				e.stopPropagation();
				var inputVal = $input.val().trim();
				if(inputVal != '' && !isNaN(inputVal) && inputVal >= 0)
					$edit.find('input[name="hour-type"]:checked').closest('.edit-section').find('.edit-type-hour').html((inputVal == '' ? NO_DATE : inputVal));
				$edit.find('input[name="hour-type"]').each(function(){
					var input = $(this).closest('.edit-section').find('.edit-type-hour').html();
					if(input != NO_DATE && !isNaN(input)){
						if(decimalPlaces(input) <= 1)
							input = Number(input).toFixed(1);
						else
							input = Number(input).toFixed(2);
						oExamHour[$(this).val()].current = input;
					}else if(input == NO_DATE)
						oExamHour[$(this).val()].current = NO_DATE;
				});
				
				$this.html(createExamHourCell(examHourType, matterId));
				updateUnsavedChanges();
				removeHourEditMode($this);
			});
			
		});	
		
	};
	
	var getExamHourToolTipContent = function(oExamHour, type){
		var sHTML = [];
		sHTML.push('<div class="tooltip-row '+(oExamHour['original'] == NO_DATE?'notset':'')+'">');
		sHTML.push('<span class="milestone-date-type" style="background-color: #0055ff; color: #ffffff; border: 1px solid #0055ff;">'+type+' </span>');
		sHTML.push('<span class="milestone-date"> '+(oExamHour['original'] == NO_DATE ? 'Not Set': oExamHour['original']));
		sHTML.push('</span> ');
		if(oExamHour.updatedDate && oExamHour.isUpdate != 'true')
			sHTML.push(' by <span>' + oExamHour.updatedBy + ' on ' + $.format.date(oExamHour.updatedDate, 'MM/dd/yy h:mm a') + '</span>');
		if(oExamHour.isUpdate == 'true')
			sHTML.push('<span class="edit-type-changes" title="This date has been changed and not yet saved.">*</span>');
		sHTML.push('</div>');
		return sHTML.join('');
	};
	var attachActions = function($tab) {
		var $table = $tab.find('.dateTable');
		var $matterScroll = $tab.find('.matterScroll');
		var $dateScroll = $tab.find('.dateScroll');
		
		/** Custom scroll */
		$table.scroll(function() {
			$matterScroll.scrollLeft($(this).scrollLeft());
			$dateScroll.scrollTop($(this).scrollTop());
		});
		
		/** Targets */
		var showHideTargetSection = function($header, $sections) {
			if($header.hasClass('expanded')) {
				$header.removeClass('expanded');
				$sections.removeClass('expanded').hide();
			} else {
				$header.addClass('expanded');
				$sections.show();
			}
		};
		
		$tab.find('.targets').click(function() {
			var $header = $tab.find('.targets');
			showHideTargetSection($header, $header.hasClass('expanded') ? $tab.find('.target, .target-milestone') : $tab.find('.target'));
		});
		$tab.find('.target').click(function() {
			var $header = $tab.find('.target[data-target-id="' + $(this).attr('data-target-id') + '"]');
			showHideTargetSection($header, $tab.find('.target-milestone').filter(function(){
				return $(this).attr('data-target-id') == $header.attr('data-target-id');
			}));
		});
		
		/** Attach tooltip */
		$tab.tooltip({
			items : '.cell:not(.edit-date)',
			tooltipClass : 'milestone-tooltip',
			show: { 'delay' : '400' },
			position:  { my: 'left top', at: 'left bottom', collision: 'fit flip', within: '#tabs' },
			content : function(a) {
				if(isLoadingData)
					return false;
				if($(this).hasClass('edit-date'))
					return false;
				var matterId = $(this).attr('data-matter-id');
				var milestoneId = $(this).attr('data-milestone-id');
				
				var oMilestone = null;
				var sTargetId = $(this).closest('.row').attr('data-target-id');
				if(sTargetId)
					oMilestone = targetJSON[sTargetId].milestones[milestoneId];
				else
					oMilestone = $(this).closest('#milestones').length ? milestonesJSON[milestoneId] : otherDatesJSON[milestoneId];
				if(oMilestone == null || $.isEmptyObject(oMilestone))
					return false;
				
				var oDates = oMilestone.matters[matterId].dates;
				var sHTML = [];
				for(var key in oDates) {
					var oDate = oDates[key];
					var sRowClass = oDate.milestoneDate != '' ? 'set' : 'notset';
					var errorType = $(this).hasClass('errorType1') ? 'errorType1' : ($(this).hasClass('errorType2') ? 'errorType2' : ($(this).hasClass('errorType3') ? 'errorType3' : ''));
					sHTML.push('<div class="tooltip-row ' + sRowClass + ($(this).hasClass('error') ? ' error ' : '') + errorType + '">');
					sHTML.push(getDateTypeFlag(oDate.starTypeId, true));
					sHTML.push('<span class="milestone-date">');
					if(oDate.milestoneDate != '')
						sHTML.push(parseMilestoneDatesFromServer(oDate.milestoneDate));
					else
						sHTML.push('Not Set');
					sHTML.push('</span>');
					if(oDate.updatedDate && oDate.isUpdate != 'true')
							sHTML.push(' by <span>' + oDate.updatedBy + ' on ' + $.format.date(oDate.updatedDate, 'MM/dd/yy h:mm a') + '</span>');
					if(oDate.isUpdate == 'true') {
						sHTML.push('<span class="edit-type-changes" title="This date has been changed and not yet saved.">*</span>');
						if(oDate.milestoneDate)
							sHTML.push('<span class="edited-date">' + parseMilestoneDatesFromServer(oDate.originalMilestoneDate) + '</span>');
					}
					sHTML.push('</div>');
				}
				return sHTML.join('');
			}
		});
		$('.ui-tooltip').remove();
		
		/** Edit dates */
		$tab.find('.cell.editable').click(function() {
			if($(this).hasClass('edit-date'))
				return;
			var $this = $(this);
			
			var oDateSet = null;
			var sTargetId = $this.closest('.row').attr('data-target-id');
			if(sTargetId)
				oDateSet = targetJSON[sTargetId].milestones;
			else
				oDateSet = $this.closest('#milestones').length ? milestonesJSON : otherDatesJSON;
			if(oDateSet == null)
				return;
			
			var oMilestone = oDateSet[$this.attr('data-milestone-id')];
			var oDates = oMilestone.matters[$this.attr('data-matter-id')];
			var effectiveDateType = $this.find('.milestone-date-type').attr('data-date-type-id');
			
			// UI Cleanup before starting edit mode
			$tab.find('.cell.edit-date').not($this).each(function(){ $(this).find('.set-values').click(); });
			$('.ui-tooltip').remove();
			
			// Start Edit Mode
			var $edit = addEditMode($this, oDates, oDates.name + ' ' + oMilestone.name);
			var $radios = $edit.find('input[name="date-type"]');
			var $input = updateMilestoneCell($this, oDates, effectiveDateType == 'none' ? $radios.filter(':enabled').first().val() : effectiveDateType);

			// Date type radio selection change
			var $radioSelected = effectiveDateType == 'none' ? $radios.filter(':enabled').first() : $radios.filter('[value="' + effectiveDateType + '"]');
			$radioSelected.prop('checked', 'true').attr('data-default', 'true'); // select date type
			
			var datePicker = null;
			var setDate = function(){
				if(datePicker != null) {
					datePicker.datepicker('destroy');
					datePicker = null;
				}
				
				if(storeDateValue($input, oDates.dates[$radioSelected.val()])) { // set values to be pending
					updateDateValues($edit, oDates);
					$input.removeClass('invalidDate');
				} else
					$input.addClass('invalidDate');
			};
			
			// Setup datepicker
			$this.on('click', '.milestone-edit', function(){
				var wasBlank = $input.find('.date-input').val() == '';
				if(datePicker != null) {
					datePicker.datepicker('destroy');
					datePicker = null;
					return;
				}
				datePicker = $input.find('#datepicker').datepicker({
					altField: $input.find('.date-input'),
					dateFormat: 'mm/dd/y', maxDate: $radioSelected.val() == '3' ? '0' : null,
					showOtherMonths : true, selectOtherMonths: true,
					changeMonth: true, changeYear: true,
					yearRange: '-10:+10', firstDay: 0,
					defaultDate: $input.find('.date-input').val(),
					onSelect: setDate
				});
				if(wasBlank)
					$input.find('.date-input').val('');
			});
			
			$radios.change(function() {
				setDate();
				$radioSelected = $(this);
				
				if(datePicker != null) {
					datePicker.datepicker('destroy');
					datePicker = null;
				}
				
				$this.removeClass('isnext islate isapproaching');
				if($radioSelected.attr('data-default') == 'true')
					$this.addClass(getCellClasses(oDates));
					
				$input = updateMilestoneCell($this, oDates, $radioSelected.val());
			});
			
			var typeTimeout = null;
			$this.on('input propertychange keyup', '.date-input', function() { $input.removeClass('invalidDate'); clearTimeout(typeTimeout); typeTimeout = setTimeout(setDate, 1000); });
			
			// Reset pending values and close edit mode
			$edit.find('.cancel-values').click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				for(dateId in oDates.dates) {
					var oDate = oDates.dates[dateId];
					if(!(areEqual(oDate.milestoneDate, oDate.previousMilestoneDate))) { // if date was changed in this edit session
						oDate.milestoneDate = oDate.previousMilestoneDate; // reset milestone date to previously set
						oDate.previousMilestoneDate = ''; // clear temp storage of previous date
						if(areEqual(oDate.originalMilestoneDate, oDate.milestoneDate)) { // remove isUpdate flag if no change from original
							delete oDate.isUpdate;
							delete oDate.wasUpdate;
						}
					}
				}
				removeEditMode($this);
				$this.html(createMilestoneCell(oDates)).off('input propertychange', '.date-input').off('click', '.milestone-edit');
			});

			// Set pending values to actual values and close edit mode
			$edit.find('.set-values').click(function(e) {
				e.stopPropagation();
				var checkedId = $edit.find('input[name="date-type"]:checked').val();
				
				storeDateValue($input, oDates.dates[checkedId], true);
				updateUnsavedChanges();
				
				removeEditMode($this);
				$this.html(createMilestoneCell(oDates)).off('input propertychange', '.date-input').off('click', '.milestone-edit');
			});
		});
	};
	
	var updateMilestoneCell = function($cell, oDates, forceType) {
		$cell.children(':not(#edit-date)').remove();
		$cell.prepend(createMilestoneCell(oDates, forceType ? forceType : null));
		return toggleDateInput($cell, true);
	};
	
	var storeDateValue = function($input, oDate, isSetValues) { // returns if valid date
		var sDate = $input.find('.date-input').val();
		if(/^(0?[1-9]|1[012])\/(0?[1-9]|[12]\d|3[01])\/(\d{2}|(20\d{2}))$/.test(sDate) && parseMilestoneDate(sDate) || sDate == '') { // valid date
			if(sDate != '') {
				sDate = sDate.replace(/\/(\d{2})$/, '/20$1'); // year must be > 2000
				sDate = $.datepicker.formatDate('yy-mm-dd', new Date(sDate)) + 'T12:00:00-04:00';
			}
			if(areEqual(sDate, oDate.originalMilestoneDate)) {
				delete unsavedChangesJSON[$input.closest('.cell').attr('id') + '_s' + oDate.starTypeId];
				if(isSetValues)
					delete oDate.wasUpdate;
				delete oDate.isUpdate;
			} else {
				unsavedChangesJSON[$input.closest('.cell').attr('id') + '_s' + oDate.starTypeId] = true;
				if(isSetValues)
					oDate.wasUpdate = 'true';
				oDate.isUpdate = 'true';
			}
			oDate.milestoneDate = sDate;
			return true;
		}
		return false;
	};
	
	var updateDateValues = function($edit, oDates) {
		for(var oDateType in oDates.dates) {
			var sDate = oDates.dates[oDateType].milestoneDate;
			var $editRow = $edit.find('.edit-type[data-star-id="' + oDateType + '"]');
			$editRow.find('.edit-type-date').html(sDate != '' ? parseMilestoneDatesFromServer(sDate) : NO_DATE);
			if(oDates.dates[oDateType].isUpdate == 'true')
				$editRow.find('.edit-type-changes').html('*');
			else
				$editRow.find('.edit-type-changes').html('');
		}
	};
	
	var toggleDateInput = function($cell, bShow) {
		var $date = $cell.find('.milestone-date');
		var sDate = $date.attr('data-date');
		if(bShow) {
			$date.html('<input class="date-input" type="text" value="' + sDate + '"/><div id="datepicker"></div>');
			$date.find('input').select();
			return $date;
		}
		var newDate = $date.find('input').val(); 
		$date.attr('data-date', newDate);
		return $date.html(newDate != '' ? formatMilestoneDate(newDate) : NO_DATE);
	};
	
	var addEditMode = function($cell, oDates, sName) {
		$cell.find('#edit-date').remove();
		
		$cell.addClass('edit-date');
		
		var $table = $cell.closest('.table');
		if($cell.offset().top - $table.offset().top > $table.outerHeight() / 2 - $cell.outerHeight() / 2)
			$cell.addClass('flipY');
		if($cell.offset().left - $table.offset().left > $table.outerWidth() / 2 - $cell.outerWidth() / 2)
			$cell.addClass('flipX');
		
		var sHTML = [];
		sHTML.push('<div id="edit-date"><div class="edit-header ellipsis">Edit ' + sName + '</div>');
		for(var starId in STAR_TYPE_LABELS) {
			if(starId == 'none' || oDates.dates[starId] == null)
				continue;
			var oDate = oDates.dates[starId];
			oDate.previousMilestoneDate = oDate.milestoneDate;
			var sDate = oDate.milestoneDate;
			sHTML.push('<div class="edit-type edit-section' + (oDate.isEditable == 'false' ? ' readonly' : '') + '" data-star-id="' + starId + '">');
			sHTML.push('<label><input type="radio" name="date-type" value="' + starId + '" ');
			if(oDate.isEditable == 'false') // is readonly star type
				sHTML.push('disabled="disabled" ');
			sHTML.push('/>' + getDateTypeFlag(starId, true) + '</label>');
			sHTML.push('<div class="edit-type-date">');
			sHTML.push(sDate != '' ? parseMilestoneDatesFromServer(sDate) : (oDate.isEditable == 'false' ? 'Cannot be edited' : NO_DATE));
			sHTML.push('</div>');
			sHTML.push('<div class="edit-type-changes" title="This date has been changed and not yet saved.">' + (oDates.dates[starId].isUpdate == 'true' ? '*' : '') + '</div>');
			sHTML.push('</div>');
		}
		sHTML.push('<a href="#" class="cancel-values">Cancel</a>');
		sHTML.push('<input type="button" class="set-values" value="Set Values"/>');
		sHTML.push('</div>');
		return $(sHTML.join('')).appendTo($cell);
	};
	
	var removeEditMode = function($cell) {
		$cell.removeClass('edit-date');
		toggleDateInput($cell, false);
		$cell.find('#edit-date').remove();
	};
	
	var removeHourEditMode = function($cell) {
		$cell.removeClass('edit-hour');
		$cell.find('#edit-hour').remove();
	};
	
	var checkExamHoursChanged = function() {
		unsavedExamHoursJSON = {};
		for(var matterId in examHoursJSON){
			var oExamHour = examHoursJSON[matterId];
			for(var type in oExamHour){
				for(var examhour in oExamHour[type]){
					if(oExamHour[type][examhour]['current'] != oExamHour[type][examhour]['original'])
						unsavedExamHoursJSON[matterId+'_'+type+'_'+examhour] = true;
				}
			};
		};
	};
	
	var updateUnsavedChanges = function() {
		checkExamHoursChanged();
		var $unsaved = $('#changes .save-text');
		var $button = $('#save .save-button');
		var nCount = Object.keys(unsavedChangesJSON).length;
		var nExamHourCount = Object.keys(unsavedExamHoursJSON).length;
		if(nCount <= 0 && nExamHourCount <= 0) {
			$('#save .save-button').addClass('disabled').attr('disabled', 'disabled').attr('title', 'There are no unsaved changes.');
			$('#changes .save-text').empty();
		} else {
			$button.removeClass('disabled').removeAttr('disabled').removeAttr('title');
			var sMessage = 'There ';

			if(nCount > 0 && nExamHourCount <= 0)
				sMessage = sMessage	+ (nCount == 1 ? 'is ' : 'are ') + nCount + ' unsaved date' + (nCount == 1 ? '' : 's');
			else if(nCount <= 0 && nExamHourCount > 0)
				sMessage = sMessage	+ (nExamHourCount == 1 ? 'is ' : 'are ') + nExamHourCount + ' unsaved Exam Hour' + (nExamHourCount == 1 ? '' : 's');
			else 
				sMessage = sMessage	+ (nCount == 1 ? 'is ' : 'are ') + nCount + ' unsaved date' + (nCount == 1 ? '' : 's')+' and ' + nExamHourCount + ' unsaved Exam Hour' + (nExamHourCount == 1 ? '' : 's');
			
			sMessage = sMessage + '.';
			//var sMessage = 'There are unsaved changes.';
			$unsaved.html('<div class="edit-type-changes">*</div> ' + sMessage + '<a href="#" class="reset-all" title="All unsaved changes will be reset to saved values.">Reset all</a>');
			$unsaved.find('.reset-all').click(function(e){
				e.preventDefault();
				milestones.render();
			});
		}
	};
	
	var resetUnsavedChanges = function() {
		unsavedChangesJSON = {};
		updateUnsavedChanges();
	};
	
	var setLastUpdated = function() {
		if(LAST_UPDATED)
			$('#changes .updated-text').removeClass('no-dates').html('Last saved on ' + $.format.date(LAST_UPDATED, 'MM/dd/yy h:mm a'));
		else
			$('#changes .updated-text').addClass('no-dates').html('No dates have been set.');
	};
	
	var saveAll = function(isClosePrimary) {
		$('#loading').show();

		$('.error').removeClass('error'); // remove errored out cells
		$('#errors').hide().find('.errors').empty(); // clear previous errors
		
		var examHoursPromises = [];
		
		for(mId in mattersJSON) { // try to save all domains
			examHoursPromises.push(milestones.saveExamHours(mId, function(bSuccess, oXML) {
				if(oXML == null) // nothing to save
					return;
				
				if(bSuccess){
					milestones.buildExamHoursJson(oXML);
				}else {
					SHOULD_CLOSE_WINDOW = false;
					handleSaveErrors(oXML, matterId);
				}
			}));
		}	
		
		$.when.apply(null, examHoursPromises).done(function(){
			var promises = [];
			var primaryId = milestones.getPrimaryMatterId();
			var allClosed = true;
			
			for(mId in mattersJSON) { // try to save all domains
				if(isClosePrimary && mId == primaryId) // if closing and saving primary domain
					continue; // skip iteration, save primary last
				
				promises.push(saveMatter(mId, function(bSuccess, isClosed, oXML, matterId) {
					if(!isClosed)
						allClosed = false;
					
					if(oXML == null) // nothing to save
						return;
					
					if(bSuccess){
						milestones.buildJSON(oXML); 
					}else {
						SHOULD_CLOSE_WINDOW = false;
						commitmentsExistsArr[matterId] = true;
						handleSaveErrors(oXML, matterId);
					}
				}));
				
			}
			
			$.when.apply(null, promises).done(function(){
					var primaryPromise = null;
					if(isClosePrimary) {
						if(!allClosed) { // if all other domains are not closed
							handleSaveErrors('All secondary domains must be closed before the primary domain can be closed.', primaryId);
						} else { // all domains are closed, save and close the primary*/
							primaryPromise = saveMatter(primaryId, function(bSuccess, isClosed, oXML) {
								if(oXML == null) // nothing to save, should never happen...
									return;
								
								if(bSuccess)
									milestones.buildJSON(oXML);
								else {
									SHOULD_CLOSE_WINDOW = false;
									commitmentsExistsArr[primaryId] = true;
									handleSaveErrors(oXML, primaryId);
								}
							});
						}
					}
					$.when(primaryPromise).done(function(){ // wait for primary to save if there is one
						if(SHOULD_CLOSE_WINDOW)
							document.examWorkspaceComponent.workspaceAccess.requestClose();
						milestones.setTabContent($container.find('#milestones'), milestonesJSON, targetJSON);
						milestones.setTabContent($container.find('#otherdates'), otherDatesJSON);
						milestones.setHoursContent($container.find('#hours'));
						updateUnsavedChanges();
						setLastUpdated();
						$('#saving').remove();
						$('#loading').hide();
					});
			});
		});
	};
	
	var saveMatter = function(matterId, callback) {
		var oMatter = mattersJSON[matterId];
		var saveRequest = {
			examId : examId,
			domainId : oMatter.domainId,
			lastUpdatedDate : oMatter.lastUpdatedDate,
			matterId : matterId,
		};
		if(oMatter.actionCode && oMatter.actionCode != '')
			saveRequest['removeCommitments'] = oMatter.actionCode;
		
		var oExamComplete = milestones.getMilestone(EXAM_COMPLETE_ID, matterId, STAR_TYPE_IDS.ACTUAL);
		var isClosed = oExamComplete.milestoneDate != '';
		
		var toSave = [];
		var toSaveIds = [];
		milestones.traverseMatter(matterId, function(oDate, mId, dId, sId, tId) {
			if(areEqual(oDate.milestoneDate, oDate.originalMilestoneDate))
				return;
			var m = {
				cntxtKey : oMatter.matterId,
				eventTypeId : oDate.eventTypeId
			};
			if(oDate.primaryKey)
				m.id = oDate.primaryKey;
			if(oDate.milestoneDate)
				m.eventDate = oDate.milestoneDate;
			if(tId) {
				var oTarget = tId.split('|');
				m.targetId = oTarget[1];
				m.targetType = oTarget[0];
			}
			toSave.push(m);
			toSaveIds.push(milestones.getIdentifier(dId, mId, tId, sId));
		});
		if(toSave.length < 1) {
		
			callback(true, isClosed);
			return;
		}
		
		saveRequest.milestones = toSave;
			
		return $.ajax({
			headers : { 'Accept' : 'application/xml', 'Content-Type' : 'application/json' },
			type : 'POST',
			url : oMatter.isPrimary == 'true' ? 'services/milestones/saveprimary' : 'services/milestones/save',
			data : JSON.stringify(saveRequest),
			cache : false,
			dataType : 'xml'
		}).done(function(data) {
			var oXML = $(data);
			
			if(oXML.find('response').attr('status') === '1') { // success
				document.examWorkspaceComponent.workspaceAccess.onChange('MILESTONE_UPDATED');
				
				if(matterId == milestones.getPrimaryMatterId()) {
					if(!areEqual(oExamComplete.milestoneDate, oExamComplete.originalMilestoneDate)) { // exam complete has changed
						document.examWorkspaceComponent.workspaceAccess.onChange('EXAM_STATUS_CHANGED');
						document.examWorkspaceComponent.workspaceAccess.onChange(isClosed ? 'EXAM_CLOSED' : 'EXAM_OPENED'); // is the current value now set?
						location.reload();
						return;
					}
				}
				
				// remove all unsaved changes that were successful
				for(var i = 0; i < toSaveIds.length; i++)
					delete unsavedChangesJSON[toSaveIds[i]];
				updateUnsavedChanges();
				
				oXML.find('domains domain[matterid!=' + matterId + ']').remove(); // remove unnecessary domains from result for reload
				
				callback(true, isClosed, oXML, matterId);
			} else
				callback(false, false, oXML, matterId);
		});
	};
	
	var handleSaveErrors = function(o, matterId) {
		var $errors = $('#errors');
		
		if($errors.is(':empty'))
			$errors.html('<div class="hide-errors"><a href="#" onclick="$(\'#errors\').hide(\'fade\', {}, 500); return false;">hide</a></div></div><ul class="errors"></ul>');
		
		if(typeof o === 'string') { // UI caught error
			$errors.find('.errors').prepend('<li class="errorTimestamp">' + $.format.date(new Date(), 'MM/dd/yy h:mm a') + '</li><li>' + o + '</li>');
			$errors.show('blind', {}, 500);
			return;
		}
		
		var oXML = o;
		var sHTML = [];
		if(Object.keys(mattersJSON).length > 1)
			sHTML.push('<li class="domainLabel">For Domain: ' + DOMAIN_NAMES[mattersJSON[matterId].domainId] +' ('+mattersJSON[matterId].districtname+') ' +'</li>');
		sHTML.push('<li class="errorTimestamp">' + $.format.date(new Date(), 'MM/dd/yy h:mm a') + '</li>');
		
		var isReset = false;
		var status = oXML.find('response').attr('status');
		switch(status) {
			case '-1': // Call error
				showError('/milestones/save', oXML.find('response').find('error').text());
				return;
			case '-2': // Concurrency error
				var oConcurrencyDialog = EXAM.Dialog.create({ 'title' : 'Save Milestones Error' });
				oConcurrencyDialog.setContent('The dates on this exam have been updated by you or another user. Click Ok to refresh or Cancel to stay.');
				var fnDestroy = function(){ oConcurrencyDialog.destroy(); };
				oConcurrencyDialog.setDismissCallback(fnDestroy);
				oConcurrencyDialog.setCancelCallback(fnDestroy);
				oConcurrencyDialog.setPersistCallback(function(){
					milestones.render();
					oConcurrencyDialog.destroy();
				});
				return;
			case '-3': // commitments error
				if($('.commitmentsDialog').length == 1)
					return;
				var oCommitmentsDialog = EXAM.Dialog.create({ 'title' : 'Confirm Commitments Removal', 'customClass' : 'commitmentsDialog' });
				var sHTML = [];
				sHTML.push('There are weekly commitments set that will be affected by this date change. The following Staff members on this exam have weekly commitments that must be removed: <br>');
				sHTML.push('<ul>');
				oXML.find('response staffList staff').each(function(){
					sHTML.push('<li>'+$(this).text()+'</li>');
				});
				sHTML.push('</ul>');
				sHTML.push('Please confirm to continue saving milestones.');
				oCommitmentsDialog.setContent(sHTML.join(''));
				var disMissCommitments = function(){
					oCommitmentsDialog.destroy();
					$.each(commitmentsExistsArr, function(key, value) {
						mattersJSON[key]['actionCode'] = null;
					});
					commitmentsExistsArr = {};
				};
				oCommitmentsDialog.setDismissCallback(disMissCommitments);
				oCommitmentsDialog.setPersistCallback(function(){
					$.each(commitmentsExistsArr, function(key, value) {
						mattersJSON[key]['actionCode'] = value;
					});
					commitmentsExistsArr = {};
					saveAll();
					oCommitmentsDialog.destroy();
				});
				oCommitmentsDialog.setCancelCallback(disMissCommitments);
				return;		
			case '-13': // STATUS_ERROR_STAR_MATTER_CLOSE
				sHTML.push('<li class="errorSummary">There were errors closing the matter. Please log into STAR to close the matter.</li>');
				isReset = true;
				
				// Only case where exam was closed and page should not hard refresh
				IS_EXAM_OPEN = false;
				IS_READ_ONLY = true;
				handleReadOnly();
				generateSaveButtons();
				
				break;
			case '-14': // STATUS_ERROR_STAR_DISPOSITON
				sHTML.push('<li class="errorSummary">There were errors in sending the dispositions to STAR.</li>');
				isReset = true;
				IS_PRESERVE_EXAM_COMPLETE_ACTUAL = true; // preserve exam complete actual for primary
				break;
			case '-15': // STATUS_ERROR_STAR_REOPEN_AFTER_DISPOSITON
				sHTML.push('<li class="errorSummary">There were errors re-opening the exam due to the failure to send the dispositions.</li>');
				isReset = true;
				IS_PRESERVE_EXAM_COMPLETE_ACTUAL = true; // preserve exam complete actual for primary
				break;
			case '-16': // STATUS_ERROR_STAR_MERGE_MATTER
				sHTML.push('<li class="errorSummary">Secondary domain matters must have MERGED action before primary domain can be closed. Please log into STAR to add MERGED actions.</li>');
				isReset = true;
				IS_PRESERVE_EXAM_COMPLETE_ACTUAL = true; // preserve exam complete actual for primary
				break;
			case '-17': // STATUS_ERROR_STAR_REOPEN_AFTER_MERGE
				sHTML.push('<li class="errorSummary">There was an error closing the exam, but the Exam Close Actual date could not be reset. Please log into to STAR to remove the date.</li>');
				isReset = true;
				IS_PRESERVE_EXAM_COMPLETE_ACTUAL = true; // preserve exam complete actual for primary
				break;
			default:
				//sHTML.push('<li class="errorSummary">There was an unknown error.</li>');
				break;
		}
		
		if(isReset) {
			document.examWorkspaceComponent.workspaceAccess.onChange('MILESTONE_UPDATED'); // fire the event about the milestones being updated
			if(status === '-13' || status === '-15')
				document.examWorkspaceComponent.workspaceAccess.onChange('EXAM_CLOSED'); // the exam has been closed, so notify the exam about the close 
			
			milestones.render(); // requesting the page to reload with latest values
		}

		sHTML.push('<ul class="errorList">');
		oXML.find('errorlistitems errorlistitem').each(function(){
			var oError = $(this);
			var oEntry = {};
			oEntry.code = oError.find('code').text();
			oEntry.targetId = oError.find('branchcrd').text();
			oEntry.milestoneId = oError.find('milestoneid').text();
			oEntry.milestoneName = $.trim(oError.find('milestonename').text());
			if(oError.children().length == 0)
				oEntry.message = 'Error: saveMilestones <br>' + oError.text();
			else
				oEntry.message = oError.find('message').text();
			oEntry.datetypeId = oError.find('datetypeid').text().toLowerCase();
			
			if(oEntry.milestoneId > 0) {
				if(oEntry.targetId > 0) { // is target
					$('t' + oEntry.targetId + '_d' + matterId + '_m' + oEntry.milestoneId).addClass('error errorType' + oEntry.datetypeId);
					sHTML.push('<li><b>Target #' + oEntry.targetId + (oEntry.milestoneName ? ' ' + oEntry.milestoneName: '') + '</b>: ' + oEntry.message + '</li>');
					//TODO show target
					//$('.branchheader').addClass('expanded');
					//$('.branchmilestone').show();
				} else { // is milestone or other date
					$('d' + matterId + '_m' + oEntry.milestoneId).addClass('error errorType' + oEntry.datetypeId);
					sHTML.push('<li>' + (oEntry.milestoneName ? '<b>' + oEntry.milestoneName + '</b>: ' : '') + oEntry.message + '</li>');
				}
			} else
				sHTML.push('<li>' + oEntry.message + '</li>');
		});
		sHTML.push('</ul>');
		
		$errors.find('.errors').prepend(sHTML.join(''));
		$errors.show('blind', {}, 500);
	};
	
	return {
		isExam : true,
		requestMilestones : function(callback){
			isLoadingData = true;
			$('#loading').show();
			$.ajax({
				type : 'GET',
				url : 'services/milestones/get', //'milestones2.xml'
				data: {
					examId: examId,
				},
				cache : false,
				dataType : 'xml'
			}).done(function(data) {
				var oXML = $(data);
				if (oXML.find('response').attr('status') !== '1') {
					showError('/milestones/get', oXML.find('response').find('error').text());
					return false;
				}
				
				milestones.buildJSON(oXML);
				
				if(callback)
					callback();
				
			}).fail(function(jqXHR, textStatus) {
				showError('getMilestones', textStatus);
			}).always(function() {
				$('#loading').hide();
			});
		},
		init : function(oXML) {
			if(!getParamsFromURL())
				return;
			
			generateFrameHTML();
			handleReadOnly();
			
			milestones.render();
		},
		render : function() {
			milestones.requestMilestones(function(){
				setLastUpdated();
				resetUnsavedChanges();
				
				// remaining unsaved change for exam complete actual for primary
				if(IS_PRESERVE_EXAM_COMPLETE_ACTUAL) {
					unsavedChangesJSON[milestones.getIdentifier(milestones.getPrimaryMatterId(), EXAM_COMPLETE_ID, null, STAR_TYPE_IDS.ACTUAL)] = true;
					updateUnsavedChanges();
					IS_PRESERVE_EXAM_COMPLETE_ACTUAL = false;
				}
				
				milestones.setTabContent($container.find('#milestones'), milestonesJSON, targetJSON);
				milestones.setTabContent($container.find('#otherdates'), otherDatesJSON);
				milestones.setHoursContent($container.find('#hours'));
			});
		},
		setHoursContent : function($tab){
			var sHTML = [];
			sHTML.push('<div class="scroll matterScroll"></div>');
			sHTML.push('<div class="scroll dateScroll"></div>');
			sHTML.push('<div class="table dateTable"></div>');
			$tab.html(sHTML.join(''));

			var matterHTML = [];
			for(var matterId in mattersJSON)
				matterHTML.push('<div class="column domain' + (mattersJSON[matterId].isPrimary == 'true' ? ' primary' : '') + '">' + mattersJSON[matterId].name+(mattersJSON[matterId].isPrimary == 'true' ? ' (Primary)' : '')+'<br>' +mattersJSON[matterId].districtname + '</div>');
			$tab.find('.matterScroll').html(matterHTML.join('')).addClass(function() {
				return Object.keys(mattersJSON).length <= 1 ? 'one-matter' : 'multi-matter'; // different styling for only 1 matter
			});
			
			$tab.find('.dateScroll').append('<div class="row date ellipsis" title="Staff Hours">Staff Hours</div>');
			$tab.find('.dateScroll').append('<div class="row date ellipsis" title="Carry Over">Carry Over</div>');
			
			//examHoursJSON[$examHour.attr('matterid')][type]['original']
			
			var tableHTML = [];
			tableHTML.push('<div class="row">');
			tableHTML.push(createExamHoursRow('staffhours'));
			tableHTML.push('</div>');
			tableHTML.push('<div class="row">');
			tableHTML.push(createExamHoursRow('carryoverhours'));
			tableHTML.push('</div>');

			$tab.find('.dateTable').append(tableHTML.join(''));
			
			attachExamHourActions($tab);
		},
		buildJSON : function(oXML) {
			oXML.find('response > milestones > milestone').each(function() {
				var $milestone = $(this);
				var milestoneId = $milestone.attr('milestoneid');
				var oMilestone = milestonesJSON[milestoneId];
				if(oMilestone == null) {
					oMilestone = { matters: {} };
					milestonesJSON[milestoneId] = oMilestone;
				}
				oMilestone.name = $milestone.children('milestonename').text();

				$milestone.find('domains > domain').each(function() {
					oMilestone.matters[$(this).attr('matterid')] = populateMatter($(this), milestoneId);
				});
			});

			oXML.find('response > targets > target').each(function() {
				var $target = $(this);
				var targetId = $target.attr('targettype') + '|' + $target.attr('targetid');
				var oTarget = targetJSON[targetId];
				if(oTarget == null) {
					oTarget = { milestones: {} };
					targetJSON[targetId] = oTarget;
				}
				oTarget.targetType = $target.attr('targettype');
				oTarget.targetDescription = $target.children('targetdesc').text();
				
				$target.find('milestones > milestone').each(function() {
					var $milestone = $(this);
					var milestoneId = $milestone.attr('milestoneid');
					var oMilestone = oTarget.milestones[milestoneId];
					if(oMilestone == null) {
						oMilestone = { matters: {} };
						oTarget.milestones[milestoneId] = oMilestone;
					}
					oMilestone.name = $milestone.children('milestonename').text();

					$milestone.find('domains > domain').each(function() {
						oMilestone.matters[$(this).attr('matterid')] = populateMatter($(this), milestoneId);
					});
				});
			});

			oXML.find('response > otherdates > otherdate').each(function() {
				var $milestone = $(this);
				var milestoneId = $milestone.attr('milestoneid');
				var oMilestone = otherDatesJSON[milestoneId];
				if(oMilestone == null) {
					oMilestone = { matters: {} };
					otherDatesJSON[milestoneId] = oMilestone;
				}
				oMilestone.name = $milestone.children('milestonename').text();
				oMilestone.type = $milestone.children('otherdatetype').text();

				$milestone.find('domains > domain').each(function() {
					oMilestone.matters[$(this).attr('matterid')] = populateMatter($(this), milestoneId);
				});
			});
			
			milestones.buildExamHoursJson(oXML);
			isLoadingData = false;
		},buildExamHoursJson : function(oXML){
			oXML.find('response > examhours > examhour').each(function() {
				var $examHour = $(this);
				examHoursJSON[$examHour.attr('matterid')] = {};
				examHoursJSON[$examHour.attr('matterid')].lastUpdatedDate = $examHour.find('lastupdateddate').text(),
				setupMatterExamHour('staffhours', 'initial', $examHour);
				setupMatterExamHour('staffhours', 'revised', $examHour);
				setupMatterExamHour('carryoverhours', 'initial', $examHour);
				setupMatterExamHour('carryoverhours', 'revised', $examHour);
			});
		},traverseAll : function(fn) {
			if(fn == null)
				return;
			for(mId in milestonesJSON)
				for(matId in milestonesJSON[mId].matters)
					for(sId in milestonesJSON[mId].matters[matId].dates)
						fn(milestonesJSON[mId].matters[matId].dates[sId], mId, matId, sId);
			for(mId in otherDatesJSON)
				for(matId in otherDatesJSON[mId].matters)
					for(sId in otherDatesJSON[mId].matters[matId].dates)
						fn(otherDatesJSON[mId].matters[matId].dates[sId], mId, matId, sId);
			for(tId in targetJSON)
				for(mId in targetJSON[tId].milestones)
					for(matId in targetJSON[tId].milestones[mId].matters)
						for(sId in targetJSON[tId].milestones[mId].matters[matId].dates)
							fn(targetJSON[tId].milestones[mId].matters[matId].dates[sId], mId, matId, sId, tId);
		},
		traverseMatter : function(matId, fn) {
			if(fn == null)
				return;
			for(mId in milestonesJSON)
				for(sId in milestonesJSON[mId].matters[matId].dates)
					fn(milestonesJSON[mId].matters[matId].dates[sId], mId, matId, sId);
			for(mId in otherDatesJSON)
				for(sId in otherDatesJSON[mId].matters[matId].dates)
					fn(otherDatesJSON[mId].matters[matId].dates[sId], mId, matId, sId);
			for(tId in targetJSON)
				for(mId in targetJSON[tId].milestones)
					for(sId in targetJSON[tId].milestones[mId].matters[matId].dates)
						fn(targetJSON[tId].milestones[mId].matters[matId].dates[sId], mId, matId, sId, tId);
		},
		getMilestone : function(mId, matId, sId) {
			var o = milestonesJSON[mId] || otherDatesJSON[mId] || null;
			if(o && matId) {
				o = o.matters[matId] || null;
				if(o && sId)
					o = o.dates[sId] || null;
			}
			return o;
		},
		getTarget : function(tId, mId, matId, sId) {
			var o = targetJSON[tId] || null;
			if(o && mId) {
				o = o.milestones[mId] || null;
				if(o && dId) {
					o = o.matters[matId] || null;
					if(o && sId)
						o = o.dates[sId] || null;
				}
			}
			return o;
		},
		getIdentifier : function(dId, mId, tId, sId) {
			var id = [];
			if(tId)
				id.push('t' + tId);
			if(dId)
				id.push('d' + dId);
			if(mId)
				id.push('m' + mId);
			if(sId)
				id.push('s' + sId);
			return id.join('_');
		},
		getByIdentifier : function(sIdentifier) {
			var id = sIdentifier.split('_');
			var dId = null; mId = null; tId = null; sId = null;
			for(var i = 0; i < id.length; i++) {
				switch(id[i].charAt(0)) {
					case 't':
						tId = id[i].substring(1);
						break;
					case 'd':
						dId = id[i].substring(1);
						break;
					case 'm':
						mId = id[i].substring(1);
						break;
					case 's':
						sId = id[i].substring(1);
						break;
				}
			}
			return tId ? milestones.getTarget(tId, mId, dId, sId) : milestones.getMilestone(mId, dId, sId);
		},
		getPrimaryMatterId : function() {
			for(mId in mattersJSON)
				if(mattersJSON[mId].isPrimary == 'true')
					return mId;
			return false;
		},
		setTabContent : function($tab, oDates, oTargets) {
			var sHTML = [];
			sHTML.push('<div class="scroll matterScroll"></div>');
			sHTML.push('<div class="scroll dateScroll"></div>');
			sHTML.push('<div class="table dateTable"></div>');
			$tab.html(sHTML.join(''));
			
			if($.isEmptyObject(oDates))
				return;
			
			var matterHTML = [];
			for(var matterId in mattersJSON)
				matterHTML.push('<div class="column domain' + (mattersJSON[matterId].isPrimary == 'true' ? ' primary' : '') + '">' + mattersJSON[matterId].name + (mattersJSON[matterId].isPrimary == 'true' ? ' (Primary)' : '')+ '<br>' +mattersJSON[matterId].districtname+ '</div>');
			$tab.find('.matterScroll').html(matterHTML.join('')).addClass(function() {
				return Object.keys(mattersJSON).length <= 1 ? 'one-matter' : 'multi-matter'; // different styling for only 1 matter
			});
			
			var dateHTML = [];
			var tableHTML = [];
			for(var dateId in oDates) {
				var oDate = oDates[dateId];
				
				var sName = oDate.name;
				var sImg = '';
				var oNumber = oDate.name.match(/\d+/);
				if(oNumber && oNumber[0] >= 1 && oNumber[0] <= 5) {
					sImg = '<img src="images/icon_number' + oNumber[0] + '_16x12.png" alt="' + oNumber[0] + '" />';
					sName = oNumber[0] == 1 ? oDate.name.replace(/\d+/, '') : '';
				}
				
				dateHTML.push('<div class="row date ellipsis" title="' + oDate.name + '">' + sName + sImg);
				if(dateId == '10' && !IS_EXAM_OPEN && IS_STAFF_ASSIGNED)
					dateHTML.push('<a href="#" id="reopen" onclick="milestones.reopen(); return false;">Reopen Exam</a>');
				dateHTML.push('</div>');
				
				tableHTML.push('<div class="row">');
				for(var matterId in oDate.matters) {
					var oCell = oDate.matters[matterId];
					var sEditable = isEditable(oCell) && !IS_READ_ONLY ? 'editable ' : 'readonly ';
					tableHTML.push('<div id="' + milestones.getIdentifier(matterId, dateId) + '" class="cell ' + sEditable + getCellClasses(oCell) + '" data-matter-id="' + matterId);
					tableHTML.push('" data-milestone-id="' + dateId + '">');
					tableHTML.push(createMilestoneCell(oCell));
					tableHTML.push('</div>');
				}
				tableHTML.push('</div>');
				
				if(dateId != '2' || $.isEmptyObject(oTargets)) // if after On-site start, push Targets
					continue;
				
				dateHTML.push('<div class="row date targets"><div class="img"></div>Targets</div>');
				tableHTML.push('<div class="row targets"></div>'); // empty header row
				
				for(var targetId in targetJSON) {
					var targetName = targetJSON[targetId].targetDescription + ' (' + targetId.split('|')[1] + ')';
					var targetType = targetJSON[targetId].targetType;
					dateHTML.push('<div class="row date target" title="' + targetName + '" data-target-id="' + targetId + '"><div class="img"></div>');
					dateHTML.push(targetType == 'REGISTERED_REP' ? 'Rep' : (targetType == 'BRANCH' ? 'Branch' : 'No type'));
					dateHTML.push('</div>');
					
					tableHTML.push('<div class="row target ellipsis" data-target-id="' + targetId + '">');
					tableHTML.push(targetName);
					tableHTML.push('</div>');
					
					var nCount = 1;
					for(var targetMilestoneId in targetJSON[targetId].milestones) {
						var oTargetMilestone = targetJSON[targetId].milestones[targetMilestoneId];
						var isLast = nCount == Object.keys(targetJSON[targetId].milestones[targetMilestoneId]).length;
						dateHTML.push('<div class="row date target-milestone' + (isLast ? ' last' : '') + '" data-target-id="' + targetId + '">' + oTargetMilestone.name + '</div>');

						tableHTML.push('<div class="row target-milestone' + (isLast ? ' last' : '') + '" data-target-id="' + targetId + '">');
						
						for(var targetMatterId in oTargetMilestone.matters) {
							var oCell = oTargetMilestone.matters[targetMatterId];
							var sEditable = isEditable(oCell) && !IS_READ_ONLY ? 'editable ' : 'readonly ';
							tableHTML.push('<div id="' + milestones.getIdentifier(targetMatterId, targetMilestoneId, targetId) + '" class="cell ' + sEditable + getCellClasses(oCell));
							tableHTML.push('" data-matter-id="' + targetMatterId);
							tableHTML.push('" data-milestone-id="' + targetMilestoneId + '">');
							tableHTML.push(createMilestoneCell(oCell));
							tableHTML.push('</div>');
						}
						tableHTML.push('</div>');
						nCount++;
					}
				}
			}
			$tab.find('.dateScroll').html(dateHTML.join(''));
			$tab.find('.dateTable').html(tableHTML.join(''));
			
			// target header
			$tab.find('.dateTable').find('.targets, .target, .target-milestone').width(function(){
				return $(this).parent().is(':visible') ? $(this).parent()[0].scrollWidth : ($('body').width()-220);
			});
			
			attachActions($tab);
		},
		isDirty : function() {
			var isDirty = false;
			milestones.traverseAll(function(oDate) {
				if(oDate.isUpdate == 'true')
					isDirty = true;
			});
			return isDirty;
		},
		save : function(isCloseWindow) {
			SHOULD_CLOSE_WINDOW = isCloseWindow || false;
			var primaryMatterId = milestones.getPrimaryMatterId();
			var oExamCompletePrimary = milestones.getMilestone(EXAM_COMPLETE_ID, primaryMatterId, STAR_TYPE_IDS.ACTUAL);
			if(oExamCompletePrimary.milestoneDate != '' && !areEqual(oExamCompletePrimary.milestoneDate, oExamCompletePrimary.originalMilestoneDate)) { // has exam complete been set?
				milestones.getCloseReports(primaryMatterId);
				return;
			}
			saveAll();
		},
		saveExamHours : function(matterId, callback){
			var oExamHour = examHoursJSON[matterId];
			var saveRequest = {
				examId : examId,
				matterId : matterId
			};
			var toSaveIds = [];
			for(var type in oExamHour){
				for(var examhour in oExamHour[type]){
					if(oExamHour[type][examhour]['current'] != oExamHour[type][examhour]['original']){
						var m = {
								examHourId : oExamHour[type][examhour]['id'],
								matterId : matterId,
								hoursTypeId : oExamHour[type][examhour]['typeid'],
								hours : (oExamHour[type][examhour]['current'] != NO_DATE ? oExamHour[type][examhour]['current'] : null),
								lastUpdatedDate : oExamHour[type][examhour]['lastUpdatedDate']
						};
						toSaveIds.push(m);
					}
				};
			};
			
			if(toSaveIds.length <=0 )
				return;
			saveRequest.lastUpdatedDate = examHoursJSON[matterId]['lastUpdatedDate'];
			saveRequest.examHours = toSaveIds;
			
			return $.ajax({
				headers : { 'Accept' : 'application/xml', 'Content-Type' : 'application/json' },
				type : 'POST',
				url : 'services/milestones/saveexamhours',
				data : JSON.stringify(saveRequest),
				cache : false,
				dataType : 'xml'
			}).done(function(data) {
				var oXML = $(data);
				if (oXML.find('response').attr('status') != '1') {
					callback(false, oXML);
				}else{
					document.examWorkspaceComponent.workspaceAccess.onChange('MILESTONE_UPDATED');
					callback(true, oXML);
				}	
			});
		},
		getCloseReports : function(primaryMatterId) {
			$('#loading').show();
			$.ajax({
				type : 'GET',
				url : 'services/milestones/getclosereports',
				data : {
					examId: examId
				},
				cache : false,
				dataType : 'xml'
			}).done(function(data) {
				var oXML = $(data);
				if (oXML.find('response').attr('status') != '1') {
					showError('/milestones/getclosereports', oXML.find('closereports').find('error').text());
					return false;
				}
				var closeAlert = EXAM.Dialog.create({ 'title' : 'Close Exam', 'width' : '500px' });
				var sHTML = [];
				sHTML.push('<div class="examCloseReports">You are about to close this exam.');
				
				var oOnsiteStart = milestones.getMilestone(ONSITE_START_ID, primaryMatterId, STAR_TYPE_IDS.ACTUAL);
				var oExamWorkStart = milestones.getMilestone(EXAM_WORK_START, primaryMatterId, STAR_TYPE_IDS.ACTUAL);
				var category = (mattersJSON[primaryMatterId].mattercategory && mattersJSON[primaryMatterId].mattercategory == 2) ? 'cause' : 'cycle';
				
				if(category == 'cycle' && (!oOnsiteStart || oOnsiteStart.milestoneDate == '')){
					sHTML.push('<br/><br/><span style="color:red">Actual date has not been provided for the On-site Start milestone.</span>');
					sHTML.push('<br/><br/>Reports will not be submitted to Report Center.');
					sHTML.push('By clicking OK, you acknowledge that the Exam can be closed without any reports being set to Report Center.');
				}else if(category == 'cause' && (!oExamWorkStart || oExamWorkStart.milestoneDate == '')){
					sHTML.push('<br/><br/><span style="color:red">Actual date has not been provided for the Exam Announcement milestone.</span>');
					sHTML.push('<br/><br/>Reports will not be submitted to Report Center.');
					sHTML.push('By clicking OK, you acknowledge that the Exam can be closed without any reports being set to Report Center.');
				}else{
					var $closereports = oXML.find('closereports closereport');
					if($closereports.length > 0) {
						sHTML.push('<br/><br/>The following reports will be submitted to Report Center:<ul>');
						$closereports.each(function() {
							sHTML.push('<li><b>' + $(this).find('reportname').text() + ':</b> ');
							sHTML.push($(this).find('filecount').text());
							sHTML.push($(this).find('filename').text());
							sHTML.push(' <span class="packageCode">(');
							sHTML.push($(this).find('packagecode').text());
							sHTML.push(')</span> </li>');
						});
						sHTML.push('</ul>');
					}
					if($closereports.length == 0)
						sHTML.push(' By clicking OK, you acknowledge that no reports will be sent to Report Center.');
					else
						sHTML.push(' By clicking OK, you acknowledge these are the correct reports to be sent to Report Center.');
				}
				sHTML.push('</div>');
				closeAlert.setContent(sHTML.join(''));
				closeAlert.setPersistCallback(function() {
					closeAlert.destroy();
					saveAll(true);
				});
			}).fail(function(jqXHR, textStatus) {
				showError('/milestones/getclosereports', textStatus);
			}).always(function(){
				$('#loading').hide();
			});
		},
		reopen: function(){
			var reopenDialog = EXAM.Dialog.create({ 'title': 'Reopen Exam', 'content': 'Are you sure you would like to reopen this exam?' });
			reopenDialog.setPersistCallback(function(){
				reopenDialog.destroy();
				milestones.resetActualDate(milestones.getPrimaryMatterId());
				milestones.save();
			});
		},
		resetActualDate: function(dId, isRefresh) {
			var oCompleteActual = milestones.getMilestone(EXAM_COMPLETE_ID, dId, STAR_TYPE_IDS.ACTUAL);
			oCompleteActual.milestoneDate = '';
		}
	};
}();

milestones.params = (function() {
	var arr = [];
	return {
		add : function(name, value) {
			arr.push(encodeURIComponent(name) + '=' + encodeURIComponent(value));
			return this;
		},
		clear : function() {
			arr = [];
			return this;
		},
		serialize : function() {
			return arr.join('&');
		}
	};
})();