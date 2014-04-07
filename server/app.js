
/**
 * Module dependencies.
 */

/* some useful links:
http://nodejs.org/api/dgram.html
*/

var express = require('express');
var app = express();
var user = require('./routes/user');
var server = require('http').createServer(app);
var io = require('socket.io').listen(server);
var path = require('path');
var dgram = require('dgram');
var net = require('net');


// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

io.set('log level', 1); //reduces logging

// UDP shenanigans : communicating with the android phone
// see http://nodejs.org/api/dgram.html for more deets
var UDPserver = dgram.createSocket("udp4");

UDPserver.on("error", function (err) {
  console.log("server error:\n" + err.stack);
  server.close();
});

UDPserver.on("message", function (msg, rinfo) {
  console.log("server got: " + msg + " from " +
    rinfo.address + ":" + rinfo.port);
});

UDPserver.on("listening", function () {
  var address = server.address();
  console.log("server listening " +
      address.address + ":" + address.port);
});

UDPserver.bind(8888);
// server listening 0.0.0.0:8888


// TCP shenanigans: sending things to the phone
var phones = {}; //address: object

var TCPserver = net.createServer(function(socket) { //'connection' listener
  phones[socket.remoteAddress] = socket;
  console.log('server connected');
  console.log('remote address: ' + socket.remoteAddress);
  console.log('remote port: ' + socket.remotePort);
  console.log('phones list: ' + phones)
  socket.on('end', function() {
    console.log('server disconnected');
    delete phones[socket.remoteAddress];
    console.log('phones list: ' + phones);
  });
  socket.write('hello (from the server)\r\n');
  socket.write('bob (from the server)\r\n');
  socket.pipe(socket).pipe(process.stdout);
});
TCPserver.listen(9999, function() { //'listening' listener
  console.log('server bound');
});



//socket.io things
io.sockets.on('connection', function (socket) {
  socket.emit('news', { hello: 'world' }); 
  
  socket.on('message', function (data) {
    console.log(data);
    phones[data.address].write(data.text + '\r\n');
  });
});



// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.get('/', function(req, res){
  res.render('index', { title: 'eye-helper!!', phones:phones});
  }
);
app.get('/users', user.list);

server.listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});
