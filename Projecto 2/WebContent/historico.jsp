<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Util.*"%>
<%@ page import="RMI.RMI,java.util.ArrayList;"%>

<jsp:include page="/WEB-INF/header.jsp" />
<%
	RMI rmi = (RMI) session.getAttribute("rmi");
	User utilizador = (User) request.getAttribute("utilizador");
	ArrayList<Transaccao> historico = rmi.transaccoes(utilizador.getId(), 0);
%>

<div class="container">
	<div class="row">

		<jsp:include page="/WEB-INF/sidebar.jsp" />


		<div class="col-md-9">
			<div class="conteudo">
				<div class="titulo">
					<h1>Histórico</h1>
				</div>

				<table class="table">
					<thead>
						<tr>
							
							<th>Tipo</th>
							<th>Ideia</td>
							<th>Shares trocadas</th>
							<th>Preço/share (Cz)</th>
							<th>Total (Cz)</th>
							<th>Data</th>
						</tr>
					</thead>
					<tbody>
					<% for (Transaccao s : historico) { %>
						<tr>
							<td><span class="label label-<%=(s.getTipo().compareTo("compra") == 0) ? "danger" : "success"%>"><%=s.getTipo()%></span></td>
							<td><a href="ideia.jsp?id=<%=s.getIdIdeia()%>"><%=s.getIdIdeia()%> Link</a></td>
							<td><%=s.getNumShares()%></td>
							<td><%=s.getPreco()%></td>
							<td><%=s.getPago()%></td>
							<td><%=s.getTimestamp()%></td>
							
						</tr>
					<% } %>
					</tbody>
				</table>




			</div>
		</div>
	</div>
</div>

<jsp:include page="/WEB-INF/footer.jsp" />
