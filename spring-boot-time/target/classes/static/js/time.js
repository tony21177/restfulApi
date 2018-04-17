var url="http://"+window.location.host;
function getTime(){
	 $.ajax({
	        url: url+"/time"
	    }).then(function(data) {
	       $("#unauthenticated").text("authenticated by FB!");
	       $('#unixTime').text("Unix Time : "+data.unixTime);
	       $('#serverTime').text("Server Time : "+data.serverTime);
	       
	    },function(data){
	    	if(data.status=="401"){
	    		$("#unauthenticated").text("尚未登入,無法使用服務(請以FB帳號登入)");
	    	}
	    });
}

$( document ).ready(function() {
    getUserInfo();
});

function getUserInfo(){
	$.ajax({
        url: url+"/user"
    }).then(function(data) {
    	if(data.authenticated==true){
        	$('.logout').show();
    	    $('.login').hide();
    	    $('.name').html(data.userAuthentication.details.name);
    	    $('.picture').attr("src",data.userAuthentication.details.picture.data.url);
    	    $('.picture').show();
        	}
    });
}


//when you logged out,you can see the new cookies and headers in the requests that the browser exchanges with the local server
function logout(){
	$.post("/logout", function() {
	    $(".name").html('');
	    $(".picture").hide();
	    $('.logout').hide();
	    $('.login').show();
	    $("#unauthenticated").text("");
	    $('#unixTime').text("");
	    $('#serverTime').text("");
	    
	    //若要登出FB需要使用FB的SDK來登出,因為無法刪除和現在頁面不同domain的cookie...(只能存取跨子網域的cookie)
	})
}

//所有ajax呼叫都會套用到此設定把從後端回傳回來的cookie中取得XSFR-TOKEN,然後在所有請求端段API的X-XSRF-TOKEN傳給後端一樣的TOKEN
//有可能更動到資料庫的都要
$.ajaxSetup({
	beforeSend : function(xhr, settings) {
	  if (settings.type == 'POST' || settings.type == 'PUT'
	      || settings.type == 'DELETE') {
	    if (!(/^http:.*/.test(settings.url) || /^https:.*/
	        .test(settings.url))) {
	      // Only send the token to relative URLs i.e. locally.
	      //若是後端Spring security有啟用.csrf(),沒下面這段會出現status code=403,message=Invalid CSRF Token 'null' was found on the request parameter '_csrf' or header 'X-XSRF-TOKEN'.	
	      xhr.setRequestHeader("X-XSRF-TOKEN",
	          Cookies.get('XSRF-TOKEN'));
	    }
	  }
	}
	});


