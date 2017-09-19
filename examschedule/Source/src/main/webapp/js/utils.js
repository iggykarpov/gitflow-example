function getParameter(name) {
	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + name + "=([^&#]*)";
	var regex = new RegExp(regexS);
	var results = regex.exec(window.location.href);
	if (results == null)
		return "";
	else
		return results[1];
}

function reportError(o) {
	var sText = [];
	sText.push("Request failed.");
	if (o.attr("status"))
		sText.push(" Code: " + o.attr("status"));
	if (o.find("error"))
		sText.push(" \n Error: " + o.find("error").text());
	EXAM.Error.create({
		"error" : sText.join('')
	});
	$('#loading').hide();
}

function formatDate(s) {
	var dateArr = s.split("-");
	if (dateArr.length != 3)
		return "";
	return dateArr[1] + "/" + dateArr[2] + "/" + dateArr[0];
}

function parseDate(s) {
	//alert(new Date(1475035200000)); 
	//return ;
	if (s == "")
		return "";
	try {
		var d = $.datepicker.parseDate('yy-mm-dd', s.substring(0, 10));		
	} catch (e) {
		return null;
	}
	return $.datepicker.formatDate('mm/dd/y', d);

}


function parseInputDate(s) {
	if(s == "")
		return "";	
	try {
		var d = $.datepicker.parseDate('mm/dd/y', s);
	} catch(e) {
		try {
			d = $.datepicker.parseDate('mm/dd/yy', s);
		} catch(e) {
			return null;	
		}
	}
	return d;
}

function getDateFromString(s) {
	if (s == "")
		return null;
	try {
		var d=$.datepicker.parseDate('mm/dd/y', s);
		return d;
	} catch(e) {
		return null;	
	}
}

$.fn.extend({ // disable selection of HTML elements
	disableSelection : function() {
		this.each(function() {
			if (typeof this.onselectstart != 'undefined')
				this.onselectstart = function() {
					return false;
				};
			else if (typeof this.style.MozUserSelect != 'undefined')
				this.style.MozUserSelect = 'none';
			else
				this.onmousedown = function() {
					return false;
				};
		});
	}
});

function hiddenLink(url) {
	$('#hiddenDownloader').remove();
	
    $('<iframe />', {
        name: 'hiddenDownloader',
        id:   'hiddenDownloader',
        style: 'display: none',
        src: 	url
    }).appendTo('body');
}

function formatText(s, nl2br) {
	s = String(s)
	.replace(/&/g, '&amp;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');
	
	if (nl2br)
		s = s.replace(/([^>\r\n]?)(\r\n|\n\r|\r|\n)/g, '$1<br />$2');
	return s;
}

//Download a file or execute mailto link
function hiddenLink(url) {
    var hiddenIFrameID = 'hiddenDownloader';
    var iframe = document.getElementById(hiddenIFrameID);
    if (iframe === null) {
        iframe = document.createElement('iframe');
        iframe.id = hiddenIFrameID;
        iframe.style.display = 'none';
        document.body.appendChild(iframe); 
    } else {
    	iframe.src = 'about:blank';
    }
    iframe.src = url;
}