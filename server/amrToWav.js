var fs = require('fs')
var cloudconvert = new (require('cloudconvert'))('QQV7TQn9YsEI2_wsguwHCRN-aX77isxLovirFudr-CRpvbKd6zmP2hBYqKhKh3V6Tp7vTHGj8em09haFOT3AqQ');

fs.createReadStream('./Recorded/recordedFile.amr')
.pipe(cloudconvert.convert({
    "inputformat": "amr",
    "outputformat": "wav",
    "input": "upload"
}))
.pipe(fs.createWriteStream('./Recorded/recordedFile.wav'));
