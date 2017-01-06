var fs = require('fs');
var dirname = "/home/youngsoo/SherlockHums/server/Recorded";
var recordedFileAMR = "recordedFile.amr";
var recordedFileWAV = "recordedFile.wav"; 
var chunks = [];

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


    // UPLOAD RECORDED FILE
    app.post('/recorded', function(req, res){
        chunks = [];
        req.on('data', function(chunk){
            chunks.push(chunk);
        });

        req.on('end', function(){
            var data = Buffer.concat(chunks);
            fs.writeFile(dirname + "/" + recordedFileAMR, data, 'binary', function(err){
                if (err) {
                    console.log("Can't upload recorded file! " + err);
                } else {
                    console.log("Upload of the recorded file is successful!");
                    console.log("Data : " + data);
                }
            });
            res.send("Upload of the recorded file is successful!");
            res.end();
        })
        runScript('./amrToWav.js', function(err){
            if (err) throw err;
            console.log('Converting amr to wav is complete!');
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

