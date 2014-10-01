<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Util.*"%>
<%@ page import="RMI.RMI,java.util.ArrayList,java.lang.*;"%>

<jsp:include page="/WEB-INF/header.jsp" />

<%
	RMI rmi = (RMI) request.getAttribute("rmi");
	User utilizador = (User) request.getAttribute("utilizador");
	
	int pagina = 1;
	int numIdeiasPorPagina = 6;
	
	try {
		pagina = Integer.parseInt(request.getParameter("pagina"));
		pagina = (pagina == 0) ? 1 : pagina;
	} catch (Exception e) {
	}

	ArrayList<Ideia> ideias = null;

%>

<div class="container">
	<div class="row">

		<jsp:include page="/WEB-INF/sidebar.jsp" />

		<div class="col-md-9">
			<div class="conteudo">

				<%
					ideias = rmi.getIdeiasUser(utilizador.getId(),pagina,numIdeiasPorPagina);
					int numIdeias = ideias.size();
				%>
				<div class="titulo">
					<h1>Minhas Ideias  <span class="pull-right"><a href="home.jsp" class="btn btn-danger btn-xs"><span class="glyphicon glyphicon-arrow-left"></span> Voltar</a></span></h1>
				</div>


				<div class="panel panel-default widget">
					<div class="panel-heading">
						<h3 class="panel-title">Das mais recentes para as mais antigas</h3>
					</div>
						<div class="panel-body">
							<ul class="list-group">

								<%
									for (Ideia i : ideias) {
										ArrayList<Topico> topicos = rmi.getTopicosIdeia(i.getIdIdeia());
										Share share = rmi.getUserIdeiaShares(utilizador.getId(), i.getIdIdeia());
								%>

								<li class="list-group-item">
									<div id="ideia-1" class="row">
										<div class="col-xs-8 col-sm-8 col-md-9">
											<div>
												<a href="ideia.jsp?id=<%= i.getIdIdeia() %>"> <%= i.getTitulo() %></a>
												<div class="mic-info">
													por: <a href="#"><%= i.getUsername() %></a> a <%= i.getData() %>
													<% for(Topico t : topicos){ %>
														<a href="topico.jsp?id=<%=t.getId() %>" class="label label-default">#<%=t.getTag() %></a>
													<% } %>
												</div>
											</div>
										
											<div class="comment-text"><span class="glyphicon glyphicon-comment"></span> <%= i.getTexto() %></div>
											<div class="action">
												<button type="submit" class="btn btn-success btn-xs">
													<span class="glyphicon glyphicon-list-alt"></span>
													<%=share.getNum_shares() %> shares
												</button>
												<%	if (share != null && share.getNum_shares() == 100000) { %>
													<form style="display: inline;" action="ApagarIdeiaServlet" method="POST">
														<input type="hidden" name="idIdeia" value="<%=i.getIdIdeia()%>">
														<button type="submit" class="btn btn-danger btn-xs">
															<span class="glyphicon glyphicon-trash"></span>
														</button>
													</form>
												<%
													}
												%>
												
											</div>
										</div>
										<div class="col-xs-4 col-sm-4 col-md-3">
											<div class="btn btn-info btn-block preco">
												<h1>
													<span id="preco"><%= rmi.ultimoPreco(i.getIdIdeia()) %></span>Cz
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
					  	<li class="<%=(pagina > 1) ? "" : "disabled" %>"><a href="ideias.jsp?pagina=<%= pagina-1 %>">&laquo;</a></li>
					  	<% }

					  	int numPaginas = (int) Math.ceil((numIdeias / numIdeiasPorPagina));
					  	for(int i = 0; i < numPaginas; i++){ %>
					  		<li class="<%=(pagina == i+1) ? "active" : "" %>"><a href="ideias.jsp?pagina=<%= i+1 %>"><%= i+1 %> <span class="sr-only">(current)</span></a></li>
					  	<% } if(pagina < numPaginas){ %>
					  	<li class=<%=(pagina < numPaginas) ? "" : "disabled" %>><a href="ideias.jsp?pagina=<%= pagina+1 %>">&raquo;</a></li>
					  	<% } %>
					</ul>




				</div>
			</div>
		</div>
	</div>

	<jsp:include page="/WEB-INF/footer.jsp" />