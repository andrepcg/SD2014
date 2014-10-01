var ws = null;

function connect() {
	var curr = window.location.href;
	var domain = curr.substring(curr.indexOf("://")+3,curr.indexOf(":8080"));
	var target = "ws://"+domain+":8080/ideabroker/WebSocket";

	if ('WebSocket' in window) {
		ws = new WebSocket(target);
	} else if ('MozWebSocket' in window) {
		ws = new MozWebSocket(target);
	} else {
		alert('WebSocket is not supported by this browser.');
		return;
	}
	ws.onopen = function() {
		console.log('Info: WebSocket connection opened.');
	};
	ws.onmessage = function(event) {
		receber(event.data);
	};
	ws.onclose = function() {
		console.log('Info: WebSocket connection closed.');
	};
}

function disconnect() {
	if (ws != null) {
		ws.close();
		ws = null;
	}
}

function send(message) {
	if (ws != null) {
		ws.send(message);
	} else {
		console.log('WebSocket connection not established, please connect.');
	}
}

function receber(data){
	var j = JSON.parse(data);
	if(j.notificacao){
		noty({
	  		text: tipo(j.notificacao[0].tipo)+' '+j.notificacao[0].num_shares+' shares a '+j.notificacao[0].preco + ' Coinz na ideia ' + j.notificacao[0].ideia,
	  		type: 'information',
	      dismissQueue: true,
	  		layout: 'bottomLeft',
	  		theme: 'defaultTheme'
	  	});
	}
	else if(j.preco){
		$("[idideia="+j.preco[0].ideia+"]").val(j.preco[0].ultimo_preco);
		$("[idideia="+j.preco[0].ideia+"]").text(j.preco[0].ultimo_preco);
	}
}

function tipo(str){
	if(str == "compra")
		return "Comprou";
	else if(str == "venda")
		return "Vendeu";
}

connect();

