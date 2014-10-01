<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Util.*"%>
<%@ page import="RMI.RMI,java.util.ArrayList,java.lang.*;"%>

<jsp:include page="/WEB-INF/header.jsp" />

<%
	RMI rmi = (RMI) session.getAttribute("rmi");
	User utilizador = (User) request.getAttribute("utilizador");
	
	int pagina = 1;
	int numIdeiasPorPagina = 6;
	
	try {
		pagina = Integer.parseInt(request.getParameter("pagina"));
		pagina = (pagina == 0) ? 1 : pagina;
	} catch (Exception e) {
	}

	ArrayList<Ideia> ideias = (ArrayList<Ideia>) request.getAttribute("ideias");

%>

<div class="container">
	<div class="row">

		<jsp:include page="/WEB-INF/sidebar.jsp" />

		<div class="col-md-9">
			<div class="conteudo">

				<%
						int numIdeias = ideias.size();
				%>
				<div class="titulo">
					<h1>Ideias  <span class="pull-right"><a href="home.jsp" class="btn btn-danger btn-xs"><span class="glyphicon glyphicon-arrow-left"></span> Voltar</a></span></h1>
				</div>


				<div class="panel panel-default widget">
					<div class="panel-heading">
						<h3 class="panel-title">Das mais recentes para as mais antigas</h3>
					</div>
						<div class="panel-body">
							<ul class="list-group">

								<%
									for (int x = ((pagina - 1) * numIdeiasPorPagina); x < (pagina * numIdeiasPorPagina) && x < numIdeias; x++) {
										Ideia i = ideias.get(x);
										ArrayList<Topico> topicos = rmi.getTopicosIdeia(i.getIdIdeia());
										Share share = rmi.getUserIdeiaShares(utilizador.getId(), i.getIdIdeia());
								%>

								<li class="list-group-item">
									<div id="ideia-1" class="row">
										<div class="col-md-10">
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
													<% if(share != null){ %>
													<%= share.getNum_shares() %>
													<% }else { %> 0 <% } %>shares
												</button>
												<a href="ideia.jsp?id=<%= i.getIdIdeia() %>#comprar_shares" class="btn btn-primary btn-xs" title="comprar">
													<span class="glyphicon glyphicon-bullhorn"></span> comprar
												</a>
												<% if(i.getUsername().compareTo(utilizador.getUsername()) == 0){ %>
												<button type="button" class="btn btn-info btn-xs" title="Ideia submetida">
													<span class="glyphicon glyphicon-user"></span>
												</button>
												<% } %>
												
											</div>
										</div>
										<div class="col-md-2">
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