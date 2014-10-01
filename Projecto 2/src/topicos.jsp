<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Util.*" %>
<%@ page import="RMI.RMI,java.util.ArrayList;" %>

<jsp:include page="/WEB-INF/header.jsp"/>
<%
	RMI rmi = (RMI) session.getAttribute("rmi");
	User utilizador = (User) request.getAttribute("utilizador");
	ArrayList<Topico> topicos = rmi.mostraTopicos(false);
%>

	<div class="container">
		<div class="row">

			<jsp:include page="/WEB-INF/sidebar.jsp" />
			
			
			<div class="col-md-9">
				<div class="conteudo">
					<div class="titulo">
						<h1>Tópicos</h1>
					</div>

					<div class="list-group">
					
					<%
					
						for(Topico t : topicos){
							int numIdeias = rmi.numIdeiasTopico(t.getId());
							out.println("<a href='topico.jsp?id="+t.getId()+"' class='list-group-item'><h4 class='list-group-item-heading'>"+t.getTag()+"<span class='label label-info pull-right'>"+numIdeias+"</span></h4></a>");
						}
					
					%>

						
					</div>
				</div>
			</div>
		</div>
	</div>

<jsp:include page="/WEB-INF/footer.jsp" />
