
var 
    express = require('express'),
    path = require('path');

var app = express();

app.use('/front', express.static(path.join(__dirname, 'front')));

app.listen(3000);