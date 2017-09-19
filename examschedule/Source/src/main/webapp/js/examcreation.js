//Maven refactored project
if (window.location.host.lastIndexOf("finra.org") != -1) {
	document.domain = "finra.org";
}

document.examWorkspaceComponent = {
		preClose : function(callback) {
		},
		workspaceAccess : {
			onChange : function(typeOfChange) {
				alert("Fire onChange event: " + typeOfChange);
			},
			goToPlace : function(place, path) {
				alert("Go to Place requested for " + place + " with " + path);
			},
			requestClose : function() {
				$(document.body).html("");
			}
		}
	};
		

$(function() { 
	var examCreateMain = {};
	examCreateMain.tabMap = {
			"annualTabContent": "/esched/planning_annual.html",
			"individualTabContent": "/examcore/manage_exam.html"
	}
	examCreateMain.init = function (){
		$("#examcreation-main").hide();
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
			 var hasExamCreatePermission = oUserPerm.find("individual").length && oUserPerm.find("individual").text() == "Y";
						 
			 document.hasAnnualPermission = hasAnnualPermission;
			 document.hasExamCreatePermission = hasExamCreatePermission;
			 
			 if(!hasAnnualPermission && !hasExamCreatePermission){
				 $("#examcreation-main").remove();
				 EXAM.Error.create({"error" : 'You do not have permission to view this page.'});
				 return false;
			 }
			 $("#examcreation-main").show();
			 if(!hasAnnualPermission) {
				 $("#annualTab").remove();
				 $("#annualTabContent").remove();
			 }
			 if(!hasExamCreatePermission) {
				 $("#individualTab").remove();
				 $("#individualTabContent").remove();
			 }
			 
			 $( "#examcreation-tabs" ).tabs({
					activate: function(event, ui){
						var tabId = ui.newPanel.attr("id");
						var tabUrl = examCreateMain.tabMap[tabId];
						examCreateMain.loadTab(tabId, tabUrl);
					}					
			 });
				
			 var defaultTab = $("#examcreation-tabs .ui-state-active a");
			 var defaultTabId = defaultTab.attr("href").replace("#", "");
				
			 examCreateMain.loadTab(defaultTabId, examCreateMain.tabMap[defaultTabId]);
				
			 if(window.parent){
					var $doc = $(window.parent.document);
					var selectedTab = $doc.find(".tab-main-panel .tab-header-selected");
					if(selectedTab && selectedTab.length){
						var left = selectedTab.offset().left;
						var width = selectedTab.width();
						
						$("#tabHighlightEl").css("left", (left + (width/2) - 4) + "px");
					}
					
			 }
			
		}).fail(function( jqXHR, textStatus ) {
			$('#loading').hide();
			$("#examcreation-main").remove();
			EXAM.Error.create({"error" : 'Request failed.'});
		});	
		
		
	}
	
	examCreateMain.loadTab = function(tabId, url) {
		var iframeEl = $("#"+tabId).find("iframe");

	    if (iframeEl.length == 0) {
	    	//$("#loading").show();
	        var html = [];
	        html.push('<div class="tabIframeWrapper">');
	        html.push('<iframe class="iframetab" id="iframe_' + tabId + '" src="' + url + '">Loading...</iframe>');
	        html.push('</div>');
	        $("#"+tabId).append(html.join(""));
	        iframeEl = $("#"+tabId).find("iframe");
	        
	        iframeEl.unbind("load").load(function() {	    	    
		    	//iframeEl.show();
	    	    $("#loading").hide();
	    	});	 
	    }
	    /*else{
	    	iframeEl.hide();
	    	//document.getElementById('iframe_' + tabId).contentDocument.location.reload(true);
	    	iframeEl.attr("src", iframeEl.attr("src"));
	    	   	
	    }
	    */
	    
	    
	    return false;
	}
	
	examCreateMain.init();
	
	
});