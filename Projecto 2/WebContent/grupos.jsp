<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Util.*"%>
<%@ page import="RMI.RMI,java.util.ArrayList,java.lang.*;"%>

<jsp:include page="/WEB-INF/header.jsp" />

<%
	RMI rmi = (RMI) request.getAttribute("rmi");
	User utilizador = (User) request.getAttribute("utilizador");
	ArrayList<Grupo> grupos = rmi.getUserGroups(utilizador.getId());

	boolean manager = false;
%>

<div class="container">
	<div class="row">

		<jsp:include page="/WEB-INF/sidebar.jsp" />

		<div class="col-md-9">
			<div class="conteudo">

				<div class="titulo">
					<h1>
						Grupos <span class="pull-right"><a href="home.jsp" class="btn btn-success btn-xs"><span class="glyphicon glyphicon-plus"></span> Criar grupo</a></span>
					</h1>
				</div>


				<ul class="list-group">

					<%
						for (Grupo g : grupos) {
							Grupo detalhesGrupo = rmi.getGroupDetails(g.getId());
					%>

					<li grupo="<%=detalhesGrupo.getId()%>" managerID="<%=detalhesGrupo.getIdManager()%>" class="list-group-item">
						<div class="row">
							<div class="col-xs-6 col-md-6">
								<div>
									<a href="grupos.jsp?id=<%=detalhesGrupo.getId()%>"><h4><%=detalhesGrupo.getNome()%></h4></a>
									<div class="mic-info">
										<b>Manager:</b> <a href="utilizador.jsp?id=<%=detalhesGrupo.getIdManager()%>"><%=detalhesGrupo.getManager()%></a>
									</div>
									<div class="mic-info">
										<b>Tópicos:</b>
										<%
											for (Topico t : detalhesGrupo.getTopicos()) {
										%>
										<a class="label label-default" href="topico.jsp?id=<%=t.getId()%>">#<%=t.getTag()%>
										</a>
										<%
											}
										%>
									</div>
									<div class="mic-info">
										<b>Membros:</b>
										<%
											for (User u : detalhesGrupo.getmembros()) {
										%>
										<a href="utilizador.jsp?id=<%=u.getId()%>"><%=u.getUsername()%>, </a>
										<%
											}
										%>
									</div>
								</div>
							</div>
							<%
								if (detalhesGrupo.getIdManager() == utilizador.getId()) {
									manager = true;
							%>

							<div class="col-xs-6 col-md-6">
								<button idgrupo="<%=detalhesGrupo.getId()%>" type="button" data-toggle="modal" data-target="#myModal" class="btn btn-warning btn-block gerirUtilizadores">
									<h1>
										<span class="glyphicon glyphicon-user"></span> Gerir Utilizadores
									</h1>
								</button>
							</div>
							<%
								}
							%>

						</div>
					</li>

					<%
						}
					%>

				</ul>

				<%
					if (manager) {
				%>
				<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="myModalLabel">Gerir Utilizadores</h4>
							</div>
							<div class="modal-body">
								<ul id="listaUsers"></ul>
								<form id="adicionarUtilizador" class="form-inline" role="form">
									<div class="form-group">
										<input type="text" class="form-control" name="utilizador" id="utilizador" placeholder="ID ou Username">
									</div>

									<button  type="submit" class="btn btn-success">Adicionar</button>
									<span id="estado"></span>
								</form>
							</div>

							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">Fechar</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<%
					}
				%>
			</div>
		</div>
	</div>
</div>

<jsp:include page="/WEB-INF/footer.jsp" />

<script>
$(function() {
var listaUsers = $("#listaUsers");
$(".gerirUtilizadores").click(
function() {

var grupoid = $(this).attr('idgrupo');
var managerID = $(this).parent().parent().parent().attr("managerid");
$( "#estado" ).text("");

$.ajax({url : "ajax/GetUsersFromGroup?id="+ grupoid,
	dataType : "json",
	}).done(function(data) {
		listaUsers.html("");
		$.each(data,function(key,val) {
			var botao = " <button userid='" + val["id"] +"' type='button' class='btn btn-danger btn-xs excluir_utilizador'><span class='glyphicon glyphicon-trash'></span></button>";
			var entry = "<li id='uid" +val["id"] + "'>"+ val["username"] + ((val["id"] == managerID) ? "" : botao) + "</li>";
			
			listaUsers.append(entry);

		});
		
		$("#adicionarUtilizador").unbind();
		$('.excluir_utilizador').unbind();
		
		$( "#adicionarUtilizador" ).submit(function( event ) {
			  event.preventDefault();
			  var uid = $("[name=utilizador]").val();
			  $.ajax({
					url : "ajax/AddUserGrupo?grupo="+ grupoid+ "&uid="+ uid
				}).done(function(data) {
					var uid = data.substr(0,data.indexOf(";"));
					if (uid != -1) {
						var entry = "<li>"+ uid + "</li>";
						
						listaUsers.append(entry);
						$( "#estado" ).text("Utilizador adicionado");
					} else
						$( "#estado" ).text("Utilizador não encontrado");

				});
		});
		
		$('.excluir_utilizador').click(function() {
			var uid = $(this).attr('userid');
			$.ajax({
				url : "ajax/RemoverUserGrupo?grupo="+ grupoid+ "&uid="+ uid
			}).done(function(data) {
				if (data.indexOf("removido") >= 0) {
					$("#uid"+ uid).remove();
				} else
					console.log(data);
				});
		});
		
	});

		
	});
});


</script>