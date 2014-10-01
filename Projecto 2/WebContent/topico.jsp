<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Util.*"%>
<%@ page import="RMI.RMI,java.util.ArrayList,java.lang.*;"%>

<jsp:include page="/WEB-INF/header.jsp" />

<%
	RMI rmi = (RMI) session.getAttribute("rmi");
	User utilizador = (User) request.getAttribute("utilizador");
	
	int idTopico = -1;
	int pagina = 1;
	int numIdeiasPorPagina = 6;
	
	try {
		idTopico = Integer.parseInt(request.getParameter("id"));
		pagina = Integer.parseInt(request.getParameter("pagina"));
		pagina = (pagina == 0) ? 1 : pagina;
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
					if (idTopico > 0) {
						int numIdeiasTopico = rmi.numIdeiasTopico(idTopico);
						topico = rmi.getTopico(idTopico);
						ideias = rmi.ideiasTopico(idTopico,pagina,numIdeiasPorPagina);
				%>
				<div class="titulo">
					<h1>#<%=topico.getTag() %> <span class="pull-right"><a href="home.jsp" class="btn btn-danger btn-xs"><span class="glyphicon glyphicon-arrow-left"></span> Voltar</a></span></h1>
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
										<div class="col-xs-8 col-sm-8 col-md-9">
											<div>
												<a href="ideia.jsp?id=<%= i.getIdIdeia() %>"> <%= i.getTitulo() %></a>
												<div class="mic-info">
													por: <a href="#"><%= i.getUsername() %></a> a <%= i.getData() %>
												</div>
											</div>
											<div class="comment-text"><span class="glyphicon glyphicon-comment"></span> <%= i.getTexto() %></div>
											<div class="action">
												<a href="ideia.jsp?id=<%= i.getIdIdeia() %>#comprar_shares" class="btn btn-primary btn-xs" title="comprar">
													<span class="glyphicon glyphicon-bullhorn"></span> comprar
												</a>

												<% if(i.getUsername().compareTo(utilizador.getUsername()) == 0){ %>
												<button type="button" class="btn btn-info btn-xs" title="Delete">
													<span class="glyphicon glyphicon-user"></span>
												</button>
												<% } %>
												
											</div>
										</div>
										<div class="col-xs-4 col-sm-4 col-md-3">
											<div class="btn btn-info btn-block preco">
												<h1>
													<span id="preco" ideia="<%=i.getIdIdeia()%>"><%= rmi.ultimoPreco(i.getIdIdeia()) %></span>Cz
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
						<% if(pagina > 1){ %>
					  	<li class="<%=(pagina > 1) ? "" : "disabled" %>"><a href="topico.jsp?id=<%= idTopico %>&pagina=<%= pagina-1 %>">&laquo;</a></li>
					  	<% }

						int numPaginas = (int) Math.ceil((numIdeiasTopico / numIdeiasPorPagina));
					  	for(int i = 0; i < numPaginas; i++){ %>
					  		<li class="<%=(pagina == i+1) ? "active" : "" %>"><a href="topico.jsp?id=<%= idTopico %>&pagina=<%= i+1 %>"><%= i+1 %> <span class="sr-only">(current)</span></a></li>
					  	<% } if(pagina < numPaginas){ %>
					  	<li class=<%=(pagina < numPaginas) ? "" : "disabled" %>><a href="topico.jsp?id=<%= idTopico %>&pagina=<%= pagina+1 %>">&raquo;</a></li>
					  	<% } %>
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