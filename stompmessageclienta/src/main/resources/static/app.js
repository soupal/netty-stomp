var stompClient = null;

function connect() {
	var socket = new SockJS('/gs-guide-websocket');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/greetings', function(greeting) {
			console.log("==greeting==" + greeting);
			showGreeting(JSON.parse(greeting.body).content);
		});
	});
}

function sendName() {
	stompClient.send("/app/hello", {}, JSON.stringify({
		'name' : $("#name").val()
	}));
	$("#greetings").append(
			"<tr><td>" + "<img src=\"icon2.jpg\"  width=\"25\">"
					+ "</td></tr><tr><td>" + $("#name").val() + "</td></tr>");
	$("#name").val("");
}

function showGreeting(message) {
	$("#greetings").append(
			"<tr><td>" + "<img src=\"icon1.jpg\"  width=\"25\">"
					+ "</td></tr><tr><td>" + message + "</td></tr>");
}

$(function() {
	$("form").on('submit', function(e) {
		e.preventDefault();
	});
	connect();
	$("#send").click(function() {
		sendName();
	});
});
