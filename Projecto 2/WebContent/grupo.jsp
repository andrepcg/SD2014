<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Util.*"%>
<%@ page import="RMI.RMI,java.util.ArrayList,java.lang.*;"%>

<jsp:include page="/WEB-INF/header.jsp" />

<%
	RMI rmi = (RMI) session.getAttribute("rmi");
	User utilizador = (User) request.getAttribute("utilizador");
	
	
	
	int idGrupo = -1;
	
	try {
		idGrupo = Integer.parseInt(request.getParameter("id"));
	} catch (Exception e) {
	}

	ArrayList<Ideia> ideias = null;
	Topico topico = null;

%>

<div class="container">
	<div class="row">

		<jsp:include page="/WEB-INF/sidebar.jsp" />

		<div class="col-md-9">
			<div class="conteudo">

				<%
					if (idGrupo > 0) {
						ArrayList<User> usersGroup = rmi.getUsersFromGroup(idGrupo);
						Grupo detalhesGrupo = rmi.getGroupDetails(idGrupo);
				%>
				<div class="titulo">
					<h1><%=topico.getNome()%> <span class="pull-right"><a href="home.jsp" class="btn btn-danger btn-xs"><span class="glyphicon glyphicon-arrow-left"></span> Voltar</a></span></h1>
				</div>


				<div class="panel panel-default widget">
					<div class="panel-heading">
						<span class="glyphicon glyphicon-comment"></span>
						<h3 class="panel-title">Recent Comments</h3>
					</div>
						<div class="panel-body">
							<ul class="list-group">

								<%
									for (Ideia i : ideias) {
								%>

								<li class="list-group-item">
									<div id="ideia-1" class="row">
										<div class="col-md-10">
											<div>
												<a href="ideia.jsp?id=<%= i.getIdIdeia() %>"> <%= i.getTitulo() %></a>
												<div class="mic-info">
													por: <a href="#"><%= i.getUsername() %></a> a <%= i.getData() %>
												</div>
											</div>
											<div class="comment-text"><span class="glyphicon glyphicon-comment"></span> <%= i.getTexto() %></div>
											<div class="action">
												<button type="button" class="btn btn-primary btn-xs" title="negociar">
													<span class="glyphicon glyphicon-bullhorn"></span> negociar
												</button>
												<button type="button" class="btn btn-success btn-xs" title="Approved">
													<span class="glyphicon glyphicon-ok"></span>
												</button>
												<% if(i.getUsername().compareTo(utilizador.getUsername()) == 0){ %>
												<button type="button" class="btn btn-danger btn-xs" title="Delete">
													<span class="glyphicon glyphicon-trash"></span>
												</button>
												<% } %>
												
											</div>
										</div>
										<div class="col-md-2">
											<div class="btn btn-info btn-block preco">
												<h1>
													<span id="preco"><%= rmi.ultimoPreco(i.getIdIdeia()) %></span>€
												</h1>
											</div>
										</div>
									</div>
								</li>

								<%
									}
								%>

							</ul>

						</div>
					</div>

					<ul class="pagination">
					  	<li class="<%=(pagina > 1) ? "" : "disabled" %>"><a href="topico.jsp?id=<%= idTopico %>&pagina=<%= pagina-1 %>">&laquo;</a></li>
					  	<% 
					  	int numPaginas = (int)(numIdeiasTopico / numIdeiasPorPagina) + (((numIdeiasTopico/numIdeiasPorPagina) % 2 != 0) ? 1 : 0);
					  	for(int i = 0; i < numPaginas; i++){ %>
					  		<li class="<%=(pagina == i+1) ? "active" : "" %>"><a href="topico.jsp?id=<%= idTopico %>&pagina=<%= i+1 %>"><%= i+1 %> <span class="sr-only">(current)</span></a></li>
					  	<% } %>
					  	<li class=<%=(pagina < numPaginas) ? "" : "disabled" %>><a href="topico.jsp?id=<%= idTopico %>&pagina=<%= pagina+1 %>">&raquo;</a></li>
					</ul>


						<%

							} else {
						%>
						Nao foi escolhido um topico
						<%
							}
						%>


				</div>
			</div>
		</div>
	</div>

	<jsp:include page="/WEB-INF/footer.jsp" />