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
	
	 $.get("/popup", function(html_string)
	 {
		 var dialog = bootbox.dialog({
				title: 'Work Log for Ticket : '+obj.id,
				message: html_string,
				buttons: {
				    ok: {
				        label: "Log Work",
				        className: 'btn btn-success',
				        callback: function(){
			 
							$.ajax({
								url : "logWork",
								data : {
									timeSpent : $("#timeSpent").val(),
									remainingEst : $("input[name='remainingEst']:checked").val(),
									manualEst : $("#manualEst").val(),
									comments : $("#comments").val(),
									ticket : obj.id
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
				    }
				}
		});
	 },'html'); 
	
	
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