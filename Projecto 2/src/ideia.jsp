<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Util.*"%>
<%@ page import="RMI.RMI,java.util.ArrayList;"%>

<jsp:include page="/WEB-INF/header.jsp" />

<%
	RMI rmi = (RMI) session.getAttribute("rmi");
	int idIdeia = -1;
	try {
		idIdeia = Integer.parseInt(request.getParameter("id"));
	} catch (Exception e) {

	}

	Ideia ideia = null;
	User utilizador = (User) request.getAttribute("utilizador");
%>

<div class="container">
	<div class="row">

		<jsp:include page="/WEB-INF/sidebar.jsp" />

		<div class="col-md-9">
			<div class="conteudo">

				<%
					if (idIdeia > 0) {
										ideia = rmi.seleccionarIdeia2(idIdeia);
										//Mercado mercado = oracle.mercadoShares(idIdeia);
										Mercado mercado = rmi.getMercado(idIdeia);
										ArrayList<Topico> topicos = rmi.getTopicosIdeia(idIdeia);
										Share share = rmi.getUserIdeiaShares(utilizador.getId(), idIdeia);
										double ultimoPreco = rmi.ultimoPreco(idIdeia);
										Mercado ordensUser = rmi.ordensUser(utilizador.getId(), idIdeia);
										double menorPreco = mercado.getOrdensVenda().get(0).getPreco_por_share();
				%>
				<div class="titulo">
					<h1><%=ideia.getTitulo()%></h1>
				</div>

				<button type="submit" class="btn btn-success btn-xs">
					<span class="glyphicon glyphicon-list-alt"></span>
					<%
						if (share != null){
					%>
					<%=share.getNum_shares()%>
					<%
						}else{
					%>
					0
					<%
						}
					%>
					shares
				</button>


				<div class="row" style="margin-top: 20px">
					<div class="col-md-12">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">
									<strong><%=ideia.getTitulo()%></strong>
									<%
										if (share != null && share.getNum_shares() == 100000) {
									%>
									<form style="display: inline;" action="ApagarIdeiaServlet" method="POST">
										<input type="hidden" name="idIdeia" value="<%=ideia.getIdIdeia()%>">
										<button type="submit" class="btn btn-danger btn-xs">
											<span class="glyphicon glyphicon-trash"></span>
										</button>
									</form>
									<%
										}
									%>
									<small class="pull-right">por: <a href="#"><%=ideia.getUsername()%></a> a <%=ideia.getData()%></small>
								</h3>

							</div>
							<div class="panel-body">
								<p><%=ideia.getTexto()%></p>
								<%
									for (Topico t : topicos) {
								%>
								<a href="topico.jsp?id=<%=t.getId()%>" class="label label-default">#<%=t.getTag()%></a>
								<%
									}
								%>

							</div>

						</div>
					</div>
				</div>
				<%
					if (ideia.getRespostas() != null && ideia.getRespostas().size() > 0) {
										for (Ideia i : ideia.getRespostas()) {
											topicos = rmi.getTopicosIdeia(i.getIdIdeia());
				%>

				<div class="row">
					<div class="col-md-2" style="font-size: 85px; text-align: center;">
						<span class="glyphicon glyphicon-share-alt"></span>
					</div>
					<div class="col-md-10">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">
									Rsp:
									<%=i.getTitulo()%>
									<small class="pull-right">por: <a href="#"><%=i.getUsername()%></a> a <%=i.getData()%></small>
								</h3>

							</div>
							<div class="panel-body">
								<p><%=i.getTexto()%></p>
								<%
									for (Topico t : topicos) {
								%>
								<a href="topico.jsp?id=<%=t.getId()%>" class="label label-default">#<%=t.getTag()%></a>
								<%
									}
								%>



							</div>

						</div>
					</div>
				</div>
				<%
					}
														}
				%>

				<div class="row">
					<div class="col-md-12">
						<div class="well">
							<form id="comprar_shares" method="POST" class="form-inline" role="form">
								<div class="row">
									<div class="col-xs-2">
										<input type="text" class="form-control" placeholder="shares" name="num_shares" data-toggle="tooltip" title="Número de shares a comprar (inteiro)">
									</div>
									<div class="col-xs-2">
										<fieldset id="preco_field" data-toggle="tooltip" title="Preço por share (inactivo se Market Order) (2 casas decimais)" disabled>
											<input name="preco_ordem" type="text" class="form-control" value="<%=menorPreco%>">
										</fieldset>
									</div>
									<div class="col-xs-3">
										<select name="tipo_ordem" class="form-control" data-toggle="tooltip" title="Market Order: compra ao menor preço de venda; Limit Order: define um preço máximo de compra por share">
											<option value="market" selected="selected">Market Order</option>
											<option value="limit">Limit Order</option>
										</select>
									</div>
									<div class="col-xs-3">
										<div class="compra_total">
											<p>
												<strong>Total:</strong> <span id="total_shares">0</span> shares
											</p>
											<p>
												<span id="total_preco">0</span> Coinz
											</p>
										</div>
									</div>
									<div class="col-xs-2">
										<button type="submit" class="btn btn-primary">Comprar ideia</button>
									</div>
								</div>

							</form>
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-md-12">
						<div class="panel panel-success widget">
							<div class="panel-heading">
								<h3 class="panel-title">Suas ordens</h3>

							</div>
							<div class="panel-body">
								<form id="form_ordens" method="POST" role="form">
									<table class="table table-striped">
										<thead>
											<tr>
												<th>Ordem</th>
												<th># Shares</th>
												<th>Preço/share (Cz)</th>
												<th>Tipo</th>
												<th></th>
											</tr>
										</thead>
										<tbody>

											<%
												int x = 0;
												for(OrdemVenda o : ordensUser.getOrdensVenda()){
													
											%>

											<tr row="<%=x %>" classe="venda" idOrdem="<%=o.getId()%>">

												<td><span class="label label-danger">Venda</span></td>
												<td style="width: 15%"><span class="label label-default" name="num_shares"><%=o.getNum_shares()%></span></td>
												<td style="width: 20%"><input  type="text" class="form-control" name="preco_por_share" value="<%=o.getPreco_por_share()%>"></td>
												<td><select name="tipo" class="form-control">
														<option value="limit" <%=(o.getTipo() == 1) ? "selected" : ""%>>Limit</option>
														<option value="market" <%=(o.getTipo() == 0) ? "selected" : ""%>>Market</option>
												</select></td>
												<td><button row="<%=x++%>" idOrdem="<%=o.getId()%>" value="editar" type="submit" class="btn btn-warning">Editar ordem</button></td>

											</tr>

											<%
												}
											%>
											<%
												for(OrdemCompra o : ordensUser.getOrdensCompra()){
											%>
											<tr row="<%=x %>" classe="compra" idOrdem="<%=o.getId()%>">
												<td><span class="label label-success">Compra</span></td>
												<td style="width: 15%"><input type="text" class="form-control" name="num_shares" value="<%=o.getNum_shares()%>"></td>
												<td style="width: 20%"><input type="text" class="form-control" name="preco_por_share" value="<%=o.getPreco_por_share()%>"></td>
												<td><select name="tipo" class="form-control">
														<option value="limit" <%=(o.getTipo() == 1) ? "selected" : ""%>>Limit</option>
														<option value="market" <%=(o.getTipo() == 0) ? "selected" : ""%>>Market</option>
												</select></td>
												<td><button row="<%=x%>" idOrdem="<%=o.getId()%>" value="editar" type="submit" class="btn btn-warning">Editar ordem</button>
													<button row="<%=x++%>" idOrdem="<%=o.getId()%>" value="remover" type="submit" class="btn btn-danger">
														<span class="glyphicon glyphicon-trash"></span>
													</button></td>
											</tr>
											<%
												}
											%>
										</tbody>
									</table>
								</form>
							</div>
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-md-6">
						<div class="panel panel-info widget" style="text-align: right;">
							<div class="panel-heading">
								<h3 class="panel-title">Ordens de compra</h3>

							</div>
							<div class="panel-body">
								<table class="table table-striped table-condensed">
									<thead>
										<tr>
											<th style="text-align: right;">Soma</th>
											<th style="text-align: right;">Quantidade</th>
											<th style="text-align: right;">Preço</th>
										</tr>
									</thead>
									<tbody id="ordens_compra">
										<%
											double sum = 0.0;
																			for (OrdemCompra oC : mercado.getOrdensCompra()) {
										%>
										<tr>
											<td><%=sum += oC.getNum_shares()%></td>
											<td><%=oC.getNum_shares()%></td>
											<td><%=oC.getPreco_por_share()%></td>
										</tr>
										<%
											}
										%>
									</tbody>
								</table>

							</div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="panel panel-info widget" style="text-align: left;">
							<div class="panel-heading">
								<h3 class="panel-title">Ordens de venda</h3>

							</div>
							<div class="panel-body">
								<table class="table table-striped table-condensed">
									<thead>
										<tr>
											<th>Preço</th>
											<th>Quantidade</th>
											<th>Soma</th>
										</tr>
									</thead>
									<tbody id="ordens_venda">
										<%
											sum = 0.0;
											for (OrdemVenda oV : mercado.getOrdensVenda()) {
										%>
										<tr>
											<td><%=oV.getPreco_por_share()%></td>						
											<td><%=oV.getNum_shares()%></td>
											<td><%=sum += oV.getNum_shares()%></td>
										</tr>
										<%
											}
										%>
									</tbody>
								</table>

							</div>
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-md-12">
						<div id="container" style="height: 500px; min-width: 500px"></div>
					</div>
				</div>



				<%
					} else {
				%>
				Nao foi seleccionada uma ideia
				<%
					}
				%>


			</div>
		</div>
	</div>
