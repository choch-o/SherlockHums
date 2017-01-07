var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var musicSchema = new Schema({
    title: String,
    singer: String,
    path: String
});

module.exports = mongoose.model('music', musicSchema);

