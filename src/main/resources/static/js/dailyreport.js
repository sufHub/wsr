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

