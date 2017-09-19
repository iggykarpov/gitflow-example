EXAM.Error = (function() {
	return{
		errors: {},
		create:  function(params){
			
			var oWin = findWorkspace(window);
			if (!oWin) {
				console.log("Unable to locate Workspace window.");
				return;
			}

			var currentErrorText = 	'Please help improve the system by clicking on the "Report this Error" button below and follow the instructions in the email. '+ 
				'If you need immediate assistance, please contact the FINRA Help Desk at 240-386-4141.';
			var error = (params['error'] ? params['error'] : 'There was an uncaught exception.');
			
			var sHTML = [];
			sHTML.push('<div class="overlay overlay_error"></div>');
			sHTML.push('<div class="errorInfo">');
			sHTML.push('<div class="header"> Oops! There was an error...</div>');
			//sHTML.push('<div class="content"> '+currentErrorText+' </div>');
            sHTML.push('<div class="content"><div style="margin-bottom: 15px;">'+error+'</div>'+currentErrorText+' </div>');
            sHTML.push('<div class="button ok">Ok</div>');
			sHTML.push('<div class="button reportError">Report this Error</div>');
			sHTML.push('</div>');
		
			oWin.$('body').append(sHTML.join(''));
			
			oWin.$('.errorInfo').css({'width' : 500+'px'});
			//oWin.$('.errorInfo').css({'height' : 155+'px'});
			
			var windowHeight = oWin.innerHeight,
			windowWidth = oWin.innerWidth,
			inWidth = oWin.$('.errorInfo').outerWidth(),
			inHeight = oWin.$('.errorInfo').outerHeight();
			oWin.$('.errorInfo').css({'left' : ((windowWidth - inWidth)/2)+'px'});
			oWin.$('.errorInfo').css({'top' : ((windowHeight - inHeight)/2)+'px'});
			
			oWin.$('.errorInfo').find('.ok').click(function(){
				oWin.$('.overlay_error').remove();
				oWin.$('.errorInfo').remove();
			});
			
			oWin.$('.errorInfo').find('.reportError').click(function(){
				oWin.$('.overlay_error').remove();
				oWin.$('.errorInfo').remove();

				var sSubject = 'Open Remedy ticket for Contacts error and assign to Exam Support Group';
		    	var sBody = [];
				sBody.push(' Please follow these instructions to report the error:');
				sBody.push('\n\n');
				sBody.push('1. Briefly describe the actions you were performing when the error occurred.\n');
				sBody.push('\n\n\n');
				sBody.push('2. If possible, please attach a screen shot of the error.');
				sBody.push('\n\n\n');
				sBody.push('Technical details of error:\n');
				sBody.push('a. Date/Timestamp of error: '+ (new getServerTime()));
				sBody.push('\n');
				sBody.push('b. Type of browser window being used: '+navigator.userAgent);
				sBody.push('\n');
				sBody.push('c. Screen size resolution: '+window.screen.width+'X'+window.screen.height);
				sBody.push('\n');
				sBody.push('d. View port size: Width:'+window.screen.availWidth+'  Height:'+window.screen.availHeight);
				sBody.push('\n');
				sBody.push('e. View port Location: '+window.location.href);
		    	
				var k = 'mailto:Help@finra.org?subject=' + encodeURIComponent(sSubject) + '&body=' + encodeURIComponent(sBody.join('')).substr(0, 1900);
				hiddenLink(k);
			});
			
		}
	};
})();