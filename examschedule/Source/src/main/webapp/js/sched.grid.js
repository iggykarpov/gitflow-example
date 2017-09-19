PS.Grid = {};

PS.grid = function() {
	var request = function(config) {
		$('#loading').show();
		
		// clear 'select all' checkbox
		if($('#selectAll').length) $('#selectAll').attr('checked', false);
		
		if($('#export').length) $('#export').click(function(){ config.exportExcel(); });
			
			/*var url=null;
			if(this.getURL())
				url=this.getURL().substring(this.getURL().lastIndexOf('/') + 1);
			*/
			/*$.ajax({
				type : "GET",
				url : 'services/PS/exportExcel',
				data : {
					gridrequest:
						JSON.stringify({
								 smartFilters: PS.grid.filter.getFilters()})
				},
				cache : false,
				async : true,
				dataType : "text"
			}).done(function(data) {
				
			});*/
		//});
		
		
		
		var gridData=config.getGridInputData();
		gridData["smartFilters"]=PS.grid.filter.getFilters();

		var oCallBack = config.getCallback();
		
		$.ajax({
			type : "POST",
			url : config.getURL(), //["url"],
			data :
				JSON.stringify(gridData), //{pageSize:config["ps"],pageNum:config["pn"],smartFilters: PS.grid.filter.getFilters()}
			cache : false,
			async : true,
			contentType: 'application/json',
			dataType : 'json'
			}).done(function(data) {
				
				if (!data || data["status"] == "-1") {
					EXAM.Error.create({"error": 'Request failed'});
					$('#loading').hide();
					return false;
				}
				$('#prescheduler').scrollTop(0);
				var total = parseInt(data["total"]);
				if (total == 0) {
					$('#grid').html('<div class="noRecordsMsg">No records found.</div>');

					$("#pagination").css('visibility', 'hidden');
					$('#loading').hide();
					return;
				}

				var sHTML = [];
				var lastPage = Math.ceil(total / config.getPageSize());
				config.setNumPages(lastPage);

				var to = (config.getPageNumber() * config.getPageSize());

				var isFirst = (config.getPageNumber() == 1);
				var records= data["psExams"] ? data["psExams"].length : data.length;
				var isLast = (config.getPageNumber() == lastPage);

				var maxRecords=((config.getPageNumber()-1)*config.getPageSize())+records;
				sHTML.push('<ul class="pagination">');
				sHTML.push('<li><a href="#" id="prev"  title="Previous" ' + (isFirst ? 'class="disabled"': 'onclick="PS.View.prevPage()" ') + '><span>«</span></a></li>');
				sHTML.push('<li><span id="total">'+((config.getPageNumber()-1)*config.getPageSize()+1)+' - '+(maxRecords > total ? total: maxRecords)+' of '+total+'</span></li>');
				sHTML.push('<li><a href="#" id="next" title="Next" ' + (isLast ? 'class="disabled"': 'onclick="PS.View.nextPage()" ') +'><span>»</span></a></li>');
				sHTML.push('</ul>');


				$("#pagination").html(sHTML.join(''));
				$("#pagination").css('visibility', 'visible');

				oCallBack.fn(data); //config["callback"](oXML);
				$('#loading').hide();
			}).fail(function(jqXHR, textStatus) {
				//console.log("Got error!");
				EXAM.Error.create({"error": 'Request failed'+textStatus});
				$('#loading').hide();
				
				//showError(config["url"], textStatus);
			}
		);
		return true;
	};
	
	
	var Grid = function() {
		var sURL = null;
		var sExclURL = null;
		
		var oGridInputData ={};
		var oCallback = null;
		var numPages = 0;
		
		function isValid() {
			if (isNaN(nPageSize) || nPageSize < 1) {
				alert('Page Size is required.');
				return false;
			}
			return true;
		}
		
		return {
			init : function(){
				// TODO: refactor to follow RAW approach. Change how first call is initiated. VRuzha
				//return request(this);
			},
			render : function() {
				return request(this);
			},
			setURL : function(s1) {
				sURL = s1;
				return true;
			},
			getURL : function() {
				return sURL;
			},
			setExclURL : function(s1) {
				sExclURL = s1;
				return true;
			},
			getExclURL : function() {
				return sExclURL;
			},
			setPageSize : function(n, isRefresh) {
				oGridInputData.pageSize = n;
				if(isRefresh){
					return this.render();
				}	
				return true;
			},
			getPageSize : function() {
				return oGridInputData.pageSize;
			},
			setPageNumber : function(n, isRefresh) {
				oGridInputData.pageNum = n;
				if(isRefresh){
					return this.render();
				}	
				return true;
			},
			getPageNumber : function() {
				return oGridInputData.pageNum;
			},
			setNumPages : function(n) {
				numPages = n;
			},
			getNumPages : function() {
				return numPages;
			},
			setGridInputData : function(o){
				oGridInputData = o;
			},
			getGridInputData : function(){
				return oGridInputData;
			},
			setCallback : function(oContainer, oFn) {
				oCallback = {container: oContainer, fn: oFn };
				return true;
			},
			getCallback : function() {
				return oCallback;
			},
			getPage : function(n) {
				if (isNaN(n) || n < 1 || n == this.getPageNumber())
					return false;
				this.setPageNumber(n, true);
			},
			nextPage : function() {
				var pageNum = this.getPageNumber();
				if(pageNum >=  this.getNumPages())
					return false;
				else
					pageNum++;
				this.setPageNumber(pageNum, true);
			},
			prevPage : function(o) {
				var pageNum = this.getPageNumber();
				if(pageNum <= 1)
					return false;
				else
					pageNum--;
				this.setPageNumber(pageNum, true);
			},
			exportExcel : function() {
				
				$('#loading').show();
				
				var gridRegCall = this.getExclURL(); // ? this.getExclURL() : ('/Workspace/app/excel/' + this.getGridRequestName());
				
				if(gridRegCall==null || gridRegCall==""){
					EXAM.Error.create({"error": 'Excel URL should be provided.'});
					$('#loading').hide();
					return;
				}
				
				var gridData=this.getGridInputData();
				gridData["smartFilters"]=PS.grid.filter.getFilters();

				hiddenLink(gridRegCall + '?gridrequest=' + encodeURIComponent (JSON.stringify(gridData)));
				
				window.setTimeout(function () { $('#loading').hide(); }, 2000);
			},
			getGridRequestName : function(){
				if(this.getURL())
					return this.getURL().substring(this.getURL().lastIndexOf('/') + 1);
				else
					return;
			}
		};
	};
	
	return {
		create : function() {
			var g = new Grid();
			return g;
		}
	};
	
}();


PS.grid.filter= (function() {
	var arr = {};
	return {
		update : function(name, value) {
			
			if(value==null || value=="false" || value=="null"){
				delete arr[name];
			}else{
				arr[name]=value;
			}
			
			return this;
		},
		clear : function() {
			arr = {};
			return this;
		},
		serialize : function() {
			var sF="";
			for (var f in arr) {
				if (arr.hasOwnProperty(f)) {
					if(sF.length>0) sF+="&";
					sF+=f+"="+arr[f];
				}
			}
			return sF;
		},
		getFilters: function(){
			return arr;
		}
	};
})();
