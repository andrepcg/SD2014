<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Util.*" %>
<%@ page import="RMI.RMI,java.util.ArrayList;" %>

<%
	//OracleJDBC oracle = new OracleJDBC();
	RMI rmi = (RMI) session.getAttribute("rmi");

	//User utilizador = (User) session.getAttribute("user");
	
	String username = (String) session.getAttribute("username");
	int userID = (Integer) session.getAttribute("userId");
	
	User utilizador = null;; 
	if(rmi != null)
		utilizador = rmi.getUser(userID);
	//User utilizador = (User) session.getAttribute("user");
	
	request.setAttribute("utilizador", utilizador);
	request.setAttribute("rmi", rmi);
	%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">

<title>Home</title>

<!-- Bootstrap core CSS -->
<link href="css/bootstrap.css" rel="stylesheet">

<!-- Custom styles for this template -->
<link href="css/estilo.css" rel="stylesheet">


</head>

<body>

	<div class="navbar navbar-default navbar-fixed-top" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="/ideabroker">IdeaBroker</a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav">
					<li><a href="ideias.jsp">Ideias</a></li>
					<li><a href="grupos.jsp">Grupos</a></li>
					<li><a href="topicos.jsp">Tópicos</a></li>
				</ul>

				<form class="navbar-form navbar-left form-inline" method="POST" role="search" action="Pesquisar">
					<div class="form-group">
						<input type="text" class="form-control col-xs-1 col-md-1" name="pesquisa" placeholder="Pesquisar...">
					</div>
				</form>
				
				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><b><%= utilizador.getUsername() %></b> <span class="badge"><%= utilizador.getDeicoins() %> Coinz</span> <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="minhasideias.jsp">Minhas ideias</a></li>
							<li><a href="carteira.jsp">Carteira de Shares</a></li>
							<li><a href="historico.jsp">Histórico Transacções</a></li>
							<li><a href="definicoes.jsp">Definições</a></li>
							<li><a href="Logout">Logout</a></li>
							<% if(utilizador.getFacebookID() == null || (utilizador.getFacebookID() != null && utilizador.getFacebookID().equals(""))){ %>
								<li role="presentation" class="divider"></li>
								<li><a onClick="login()" href="#">Ligar ao Facebook</a></li>
							<% } %>
						</ul>
					</li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</div>
	
<% if(utilizador.getFacebookID() == null || (utilizador.getFacebookID() != null && utilizador.getFacebookID().length() > 3)){ %>
	<div id="fb-root"></div>
	<script type="text/javascript">
//<![CDATA[
window.fbAsyncInit = function() {
   FB.init({
     appId      : '327065317436007', // App ID
     channelURL : '', // Channel File, not required so leave empty
     status     : true, // check login status
     cookie     : true, // enable cookies to allow the server to access the session
     oauth      : true, // enable OAuth 2.0
     xfbml      : false  // parse XFBML
   });
};

function fb(r){
	
	$.ajax({
		  type: "POST",
		  url: "Fbconnect",
		  data: {"status" : r.status, "accessToken" : r.authResponse.accessToken, "signedRequest" : r.authResponse.signedRequest, "userID" : r.authResponse.userID}
	}).done(function( msg ) {
		noty({
			text : 'Ligado ao Facebook!',
			type : 'information',
			dismissQueue : true,
			layout : 'bottomLeft',
			theme : 'defaultTheme'
		});
		setTimeout(function(){location.reload()},2000);
	});
	
}


function login(){
FB.getLoginStatus(function(r){
     if(r.status === 'connected'){
    	 fb(r);
     }else{
        FB.login(function(r) {
            if(r.authResponse) {
            	fb(r);
            } else {
              // user is not logged in
            }
     },{scope:'email,publish_actions'}); // which data to access from user profile
 }
});
}
// Load the SDK Asynchronously
(function() {
   var e = document.createElement('script'); e.async = true;
   e.src = document.location.protocol + '//connect.facebook.net/en_US/all.js';                 
   document.getElementById('fb-root').appendChild(e);
}());
//]]>
</script>
<% } %>