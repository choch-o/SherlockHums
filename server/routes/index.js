var fs = require('fs');
var dirname = "/home/youngsoo/SherlockHums/server/Recorded";
var recordedFileAMR = "recordedFile.amr";
var recordedFileWAV = "recordedFile.wav";
var recordedFileMID = "recordedFile.mid";
var chunks = [];
var PythonShell = require('python-shell');
var uuid = require('node-uuid');

var admin = require("firebase-admin");

var serviceAccount = require("../SherlockHums-c51073a5b67e.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://sherlockhums-25f4c.firebaseio.com"
});

var db = admin.database();
var ref = db.ref("/");

var gcloud = require("google-cloud");

var storage = gcloud.storage({
    projectId: "sherlockhums-25f4c",
    keyFilename: "./SherlockHums-c51073a5b67e.json"
});

var bucket = storage.bucket("sherlockhums-25f4c.appspot.com");

module.exports = function(app, Music){

    // INITIAL TEXT
    app.get('/', function(req, res){
        res.send('Welcome to SherlockHums!');
        console.log("Get page!");
    });

    // GET RECORDED FILE (.amr)
    app.get('/recorded.amr', function(req, res){
        res.sendFile(dirname + '/' + recordedFileAMR);
        console.log("Get recorded file(.amr)!");
    });

    // GET RECORDED FILE (.wav)
    app.get('/recorded.wav', function(req, res){
        res.sendFile(dirname + '/' + recordedFileWAV);
        console.log("Get recorded file(.wav)!");
    });

    // GET RECORDED FILE (.mid)
    app.get('/recorded.mid', function(req, res){
        res.sendFile(dirname + '/' + recordedFileMID);
        console.log("Get recorded file(.mid)!");
    });

    // UPLOAD RECORDED FILE
    app.post('/recorded', function(req, res){
        chunks = [];
        req.on('data', function(chunk){
            chunks.push(chunk);
        });

        req.on('end', function(){
            var data = Buffer.concat(chunks);
            fs.writeFile(dirname + "/" + recordedFileWAV, data, 'binary', function(err){
                if (err) {
                    console.log("Can't upload recorded file! " + err);
                } else {
                    console.log("Uploading the recorded file to node.js server - Successful!");
                }
            });
        })

        PythonShell.run('audio_to_midi_melodia.py', function(err, results){
            console.log('Converting wav to mid - Complete!');
            
            var recordedId = uuid.v1() + '.mid';
            console.log("uuid : " + recordedId);

//            postMIDfileToFirebase(recordedId, function(rId) {
//                ref.child("game").update({mid: rId});
//                console.log("firebase DB : modify mid path (" + rId + ")");
//            });

            var localReadStream = fs.createReadStream(dirname + '/' + recordedFileMID);
            var remoteWriteStream = bucket.file(recordedId).createWriteStream();
            localReadStream.pipe(remoteWriteStream);
            remoteWriteStream.on('finish', function() {
                console.log("Posting mid to firebase storage - Complete!");
//                ref.child("midi").child("midi_path").set(recordedId, function(error) {
//                ref.child("midi").update({midi_path: recordedId}, function(error) {
//                ref.child("game").update({mid: recordedId}, function(error) {
//                    if (error) {
//                        console.log("Data could not be saved. " + error);
//                    } else {
//                        console.log("firebase DB : modify mid path (" + recordedId + ")");
//                    }
//                });
                
                res.send(recordedId);
                res.end();
            });
        });

    });

}

//function postMIDfileToFirebase(recordedId, callback) {
//    var localReadStream = getLocalReadStream(fs, dirname, recordedFileMID);
//    var remoteWriteStream = getRemoteWriteStream(bucket, recordedId);
//    localReadStream.pipe(remoteWriteStream);
//    remoteWriteStream.on('finish', function() {
//        console.log("Posting mid to firebase storage - Complete!");
//        callback(recordedId);
//    });
//}
//
//function getLocalReadStream(fs, dirname, recordedFileMID) {
//    return fs.createReadStream(dirname + '/' + recordedFileMID);
//}
//
//function getRemoteWriteStream(bucket, recordedId) {
//    return bucket.file(recordedId).createWriteStream();
//}
//
