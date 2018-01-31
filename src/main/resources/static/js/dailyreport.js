function login(){

	var username = $("#username").val();
	var password = $("#password").val();
	$.ajax({
		url : "login",
		data : {
		username : username,
		password : password
	}
	}).then(
			function(data) {
				if(data == false){
					alert("Authetication Failed !!")
				}else{
					window.location.href="/view";
				}
			});

}

function logWork(obj){
	
	var ticket = obj.id;
	
	$.ajax({
		url : "popup",
		data : {
		ticket : obj.id,
		}
	}).then(
			function(html_string) {

				var dialog = bootbox.dialog({
					title: 'Work Log for Ticket : '+ticket,
					message: html_string,
					buttons: {
					ok: {
					label: "Log Work",
					className: 'btn btn-success',
					callback: function(){

					var timeSpent = $("#timeSpent").val();
					var manualEst = $("#manualEst").val();

					var remAuto = $('#remAuto').prop('checked');
					var remManual = $('#remManual').prop('checked');



					if(remAuto == true && timeSpent == ""){
						alert("Enter the Time Spent !!")
					}

					if(remManual == true && manualEst == ""){
						alert("Enter the Remaining Estimate !!")
					}

					$.ajax({
						url : "logWork",
						data : {
						timeSpent : $("#timeSpent").val(),
						remainingEst : $("input[name='remainingEst']:checked").val(),
						manualEst : $("#manualEst").val(),
						comments : $("#comments").val(),
						excelDP : $("#excelDP").val(),
						excelEstComm : $("#excelEstComm").val(),
						ticket : ticket
					}
					}).then(
							function(data) {
								if(data == "index"){
									alert("Session TimeOut !!");
									window.location.href="/";
								}
								else if(data == "error"){
									alert("Error While Logging Work !!")
								}
								else if(data == "invalidTime"){
									alert("Invalid Time Format!")
								}
								else{
									alert("Work Logged !!")
									window.location.href="/view";
								}
							});

				}}}});
				
	});

//	$.get("/popup?ticket="+obj.id, function(html_string)
//			{
//		
//
//			},'html'); 


}

function logOut(){

	$.ajax({
		url : "logOut"
	}).then(
			function(data) {
				if(data="logout"){
					window.location.href="/";
				}
			}
			);

}

function generateExcel(){
	window.location.href="/generateExcel";
}

function workLogToday(){
	
	$.ajax({
		url : "workLogToday"
	}).then(
			function(html_string) {
				var dialog = bootbox.dialog({
					title: 'Work Logged : ',
					message: html_string,
					buttons: {
					ok: {
					label: "Close",
					className: 'btn btn-danger',
					callback: function(){
				}}}});
			}
			);
}