

var socket = io.connect('http://localhost');
   socket.on('news', function (data) {
   console.log(data);
   socket.emit('my other event', { my: 'data' });
  });

//enter key shenanigans
$('#phones').keyup(function (e) {
	if (e.keyCode === 13) {
		console.log(e.target);
		socket.emit('message', {'address': e.target.id,'text': e.target.value});
	}
})