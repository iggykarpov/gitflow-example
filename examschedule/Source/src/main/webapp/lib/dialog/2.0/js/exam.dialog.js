var EXAM = function(){
	return {
		
	};
}();

EXAM.Dialog = (function() {
	var uniqueKey = 1000000;
	return {
		dialogs : {},
		getNextUniqueKey : function() {
			return "dialog_" + (uniqueKey++);
		},
		create :  function(params){
			var dialogTitle = '';

			var oWin = findWorkspace(window);
			if (!oWin) {
				console.log("Unable to locate Workspace window.");
				return;
			}
			
			var sUniqueKey = oWin.EXAM.Dialog.getNextUniqueKey();

			if(params["title"]&& params["title"].length > 0)
				dialogTitle = params["title"];
			else
				dialogTitle = 'Alert';
			
			if(!params['width'])
				params['width'] = '450px';
			
			var zIndex = 12000 + 2*(oWin.EXAM.Dialog.getCurrentDialogCount()); //Object.keys(EXAM.Dialog.dialogs).length;//
			
			if(oWin.EXAM.Dialog.getCurrentDialogCount() == 0)
				oWin.$('body').append('<div class="overlay overlay_' + sUniqueKey + '"></div>');
			else
				oWin.$('body').append('<div class="overlay overlay_' + sUniqueKey + '" style="z-index:'+(zIndex-1)+' "></div>');
			
			var sHTML = [];
			sHTML.push('<div class="dialog' + (params["customClass"] ? ' ' + params["customClass"] : '') + '" id="'+sUniqueKey+'">');
			sHTML.push('<div class="dialogHeader">');
			//sHTML.push('<div class="dialogItem icon"></div>');
			sHTML.push('<div class="dialogItem title"><h2>'+dialogTitle+'</h2></div>');
			sHTML.push('<div class="dialogItem close" title="Close"><span onclick="EXAM.Dialog.getDialogByID(\''+sUniqueKey+'\').doDismissCallback()" title="Close">&times;</span></div>');
			sHTML.push('</div>');
			sHTML.push('<div class="dialogBodyContainer">');
			sHTML.push('<div class="dialogBody clearfix">');
			if (params["content"])
				sHTML.push(params["content"]);
			sHTML.push('</div>');
			sHTML.push('</div>');
			sHTML.push('<div class="dialogFooter"><div class="footerInfo"></div>');
			if(!params["hideDialogActions"]) {
				sHTML.push('<div class="dialogActions">');
				sHTML.push('<a href="#" class="dialogAction cancelAction" onclick="EXAM.Dialog.getDialogByID(\''+sUniqueKey+'\').doCancelCallback(); return false;">Cancel</a>');
				sHTML.push('<a href="#" class="dialogAction okAction" onclick="EXAM.Dialog.getDialogByID(\''+sUniqueKey+'\').doPersistCallback(this); return false;">Ok</a>');
				sHTML.push('</div>');
			}
			sHTML.push('</div>');
			sHTML.push('</div>');
			
			oWin.$('body').append(sHTML.join(''));
			
			$('.overlay_'+sUniqueKey).find('.dialogHeader').focus();
			oWin.$('.overlay_'+sUniqueKey).find('.dialogHeader').focus(); // blur focus from text inputs on the page

			//if(params["resizable"])
				//$('#'+inDialogId).resizable({ containment: 'window' });
			
			//if(params["draggable"])
				//oWin.$('#'+inDialogId).draggable({ handle: '.dialogHeader', containment: 'window', scroll: 'false' });

			var obj = {
				dialogId : sUniqueKey,
				dialogTitle : dialogTitle,
				cancelCallback : null,
				persistCallback : null,
				dismissCallback : null,
				zIndex : zIndex,
				isDirty : false,
				getID : function(){
					return this.dialogId;
				},
				getSelector : function(){
					return ('#' + this.dialogId);
				},
				setTitle : function(inDialogTitle){
					this.dialogTitle = inDialogTitle;
					oWin.$('#'+this.getID()).find('.dialogHeader .dialogItem.title').html('<h2>'+inDialogTitle+'</h2>');
				},
				setFooterInfo : function(html){
					oWin.$('#'+this.getID()).find('.dialogFooter .footerInfo').html(html);
				},
				getTitle : function(){
					return this.dialogTitle;
				},
				setDirtyFlag : function(){
					this.isDirty = true;
				},
				getDirtyFlag : function(){
					var isDirty = false;
					var dlg = oWin.$('#'+sUniqueKey);
					if(dlg.find('iframe').length > 0)
						dlg = dlg.find('iframe').contents();
					
					dlg.find('input[type=text], textarea, select').each(function () {
						if($(this).attr("checkDirty") == "true"){
							if( (($(this).attr("defaultval")==null)?"":$(this).attr("defaultval")) != (($(this).val()==null)?"":$(this).val()) ){
						    	isDirty = true;
						    	return false;
							}
						}
					});
					return isDirty;
					
				},
				getDirtyFlagWithoutCheck : function(){
					return this.isDirty
				},
				setZIndex: function(inZIndex){
					oWin.$('#' + this.getID()).css({'z-index' : inZIndex});
					this.zIndex = inZIndex;
				},
				setDimensions : function(width, height) {
					if(width)
						oWin.$('#' + this.getID()).css({'width' : width});
					if(height)
						oWin.$('#' + this.getID()).css({'height' : height});
				},
				setPosition: function(){
					var windowHeight = oWin.innerHeight,
						windowWidth = oWin.innerWidth,
						inWidth = oWin.$('#' + this.getID()).outerWidth(),
						inHeight = oWin.$('#' + this.getID()).outerHeight();
					oWin.$('#'+this.getID()).css({'left' : ((windowWidth - inWidth)/2)+'px'});
					oWin.$('#'+this.getID()).css({'top' : ((windowHeight - inHeight)/2)+'px'});
				},
				getContent : function(){
					return oWin.$('#'+this.getID()).find('.dialogBody');
				},
				setContent : function(inContent){
					oWin.$('#'+this.getID()).find('.dialogBody').html(inContent);
					var self = this;
					oWin.$('#'+this.getID()).find('textarea').change(function(){ 
						self.setDirtyFlag();
					});
					this.setPosition();
				},
				setCancelCallback : function(cancelCallbackFunc, customStr){
					this.cancelCallback = cancelCallbackFunc;
					if(customStr !== undefined)
						(oWin.$('#'+this.getID()).find('.cancelAction')).html(customStr);
				},
				doCancelCallback : function(){
					if (!this.cancelCallback) {
						this.destroy();
						return;
					}
					this.cancelCallback();
				},
				setPersistCallback : function(persistCallbackFunc,customStr,disabledOnLoad){
					this.persistCallback = persistCallbackFunc;
					var okButton = oWin.$('#'+this.getID()).find('.okAction');
					if(customStr !== undefined)
						okButton.html(customStr);
					if(disabledOnLoad)
						okButton.attr('disabled','disabled').addClass('disabled');
				},
				doPersistCallback : function(o){
					if (o) {
						if (this.inactive)
							return;
						this.inactive = true;
						var oThis = this;
						window.setTimeout(function() { oThis.inactive = false;}, 2000);
					}
					if (!this.persistCallback) {
						alert("ok button clicked.");
						return;
					}
					this.persistCallback();
				},
				enablePersistButton : function(enable){
					var okButton = oWin.$('#'+this.getID()).find('.okAction');
					if(enable)
						okButton.removeAttr('disabled').removeClass('disabled');
					else
						okButton.attr('disabled','disabled').addClass('disabled');
				},
				setDismissCallback : function(dismissCallbackFunc){
					this.dismissCallback = dismissCallbackFunc;
				},
				doDismissCallback : function(){
					if (!this.dismissCallback) {
						this.destroy();
						return;
					}
					this.dismissCallback();
				},
				destroy : function(){
					oWin.$('#'+this.getID()).remove();
					delete oWin.EXAM.Dialog.dialogs[this.getID()];
					oWin.$('.overlay_'+this.getID()).remove(); 
				},
				hideCancel : function(){
					(oWin.$('#'+this.getID()).find('.cancelAction')).remove();
				}
			};
			
			if(params["component"])
				obj.setContent(params["component"].htmlContent);
			
			obj.setZIndex(zIndex);
			oWin.EXAM.Dialog.setDialogByID(sUniqueKey, obj);
			
			obj.setDimensions(params['width'], params['height']);
			obj.setPosition();
			$(oWin).resize(function(){
				obj.setDimensions();
				obj.setPosition();
			});
			
			return obj;
		},
		alert: function(params) {
			var dlg = EXAM.Dialog.create(params);
			dlg.hideCancel();
			dlg.setPersistCallback(function(){dlg.destroy();});
			return dlg;
		},
		/*confirm: function(inDialId){
			var conf=EXAM.Dialog.create(inDialId);
			conf.setTitle("Unsaved Changes");
			conf.setContent('You have unsaved changes.');
			return conf;
		},*/
		getDialogByID: function(inDialId){
			var oWin = findWorkspace(window);
			if (!oWin) {
				console.log("Unable to locatate Workspace window.");
				return;
			}
			return oWin.EXAM.Dialog.dialogs[inDialId];
		},
		setDialogByID: function(inDialId, obj) {
			var oWin = findWorkspace(window);
			if (!oWin) {
				console.log("Unable to locatate Workspace window.");
				return;
			}
			return oWin.EXAM.Dialog.dialogs[inDialId] = obj;
		},
		getCurrentDialogCount : function(){
			var oWin = findWorkspace(window);
			if (!oWin) {
				console.log("Unable to locatate Workspace window.");
				return 0;
			}
			return (oWin.$('body').find('.errorDialog ').length) + (oWin.$('body').find('.dialog').length);
		}
	};
})();