var fs = require('fs');
var dirname = "/home/youngsoo/SherlockHums/server/Recorded";
var recordedFileName = "recordedFile.amr";
var chunks = [];

module.exports = function(app, Music){

    // INITIAL TEXT
    app.get('/', function(req, res){
        res.send('Welcome to SherlockHums!');
        console.log("Get page!");
    });

    // GET RECORDED FILE
    app.get('/recorded', function(req, res){
        res.sendFile(dirname + '/' + recordedFileName);
        console.log("Get recorded file!");
    });

    // UPLOAD RECORDED FILE
    app.post('/recorded', function(req, res){
        chunks = [];
        req.on('data', function(chunk){
            chunks.push(chunk);
        });

        req.on('end', function(){
            var data = Buffer.concat(chunks);
            fs.writeFile(dirname + "/" + recordedFileName, data, 'binary', function(err){
                if (err) {
                    console.log("Can't upload recorded file! " + err);
                } else {
                    console.log("Upload of the recorded file is successful!");
                    console.log("Data : " + data);
                }
            });
            res.send("Upload of the recorded file is successful!");
            res.end();
        });
    });

}
