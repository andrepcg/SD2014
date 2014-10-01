<%@ page import="Util.*"%>
<%@ page import="RMI.RMI,java.util.ArrayList,java.lang.*;"%>

<%
	//OracleJDBC oracle = (OracleJDBC) request.getAttribute("oracle");
	RMI rmi = (RMI) request.getAttribute("rmi");
	User utilizador = (User) request.getAttribute("utilizador");
	ArrayList<Grupo> grupos = rmi.getUserGroups(utilizador.getId());
	
%>

<div class="col-md-3">

	<a href="criarIdeia.jsp" class="btn btn-primary btn-block btn-lg" style='margin-top: 20px;'><span class="glyphicon glyphicon-plus"></span> Criar Ideia</a>
	

	<div class="panel panel-info" style='margin-top: 20px;'>
		<div class="panel-heading">
			<h3 class="panel-title">Grupos</h3>
		</div>

		<%
			if (grupos.size() > 0) {
		%>
		<div class="list-group">

			<%
				for (Grupo g : grupos) {
			%>
			<a href="grupo.jsp?id=<%=g.getId()%>" class="list-group-item"><%=g.getNome()%></a>
			<%
				}
			%>
			<a href="criarGrupo.jsp" class="list-group-item"><span class="label label-success">+ Criar Grupo</span></a>
		</div>
		<%
			}else{
		%>
		<div class="panel-body">
			<p>Não pertence a nenhum grupo</p>
			<a href="criarGrupo.jsp"><span class="btn btn-success btn-xs">+ Criar Grupo</span></a>
		</div>
		<% } %>
	</div>


</div>