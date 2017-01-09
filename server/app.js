// app.js

var express     = require('express');
var app         = express();
var mongoose    = require('mongoose');
var port        = process.env.PORT || 3000;
var server      = app.listen(port, function(){
    console.log("Express server has started on port " + port)
});

var db = mongoose.connection;
db.on('error', console.error);
db.once('open', function(){
    console.log("Connected to mongod server");
});

mongoose.connect("mongodb://localhost:27017/sherlockhums");

var Music = require('./models/music');
var router = require('./routes')(app, Music);