</div>

<jsp:include page="/WEB-INF/footer.jsp" />

<script src="http://code.highcharts.com/stock/highstock.js"></script>

<script type="text/javascript">
$(function () {

    var idIdeia = <%= idIdeia %> ;
    var tipo_ordem = $("[name=tipo_ordem]");
    var preco_field = $("#preco_field");
    var num_shares = $("[name=num_shares]");
    var preco_total = $("#total_preco");
    var total_shares = $("#total_shares");
    var preco = $("[name=preco_ordem]");

    num_shares.tooltip();
    preco_field.tooltip();
    tipo_ordem.tooltip();

    $("#form_ordens").submit(
        function (event) {
            event.preventDefault();

            var idordem = $("button[type=submit][clicked=true]").attr("idordem");
            var buttonRow = $("button[type=submit][clicked=true]").attr("row");
            var linha = $("#form_ordens tr[row=" + buttonRow + "]");

            var ordem = $(linha).attr("classe");
            var num_shares = $(linha).find("[name=num_shares]").val();
            if (num_shares == "")
                num_shares = $(linha).find("[name=num_shares]").text();

            var accao = $(linha).find("[clicked=true]").val();
            var preco_por_share = $(linha).find(
                "[name=preco_por_share]").val();
            var tipo = $(linha).find("[name=tipo]").val();

            var formData = "idOrdem=" + idordem + "&ideia=" + idIdeia + "&classe=" + ordem + "&num_shares=" + Math.round(num_shares) + "&tipo=" + tipo + "&preco_por_share=" + (parseFloat(preco_por_share)).toFixed(2) + "&accao=" + accao;

            $.ajax({
                url: "ajax/EditarOrdem",
                type: "POST",
                data: formData,
                success: function (data, textStatus, jqXHR) {
                    var res = JSON.parse(data);
                    if (accao == "remover") {
                        if (res.resultado == "removido")
                            linha.remove();
                    } else if (accao == "editar") {
                        if (res.resultado.indexOf("_pendente") >= 0) {
                            noty({
                                text: 'Ordem pendente',
                                type: 'warning',
                                dismissQueue: true,
                                layout: 'bottomLeft',
                                theme: 'defaultTheme'
                            });
                        } else {
                            noty({
                                text: ordem + res.shares + ' shares a ' + res.ultimo_preco + 'Coinz na ideia ' + res.ideia,
                                type: 'success',
                                dismissQueue: true,
                                layout: 'bottomLeft',
                                theme: 'defaultTheme'
                            });

                            //console.log(data);
                            send(data);
                        }

                    }
                    console.log(data);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log("erro");
                }
            });
        });

    $("#form_ordens button[type=submit]").click(
        function () {
            $("button[type=submit]", $(this).parents("form"))
                .removeAttr("clicked");
            $(this).attr("clicked", "true");
        });

    $("#comprar_shares").submit(
        function (event) {
            event.preventDefault();
            var formData = "ideia=" + idIdeia +"&num_shares=" + Math.round(num_shares.val()) + "&tipo=" + tipo_ordem.val() + "&preco_por_share=" + (parseFloat(preco.val())).toFixed(4);
            console.log(formData);

            $.ajax({
                url: "ajax/ComprarShares",
                type: "POST",
                data: formData,
                success: function (data, textStatus, jqXHR) {
                    var o = JSON.parse(data);


                    if (o.resultado == "compra_pendente") {
                    	$("#form_ordens tbody").append("<tr classe='compra'><td><span class='label label-success'>Compra</span></td><td style='width: 15%'><span class='label label-default' name='num_shares'>"+Math.round(num_shares.val())+"</span></td><td style='width: 20%'><input type='text' class='form-control' name='preco_por_share' value='"+(parseFloat(preco.val())).toFixed(4)+"'></td><td></td><td></td></tr>");
                        noty({
                            text: 'Compra pendente',
                            type: 'warning',
                            dismissQueue: true,
                            layout: 'bottomLeft',
                            theme: 'defaultTheme'
                        });
                    } else {
                        noty({
                            text: 'Comprou ' + o.shares + ' shares a ' + o.ultimo_preco + 'Coinz na ideia ' + o.ideia,
                            type: 'success',
                            dismissQueue: true,
                            layout: 'bottomLeft',
                            theme: 'defaultTheme'
                        });

                        //console.log(data);
                        send(data);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log("erro");
                }
            });

        });

    tipo_ordem.change(function () {
        if (tipo_ordem.val() == "market") {
            preco_field.attr("disabled", true);
        } else {
            preco_field.attr("disabled", false);
        }
    });

    num_shares.keyup(function () {
        calcPreco();
    });

    preco.keyup(function () {
        calcPreco();
    });

    setInterval(function () {
        $.ajax({
            url: "ajax/MercadoOrdens",
            type: "GET",
            data: "ideia=" + idIdeia,
            success: function (data, textStatus, jqXHR) {
                var res = JSON.parse(data);
                $("#ordens_venda tr").remove();
                if (res.vendas.length > 0) {
                    var i = 0;
                    
                    var soma = 0;
                    for (i = 0; i < res.vendas.length; i++) {

                        soma += res.vendas[i].qtd;
                        var row = "<tr><td>" + res.vendas[i].preco + "</td><td>" + res.vendas[i].qtd + "</td><td>" + soma + "</td></tr>";
                        $("#ordens_venda").append(row);
                    }

                }
                $("#ordens_compra tr").remove();
                if (res.compras.length > 0) {
                    var i = 0;
                    
                    var soma = 0;
                    for (i = 0; i < res.compras.length; i++) {
                        soma += res.compras[i].qtd;
                        var row = "<tr><td>" + soma + "</td><td>" + res.compras[i].qtd + "</td><td>" + res.compras[i].preco + "</td></tr>";
                        $("#ordens_compra").append(row);
                    }

                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("erro");
            }
        });
    }, 5000);

    $.getJSON('ajax/HistorialPreco?ideia=' + idIdeia, function (data) {
        // Create the chart
        $('#container').highcharts('StockChart', {

            rangeSelector: {
                selected: 1
            },

            series: [{
                name: 'CoinZ',
                data: data,
                tooltip: {
                    valueDecimals: 3
                }
            }]
        });
    });

    function calcPreco() {
        var total = (Math.round(num_shares.val()) * (parseFloat(preco.val())).toFixed(4)).toFixed(3);
        preco_total.text(total);
        total_shares.text(Math.round(num_shares.val()));
    }

});
</script>

