EXAM.GA = (function() {
	return {
		init: function() {
			//tracking code for EXAM
			(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
					(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
				m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
			})(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

			ga('create', 'UA-91779829-3', 'auto');

			//ga('set', 'page', window.location.pathname);
			//ga('set', 'userId', userId);

			//ga('send', 'pageview');
            EXAM.GA.onPageHit();

		},
		onPageHit: function(data){ // data = {page: "/esched/planning_annual.html", title: "Exam Prescheduling"}
			var url = data && data.page ? data.page : window.location.pathname;
			var title = data && data.title ? data.title : document.title;

			ga('set', { page: url, title: title});
			ga('send', 'pageview');
		},
        onEventHit: function(data){
            if(data) {
                ga('send', {
                    hitType: 'event',
                    eventCategory: data.eventCategory || "Not Available",
                    eventAction: data.eventAction || "Not Available",
                    eventLabel: data.eventLabel || "Not Available"
                });
            }
        }
	};
})();

$(function() {
	EXAM.GA.init();
});

