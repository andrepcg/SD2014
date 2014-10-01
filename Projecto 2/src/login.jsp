<%

	String registo = request.getParameter("registo");
	String erro = request.getParameter("erro");
%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="../../docs-assets/ico/favicon.png">

    <title>Grid Template for Bootstrap</title>

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="css/estilo.css" rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy this line! -->
    <!--[if lt IE 9]><script src="../../docs-assets/js/ie8-responsive-file-warning.js"></script><![endif]-->

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>

  <div class="navbar navbar-default navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">Project name</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="#">Home</a></li>

          </ul>
          <ul class="nav navbar-nav navbar-right">
            <li class="active"><a href="../navbar-fixed-top/">Fixed top</a></li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>

	<div class="container">
		<div class="row">
		
			<div class="col-md-8">
			
				<% if(registo != null && registo.equals("erro")){ %>
					<div class="alert alert-danger">Erro no registo</div>
				<% } else if(erro != null && erro.equals("login")){ %>
					<div class="alert alert-danger">Username ou password invalidos</div>
				<% } else if(registo != null && registo.equals("sucesso")){ %>
					<div class="alert alert-success">Registado com sucesso! Faca agora login.</div>
				<% } %>
				
				<div class="jumbotron">
					<h1><strong>IdeaBroker</strong></h1>
					<p>Idea Management and Trading</p>
					<ul>
						<li>algo que descreve</li>
					</ul>
				</div>
			</div>
			<div class="col-md-4">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">Please sign in</h3>
					</div>
					<div class="panel-body">
						<form accept-charset="UTF-8" action="LoginServlet" method="POST" role="form">
						<fieldset>
							<div class="form-group">
								<input class="form-control" placeholder="Username" name="username" type="text">
							</div>
							<div class="form-group">
								<input class="form-control" placeholder="Password" name="password" type="password" value="">
							</div>
							<div class="checkbox">
								<label>
									<input name="remember" type="checkbox" value="true"> Lembrar-me
								</label>
							</div>
							<input name="accao" class="btn btn-lg btn-success btn-block" type="submit" value="Login">
							<input name="accao" class="btn btn-lg btn-info btn-block" type="submit" value="Registar">
						</fieldset>
						</form>
						<div class="login-or">
							<hr class="hr-or">
							<span class="span-or">or</span>
						</div>
						<h3>Login ou registo com</h3>
						  <div class="row">
							<div class="col-md-12">
							  <a href="#" onclick="login()" class="btn btn-lg btn-primary btn-block">Facebook</a>
							</div>
						  </div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<script src="js/jquery-2.0.3.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
	
	<script type="text/javascript" src="js/noty/jquery.noty.js"></script>
	
	<script src="js/noty/layouts/bottomLeft.js"></script>
	<script type="text/javascript" src="js/noty/themes/default.js"></script>
	
	<div id="fb-root"></div>
<script type="text/javascript">

window.fbAsyncInit = function() {
   FB.init({
	   // 327065317436007
	   
     appId      : '327065317436007', // App ID
     channelURL : '', // Channel File, not required so leave empty
     status     : true, // check login status
     cookie     : true, // enable cookies to allow the server to access the session
     oauth      : true, // enable OAuth 2.0
     xfbml      : false  // parse XFBML
   });
};

function login(){
FB.getLoginStatus(function(r){
	
     if(r.status === 'connected'){
    	 $.ajax({
    		  type: "POST",
    		  url: "Fblogin",
    		  data: {"status" : r.status, "accessToken" : r.authResponse.accessToken, "signedRequest" : r.authResponse.signedRequest, "userID" : r.authResponse.userID}
    	}).done(function( msg ) {
    		window.location.href = msg;
    	});
            //window.location.href = 'Fbconnect';
     }else{
        FB.login(function(response) {
            if(response.authResponse) {
            	$.ajax({
          		  type: "POST",
          		  url: "Fblogin",
          		  data: {"status" : r.status, "accessToken" : r.authResponse.accessToken, "signedRequest" : r.authResponse.signedRequest, "userID" : r.authResponse.userID}
          		}).done(function( msg ) {
          			window.location.href = msg;
          		});
            } else {
              // user is not logged in
            }
     },{scope:'email,publish_actions'}); // which data to access from user profile
 }
});
}

(function() {
   var e = document.createElement('script'); e.async = true;
   e.src = document.location.protocol + '//connect.facebook.net/en_US/all.js';                 
   document.getElementById('fb-root').appendChild(e);
}());

</script>
	
  </body>
</html>
