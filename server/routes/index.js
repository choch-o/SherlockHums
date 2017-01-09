var fs = require('fs');
var dirname = "/home/youngsoo/SherlockHums/server/Recorded";
var recordedFileAMR = "recordedFile.amr";
var recordedFileWAV = "recordedFile.wav";
var recordedFileMID = "recordedFile.mid";
var chunks = [];
var PythonShell = require('python-shell');

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
    keyFilename: "../SherlockHums-c51073a5b67e.json"
});

var bucket = storage.bucket("gs://sherlockhums-25f4c.appspot.com");

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
                    console.log("Upload of the recorded file is successful!");
                    //console.log("Data : " + data);
                }
            });
            res.send("Upload of the recorded file is successful!");
            res.end();
        })

        PythonShell.run('audio_to_midi_melodia.py', function(err, results){
//            if (err) throw err;
//            console.log('results : %j', results);
            console.log('Converting wav to mid is complete!');
            
            ref.child("game").update({mid: "neWWWWWWWWWWWW!"});
        });

    });

}

var childProcess = require('child_process');

function runScript(scriptPath, callback) {

    // keep track of whether callback has been invoked to prevent multiple invocations
    var invoked = false;

    var process = childProcess.fork(scriptPath);

    // listen for errors as they may prevent the exit event from firing
    process.on('error', function (err) {
        if (invoked) return;
        invoked = true;
        callback(err);
    });

    // execute the callback once the process has finished running
    process.on('exit', function (code) {
        if (invoked) return;
        invoked = true;
        var err = code === 0 ? null : new Error('exit code ' + code);
        callback(err);
    });

}

