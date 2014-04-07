
/**
 * Module dependencies.
 */

/* some useful links:
http://nodejs.org/api/dgram.html
*/

var express = require('express');
var routes = require('./routes');
var user = require('./routes/user');
var http = require('http');
var path = require('path');
var dgram = require('dgram');
var net = require('net');

var app = express();

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


// UDP shenanigans : communicating with the android phone
// see http://nodejs.org/api/dgram.html for more deets
var server = dgram.createSocket("udp4");

server.on("error", function (err) {
  console.log("server error:\n" + err.stack);
  server.close();
});

server.on("message", function (msg, rinfo) {
  console.log("server got: " + msg + " from " +
    rinfo.address + ":" + rinfo.port);
});

server.on("listening", function () {
  var address = server.address();
  console.log("server listening " +
      address.address + ":" + address.port);
});

server.bind(8888);
// server listening 0.0.0.0:8888


// TCP shenanigans: sending things to the phone
var phones = []; //by socket

var server = net.createServer(function(socket) { //'connection' listener
  phones.push(socket);
  console.log('server connected');
  console.log('remote address: ' + socket.remoteAddress);
  console.log('remote port: ' + socket.remotePort);
  console.log('phones list: ' + phones)
  socket.on('end', function() {
    console.log('server disconnected');
    phones.splice(phones.indexOf(socket),1); //removing from phones
    console.log('phones list: ' + phones);
  });
  socket.write('hello (from the server)\r\n');
  socket.write('bob (from the server)\r\n');
  socket.pipe(socket).pipe(process.stdout);
});
server.listen(9999, function() { //'listening' listener
  console.log('server bound');
});



// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.get('/', routes.index);
app.get('/users', user.list);

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});
